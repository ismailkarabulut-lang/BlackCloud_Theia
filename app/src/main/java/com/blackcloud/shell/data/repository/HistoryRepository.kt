package com.blackcloud.shell.data.repository

import android.content.Context
import com.blackcloud.shell.data.database.ChatDao
import com.blackcloud.shell.data.database.ChatDatabase
import com.blackcloud.shell.data.database.ChatMessageEntity
import com.blackcloud.shell.data.database.ChatSessionEntity
import com.blackcloud.shell.data.model.KkypMetadata
import com.blackcloud.shell.data.model.ModelManager
import com.blackcloud.shell.ui.components.Message
import com.blackcloud.shell.ui.components.MessageSender
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository that abstracts Chat History operations using Room.
 */
class HistoryRepository(context: Context) {
    private val chatDao: ChatDao = ChatDatabase.getInstance(context).chatDao()

    // Moshi deserializer for KKYP issues
    private val moshi = Moshi.Builder().build()
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val listAdapter = moshi.adapter<List<String>>(stringListType)

    private fun issuesToJson(issues: List<String>?): String? {
        if (issues == null) return null
        return try {
            listAdapter.toJson(issues)
        } catch (e: Exception) {
            null
        }
    }

    private fun jsonToIssues(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            listAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Convert Entity to UI model
    private fun ChatMessageEntity.toUiMessage(): Message {
        val kkyp = if (verified != null) {
            KkypMetadata(verified = verified, issues = jsonToIssues(issuesJson))
        } else null

        return Message(
            id = id,
            sender = if (sender == "USER") MessageSender.USER else MessageSender.ASSISTANT,
            text = text,
            isComplete = isComplete,
            kkyp = kkyp
        )
    }

    // Convert UI model to Database Entity
    private fun Message.toEntity(sessionId: String, customTimestamp: Long): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            sessionId = sessionId,
            sender = sender.name,
            text = text,
            isComplete = isComplete,
            timestamp = customTimestamp,
            verified = kkyp?.verified,
            issuesJson = issuesToJson(kkyp?.issues)
        )
    }

    /**
     * Streams all past chat sessions reactively, ordered by most recently active.
     */
    val allSessions: Flow<List<ChatSessionEntity>> = chatDao.getAllSessionsFlow()

    /**
     * Streams chat sessions filtered by specific project.
     */
    fun getSessionsForProject(projectId: String): Flow<List<ChatSessionEntity>> {
        return chatDao.getSessionsForProjectFlow(projectId)
    }

    /**
     * Streams messages in a specific session, ordered by time.
     */
    fun getMessagesForSessionFlow(sessionId: String): Flow<List<Message>> {
        return chatDao.getMessagesForSessionFlow(sessionId).map { list ->
            list.map { it.toUiMessage() }
        }
    }

    /**
     * Gets all messages in a session synchronously/one-time as suspend function.
     */
    suspend fun getMessagesForSession(sessionId: String): List<Message> {
        return chatDao.getMessagesForSession(sessionId).map { it.toUiMessage() }
    }

    /**
     * Saves a message to the database, ensuring the chat session is created or updated.
     */
    suspend fun saveMessage(sessionId: String, message: Message, projectId: String?, customTimestamp: Long = System.currentTimeMillis()) {
        val existingSession = chatDao.getSessionById(sessionId)
        if (existingSession == null) {
            // Auto generate title from first message contents
            var title = "Yeni Sohbet"
            if (message.sender == MessageSender.USER) {
                val cleaned = message.text.trim()
                title = if (cleaned.length > 25) {
                    cleaned.take(22) + "..."
                } else cleaned
            }
            val activeModelName = ModelManager.getActiveModel().displayName
            val newSession = ChatSessionEntity(
                id = sessionId,
                title = title,
                projectId = projectId,
                timestamp = customTimestamp,
                modelId = activeModelName
            )
            chatDao.insertSession(newSession)
        } else {
            // Update the title if it was named "Yeni Sohbet" and this is the first real user message
            if (existingSession.title == "Yeni Sohbet" && message.sender == MessageSender.USER) {
                val cleaned = message.text.trim()
                val draftTitle = if (cleaned.length > 25) {
                    cleaned.take(22) + "..."
                } else cleaned
                chatDao.updateSessionTitle(sessionId, draftTitle)
            }
            // Touch timestamp for ordering
            chatDao.updateSessionTimestamp(sessionId, customTimestamp)
        }

        // Insert message
        chatDao.insertMessage(message.toEntity(sessionId, customTimestamp))
    }

    /**
     * Updates an existing message in Room (for SSE text accumulation chunks).
     */
    suspend fun updateMessage(sessionId: String, message: Message) {
        // Keeps the message updated locally
        chatDao.insertMessage(message.toEntity(sessionId, System.currentTimeMillis()))
    }

    /**
     * Deletes a chat session in full (Cascade deletes all messages).
     */
    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSessionById(sessionId)
    }

    /**
     * Clears all database records (Clear Cache / Soft Reset).
     */
    suspend fun clearHistory() {
        chatDao.clearAllHistory()
    }
}
