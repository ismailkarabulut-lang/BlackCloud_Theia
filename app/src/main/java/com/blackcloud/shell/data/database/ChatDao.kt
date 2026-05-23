package com.blackcloud.shell.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // --- SESSIONS ---
    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getSessionsForProjectFlow(projectId: String): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: String)

    @Query("UPDATE chat_sessions SET title = :newTitle WHERE id = :sessionId")
    suspend fun updateSessionTitle(sessionId: String, newTitle: String)

    @Query("UPDATE chat_sessions SET timestamp = :newTimestamp WHERE id = :sessionId")
    suspend fun updateSessionTimestamp(sessionId: String, newTimestamp: Long)

    // --- MESSAGES ---
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSessionFlow(sessionId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getMessagesForSession(sessionId: String): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllHistory()
}
