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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thoodhu.core.design.ThoodhuLogo
import com.example.thoodhu.data.repository.AuthRepository
import com.example.ui.theme.EagleGold
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SkyPrimary
import kotlinx.coroutines.launch

data class CountryCode(val country: String, val code: String, val flag: String)

@Composable
fun PhoneAuthScreen(
    authRepository: AuthRepository,
    onNavigateBack: () -> Unit,
    onOtpRequested: (fullPhone: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(CountryCode("India", "+91", "🇮🇳")) }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val countries = remember {
        listOf(
            CountryCode("India", "+91", "🇮🇳"),
            CountryCode("United States", "+1", "🇺🇸"),
            CountryCode("United Kingdom", "+44", "🇬🇧"),
            CountryCode("United Arab Emirates", "+971", "🇦🇪"),
            CountryCode("Singapore", "+65", "🇸🇬"),
            CountryCode("Germany", "+49", "🇩🇪"),
            CountryCode("Australia", "+61", "🇦🇺")
        )
    }

    val submitPhone = {
        val cleanPhone = phoneNumber.filter { it.isDigit() }
        if (cleanPhone.length >= 7) {
            isLoading = true
            errorMessage = null
            val fullPhone = "${selectedCountry.code}$cleanPhone"
            coroutineScope.launch {
                val result = authRepository.requestOtp(fullPhone)
                isLoading = false
                if (result.isSuccess) {
                    onOtpRequested(fullPhone)
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to send OTP"
                }
            }
        } else {
            errorMessage = "Please enter a valid phone number"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("phone_auth_screen")
            .background(MidnightNavy)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("phone_auth_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Phone Verification",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome to THOODHU",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter your mobile number to receive a secure one-time verification code.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Country selector dropdown
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { countryMenuExpanded = true }
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedCountry.flag}  ${selectedCountry.country} (${selectedCountry.code})",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(text = "▼", fontSize = 12.sp, color = SkyPrimary)
                        }
                    }

                    DropdownMenu(
                        expanded = countryMenuExpanded,
                        onDismissRequest = { countryMenuExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        countries.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${item.flag}  ${item.country} (${item.code})",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    selectedCountry = item
                                    countryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Phone input
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 15) phoneNumber = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_number_input"),
                    placeholder = { Text("Mobile number", color = Color(0xFF64748B)) },
                    leadingIcon = {
                        Text(
                            text = "${selectedCountry.code} ",
                            fontWeight = FontWeight.Bold,
                            color = SkyPrimary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    keyboardActions = KeyboardActions(onDone = { submitPhone() }),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyPrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFF87171),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag("phone_auth_error")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Privacy guarantee banner
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Zero Phone Leakage",
                            tint = EagleGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Zero Phone Exposure",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = EagleGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your phone number is only used to verify your device. You can chat freely under your @username without disclosing your number.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            // Bottom Continue Button
            Button(
                onClick = { submitPhone() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("send_otp_button"),
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
                        text = "Send Secure Code",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
