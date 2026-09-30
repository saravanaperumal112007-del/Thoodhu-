package com.example.thoodhu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thoodhu.data.repository.AuthRepository
import com.example.thoodhu.data.repository.MessagingRepository
import com.example.thoodhu.data.repository.SettingsRepository
import com.example.thoodhu.ui.screens.auth.OtpVerifyScreen
import com.example.thoodhu.ui.screens.auth.PhoneAuthScreen
import com.example.thoodhu.ui.screens.auth.ProfileSetupScreen
import com.example.thoodhu.ui.screens.chat.ChatDetailPreviewScreen
import com.example.thoodhu.ui.screens.main.MainDashboardScreen
import com.example.thoodhu.ui.screens.onboarding.OnboardingScreen
import com.example.thoodhu.ui.screens.settings.PrivacyCenterScreen
import com.example.thoodhu.ui.screens.settings.SecuritySettingsScreen
import com.example.thoodhu.ui.screens.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun ThoodhuNavGraph(
    navController: NavHostController,
    authRepository: AuthRepository,
    messagingRepository: MessagingRepository,
    settingsRepository: SettingsRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val myProfile by authRepository.myProfile.collectAsStateWithLifecycle(initialValue = null)
    val isLoggedIn = myProfile != null

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isLoggedIn = isLoggedIn,
                onNavigateNext = { destinationIsDashboard ->
                    if (destinationIsDashboard) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onAgreeAndContinue = {
                    navController.navigate(Screen.PhoneAuth.route)
                }
            )
        }

        composable(Screen.PhoneAuth.route) {
            PhoneAuthScreen(
                authRepository = authRepository,
                onNavigateBack = { navController.popBackStack() },
                onOtpRequested = { fullPhone ->
                    navController.navigate(Screen.OtpVerify.createRoute(fullPhone))
                }
            )
        }

        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            OtpVerifyScreen(
                phoneNumber = phoneNumber,
                authRepository = authRepository,
                onNavigateBack = { navController.popBackStack() },
                onOtpVerified = {
                    navController.navigate(Screen.ProfileSetup.createRoute(phoneNumber))
                }
            )
        }

        composable(
            route = Screen.ProfileSetup.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            ProfileSetupScreen(
                phoneNumber = phoneNumber,
                authRepository = authRepository,
                onProfileCompleted = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            MainDashboardScreen(
                authRepository = authRepository,
                messagingRepository = messagingRepository,
                settingsRepository = settingsRepository,
                onOpenConversation = { conversationId ->
                    navController.navigate(Screen.ChatDetail.createRoute(conversationId))
                },
                onOpenPrivacyCenter = {
                    navController.navigate(Screen.PrivacyCenter.route)
                },
                onOpenSecuritySettings = {
                    navController.navigate(Screen.SecuritySettings.route)
                },
                onSignOut = {
                    coroutineScope.launch {
                        authRepository.signOut()
                    }
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatDetailPreviewScreen(
                conversationId = conversationId,
                authRepository = authRepository,
                messagingRepository = messagingRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PrivacyCenter.route) {
            PrivacyCenterScreen(
                settingsRepository = settingsRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SecuritySettings.route) {
            SecuritySettingsScreen(
                settingsRepository = settingsRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
