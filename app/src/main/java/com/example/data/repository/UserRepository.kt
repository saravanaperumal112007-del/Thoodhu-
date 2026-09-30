package com.example.data.repository

import com.example.data.local.User
import com.example.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Clean repository abstraction for accessing and managing User data in THOODHU.
 */
class UserRepository(
    private val userDao: UserDao
) {
    /**
     * Reactive stream observing the currently logged-in user profile.
     */
    val myProfile: Flow<User?> = userDao.getMyProfile().flowOn(Dispatchers.IO)

    /**
     * Reactive stream observing all known contacts sorted by display name.
     */
    val allContacts: Flow<List<User>> = userDao.getAllContacts().flowOn(Dispatchers.IO)

    /**
     * Observes a specific user by their unique ID.
     */
    fun getUserById(userId: String): Flow<User?> =
        userDao.getUserById(userId).flowOn(Dispatchers.IO)

    /**
     * Fetches a specific user synchronously inside coroutine scope.
     */
    suspend fun getUserByIdSync(userId: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByIdSync(userId)
    }

    /**
     * Resolves a user by their sovereign @username handle.
     */
    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByUsername(username)
    }

    /**
     * Saves or replaces a user record.
     */
    suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    /**
     * Saves or replaces a batch of user records.
     */
    suspend fun saveUsers(users: List<User>) = withContext(Dispatchers.IO) {
        userDao.insertUsers(users)
    }

    /**
     * Updates an existing user record.
     */
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    /**
     * Deletes a user record.
     */
    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        userDao.deleteUser(user)
    }

    /**
     * Updates the online status and last seen timestamp of a user.
     */
    suspend fun updatePresence(
        userId: String,
        isOnline: Boolean,
        lastSeen: Long = System.currentTimeMillis()
    ) = withContext(Dispatchers.IO) {
        userDao.updateUserPresence(userId, isOnline, lastSeen)
    }

    /**
     * Clears the current user profile from local database during log-out.
     */
    suspend fun clearMyProfile() = withContext(Dispatchers.IO) {
        userDao.clearMyProfile()
    }
}
