package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String,
    val category: String,
    val followersCount: Int,
    val followingCount: Int,
    val totalReels: Int,
    val engagementRate: Float,
    val oauthAccessToken: String,
    val oauthExpiresAt: Long,
    val isCurrentActive: Boolean,
    val isAutoPilotEnabled: Boolean,
    val dailyUploadLimit: Int = 3
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val folderUri: String,
    val fileUri: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val durationSeconds: Int,
    val resolution: String,
    val detectedAt: Long,
    val status: String, // DISCOVERED, CAPTIONS_READY, QUEUED, SCHEDULED, PUBLISHED, FAILED
    val scheduledAt: Long?,
    val publishedAt: Long?,
    val caption: String,
    val hook: String,
    val hashtags: String,
    val callToAction: String,
    val tone: String,
    val targetProfileId: String,
    val uploadProgress: Float = 0f,
    val errorMessage: String? = null,
    val instaReelId: String? = null,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0
)

@Entity(tableName = "comments_suggestions")
data class CommentSuggestionEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val reelId: String,
    val reelTitle: String,
    val commenterUsername: String,
    val commenterAvatar: String,
    val commentText: String,
    val category: String, // IDEA, QUESTION, PRAISE, FEEDBACK
    val sentiment: String, // POSITIVE, NEUTRAL, CONSTRUCTIVE
    val timestamp: Long,
    val isReviewed: Boolean,
    val isConvertedToNextReel: Boolean,
    val aiQuickReply: String? = null
)

@Entity(tableName = "optimal_slots")
data class OptimalSlotEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val dayOfWeek: String,
    val timeLabel: String,
    val hourOfDay: Int,
    val minuteOfHour: Int,
    val engagementScore: Int,
    val audienceActivePercent: Int,
    val isRecommended: Boolean
)
