package com.example.thoodhu.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thoodhu.core.design.MessageDeliveryState
import com.example.thoodhu.core.design.MessageStatusIndicator
import com.example.thoodhu.data.local.entity.MessageEntity
import com.example.thoodhu.data.repository.AuthRepository
import com.example.thoodhu.data.repository.MessagingRepository
import com.example.ui.theme.EagleGold
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimaryDark
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailPreviewScreen(
    conversationId: String,
    authRepository: AuthRepository,
    messagingRepository: MessagingRepository,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val myProfile by authRepository.myProfile.collectAsStateWithLifecycle(initialValue = null)
    val conversation by messagingRepository.getConversation(conversationId).collectAsStateWithLifecycle(initialValue = null)
    val messages by messagingRepository.getMessages(conversationId).collectAsStateWithLifecycle(initialValue = emptyList())
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val sendMessage = {
        if (inputText.isNotBlank()) {
            val textToSend = inputText.trim()
            inputText = ""
            coroutineScope.launch {
                messagingRepository.sendMessage(
                    conversationId = conversationId,
                    senderId = myProfile?.id ?: "me",
                    senderName = myProfile?.displayName ?: "Me",
                    text = textToSend
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chat_detail_screen"),
        containerColor = MidnightNavy,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when (conversation?.type) {
                                        "CHANNEL" -> "📢"
                                        "GROUP" -> "👥"
                                        else -> "🦅"
                                    },
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conversation?.title ?: "Chat",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = "Encrypted",
                                    tint = EagleGold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = "End-to-end encrypted",
                                style = MaterialTheme.typography.labelSmall,
                                color = SkyPrimaryDark
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("chat_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Options", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Pinned Message banner
            Surface(
                color = Color(0xFF1E293B).copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = "Pinned",
                        tint = EagleGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pinned: Communications secured by Android Keystore hardware sandbox.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFFCBD5E1),
                        maxLines = 1
                    )
                }
            }

            // Messages scroll area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageBubble(message = msg)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Input Bar
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.testTag("chat_attach_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            contentDescription = "Attach file",
                            tint = Color(0xFF94A3B8)
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input"),
                        placeholder = { Text("Flight message...", color = Color(0xFF64748B), fontSize = 14.sp) },
                        singleLine = false,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkyPrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = { sendMessage() },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SkyPrimary)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Camera",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice note",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: MessageEntity) {
    val isMe = message.isOutgoing
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    val deliveryState = when (message.status) {
        "PENDING" -> MessageDeliveryState.PENDING
        "SENDING" -> MessageDeliveryState.SENDING
        "SENT" -> MessageDeliveryState.SENT
        "DELIVERED" -> MessageDeliveryState.DELIVERED
        "READ" -> MessageDeliveryState.READ
        else -> MessageDeliveryState.READ
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("message_bubble_${message.id}"),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) SkyPrimary else Color(0xFF1E293B),
            border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (!isMe && message.senderName.isNotBlank()) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SkyPrimaryDark,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.reactionMetadata != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF334155)),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = message.reactionMetadata ?: "",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isMe) Color(0xFFE0F2FE) else Color(0xFF94A3B8)
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        MessageStatusIndicator(
                            state = deliveryState,
                            size = 13.dp,
                            tint = if (deliveryState == MessageDeliveryState.READ) Color.White else null
                        )
                    }
                }
            }
        }
    }
}
