package com.blackcloud.shell.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing a single message within a chat session.
 * Configured with ForeignKey cascade deletion so when a session is deleted,
 * all its messages are cleaned up automatically.
 */
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val sender: String, // "USER" or "ASSISTANT"
    val text: String,
    val isComplete: Boolean,
    val timestamp: Long,
    val verified: Boolean?, // KKYP verified or not
    val issuesJson: String? // Serialized KKYP issues list
)
