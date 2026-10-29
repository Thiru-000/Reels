package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ProfileEntity
import com.example.ui.theme.InstaBlue
import com.example.ui.theme.InstaBorder
import com.example.ui.theme.InstaDivider
import com.example.ui.theme.InstaGreen
import com.example.ui.theme.InstaRed
import com.example.ui.theme.InstaSecondaryBg
import com.example.ui.theme.InstaTextLight
import com.example.ui.theme.InstaTextPrimary
import com.example.ui.theme.InstaTextSecondary
import com.example.ui.theme.InstaWhite
import com.example.ui.theme.InstagramButtonGradient
import com.example.ui.theme.InstagramStoryGradient

@Composable
fun TopHeaderBar(
    activeProfile: ProfileEntity?,
    allProfiles: List<ProfileEntity>,
    onSwitchProfile: (String) -> Unit,
    onOpenProfilesTab: () -> Unit,
    onToggleAutoPilot: (Boolean) -> Unit,
    onOpenSuggestionsTab: () -> Unit,
    unreadSuggestionsCount: Int,
    isSyncing: Boolean = false,
    onSyncAccount: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var profileMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = InstaWhite,
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Instagram Wordmark & Profile Dropdown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .testTag("app_branding_logo")
                ) {
                    Text(
                        text = "Instagram",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        color = InstaTextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Account Switcher Dropdown Chip
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { profileMenuExpanded = true }
                                .padding(horizontal = 3.dp, vertical = 2.dp)
                                .testTag("profile_switcher_chip")
                        ) {
                            Text(
                                text = activeProfile?.username?.let { "@$it" } ?: "Autopilot",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = InstaTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 110.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Switch profile",
                                tint = InstaTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = profileMenuExpanded,
                            onDismissRequest = { profileMenuExpanded = false },
                            modifier = Modifier
                                .background(InstaWhite)
                                .border(1.dp, InstaBorder, RoundedCornerShape(8.dp))
                        ) {
                            allProfiles.forEach { profile ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (profile.isCurrentActive) InstagramStoryGradient
                                                        else SolidColor(InstaBorder)
                                                    )
                                                    .padding(1.5.dp)
                                            ) {
                                                AsyncImage(
                                                    model = profile.avatarUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(25.dp)
                                                        .clip(CircleShape)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = profile.username,
                                                        color = InstaTextPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    if (profile.isCurrentActive) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Active",
                                                            tint = InstaBlue,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "${formatNumber(profile.followersCount)} followers",
                                                    color = InstaTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onSwitchProfile(profile.id)
                                        profileMenuExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider(color = InstaDivider)
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "+ Add or Switch Account",
                                        color = InstaBlue,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    profileMenuExpanded = false
                                    onOpenProfilesTab()
                                }
                            )
                        }
                    }
                }

                // Right Icons: Autopilot Status Pill & Notification Heart
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Autopilot Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (activeProfile?.isAutoPilotEnabled == true) Color(0xFFF0FDF4) else InstaSecondaryBg)
                            .border(
                                1.dp,
                                if (activeProfile?.isAutoPilotEnabled == true) InstaGreen.copy(alpha = 0.5f) else InstaBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onToggleAutoPilot(!(activeProfile?.isAutoPilotEnabled ?: false)) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Autopilot",
                            tint = if (activeProfile?.isAutoPilotEnabled == true) InstaGreen else InstaTextLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (activeProfile?.isAutoPilotEnabled == true) "Autopilot ON" else "Autopilot OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeProfile?.isAutoPilotEnabled == true) InstaGreen else InstaTextSecondary
                        )
                    }

                    // Sync icon button
                    IconButton(
                        onClick = onSyncAccount,
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = InstaBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Instagram Account",
                                tint = InstaTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Activity / Suggestions Heart
                    IconButton(
                        onClick = onOpenSuggestionsTab,
                        modifier = Modifier.size(36.dp)
                    ) {
                        if (unreadSuggestionsCount > 0) {
                            BadgedBox(badge = {
                                Badge(
                                    containerColor = InstaRed,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadSuggestionsCount", fontSize = 9.sp)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Activity",
                                    tint = InstaTextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Activity",
                                tint = InstaTextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 0.75.dp, color = InstaBorder)
        }
    }
}

fun formatNumber(num: Int): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000f)
        num >= 1_000 -> String.format("%.1fK", num / 1_000f)
        else -> num.toString()
    }
}
