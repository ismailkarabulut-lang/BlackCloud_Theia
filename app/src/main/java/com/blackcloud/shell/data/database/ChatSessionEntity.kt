package com.blackcloud.shell.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a chat conversation session.
 */
@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val id: String, // Matches sessionId
    val title: String,
    val projectId: String?,
    val timestamp: Long,
    val modelId: String? = null
)
