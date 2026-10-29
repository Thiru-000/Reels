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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.CommentSuggestionEntity
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

@Composable
fun SuggestionsScreen(
    comments: List<CommentSuggestionEntity>,
    onToggleReviewed: (commentId: String, currentReviewed: Boolean) -> Unit,
    onConvertToNextReel: (CommentSuggestionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    val clipboardManager = LocalClipboardManager.current

    val filteredComments = when (selectedCategory) {
        "IDEA" -> comments.filter { it.category == "IDEA" }
        "QUESTION" -> comments.filter { it.category == "QUESTION" }
        "FEEDBACK" -> comments.filter { it.category == "FEEDBACK" }
        "PRAISE" -> comments.filter { it.category == "PRAISE" }
        "UNREVIEWED" -> comments.filter { !it.isReviewed }
        else -> comments
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(InstaWhite)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Instagram Comments Header
        item {
            Column {
                Text(
                    text = "Comments & Reel Ideas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Viewer requests and tutorial ideas from your published Reels.",
                    fontSize = 12.sp,
                    color = InstaTextSecondary
                )
            }
        }

        // Category Filter Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val categories = listOf(
                    "ALL" to "All (${comments.size})",
                    "IDEA" to "💡 Video Ideas (${comments.count { it.category == "IDEA" }})",
                    "QUESTION" to "❓ Questions (${comments.count { it.category == "QUESTION" }})",
                    "FEEDBACK" to "⚠️ Feedback (${comments.count { it.category == "FEEDBACK" }})",
                    "PRAISE" to "🌟 Praise (${comments.count { it.category == "PRAISE" }})"
                )

                items(categories) { (key, label) ->
                    val isSelected = selectedCategory == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) InstaTextPrimary else InstaSecondaryBg)
                            .border(
                                1.dp,
                                if (isSelected) InstaTextPrimary else InstaBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategory = key }
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
        if (filteredComments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = InstaTextLight,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No comments in this category",
                        color = InstaTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Viewer suggestions from your reels will be automatically classified by AI here.",
                        color = InstaTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Comment Cards
        items(filteredComments, key = { it.id }) { comment ->
            InstagramCommentCard(
                comment = comment,
                onToggleReviewed = { onToggleReviewed(comment.id, comment.isReviewed) },
                onConvertToNextReel = { onConvertToNextReel(comment) },
                onCopyAiReply = { reply ->
                    clipboardManager.setText(AnnotatedString(reply))
                }
            )
            HorizontalDivider(thickness = 0.5.dp, color = InstaDivider)
        }
    }
}

@Composable
fun InstagramCommentCard(
    comment: CommentSuggestionEntity,
    onToggleReviewed: () -> Unit,
    onConvertToNextReel: () -> Unit,
    onCopyAiReply: (String) -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }

    val categoryColor = when (comment.category) {
        "IDEA" -> Color(0xFF9333EA)
        "QUESTION" -> InstaBlue
        "FEEDBACK" -> Color(0xFFD97706)
        else -> InstaGreen
    }

    val categoryLabel = when (comment.category) {
        "IDEA" -> "IDEA"
        "QUESTION" -> "QUESTION"
        "FEEDBACK" -> "FEEDBACK"
        else -> "PRAISE"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("suggestion_card_${comment.id}"),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = comment.commenterAvatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.commenterUsername,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(categoryColor.copy(alpha = 0.1f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = categoryLabel,
                        color = categoryColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = comment.commentText,
                fontSize = 12.sp,
                color = InstaTextPrimary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "on ${comment.reelTitle}",
                fontSize = 10.sp,
                color = InstaTextLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // AI suggested reply box
            if (comment.aiQuickReply != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(InstaSecondaryBg)
                        .border(1.dp, InstaBorder, RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = InstaBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = comment.aiQuickReply,
                            fontSize = 11.sp,
                            color = InstaTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = if (copied) "Copied!" else "Reply",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (copied) InstaGreen else InstaBlue,
                        modifier = Modifier
                            .clickable {
                                onCopyAiReply(comment.aiQuickReply)
                                copied = true
                            }
                            .padding(start = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions row: "Make Next Reel" + "Mark Done"
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (comment.isConvertedToNextReel) {
                    Text(
                        text = "✓ In Reel Queue",
                        color = InstaGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Button(
                        onClick = onConvertToNextReel,
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InstaBlue),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Make Next Reel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = if (comment.isReviewed) "Reviewed" else "Mark Done",
                    fontSize = 11.sp,
                    color = if (comment.isReviewed) InstaGreen else InstaTextSecondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onToggleReviewed() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        IconButton(
            onClick = { isLiked = !isLiked },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Like comment",
                tint = if (isLiked) InstaRed else InstaTextLight,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
