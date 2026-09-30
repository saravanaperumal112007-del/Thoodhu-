package com.example.thoodhu.data.repository

import com.example.thoodhu.data.local.dao.SettingsDao
import com.example.thoodhu.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val settingsDao: SettingsDao
) {
    val settings: Flow<UserSettingsEntity> = settingsDao.getSettings().map {
        it ?: UserSettingsEntity(
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
    }

    suspend fun updateTheme(mode: String) {
        val current = settingsDao.getSettingsSnapshot() ?: UserSettingsEntity()
        settingsDao.insertOrUpdateSettings(current.copy(themeMode = mode))
    }

    suspend fun updatePrivacy(
        lastSeen: String? = null,
        readReceipts: Boolean? = null,
        typingIndicators: Boolean? = null,
        disappearingDefault: String? = null
    ) {
        val current = settingsDao.getSettingsSnapshot() ?: UserSettingsEntity()
        val updated = current.copy(
            lastSeenPrivacy = lastSeen ?: current.lastSeenPrivacy,
            readReceiptsEnabled = readReceipts ?: current.readReceiptsEnabled,
            typingIndicatorEnabled = typingIndicators ?: current.typingIndicatorEnabled,
            disappearingDefault = disappearingDefault ?: current.disappearingDefault
        )
        settingsDao.insertOrUpdateSettings(updated)
    }

    suspend fun updateSecurity(
        biometricLock: Boolean? = null,
        lockTimeout: Int? = null,
        twoStep: Boolean? = null
    ) {
        val current = settingsDao.getSettingsSnapshot() ?: UserSettingsEntity()
        val updated = current.copy(
            biometricLockEnabled = biometricLock ?: current.biometricLockEnabled,
            lockTimeoutMinutes = lockTimeout ?: current.lockTimeoutMinutes,
            twoStepVerificationEnabled = twoStep ?: current.twoStepVerificationEnabled
        )
        settingsDao.insertOrUpdateSettings(updated)
    }
}
