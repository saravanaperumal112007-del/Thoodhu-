package com.example.data.repository

import android.content.Context
import com.example.data.local.ThoodhuDatabase

/**
 * Singleton repository provider facilitating dependency access across the application.
 */
class RepositoryProvider private constructor(context: Context) {
    private val database = ThoodhuDatabase.getInstance(context)

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    val conversationRepository: ConversationRepository by lazy {
        ConversationRepository(database.conversationDao())
    }

    val messageRepository: MessageRepository by lazy {
        MessageRepository(database.messageDao(), database.conversationDao())
    }

    companion object {
        @Volatile
        private var INSTANCE: RepositoryProvider? = null

        fun getInstance(context: Context): RepositoryProvider {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RepositoryProvider(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
