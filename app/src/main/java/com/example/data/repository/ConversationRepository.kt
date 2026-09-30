package com.example.data.repository

import com.example.data.local.Conversation
import com.example.data.local.ConversationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Clean repository abstraction for managing 1-to-1, group, channel, and community conversations.
 */
class ConversationRepository(
    private val conversationDao: ConversationDao
) {
    /**
     * Reactive stream observing all conversations ordered by pin status and latest message time.
     */
    val allConversations: Flow<List<Conversation>> =
        conversationDao.getAllConversations().flowOn(Dispatchers.IO)

    /**
     * Observes unarchived conversations only.
     */
    val activeConversations: Flow<List<Conversation>> =
        conversationDao.getActiveConversations().flowOn(Dispatchers.IO)

    /**
     * Observes archived conversations only.
     */
    val archivedConversations: Flow<List<Conversation>> =
        conversationDao.getArchivedConversations().flowOn(Dispatchers.IO)

    /**
     * Observes conversations filtered by category type (DIRECT, GROUP, CHANNEL, COMMUNITY).
     */
    fun getConversationsByType(type: String): Flow<List<Conversation>> =
        conversationDao.getConversationsByType(type).flowOn(Dispatchers.IO)

    /**
     * Observes a specific conversation by ID.
     */
    fun getConversationById(id: String): Flow<Conversation?> =
        conversationDao.getConversationById(id).flowOn(Dispatchers.IO)

    /**
     * Fetches a conversation synchronously by ID.
     */
    suspend fun getConversationByIdSync(id: String): Conversation? = withContext(Dispatchers.IO) {
        conversationDao.getConversationByIdSync(id)
    }

    /**
     * Inserts or replaces a conversation record.
     */
    suspend fun saveConversation(conversation: Conversation) = withContext(Dispatchers.IO) {
        conversationDao.insertConversation(conversation)
    }

    /**
     * Inserts or replaces multiple conversation records.
     */
    suspend fun saveConversations(conversations: List<Conversation>) = withContext(Dispatchers.IO) {
        conversationDao.insertConversations(conversations)
    }

    /**
     * Updates an existing conversation record.
     */
    suspend fun updateConversation(conversation: Conversation) = withContext(Dispatchers.IO) {
        conversationDao.updateConversation(conversation)
    }

    /**
     * Updates the preview snippet, timestamp, and status for the last message in a conversation.
     */
    suspend fun updateLastMessage(
        id: String,
        text: String,
        timestamp: Long = System.currentTimeMillis(),
        status: String = "SENT"
    ) = withContext(Dispatchers.IO) {
        conversationDao.updateLastMessage(id, text, timestamp, status)
    }

    /**
     * Marks all unread messages as read in a conversation.
     */
    suspend fun markAsRead(id: String) = withContext(Dispatchers.IO) {
        conversationDao.markAsRead(id)
    }

    /**
     * Toggles pinned priority status of a conversation.
     */
    suspend fun togglePin(id: String, isPinned: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.togglePin(id, isPinned)
    }

    /**
     * Toggles notification mute status for a conversation.
     */
    suspend fun toggleMute(id: String, isMuted: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.toggleMute(id, isMuted)
    }

    /**
     * Deletes a conversation by its unique ID.
     */
    suspend fun deleteConversation(id: String) = withContext(Dispatchers.IO) {
        conversationDao.deleteConversation(id)
    }

    /**
     * Deletes a conversation record entity.
     */
    suspend fun deleteConversation(conversation: Conversation) = withContext(Dispatchers.IO) {
        conversationDao.deleteConversation(conversation)
    }
}
