package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ProfileEntity
import com.example.data.local.VideoEntity
import com.example.ui.components.InstagramStoriesTray
import com.example.ui.components.formatNumber
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
import com.example.ui.theme.InstagramStoryGradient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QueueScreen(
    activeProfile: ProfileEntity?,
    allProfiles: List<ProfileEntity>,
    videos: List<VideoEntity>,
    connectedFolderName: String,
    isScanning: Boolean,
    isUploading: Boolean,
    uploadStage: String,
    uploadProgress: Float,
    onSelectFolder: (Uri) -> Unit,
    onScanFolder: () -> Unit,
    onGenerateCaptions: (videoId: String, tone: String) -> Unit,
    onScheduleOptimal: (videoId: String) -> Unit,
    onPublishNow: (videoId: String) -> Unit,
    onDeleteVideo: (videoId: String) -> Unit,
    onSwitchProfile: (String) -> Unit,
    onToggleAutoPilot: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var captionDialogVideo by remember { mutableStateOf<VideoEntity?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            onSelectFolder(uri)
        }
    }

    val filteredVideos = when (selectedFilter) {
        "DISCOVERED" -> videos.filter { it.status == "DISCOVERED" }
        "CAPTIONS" -> videos.filter { it.status == "CAPTIONS_READY" }
        "SCHEDULED" -> videos.filter { it.status == "SCHEDULED" }
        "PUBLISHED" -> videos.filter { it.status == "PUBLISHED" }
        else -> videos
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(InstaWhite),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Instagram Stories Tray
        item {
            InstagramStoriesTray(
                activeProfile = activeProfile,
                allProfiles = allProfiles,
                connectedFolderName = connectedFolderName,
                onSelectFolder = { folderPicker.launch(null) },
                onSwitchProfile = onSwitchProfile,
                onToggleAutoPilot = onToggleAutoPilot
            )
        }

        // 2. Professional Dashboard Banner (like Instagram Creator Dashboard)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("folder_connector_card"),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = InstaSecondaryBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Watched Media Folder",
                                fontSize = 11.sp,
                                color = InstaTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = connectedFolderName,
                                fontSize = 14.sp,
                                color = InstaTextPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = InstaBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(InstaGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Auto-Syncing",
                                    fontSize = 11.sp,
                                    color = InstaGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { folderPicker.launch(null) },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(36.dp)
                                .testTag("select_folder_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Choose Folder", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onScanFolder,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("scan_folder_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = InstaTextPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan Folder", fontSize = 12.sp, color = InstaTextPrimary)
                        }
                    }
                }
            }
        }

        // 3. Instagram Filter Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All (${videos.size})",
                    "DISCOVERED" to "Discovered (${videos.count { it.status == "DISCOVERED" }})",
                    "CAPTIONS" to "Captions Ready (${videos.count { it.status == "CAPTIONS_READY" }})",
                    "SCHEDULED" to "Scheduled (${videos.count { it.status == "SCHEDULED" }})",
                    "PUBLISHED" to "Live Reels (${videos.count { it.status == "PUBLISHED" }})"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) InstaTextPrimary else InstaSecondaryBg)
                            .border(
                                1.dp,
                                if (isSelected) InstaTextPrimary else InstaBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else InstaTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Empty state
        if (filteredVideos.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = InstaTextLight,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Videos in this Folder",
                        color = InstaTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose a video folder on your device containing reels (.mp4, .mov). Insta autopilot will detect new clips and automatically generate captions and hashtags.",
                        color = InstaTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { folderPicker.launch(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Choose Video Folder", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 4. Feed Posts List (Instagram Feed Style!)
        items(filteredVideos, key = { it.id }) { video ->
            InstagramPostReelCard(
                video = video,
                activeProfile = activeProfile,
                onOpenCaptionDialog = { captionDialogVideo = video },
                onScheduleOptimal = { onScheduleOptimal(video.id) },
                onPublishNow = { onPublishNow(video.id) },
                onDelete = { onDeleteVideo(video.id) },
                onCopyText = { text -> clipboardManager.setText(AnnotatedString(text)) }
            )
            HorizontalDivider(thickness = 0.5.dp, color = InstaDivider, modifier = Modifier.padding(vertical = 10.dp))
        }
    }

    // AI Caption Tone Dialog
    if (captionDialogVideo != null) {
        val currentVideo = captionDialogVideo!!
        var selectedTone by remember { mutableStateOf(currentVideo.tone.ifEmpty { "Viral / High-Energy" }) }

        AlertDialog(
            onDismissRequest = { captionDialogVideo = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = InstaBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Caption & Hashtags",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = InstaTextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Choose your tone style to craft viral hooks, caption body, and categorized hashtags:",
                        fontSize = 12.sp,
                        color = InstaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val tones = listOf(
                        "Viral / High-Energy",
                        "Educational / Tech",
                        "Storytelling / Aesthetic",
                        "Humor / Relatable"
                    )

                    tones.forEach { tone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedTone == tone) Color(0xFFE0F1FF) else InstaSecondaryBg)
                                .border(
                                    1.dp,
                                    if (selectedTone == tone) InstaBlue else InstaBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTone = tone }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = tone,
                                fontSize = 13.sp,
                                color = if (selectedTone == tone) InstaBlue else InstaTextPrimary,
                                fontWeight = if (selectedTone == tone) FontWeight.Bold else FontWeight.Normal
                            )
                            if (selectedTone == tone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = InstaBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onGenerateCaptions(currentVideo.id, selectedTone)
                        captionDialogVideo = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InstaBlue)
                ) {
                    Text("Generate with AI")
                }
            },
            dismissButton = {
                TextButton(onClick = { captionDialogVideo = null }) {
                    Text("Cancel", color = InstaTextSecondary)
                }
            },
            containerColor = InstaWhite
        )
    }

    // Upload Progress Modal
    if (isUploading) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = InstaBlue,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Publishing to Instagram Reels", color = InstaTextPrimary, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = uploadStage,
                        color = InstaTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = InstaBlue,
                        trackColor = InstaDivider
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(uploadProgress * 100).toInt()}% completed",
                        color = InstaTextLight,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            },
            confirmButton = {},
            containerColor = InstaWhite
        )
    }
}

@Composable
fun InstagramPostReelCard(
    video: VideoEntity,
    activeProfile: ProfileEntity?,
    onOpenCaptionDialog: () -> Unit,
    onScheduleOptimal: () -> Unit,
    onPublishNow: () -> Unit,
    onDelete: () -> Unit,
    onCopyText: (String) -> Unit
) {
    var expandedCaption by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_card_${video.id}")
    ) {
        // Post Header: Avatar, username, and status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(InstagramStoryGradient)
                        .padding(1.5.dp)
                ) {
                    AsyncImage(
                        model = activeProfile?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activeProfile?.username ?: "reel_creator",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = InstaTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "•", color = InstaTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        val statusText = when (video.status) {
                            "PUBLISHED" -> "Live"
                            "SCHEDULED" -> "Scheduled"
                            "CAPTIONS_READY" -> "Ready"
                            else -> "Draft"
                        }
                        val statusColor = when (video.status) {
                            "PUBLISHED" -> InstaGreen
                            "SCHEDULED" -> InstaBlue
                            "CAPTIONS_READY" -> Color(0xFFF59E0B)
                            else -> InstaTextSecondary
                        }
                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    Text(
                        text = "Original audio • ${video.resolution}",
                        fontSize = 10.sp,
                        color = InstaTextSecondary
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = InstaTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Post Media Area (Video Player Frame)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(48.dp)
            )

            // Duration badge bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "0:${if (video.durationSeconds < 10) "0" else ""}${video.durationSeconds}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // File size & name bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${(video.fileSizeBytes / (1024 * 1024))}MB",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }

        // Post Action Bar: Heart, Comment, Share, Bookmark (Exact Instagram layout!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = InstaTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = InstaTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Share",
                    tint = InstaTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Save",
                tint = InstaTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Likes / Stats count
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            if (video.status == "PUBLISHED") {
                Text(
                    text = "${formatNumber(video.viewsCount)} plays • ${formatNumber(video.likesCount)} likes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
            } else if (video.scheduledAt != null) {
                val sdf = SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = InstaBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Scheduled for ${sdf.format(Date(video.scheduledAt))}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = InstaBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Caption Section (username in bold + hook + caption)
            Row {
                Text(
                    text = "${activeProfile?.username ?: "creator"} ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = InstaTextPrimary
                )
                Text(
                    text = video.hook.ifEmpty { video.fileName },
                    fontSize = 12.sp,
                    color = InstaTextPrimary,
                    maxLines = if (expandedCaption) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!expandedCaption && video.caption.isNotEmpty()) {
                Text(
                    text = "...more",
                    fontSize = 12.sp,
                    color = InstaTextSecondary,
                    modifier = Modifier.clickable { expandedCaption = true }
                )
            }

            AnimatedVisibility(visible = expandedCaption) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = video.caption,
                        fontSize = 12.sp,
                        color = InstaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.callToAction,
                        fontSize = 12.sp,
                        color = InstaBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.hashtags,
                        fontSize = 11.sp,
                        color = Color(0xFF00376B),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Autopilot Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenCaptionDialog,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = InstaBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Captions", fontSize = 11.sp, color = InstaTextPrimary, fontWeight = FontWeight.SemiBold)
                }

                if (video.status != "PUBLISHED") {
                    Button(
                        onClick = onScheduleOptimal,
                        modifier = Modifier
                            .weight(1.1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Schedule ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onPublishNow,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, InstaBlue),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text("Post Now", fontSize = 11.sp, color = InstaBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
