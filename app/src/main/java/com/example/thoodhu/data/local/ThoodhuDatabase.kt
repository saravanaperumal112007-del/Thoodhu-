package com.example.thoodhu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.Conversation
import com.example.data.local.Message
import com.example.data.local.User
import com.example.thoodhu.data.local.dao.ConversationDao
import com.example.thoodhu.data.local.dao.MessageDao
import com.example.thoodhu.data.local.dao.SettingsDao
import com.example.thoodhu.data.local.dao.UserDao
import com.example.thoodhu.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Conversation::class,
        Message::class,
        UserSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ThoodhuDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: ThoodhuDatabase? = null

        fun getInstance(context: Context): ThoodhuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThoodhuDatabase::class.java,
                    "thoodhu_secure.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default user settings on first create
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.settingsDao().insertOrUpdateSettings(
                                UserSettingsEntity(
                                    id = 1,
                                    themeMode = "SYSTEM",
                                    accentColor = "SKY",
                                    fontSize = "NORMAL",
                                    chatDensity = "DEFAULT",
                                    lastSeenPrivacy = "NOBODY",
                                    readReceiptsEnabled = true,
                                    typingIndicatorEnabled = true,
                                    biometricLockEnabled = false,
                                    lockTimeoutMinutes = 1,
                                    twoStepVerificationEnabled = false,
                                    notificationPreviews = true,
                                    disappearingDefault = "OFF"
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
