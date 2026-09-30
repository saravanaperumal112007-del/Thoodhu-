package com.example.thoodhu.data.repository

import com.example.thoodhu.data.local.dao.ConversationDao
import com.example.thoodhu.data.local.dao.MessageDao
import com.example.thoodhu.data.local.entity.ConversationEntity
import com.example.thoodhu.data.local.entity.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class MessagingRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    val conversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    fun getConversation(id: String): Flow<ConversationEntity?> = conversationDao.getConversationById(id)

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForConversation(conversationId)

    /**
     * Sends a message through the THOODHU message pipeline:
     * Starts with SENDING -> transitions to SENT -> transitions to DELIVERED -> transitions to READ
     * reactive to demonstrate the original delivery status states.
     */
    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        senderName: String,
        text: String
    ) {
        val messageId = "msg-${UUID.randomUUID()}"
        val initialMessage = MessageEntity(
            id = messageId,
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            content = text,
            timestamp = System.currentTimeMillis(),
            status = "SENDING",
            reactionMetadata = null,
            isOutgoing = true
        )

        messageDao.insertMessage(initialMessage)

        // Update conversation's last message snippet
        val currentConv = conversationDao.getConversationById(conversationId)
        // Simulate lifecycle delivery progression asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            messageDao.updateMessageStatus(messageId, "SENT")

            delay(900)
            messageDao.updateMessageStatus(messageId, "DELIVERED")

            delay(1200)
            messageDao.updateMessageStatus(messageId, "READ")
        }
    }
}
