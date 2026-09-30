package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Core User entity representing local and remote user identities,
 * public cryptographic keys, and presence in THOODHU.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val username: String, // e.g. @arun123
    val displayName: String,
    val phoneNumber: String,
    val bio: String,
    val avatarUrl: String? = null,
    val isMe: Boolean = false,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false,
    val publicKey: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
