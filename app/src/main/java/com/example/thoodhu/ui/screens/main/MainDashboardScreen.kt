package com.example.thoodhu.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.thoodhu.core.design.MessageDeliveryState
import com.example.thoodhu.core.design.MessageStatusIndicator
import com.example.thoodhu.data.local.entity.ConversationEntity
import com.example.thoodhu.data.local.entity.UserEntity
import com.example.thoodhu.data.repository.AuthRepository
import com.example.thoodhu.data.repository.MessagingRepository
import com.example.thoodhu.data.repository.SettingsRepository
import com.example.ui.theme.EagleGold
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimaryDark

enum class MainTab(val label: String, val icon: ImageVector) {
    CHATS("Chats", Icons.AutoMirrored.Outlined.Chat),
    UPDATES("Updates", Icons.Outlined.DynamicFeed),
    COMMUNITIES("Communities", Icons.Outlined.Groups),
    CALLS("Calls", Icons.Outlined.Call),
    SETTINGS("Settings", Icons.Outlined.Settings)
}

@Composable
fun MainDashboardScreen(
    authRepository: AuthRepository,
    messagingRepository: MessagingRepository,
    settingsRepository: SettingsRepository,
    onOpenConversation: (conversationId: String) -> Unit,
    onOpenPrivacyCenter: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.CHATS) }
    val myProfile by authRepository.myProfile.collectAsStateWithLifecycle(initialValue = null)
    val conversations by messagingRepository.conversations.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = null)

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedFilterChip by remember { mutableStateOf("All") }

    val filterChips = listOf("All", "Unread", "Direct", "Groups", "Channels")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_dashboard_screen"),
        containerColor = MidnightNavy,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = R.drawable.ic_thoodhu_emblem),
                                    contentDescription = "THOODHU",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "THOODHU",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SkyPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "E2EE",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SkyPrimaryDark
                                    )
                                }
                            }
                            Text(
                                text = myProfile?.username ?: "@sovereign",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isSearching = !isSearching },
                            modifier = Modifier.testTag("dashboard_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Expandable Search Bar
                AnimatedVisibility(visible = isSearching) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search chats, usernames, messages...", color = Color(0xFF64748B)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_search_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyPrimary,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Filter Chips (Only for CHATS tab)
                if (selectedTab == MainTab.CHATS) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterChips) { chip ->
                            val isSelected = selectedFilterChip == chip
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) SkyPrimary else Color(0xFF1E293B),
                                modifier = Modifier
                                    .clickable { selectedFilterChip = chip }
                                    .testTag("filter_chip_$chip")
                            ) {
                                Text(
                                    text = chip,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            if (tab == MainTab.CHATS) {
                                val totalUnread = conversations.sumOf { it.unreadCount }
                                if (totalUnread > 0) {
                                    BadgedBox(badge = { Badge { Text(totalUnread.toString()) } }) {
                                        Icon(tab.icon, contentDescription = tab.label)
                                    }
                                } else {
                                    Icon(tab.icon, contentDescription = tab.label)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.label)
                            }
                        },
                        label = { Text(tab.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SkyPrimaryDark,
                            selectedTextColor = SkyPrimaryDark,
                            indicatorColor = SkyPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == MainTab.CHATS) {
                FloatingActionButton(
                    onClick = {
                        // In Phase 1, opens the guide conversation or creates a message
                        val firstConv = conversations.firstOrNull()?.id ?: "conv-thoodhu-team"
                        onOpenConversation(firstConv)
                    },
                    containerColor = SkyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_new_chat")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "New sovereign chat"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.CHATS -> {
                    val filteredConversations = remember(conversations, selectedFilterChip, searchQuery) {
                        conversations.filter { conv ->
                            val matchesFilter = when (selectedFilterChip) {
                                "Unread" -> conv.unreadCount > 0
                                "Direct" -> conv.type == "DIRECT"
                                "Groups" -> conv.type == "GROUP"
                                "Channels" -> conv.type == "CHANNEL"
                                else -> true
                            }
                            val matchesSearch = if (searchQuery.isBlank()) true else {
                                conv.title.contains(searchQuery, ignoreCase = true) ||
                                        conv.lastMessageText.contains(searchQuery, ignoreCase = true)
                            }
                            matchesFilter && matchesSearch
                        }
                    }

                    if (filteredConversations.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                                    contentDescription = "No chats",
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No conversations found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredConversations, key = { it.id }) { item ->
                                ConversationListItem(
                                    conversation = item,
                                    onClick = { onOpenConversation(item.id) }
                                )
                            }
                        }
                    }
                }

                MainTab.UPDATES -> {
                    UpdatesTabPlaceholder(onOpenSecuritySettings)
                }

                MainTab.COMMUNITIES -> {
                    CommunitiesTabPlaceholder()
                }

                MainTab.CALLS -> {
                    CallsTabPlaceholder()
                }

                MainTab.SETTINGS -> {
                    SettingsTabContent(
                        myProfile = myProfile,
                        onOpenPrivacyCenter = onOpenPrivacyCenter,
                        onOpenSecuritySettings = onOpenSecuritySettings,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationListItem(
    conversation: ConversationEntity,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("conversation_item_${conversation.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Orb with type badge
            Box {
                Surface(
                    shape = CircleShape,
                    color = when (conversation.type) {
                        "CHANNEL" -> Color(0xFF0284C7).copy(alpha = 0.2f)
                        "GROUP" -> Color(0xFF10B981).copy(alpha = 0.2f)
                        else -> Color(0xFF1E293B)
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (conversation.type) {
                                "CHANNEL" -> "📢"
                                "GROUP" -> "👥"
                                else -> "🦅"
                            },
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "12:45 PM",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (conversation.unreadCount > 0) SkyPrimary else Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Show THOODHU original delivery status indicator
                    val deliveryState = when (conversation.lastMessageStatus) {
                        "PENDING" -> MessageDeliveryState.PENDING
                        "SENDING" -> MessageDeliveryState.SENDING
                        "SENT" -> MessageDeliveryState.SENT
                        "DELIVERED" -> MessageDeliveryState.DELIVERED
                        "READ" -> MessageDeliveryState.READ
                        else -> MessageDeliveryState.READ
                    }
                    MessageStatusIndicator(
                        state = deliveryState,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = conversation.lastMessageText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = "Pinned",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = SkyPrimary,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = conversation.unreadCount.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpdatesTabPlaceholder(onOpenSecuritySettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E293B),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.DynamicFeed,
                    contentDescription = "Updates",
                    tint = SkyPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Status & Channels",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Post 24-hour self-destructing status updates and subscribe to broadcast channels in Phase 4.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun CommunitiesTabPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E293B),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = "Communities",
                    tint = EagleGold,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sovereign Communities",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Organize topic-based group spaces under unified sovereign communities coming in Phase 4.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun CallsTabPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E293B),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.VideoCall,
                    contentDescription = "Encrypted Calls",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Encrypted Voice & Video",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Peer-to-peer WebRTC signaling and crystal clear audio architecture prepared for Phase 5.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun SettingsTabContent(
    myProfile: UserEntity?,
    onOpenPrivacyCenter: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_tab_content")
    ) {
        // User profile card
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(2.dp, SkyPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🦅", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = myProfile?.displayName ?: "User",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = myProfile?.username ?: "@sovereign",
                            style = MaterialTheme.typography.bodySmall,
                            color = SkyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = myProfile?.bio ?: "Flying swift with THOODHU",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode,
                            contentDescription = "Profile QR",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Settings items
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SettingsCategoryHeader(title = "Security & Privacy")

                SettingsRowItem(
                    icon = Icons.Outlined.Shield,
                    iconTint = EagleGold,
                    title = "Privacy Center",
                    subtitle = "Last seen, read receipts, disappearing messages",
                    onClick = onOpenPrivacyCenter,
                    testTag = "settings_privacy_item"
                )

                SettingsRowItem(
                    icon = Icons.Outlined.Lock,
                    iconTint = SkyPrimary,
                    title = "Account Security & App Lock",
                    subtitle = "Biometrics, two-step verification, device keys",
                    onClick = onOpenSecuritySettings,
                    testTag = "settings_security_item"
                )

                SettingsCategoryHeader(title = "App Customization")

                SettingsRowItem(
                    icon = Icons.Outlined.Notifications,
                    iconTint = Color(0xFF38BDF8),
                    title = "Notifications & Sounds",
                    subtitle = "Eagle alerts, call ringtones, message previews",
                    onClick = {}
                )

                SettingsRowItem(
                    icon = Icons.Outlined.CloudSync,
                    iconTint = Color(0xFF10B981),
                    title = "Data & Storage Management",
                    subtitle = "Encrypted local database, auto-download",
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSignOut)
                        .testTag("sign_out_button")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sign Out & Clear Local Keys",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
        ),
        color = Color(0xFF64748B),
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
