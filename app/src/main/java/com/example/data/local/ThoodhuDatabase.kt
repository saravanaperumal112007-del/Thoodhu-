package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main RoomDatabase for THOODHU persistence layer in 'com.example.data.local'.
 * Manages the User, Conversation, and Message entities and exposes their DAOs.
 */
@Database(
    entities = [
        User::class,
        Conversation::class,
        Message::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ThoodhuDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: ThoodhuDatabase? = null

        fun getInstance(context: Context): ThoodhuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThoodhuDatabase::class.java,
                    "thoodhu_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
