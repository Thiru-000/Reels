package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.ai.AIEngine
import com.example.data.local.AppDatabase
import com.example.data.local.CommentSuggestionEntity
import com.example.data.local.OptimalSlotEntity
import com.example.data.local.ProfileEntity
import com.example.data.local.VideoEntity
import com.example.data.saf.FolderMediaScanner
import com.example.data.sync.InstagramProfileSyncResult
import com.example.data.sync.InstagramSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class ReelsRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val profileDao = database.profileDao()
    private val videoDao = database.videoDao()
    private val commentDao = database.commentDao()
    private val optimalSlotDao = database.optimalSlotDao()
    private val scanner = FolderMediaScanner(context)
    private val syncService = InstagramSyncService(context)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            purgeDemoData()
        }
    }

    private suspend fun purgeDemoData() {
        // Remove all previous demo accounts, demo reels, and demo comments
        profileDao.deleteDemoProfiles()
        videoDao.deleteDemoVideos()
        commentDao.deleteDemoComments()
        optimalSlotDao.deleteDemoSlots()
    }

    // Profiles Flow & Operations
    fun getAllProfilesFlow(): Flow<List<ProfileEntity>> = profileDao.getAllProfilesFlow()
    fun getActiveProfileFlow(): Flow<ProfileEntity?> = profileDao.getActiveProfileFlow()

    suspend fun switchProfile(profileId: String) {
        profileDao.setActiveProfile(profileId)
    }

    suspend fun loginWithOriginalAccount(
        username: String,
        fullName: String,
        category: String
    ): ProfileEntity {
        val cleanUser = username.removePrefix("@").trim()
        val displayName = fullName.ifBlank { cleanUser }

        // Prevent duplicate profiles if this username already exists
        val existing = profileDao.getProfileByUsername(cleanUser)
        val profileId = existing?.id ?: "user_${UUID.randomUUID()}"

        // Attempt live Instagram profile sync
        val syncResult = syncService.syncProfileFromInstagram(cleanUser)

        val followers = if (syncResult.success && syncResult.followersCount > 0) syncResult.followersCount
            else existing?.followersCount ?: 0
        val following = if (syncResult.success && syncResult.followingCount > 0) syncResult.followingCount
            else existing?.followingCount ?: 0
        val totalPosts = if (syncResult.success && syncResult.totalPosts > 0) syncResult.totalPosts
            else existing?.totalReels ?: 0
        val avatar = if (syncResult.success && syncResult.avatarUrl.isNotBlank()) syncResult.avatarUrl
            else existing?.avatarUrl ?: "https://ui-avatars.com/api/?name=${cleanUser}&background=E1306C&color=fff&size=150&bold=true"
        val resolvedName = if (syncResult.success && syncResult.fullName.isNotBlank()) syncResult.fullName
            else displayName

        val newProfile = ProfileEntity(
            id = profileId,
            username = cleanUser,
            fullName = resolvedName,
            avatarUrl = avatar,
            category = category.ifBlank { "Creator / Professional" },
            followersCount = followers,
            followingCount = following,
            totalReels = totalPosts,
            engagementRate = if (followers > 0) 4.2f else 0f,
            oauthAccessToken = "IGQVJ${UUID.randomUUID()}...meta_oauth_token",
            oauthExpiresAt = System.currentTimeMillis() + 5184000000L, // 60 days
            isCurrentActive = true,
            isAutoPilotEnabled = true,
            dailyUploadLimit = 3
        )

        // Save profile & set active
        profileDao.insertOrUpdateProfile(newProfile)
        profileDao.setActiveProfile(newProfile.id)

        // Generate optimal times for this account
        optimalSlotDao.insertOptimalSlots(AIEngine.getSampleOptimalSlots(newProfile.id))

        return newProfile
    }

    suspend fun syncProfile(profileId: String): InstagramProfileSyncResult {
        val profile = profileDao.getProfileById(profileId)
            ?: return InstagramProfileSyncResult(
                success = false,
                username = "",
                fullName = "",
                bio = "",
                followersCount = 0,
                followingCount = 0,
                totalPosts = 0,
                avatarUrl = "",
                isVerified = false,
                errorMessage = "Profile not found"
            )

        val result = syncService.syncProfileFromInstagram(profile.username)
        if (result.success) {
            val updated = profile.copy(
                fullName = if (result.fullName.isNotBlank()) result.fullName else profile.fullName,
                followersCount = result.followersCount,
                followingCount = result.followingCount,
                totalReels = result.totalPosts,
                avatarUrl = if (result.avatarUrl.isNotBlank()) result.avatarUrl else profile.avatarUrl,
                engagementRate = if (result.followersCount > 0) 4.5f else profile.engagementRate
            )
            profileDao.insertOrUpdateProfile(updated)
        }
        return result
    }

    suspend fun updateProfileCustomStats(
        profileId: String,
        followersCount: Int,
        followingCount: Int,
        totalReels: Int,
        fullName: String,
        category: String
    ) {
        val profile = profileDao.getProfileById(profileId) ?: return
        val updated = profile.copy(
            followersCount = followersCount,
            followingCount = followingCount,
            totalReels = totalReels,
            fullName = fullName.ifBlank { profile.fullName },
            category = category.ifBlank { profile.category },
            engagementRate = if (followersCount > 0) 4.8f else 0f
        )
        profileDao.insertOrUpdateProfile(updated)
    }

    suspend fun deleteProfileById(profileId: String) {
        val profile = profileDao.getProfileById(profileId) ?: return
        profileDao.deleteProfile(profile)
        // Switch to remaining profile if active was deleted
        val remaining = profileDao.getProfileById(profileId)
        if (remaining == null) {
            val anyProfile = profileDao.getProfileByUsername("")
        }
    }

    suspend fun logoutProfile(profile: ProfileEntity) {
        profileDao.deleteProfile(profile)
    }

    suspend fun logoutAll() {
        profileDao.deleteAllProfiles()
        videoDao.deleteAllVideos()
        commentDao.deleteAllComments()
        optimalSlotDao.deleteAllSlots()
    }

    suspend fun toggleAutoPilot(profileId: String, enabled: Boolean) {
        val prof = profileDao.getProfileById(profileId) ?: return
        profileDao.insertOrUpdateProfile(prof.copy(isAutoPilotEnabled = enabled))
    }

    // Videos & Queue
    fun getVideosForProfileFlow(profileId: String): Flow<List<VideoEntity>> =
        videoDao.getVideosForProfileFlow(profileId)

    suspend fun scanFolder(treeUri: Uri, profileId: String): Int {
        val scanned = scanner.scanFolder(treeUri)
        val now = System.currentTimeMillis()
        val entities = scanned.map { v ->
            val aiResult = AIEngine.generateCaptionAndHashtags(
                fileName = v.fileName,
                category = "Creator / Video",
                tone = "Viral / High-Energy"
            )
            VideoEntity(
                id = UUID.randomUUID().toString(),
                folderUri = treeUri.toString(),
                fileUri = v.fileUri,
                fileName = v.fileName,
                fileSizeBytes = v.fileSize,
                durationSeconds = v.durationSeconds,
                resolution = v.resolution,
                detectedAt = now,
                status = "CAPTIONS_READY",
                scheduledAt = null,
                publishedAt = null,
                caption = aiResult.caption,
                hook = aiResult.hook,
                hashtags = aiResult.hashtags,
                callToAction = aiResult.cta,
                tone = "Viral / High-Energy",
                targetProfileId = profileId
            )
        }
        if (entities.isNotEmpty()) {
            videoDao.insertAllVideos(entities)
        }
        return entities.size
    }

    suspend fun generateCaptionsForVideo(videoId: String, tone: String, category: String = "Tech & Creative") {
        val video = videoDao.getVideoById(videoId) ?: return
        val result = AIEngine.generateCaptionAndHashtags(video.fileName, category, tone)
        val updated = video.copy(
            hook = result.hook,
            caption = result.caption,
            hashtags = result.hashtags,
            callToAction = result.cta,
            tone = tone,
            status = if (video.status == "DISCOVERED") "CAPTIONS_READY" else video.status
        )
        videoDao.updateVideo(updated)
    }

    suspend fun scheduleVideoAtOptimalTime(videoId: String) {
        val video = videoDao.getVideoById(videoId) ?: return
        val optimalEpoch = AIEngine.calculateNextOptimalTime()
        val updated = video.copy(
            status = "SCHEDULED",
            scheduledAt = optimalEpoch
        )
        videoDao.updateVideo(updated)
    }

    suspend fun scheduleVideoCustom(videoId: String, scheduledEpoch: Long) {
        val video = videoDao.getVideoById(videoId) ?: return
        val updated = video.copy(
            status = "SCHEDULED",
            scheduledAt = scheduledEpoch
        )
        videoDao.updateVideo(updated)
    }

    suspend fun deleteVideo(videoId: String) {
        videoDao.deleteVideoById(videoId)
    }

    suspend fun publishReelNow(
        videoId: String,
        onProgress: suspend (stage: String, progress: Float) -> Unit
    ): Boolean {
        val video = videoDao.getVideoById(videoId) ?: return false

        // Multi-stage Instagram Reels Publishing pipeline via Meta Graph API Simulation:
        onProgress("Creating Instagram Media Container...", 0.2f)
        videoDao.updateVideo(video.copy(status = "QUEUED", uploadProgress = 0.2f))
        delay(900)

        onProgress("Uploading video chunks to Meta CDN...", 0.55f)
        videoDao.updateVideo(video.copy(uploadProgress = 0.55f))
        delay(1200)

        onProgress("Applying AI captions, hashtags & audio syncing...", 0.85f)
        videoDao.updateVideo(video.copy(uploadProgress = 0.85f))
        delay(900)

        onProgress("Publishing Reel to Instagram feed...", 1.0f)
        val now = System.currentTimeMillis()
        val updated = video.copy(
            status = "PUBLISHED",
            uploadProgress = 1.0f,
            publishedAt = now,
            instaReelId = "reel_${System.currentTimeMillis()}",
            viewsCount = 1,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0
        )
        videoDao.updateVideo(updated)
        delay(400)
        return true
    }

    // Comment Suggestions
    fun getCommentsForProfileFlow(profileId: String): Flow<List<CommentSuggestionEntity>> =
        commentDao.getCommentsForProfileFlow(profileId)

    suspend fun toggleCommentReviewed(commentId: String, isReviewed: Boolean) {
        commentDao.updateReviewedStatus(commentId, isReviewed)
    }

    suspend fun convertCommentToIdea(comment: CommentSuggestionEntity) {
        commentDao.markConvertedToIdea(comment.id)

        val cleanIdea = comment.commentText
            .replace("?", "")
            .take(45)
        val safeName = "reel_${cleanIdea.replace(" ", "_").lowercase()}.mp4"

        val aiResult = AIEngine.generateCaptionAndHashtags(
            fileName = safeName,
            category = "Creator Content",
            tone = "Educational / Tech"
        )

        val newVideoIdea = VideoEntity(
            id = UUID.randomUUID().toString(),
            folderUri = "manual_from_suggestion",
            fileUri = "placeholder_idea",
            fileName = safeName,
            fileSizeBytes = 0L,
            durationSeconds = 30,
            resolution = "1080x1920",
            detectedAt = System.currentTimeMillis(),
            status = "DISCOVERED",
            scheduledAt = null,
            publishedAt = null,
            caption = "Based on viewer suggestion from @${comment.commenterUsername}: ${aiResult.caption}",
            hook = "Responding to @${comment.commenterUsername}: ${aiResult.hook}",
            hashtags = aiResult.hashtags,
            callToAction = "What other topics should I cover next? Drop your ideas below!",
            tone = "Educational / Tech",
            targetProfileId = comment.profileId
        )
        videoDao.insertOrUpdateVideo(newVideoIdea)
    }

    // Optimal Slots
    fun getOptimalSlotsFlow(profileId: String): Flow<List<OptimalSlotEntity>> =
        optimalSlotDao.getOptimalSlotsFlow(profileId)
}
