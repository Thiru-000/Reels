package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OptimalSlotEntity
import com.example.data.local.ProfileEntity
import com.example.data.local.VideoEntity
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyticsScreen(
    activeProfile: ProfileEntity?,
    videos: List<VideoEntity>,
    optimalSlots: List<OptimalSlotEntity>,
    timeframe: String,
    onSetTimeframe: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val publishedVideos = videos.filter { it.status == "PUBLISHED" }
    val totalViews = publishedVideos.sumOf { it.viewsCount } + (activeProfile?.followersCount?.times(3) ?: 45000)
    val totalLikes = publishedVideos.sumOf { it.likesCount } + 23100
    val totalComments = publishedVideos.sumOf { it.commentsCount } + 576
    val totalShares = publishedVideos.sumOf { it.sharesCount } + 4370

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(InstaWhite)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Professional Dashboard Header
        item {
            Column {
                Text(
                    text = "Professional dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Track your Reels engagement and optimal publishing times.",
                    fontSize = 12.sp,
                    color = InstaTextSecondary
                )
            }
        }

        // Timeframe selector bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(InstaSecondaryBg)
                    .border(1.dp, InstaBorder, RoundedCornerShape(8.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("7D" to "Last 7 days", "30D" to "Last 30 days", "90D" to "Last 90 days").forEach { (tf, label) ->
                    val isSelected = timeframe == tf
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) InstaWhite else Color.Transparent)
                            .clickable { onSetTimeframe(tf) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) InstaTextPrimary else InstaTextSecondary
                        )
                    }
                }
            }
        }

        // Main Insights Card (Account Overview)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("overview_insights_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InstaWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Insights",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = InstaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${formatNumber(totalViews)} Plays",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = InstaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+24.8%",
                            color = InstaGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "vs previous $timeframe",
                            color = InstaTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = InstaDivider)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Metric Rows
                    InsightRow("Accounts reached", formatNumber((totalViews * 0.78).toInt()), "+18.4%")
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow("Accounts engaged", "${activeProfile?.engagementRate ?: 8.7}%", "+2.1%")
                    Spacer(modifier = Modifier.height(10.dp))
                    InsightRow("Profile followers", formatNumber(activeProfile?.followersCount ?: 48200), "+1,240")
                }
            }
        }

        // Peak Hours & Most Active Times (Instagram Heatmap)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("optimal_times_chart_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InstaWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Most Active Times",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = InstaTextPrimary
                            )
                            Text(
                                text = "When your followers are online on Instagram",
                                fontSize = 11.sp,
                                color = InstaTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE0F1FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "HOURS",
                                color = InstaBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hourly chart in official Instagram gradient
                    InstagramHourlyActivityCanvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Peak Time Recommendation Badges
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        optimalSlots.forEach { slot ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (slot.isRecommended) Color(0xFFFDF2F8) else InstaSecondaryBg)
                                    .border(
                                        1.dp,
                                        if (slot.isRecommended) Color(0xFFF472B6) else InstaBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${slot.timeLabel} (${slot.engagementScore}% fit)",
                                    fontSize = 11.sp,
                                    fontWeight = if (slot.isRecommended) FontWeight.Bold else FontWeight.Normal,
                                    color = if (slot.isRecommended) Color(0xFFBE185D) else InstaTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Audience Reach Breakdown (Followers vs Non-Followers)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InstaWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Followers vs. Non-Followers",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = InstaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Higher non-follower reach means algorithm explore distribution.",
                        fontSize = 11.sp,
                        color = InstaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Non-followers (Reels Explore)", fontSize = 12.sp, color = InstaTextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("78.4%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InstaBlue)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.784f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = InstaBlue,
                        trackColor = InstaDivider
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Followers", fontSize = 12.sp, color = InstaTextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("21.6%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InstaTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.216f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFA8A8A8),
                        trackColor = InstaDivider
                    )
                }
            }
        }

        // Top Performing Reels Leaderboard
        item {
            Text(
                text = "Top Reels",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = InstaTextPrimary
            )
        }

        items(publishedVideos.take(4)) { reel ->
            InstagramTopReelCard(reel)
        }
    }
}

@Composable
fun InsightRow(title: String, value: String, change: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = InstaTextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InstaTextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = change, fontSize = 11.sp, color = InstaGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun InstagramTopReelCard(reel: VideoEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(InstaSecondaryBg)
            .border(1.dp, InstaBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1E1E1E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reel.fileName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = InstaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${formatNumber(reel.viewsCount)} plays",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InstaTextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${formatNumber(reel.likesCount)} likes",
                    fontSize = 11.sp,
                    color = InstaTextSecondary
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0FDF4))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "+89% Watch",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = InstaGreen
            )
        }
    }
}

@Composable
fun InstagramHourlyActivityCanvas(modifier: Modifier = Modifier) {
    val hourlyActivity = listOf(
        15, 10, 8, 5, 8, 18, 38, 62, 85, 94, 78, 82, 89, 74, 68, 72, 84, 91, 98, 92, 95, 82, 54, 30
    )

    Canvas(modifier = modifier) {
        val barWidth = size.width / (hourlyActivity.size * 1.5f)
        val spacing = barWidth * 0.5f

        hourlyActivity.forEachIndexed { index, value ->
            val barHeight = (value / 100f) * size.height * 0.85f
            val x = index * (barWidth + spacing) + spacing
            val y = size.height - barHeight

            val isPeak = value >= 90
            val barColor = if (isPeak) {
                Brush.verticalGradient(listOf(Color(0xFF0095F6), Color(0xFF1877F2)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFCCCCCC)))
            }

            drawRoundRect(
                brush = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}
