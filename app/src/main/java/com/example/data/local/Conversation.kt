package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Core Conversation entity representing 1-to-1 chats, group chats,
 * broadcast channels, and community threads in THOODHU.
 */
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val title: String,
    val type: String = "DIRECT", // DIRECT, GROUP, CHANNEL, COMMUNITY
    val avatarUrl: String? = null,
    val lastMessageText: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val lastMessageStatus: String = "SENT", // PENDING, SENDING, SENT, DELIVERED, READ, FAILED
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val isDisappearing: Boolean = false,
    val disappearingDurationHours: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
