package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entity operations.
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isMe = 1 LIMIT 1")
    fun getMyProfile(): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserByIdSync(userId: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE isMe = 0 ORDER BY displayName ASC")
    fun getAllContacts(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE isMe = 1")
    suspend fun clearMyProfile()

    @Query("UPDATE users SET isOnline = :isOnline, lastSeenTimestamp = :lastSeen WHERE id = :userId")
    suspend fun updateUserPresence(userId: String, isOnline: Boolean, lastSeen: Long)
}
