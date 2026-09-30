package com.example.thoodhu.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thoodhu.data.repository.AuthRepository
import com.example.ui.theme.EagleGold
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SkyPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpVerifyScreen(
    phoneNumber: String,
    authRepository: AuthRepository,
    onNavigateBack: () -> Unit,
    onOtpVerified: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendCooldown by remember { mutableIntStateOf(45) }

    LaunchedEffect(Unit) {
        while (resendCooldown > 0) {
            delay(1000)
            resendCooldown--
        }
    }

    val verifyCode = {
        if (otpCode.length == 6) {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
                val res = authRepository.verifyOtp(otpCode)
                isLoading = false
                if (res.isSuccess) {
                    onOtpVerified()
                } else {
                    errorMessage = res.exceptionOrNull()?.message ?: "Verification failed"
                }
            }
        } else {
            errorMessage = "Please enter all 6 digits"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("otp_verify_screen")
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
                        modifier = Modifier.testTag("otp_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verify Code",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Enter 6-Digit Code",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A secure verification code was sent to $phoneNumber",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                            otpCode = it
                            if (it.length == 6) {
                                verifyCode()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_input"),
                    placeholder = { Text("• • • • • •", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 12.sp,
                        color = SkyPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    keyboardActions = KeyboardActions(onDone = { verifyCode() }),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFF87171),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag("otp_error_text")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fast demo fill chip
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Test Sandbox Code: 778899",
                            style = MaterialTheme.typography.bodySmall,
                            color = EagleGold
                        )
                        OutlinedButton(
                            onClick = {
                                otpCode = "778899"
                                verifyCode()
                            },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("autofill_otp_button"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Fill", fontSize = 11.sp, color = SkyPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (resendCooldown > 0) {
                        Text(
                            text = "Resend code in ${resendCooldown}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    } else {
                        TextButton(
                            onClick = {
                                resendCooldown = 45
                                coroutineScope.launch { authRepository.requestOtp(phoneNumber) }
                            },
                            modifier = Modifier.testTag("resend_otp_button")
                        ) {
                            Text("Resend SMS Code", color = SkyPrimary)
                        }
                    }
                }
            }

            Button(
                onClick = { verifyCode() },
                enabled = !isLoading && otpCode.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("verify_otp_button"),
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
                        text = "Verify & Take Flight",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
