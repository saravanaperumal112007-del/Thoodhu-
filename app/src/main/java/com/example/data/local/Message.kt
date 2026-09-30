package com.example.data.local

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Core Message entity in 'com.example.data.local' representing messages,
 * content, delivery status, and reaction metadata in THOODHU.
 */
@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["timestamp"])
    ]
)
data class Message(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT", // PENDING, SENDING, SENT, DELIVERED, READ, FAILED
    val reactionMetadata: String? = null, // Reaction metadata, e.g. "🦅:2,❤️:1" or JSON
    val senderName: String = "",
    val isOutgoing: Boolean = false,
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val replyToSender: String? = null,
    val isEdited: Boolean = false,
    val isStarred: Boolean = false,
    val messageType: String = "TEXT", // TEXT, IMAGE, VIDEO, VOICE, DOCUMENT, LOCATION, POLL
    val mediaUrl: String? = null,
    val mediaMimeType: String? = null,
    val mediaSizeBytes: Long? = null,
    val isViewOnce: Boolean = false,
    val viewOnceOpened: Boolean = false
) {
    /**
     * Backward-compatible accessor for content.
     */
    @get:Ignore
    val text: String
        get() = content
}
