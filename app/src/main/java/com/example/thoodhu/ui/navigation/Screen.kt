package com.example.thoodhu.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object PhoneAuth : Screen("auth_phone")
    data object OtpVerify : Screen("auth_otp/{phoneNumber}") {
        fun createRoute(phoneNumber: String): String = "auth_otp/${phoneNumber}"
    }
    data object ProfileSetup : Screen("auth_profile/{phoneNumber}") {
        fun createRoute(phoneNumber: String): String = "auth_profile/${phoneNumber}"
    }
    data object Dashboard : Screen("dashboard")
    data object ChatDetail : Screen("chat_detail/{conversationId}") {
        fun createRoute(conversationId: String): String = "chat_detail/${conversationId}"
    }
    data object PrivacyCenter : Screen("settings_privacy")
    data object SecuritySettings : Screen("settings_security")
}
