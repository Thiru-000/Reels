package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ProfileEntity
import com.example.ui.components.formatNumber
import com.example.ui.theme.InstaBlue
import com.example.ui.theme.InstaBorder
import com.example.ui.theme.InstaDivider
import com.example.ui.theme.InstaGreen
import com.example.ui.theme.InstaSecondaryBg
import com.example.ui.theme.InstaTextLight
import com.example.ui.theme.InstaTextPrimary
import com.example.ui.theme.InstaTextSecondary
import com.example.ui.theme.InstaWhite
import com.example.ui.theme.InstagramStoryGradient

@Composable
fun ProfilesScreen(
    activeProfile: ProfileEntity?,
    allProfiles: List<ProfileEntity>,
    onSwitchProfile: (String) -> Unit,
    onConnectNewOAuth: (username: String, fullName: String, category: String) -> Unit,
    onToggleAutoPilot: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onSyncProfile: () -> Unit,
    isSyncing: Boolean,
    onUpdateCustomStats: (followers: Int, following: Int, posts: Int, fullName: String, category: String) -> Unit,
    onRemoveProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOAuthDialog by remember { mutableStateOf(false) }
    var showEditStatsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(InstaWhite)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Instagram Profile Section (Exact Instagram Profile Layout!)
        if (activeProfile != null) {
            item {
                InstagramProfileHeader(
                    profile = activeProfile,
                    onToggleAutoPilot = onToggleAutoPilot
                )
            }

            // Real-Time Instagram Sync Card & Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = InstaSecondaryBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(if (activeProfile.followersCount > 0) InstaGreen else Color(0xFFF59E0B))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (activeProfile.followersCount > 0) "Synced with Instagram" else "Account Sync Pending",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InstaTextPrimary
                                )
                            }
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = InstaBlue,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Meta Graph API 2.0",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InstaGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSyncProfile,
                                enabled = !isSyncing,
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(38.dp)
                                    .testTag("sync_instagram_profile_button"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = InstaBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSyncing) "Syncing..." else "Sync Instagram",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { showEditStatsDialog = true },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(38.dp)
                                    .testTag("edit_profile_stats_button"),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = InstaTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verify / Set Stats",
                                    fontSize = 11.sp,
                                    color = InstaTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Row (Edit Profile / Connect New)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showOAuthDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("connect_new_profile_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Connect Account (OAuth)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { onToggleAutoPilot(!(activeProfile?.isAutoPilotEnabled ?: false)) },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (activeProfile?.isAutoPilotEnabled == true) InstaGreen else InstaTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (activeProfile?.isAutoPilotEnabled == true) "Autopilot ON" else "Autopilot OFF",
                        fontSize = 11.sp,
                        color = InstaTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Meta Accounts Center Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InstaWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Meta Accounts Center",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = InstaTextPrimary
                            )
                            Text(
                                text = "Manage multiple connected profiles simultaneously",
                                fontSize = 11.sp,
                                color = InstaTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF0FDF4))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OAUTH 2.0",
                                color = InstaGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = InstaDivider)
                    Spacer(modifier = Modifier.height(10.dp))

                    allProfiles.forEach { profile ->
                        val isCurrent = profile.id == activeProfile?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSwitchProfile(profile.id) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCurrent) InstagramStoryGradient
                                            else SolidColor(InstaBorder)
                                        )
                                        .padding(1.5.dp)
                                ) {
                                    AsyncImage(
                                        model = profile.avatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.username,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = InstaTextPrimary
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Active",
                                                tint = InstaBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${formatNumber(profile.followersCount)} followers • ${profile.category}",
                                        fontSize = 11.sp,
                                        color = InstaTextSecondary
                                    )
                                }
                            }

                            if (!isCurrent) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Switch",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InstaBlue,
                                        modifier = Modifier
                                            .clickable { onSwitchProfile(profile.id) }
                                            .padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = { onRemoveProfile(profile.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove duplicate account",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Meta OAuth Security Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InstaSecondaryBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = InstaGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Encrypted Meta OAuth tokens are stored locally on your device. Auto-publishing uses official Instagram Graph API protocols.",
                        fontSize = 11.sp,
                        color = InstaTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Log Out Button
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2)
                )
            ) {
                Text(
                    text = "Log Out of ${activeProfile?.username ?: "Account"}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }

    // Instagram OAuth Dialog
    if (showOAuthDialog) {
        InstagramOAuthConnectDialog(
            onDismiss = { showOAuthDialog = false },
            onConnect = { username, fullName, category ->
                onConnectNewOAuth(username, fullName, category)
                showOAuthDialog = false
            }
        )
    }

    // Edit Stats & Verify Dialog
    if (showEditStatsDialog && activeProfile != null) {
        EditAccountStatsDialog(
            profile = activeProfile,
            onDismiss = { showEditStatsDialog = false },
            onSave = { followers, following, posts, fullName, category ->
                onUpdateCustomStats(followers, following, posts, fullName, category)
                showEditStatsDialog = false
            }
        )
    }
}

@Composable
fun InstagramProfileHeader(
    profile: ProfileEntity,
    onToggleAutoPilot: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Big avatar with story gradient ring
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(InstagramStoryGradient)
                    .padding(2.5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(InstaWhite)
                        .padding(2.dp)
                ) {
                    AsyncImage(
                        model = profile.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            // Stats row (Posts, Followers, Following)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem("${profile.totalReels}", "Posts")
                ProfileStatItem(formatNumber(profile.followersCount), "Followers")
                ProfileStatItem("${profile.followingCount}", "Following")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Name, category, bio
        Text(
            text = profile.fullName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = InstaTextPrimary
        )
        Text(
            text = profile.category,
            fontSize = 12.sp,
            color = InstaTextSecondary
        )
        Text(
            text = "🎬 Automatic Reels Publisher & Content Lab\n⚡ Peak engagement auto-scheduler powered by AI",
            fontSize = 12.sp,
            color = InstaTextPrimary,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = InstaTextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = InstaTextPrimary
        )
    }
}

@Composable
fun InstagramOAuthConnectDialog(
    onDismiss: () -> Unit,
    onConnect: (username: String, fullName: String, category: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tech & Lifestyle") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Meta / Instagram",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaBlue
                )
                Text(
                    text = "Connect Instagram Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Authorize Insta autopilot to manage your professional account and automatically publish Reels at peak times.",
                    fontSize = 12.sp,
                    color = InstaTextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Instagram Handle (e.g. tech_flow)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstaBlue,
                        unfocusedBorderColor = InstaBorder,
                        focusedTextColor = InstaTextPrimary,
                        unfocusedTextColor = InstaTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Account Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstaBlue,
                        unfocusedBorderColor = InstaBorder,
                        focusedTextColor = InstaTextPrimary,
                        unfocusedTextColor = InstaTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Tech, Lifestyle, Travel, etc.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstaBlue,
                        unfocusedBorderColor = InstaBorder,
                        focusedTextColor = InstaTextPrimary,
                        unfocusedTextColor = InstaTextPrimary
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isBlank()) {
                        errorMessage = "Please enter an Instagram handle"
                    } else {
                        onConnect(
                            username.trim(),
                            fullName.ifBlank { username.trim() },
                            category.ifBlank { "Creator" }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = InstaBlue)
            ) {
                Text("Authorize with Meta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = InstaTextSecondary)
            }
        },
        containerColor = InstaWhite
    )
}

@Composable
fun EditAccountStatsDialog(
    profile: ProfileEntity,
    onDismiss: () -> Unit,
    onSave: (followers: Int, following: Int, posts: Int, fullName: String, category: String) -> Unit
) {
    var followersText by remember { mutableStateOf(if (profile.followersCount > 0) profile.followersCount.toString() else "") }
    var followingText by remember { mutableStateOf(if (profile.followingCount > 0) profile.followingCount.toString() else "") }
    var postsText by remember { mutableStateOf(if (profile.totalReels > 0) profile.totalReels.toString() else "") }
    var fullNameText by remember { mutableStateOf(profile.fullName) }
    var categoryText by remember { mutableStateOf(profile.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "@${profile.username}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaBlue
                )
                Text(
                    text = "Verify / Set Instagram Stats",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Set your live follower count, reels count, and account details to align with your Instagram page.",
                    fontSize = 12.sp,
                    color = InstaTextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = followersText,
                    onValueChange = { followersText = it.filter { char -> char.isDigit() } },
                    label = { Text("Followers Count (e.g. 15400)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("edit_followers_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstaBlue,
                        unfocusedBorderColor = InstaBorder,
                        focusedTextColor = InstaTextPrimary,
                        unfocusedTextColor = InstaTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = postsText,
                        onValueChange = { postsText = it.filter { char -> char.isDigit() } },
                        label = { Text("Total Posts / Reels") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("edit_posts_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InstaBlue,
                            unfocusedBorderColor = InstaBorder,
                            focusedTextColor = InstaTextPrimary,
                            unfocusedTextColor = InstaTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = followingText,
                        onValueChange = { followingText = it.filter { char -> char.isDigit() } },
                        label = { Text("Following") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("edit_following_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InstaBlue,
                            unfocusedBorderColor = InstaBorder,
                            focusedTextColor = InstaTextPrimary,
                            unfocusedTextColor = InstaTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullNameText,
                    onValueChange = { fullNameText = it },
                    label = { Text("Full Name / Creator Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstaBlue,
                        unfocusedBorderColor = InstaBorder,
                        focusedTextColor = InstaTextPrimary,
                        unfocusedTextColor = InstaTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = { Text("Category (e.g. Creator / Video)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstaBlue,
                        unfocusedBorderColor = InstaBorder,
                        focusedTextColor = InstaTextPrimary,
                        unfocusedTextColor = InstaTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val followers = followersText.toIntOrNull() ?: profile.followersCount
                    val following = followingText.toIntOrNull() ?: profile.followingCount
                    val posts = postsText.toIntOrNull() ?: profile.totalReels
                    onSave(
                        followers,
                        following,
                        posts,
                        fullNameText.ifBlank { profile.fullName },
                        categoryText.ifBlank { profile.category }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                modifier = Modifier.testTag("save_profile_stats_button")
            ) {
                Text("Save & Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = InstaTextSecondary)
            }
        },
        containerColor = InstaWhite
    )
}

