package com.example.thoodhu.data.repository

import com.example.thoodhu.data.local.dao.ConversationDao
import com.example.thoodhu.data.local.dao.MessageDao
import com.example.thoodhu.data.local.dao.UserDao
import com.example.thoodhu.data.local.entity.ConversationEntity
import com.example.thoodhu.data.local.entity.MessageEntity
import com.example.thoodhu.data.local.entity.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: UserEntity) : AuthState()
}

class AuthRepository(
    private val userDao: UserDao,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    val myProfile: Flow<UserEntity?> = userDao.getMyProfile()

    /**
     * Simulates requesting SMS OTP for the given phone number with international code.
     */
    suspend fun requestOtp(phoneNumber: String): Result<String> {
        delay(800) // Simulated network round-trip
        if (phoneNumber.length < 8) {
            return Result.failure(IllegalArgumentException("Please enter a valid phone number"))
        }
        // In local demo mode, fixed code 778899 or any 6-digit code is accepted
        return Result.success("OTP sent securely via SMS")
    }

    /**
     * Verifies the 6-digit OTP.
     */
    suspend fun verifyOtp(code: String): Result<Boolean> {
        delay(600)
        return if (code.length == 6) {
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Invalid verification code. Enter a 6-digit code."))
        }
    }

    /**
     * Completes user registration with a unique @username, display name, and bio.
     */
    suspend fun completeProfile(
        phoneNumber: String,
        username: String,
        displayName: String,
        bio: String
    ): Result<UserEntity> {
        val cleanUsername = if (username.startsWith("@")) username.lowercase() else "@${username.lowercase()}"
        if (cleanUsername.length < 4) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters"))
        }

        val userId = UUID.randomUUID().toString()
        val user = UserEntity(
            id = userId,
            username = cleanUsername,
            displayName = displayName.ifBlank { cleanUsername.removePrefix("@") },
            phoneNumber = phoneNumber,
            bio = bio.ifBlank { "Flying swift with THOODHU 🦅" },
            isMe = true,
            isOnline = true,
            publicKey = "THOODHU-ED25519-${UUID.randomUUID().toString().take(16).uppercase()}"
        )

        userDao.insertUser(user)
        seedInitialConversations(user)
        return Result.success(user)
    }

    /**
     * Seeds initial THOODHU conversations to showcase the platform's features,
     * groups, channels, and original message delivery indicators.
     */
    private suspend fun seedInitialConversations(me: UserEntity) {
        val eagleGuideId = "conv-thoodhu-team"
        val securityChannelId = "conv-thoodhu-channel"
        val communityGroupId = "conv-thoodhu-community"

        val conversations = listOf(
            ConversationEntity(
                id = eagleGuideId,
                title = "THOODHU Eagle Guide",
                type = "DIRECT",
                lastMessageText = "Welcome to THOODHU! Your communications are protected with sovereign security.",
                lastMessageTimestamp = System.currentTimeMillis(),
                lastMessageStatus = "READ",
                unreadCount = 1,
                isPinned = true
            ),
            ConversationEntity(
                id = securityChannelId,
                title = "THOODHU Announcements",
                type = "CHANNEL",
                lastMessageText = "Phase 1 initialized: Secure architecture, custom design, and local database operational.",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                lastMessageStatus = "DELIVERED",
                unreadCount = 0,
                isPinned = true
            ),
            ConversationEntity(
                id = communityGroupId,
                title = "Sovereign Messengers Group",
                type = "GROUP",
                lastMessageText = "Arun: Has everyone verified their public key in settings?",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                lastMessageStatus = "SENT",
                unreadCount = 2,
                isPinned = false
            )
        )
        conversationDao.insertConversations(conversations)

        // Seed messages in the guide conversation
        val now = System.currentTimeMillis()
        val guideMessages = listOf(
            MessageEntity(
                id = "msg-1",
                conversationId = eagleGuideId,
                senderId = "thoodhu-bot",
                senderName = "THOODHU Eagle Guide",
                content = "Welcome to THOODHU, ${me.displayName} (@${me.username.removePrefix("@")})! 🦅",
                timestamp = now - 180000,
                status = "READ",
                reactionMetadata = "🦅:1",
                isOutgoing = false
            ),
            MessageEntity(
                id = "msg-2",
                conversationId = eagleGuideId,
                senderId = "thoodhu-bot",
                senderName = "THOODHU Eagle Guide",
                content = "THOODHU symbolizes the swift eagle carrying your letters across open skies with speed, freedom, and privacy.",
                timestamp = now - 120000,
                status = "READ",
                reactionMetadata = "⚡:1",
                isOutgoing = false
            ),
            MessageEntity(
                id = "msg-3",
                conversationId = eagleGuideId,
                senderId = me.id,
                senderName = me.displayName,
                content = "Great to be here! My phone number is protected behind my @username.",
                timestamp = now - 60000,
                status = "READ",
                reactionMetadata = "❤️:1",
                isOutgoing = true
            ),
            MessageEntity(
                id = "msg-4",
                conversationId = eagleGuideId,
                senderId = "thoodhu-bot",
                senderName = "THOODHU Eagle Guide",
                content = "Welcome to THOODHU! Your communications are protected with sovereign security.",
                timestamp = now,
                status = "READ",
                reactionMetadata = "🛡️:1",
                isOutgoing = false
            )
        )
        messageDao.insertMessages(guideMessages)
    }

    suspend fun signOut() {
        userDao.clearMyProfile()
    }
}
