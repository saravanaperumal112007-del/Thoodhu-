package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Conversation entity operations.
 */
@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getActiveConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE isArchived = 1 ORDER BY lastMessageTimestamp DESC")
    fun getArchivedConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE type = :type ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getConversationsByType(type: String): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: String): Flow<Conversation?>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationByIdSync(id: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("UPDATE conversations SET lastMessageText = :text, lastMessageTimestamp = :timestamp, lastMessageStatus = :status WHERE id = :id")
    suspend fun updateLastMessage(id: String, text: String, timestamp: Long, status: String)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE id = :id")
    suspend fun togglePin(id: String, isPinned: Boolean)

    @Query("UPDATE conversations SET isMuted = :isMuted WHERE id = :id")
    suspend fun toggleMute(id: String, isMuted: Boolean)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: String)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)

    @Delete
    suspend fun deleteConversation(conversation: Conversation)
}
