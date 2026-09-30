package com.example.thoodhu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val accentColor: String = "SKY",
    val fontSize: String = "NORMAL", // SMALL, NORMAL, LARGE
    val chatDensity: String = "DEFAULT", // COMPACT, DEFAULT, RELAXED
    val lastSeenPrivacy: String = "NOBODY", // EVERYONE, CONTACTS, NOBODY
    val readReceiptsEnabled: Boolean = true,
    val typingIndicatorEnabled: Boolean = true,
    val biometricLockEnabled: Boolean = false,
    val lockTimeoutMinutes: Int = 1, // 0=immediate, 1, 5, 15
    val twoStepVerificationEnabled: Boolean = false,
    val notificationPreviews: Boolean = true,
    val disappearingDefault: String = "OFF", // OFF, 24H, 7D, 90D
    val e2eVerificationPromptShown: Boolean = true
)
