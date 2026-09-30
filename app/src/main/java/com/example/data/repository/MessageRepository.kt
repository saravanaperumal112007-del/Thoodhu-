package com.example.data.repository

import com.example.data.local.ConversationDao
import com.example.data.local.Message
import com.example.data.local.MessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Clean repository abstraction for handling message pipelines, delivery states,
 * and reactions in THOODHU.
 */
class MessageRepository(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao? = null
) {
    /**
     * Reactive stream observing all messages in a conversation ordered chronologically.
     */
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> =
        messageDao.getMessagesForConversation(conversationId).flowOn(Dispatchers.IO)

    /**
     * Retrieves a single message by ID.
     */
    suspend fun getMessageById(messageId: String): Message? = withContext(Dispatchers.IO) {
        messageDao.getMessageById(messageId)
    }

    /**
     * Inserts or replaces a message record.
     */
    suspend fun insertMessage(message: Message) = withContext(Dispatchers.IO) {
        messageDao.insertMessage(message)
    }

    /**
     * Inserts multiple message records.
     */
    suspend fun insertMessages(messages: List<Message>) = withContext(Dispatchers.IO) {
        messageDao.insertMessages(messages)
    }

    /**
     * Updates an existing message record.
     */
    suspend fun updateMessage(message: Message) = withContext(Dispatchers.IO) {
        messageDao.updateMessage(message)
    }

    /**
     * Updates message delivery status (e.g., PENDING, SENDING, SENT, DELIVERED, READ, FAILED).
     */
    suspend fun updateMessageStatus(messageId: String, status: String) = withContext(Dispatchers.IO) {
        messageDao.updateMessageStatus(messageId, status)
    }

    /**
     * Sets or increments an emoji reaction on a message (e.g., "🦅", "❤️").
     */
    suspend fun addReaction(messageId: String, emoji: String) = withContext(Dispatchers.IO) {
        val existing = messageDao.getMessageById(messageId)
        val currentMeta = existing?.reactionMetadata
        val newMeta = if (currentMeta.isNullOrBlank()) {
            "$emoji:1"
        } else {
            val parts = currentMeta.split(",").map { it.trim() }.toMutableList()
            val existingIndex = parts.indexOfFirst { it.startsWith("$emoji:") }
            if (existingIndex >= 0) {
                val count = parts[existingIndex].substringAfter(":").toIntOrNull() ?: 1
                parts[existingIndex] = "$emoji:${count + 1}"
            } else {
                parts.add("$emoji:1")
            }
            parts.joinToString(",")
        }
        messageDao.updateReactionMetadata(messageId, newMeta)
    }

    /**
     * Clears all reaction metadata from a message.
     */
    suspend fun clearReactions(messageId: String) = withContext(Dispatchers.IO) {
        messageDao.updateReactionMetadata(messageId, null)
    }

    /**
     * Pipeline method to send a new message and update the parent conversation's last message snippet.
     */
    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        senderName: String,
        content: String,
        isOutgoing: Boolean = true
    ): Message = withContext(Dispatchers.IO) {
        val message = Message(
            id = "msg-${UUID.randomUUID()}",
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            content = content,
            timestamp = System.currentTimeMillis(),
            status = "SENDING",
            isOutgoing = isOutgoing
        )
        messageDao.insertMessage(message)
        conversationDao?.updateLastMessage(
            id = conversationId,
            text = content,
            timestamp = message.timestamp,
            status = "SENDING"
        )
        message
    }

    /**
     * Deletes a specific message entity.
     */
    suspend fun deleteMessage(message: Message) = withContext(Dispatchers.IO) {
        messageDao.deleteMessage(message)
    }

    /**
     * Deletes a message by its ID.
     */
    suspend fun deleteMessageById(messageId: String) = withContext(Dispatchers.IO) {
        messageDao.deleteMessageById(messageId)
    }

    /**
     * Clears all messages in a conversation.
     */
    suspend fun clearMessagesInConversation(conversationId: String) = withContext(Dispatchers.IO) {
        messageDao.clearMessagesInConversation(conversationId)
    }
}
