package com.example.thoodhu.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thoodhu.data.repository.AuthRepository
import com.example.ui.theme.EagleGold
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SkyPrimary
import kotlinx.coroutines.launch

@Composable
fun ProfileSetupScreen(
    phoneNumber: String,
    authRepository: AuthRepository,
    onProfileCompleted: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("arun_eagle") }
    var displayName by remember { mutableStateOf("Arun") }
    var bio by remember { mutableStateOf("Flying swift with THOODHU 🦅") }
    var selectedAvatarIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val avatarEmojis = listOf("🦅", "⚡", "🛡️", "🌌", "🕊️")

    val submitProfile = {
        val cleanUser = username.trim().removePrefix("@")
        if (cleanUser.length >= 3) {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
                val res = authRepository.completeProfile(
                    phoneNumber = phoneNumber,
                    username = "@$cleanUser",
                    displayName = displayName.trim(),
                    bio = bio.trim()
                )
                isLoading = false
                if (res.isSuccess) {
                    onProfileCompleted()
                } else {
                    errorMessage = res.exceptionOrNull()?.message ?: "Profile setup failed"
                }
            }
        } else {
            errorMessage = "Username must be at least 3 characters"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_setup_screen")
            .background(MidnightNavy)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Create Your Identity",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Set up your public persona. Others can find and message you using your unique @username.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Avatar Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(2.dp, SkyPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarEmojis[selectedAvatarIndex],
                            fontSize = 42.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Choose Emblem",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        avatarEmojis.forEachIndexed { idx, emoji ->
                            val isSelected = idx == selectedAvatarIndex
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SkyPrimary.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) SkyPrimary else Color(0xFF334155),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatarIndex = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Username field
                Text(
                    text = "Unique Username",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' }
                        if (filtered.length <= 20) username = filtered
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AlternateEmail,
                            contentDescription = "Username",
                            tint = SkyPrimary
                        )
                    },
                    trailingIcon = {
                        if (username.length >= 3) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Available",
                                tint = Color(0xFF10B981)
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name field
                Text(
                    text = "Display Name",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { if (it.length <= 30) displayName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("display_name_input"),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Display Name",
                            tint = SkyPrimary
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // About / Bio field
                Text(
                    text = "About / Status",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 100) bio = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bio_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFF87171),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Security Note
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = "Keystore Key",
                            tint = EagleGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "An Ed25519 identity keypair will be sealed inside your device's Android Keystore.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { submitProfile() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("complete_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SkyPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Enter THOODHU",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
