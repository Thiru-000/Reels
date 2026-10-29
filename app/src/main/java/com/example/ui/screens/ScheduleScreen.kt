package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OptimalSlotEntity
import com.example.data.local.ProfileEntity
import com.example.data.local.VideoEntity
import com.example.ui.theme.InstaBlue
import com.example.ui.theme.InstaBorder
import com.example.ui.theme.InstaDivider
import com.example.ui.theme.InstaGreen
import com.example.ui.theme.InstaSecondaryBg
import com.example.ui.theme.InstaTextLight
import com.example.ui.theme.InstaTextPrimary
import com.example.ui.theme.InstaTextSecondary
import com.example.ui.theme.InstaWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleScreen(
    activeProfile: ProfileEntity?,
    videos: List<VideoEntity>,
    optimalSlots: List<OptimalSlotEntity>,
    onToggleAutoPilot: (Boolean) -> Unit,
    onPublishNow: (videoId: String) -> Unit,
    onRescheduleOptimal: (videoId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheduledVideos = videos.filter { it.status == "SCHEDULED" }
        .sortedBy { it.scheduledAt ?: Long.MAX_VALUE }

    var dailyUploadLimit by remember { mutableIntStateOf(activeProfile?.dailyUploadLimit ?: 3) }
    var smartSpaceOut by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(InstaWhite)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Planner Header
        item {
            Column {
                Text(
                    text = "Content Planner",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Automated optimal time distribution for maximum Reels reach.",
                    fontSize = 12.sp,
                    color = InstaTextSecondary
                )
            }
        }

        // Next Drop Card
        if (scheduledVideos.isNotEmpty()) {
            val nextVideo = scheduledVideos.first()
            val sdf = SimpleDateFormat("h:mm a (EEEE)", Locale.getDefault())
            val timeStr = nextVideo.scheduledAt?.let { sdf.format(Date(it)) } ?: "Upcoming"

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InstaGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(InstaGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = InstaGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Next Reel Drop: $timeStr",
                                    color = InstaTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = nextVideo.fileName,
                                    color = InstaTextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = { onPublishNow(nextVideo.id) },
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Post Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Peak Engagement Slots Section
        item {
            Text(
                text = "Peak Audience Windows",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = InstaTextPrimary
            )
        }

        items(optimalSlots) { slot ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = InstaWhite),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (slot.isRecommended) InstaBlue.copy(alpha = 0.5f) else InstaBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (slot.isRecommended) Color(0xFFE0F1FF) else InstaSecondaryBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (slot.isRecommended) InstaBlue else InstaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = slot.timeLabel,
                                    color = InstaTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (slot.isRecommended) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE0F1FF))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "HIGH PEAK",
                                            color = InstaBlue,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${slot.dayOfWeek} • ${slot.audienceActivePercent}% audience online",
                                color = InstaTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "${slot.engagementScore}% Potential",
                        color = InstaGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Scheduled Videos List
        item {
            Text(
                text = "Scheduled Pipeline (${scheduledVideos.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = InstaTextPrimary
            )
        }

        if (scheduledVideos.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = InstaTextLight,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No videos scheduled yet",
                        color = InstaTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Go to the Queue tab and tap 'Auto-Schedule ⚡' on any video to place it at the next optimal audience slot.",
                        color = InstaTextSecondary,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(scheduledVideos, key = { it.id }) { video ->
                InstagramScheduledVideoCard(
                    video = video,
                    onPublishNow = { onPublishNow(video.id) },
                    onReschedule = { onRescheduleOptimal(video.id) }
                )
            }
        }

        // Automation Rules
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InstaWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Autopilot Automation Settings",
                        color = InstaTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Smart Interval Spacing",
                                color = InstaTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Maintains minimum 4 hours between uploads",
                                color = InstaTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = smartSpaceOut,
                            onCheckedChange = { smartSpaceOut = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = InstaBlue
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Max Daily Reels Limit",
                                color = InstaTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$dailyUploadLimit reels per day recommended",
                                color = InstaTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(InstaSecondaryBg)
                                    .clickable { if (dailyUploadLimit > 1) dailyUploadLimit-- }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("-", color = InstaTextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$dailyUploadLimit",
                                color = InstaTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(InstaSecondaryBg)
                                    .clickable { if (dailyUploadLimit < 8) dailyUploadLimit++ }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+", color = InstaTextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstagramScheduledVideoCard(
    video: VideoEntity,
    onPublishNow: () -> Unit,
    onReschedule: () -> Unit
) {
    val sdf = SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault())
    val timeLabel = video.scheduledAt?.let { sdf.format(Date(it)) } ?: "Upcoming"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = InstaWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1A1A1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.fileName,
                        color = InstaTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = InstaBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeLabel,
                            color = InstaBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReschedule,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text("Reschedule", fontSize = 11.sp, color = InstaTextPrimary)
                }

                Button(
                    onClick = onPublishNow,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text("Post Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
