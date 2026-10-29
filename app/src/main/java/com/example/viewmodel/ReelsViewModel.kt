package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CommentSuggestionEntity
import com.example.data.local.OptimalSlotEntity
import com.example.data.local.ProfileEntity
import com.example.data.local.VideoEntity
import com.example.data.repository.ReelsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReelsRepository(
        application.applicationContext,
        AppDatabase.getDatabase(application.applicationContext)
    )

    val allProfiles: StateFlow<List<ProfileEntity>> = repository.getAllProfilesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProfile: StateFlow<ProfileEntity?> = repository.getActiveProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val videos: StateFlow<List<VideoEntity>> = activeProfile.flatMapLatest { profile ->
        if (profile != null) {
            repository.getVideosForProfileFlow(profile.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val comments: StateFlow<List<CommentSuggestionEntity>> = activeProfile.flatMapLatest { profile ->
        if (profile != null) {
            repository.getCommentsForProfileFlow(profile.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val optimalSlots: StateFlow<List<OptimalSlotEntity>> = activeProfile.flatMapLatest { profile ->
        if (profile != null) {
            repository.getOptimalSlotsFlow(profile.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFolderUri = MutableStateFlow<Uri?>(null)
    val selectedFolderUri: StateFlow<Uri?> = _selectedFolderUri.asStateFlow()

    private val _connectedFolderName = MutableStateFlow("Select Video Folder")
    val connectedFolderName: StateFlow<String> = _connectedFolderName.asStateFlow()

    private val _isFolderScanning = MutableStateFlow(false)
    val isFolderScanning: StateFlow<Boolean> = _isFolderScanning.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadStage = MutableStateFlow("")
    val uploadStage: StateFlow<String> = _uploadStage.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _analyticsTimeframe = MutableStateFlow("30D")
    val analyticsTimeframe: StateFlow<String> = _analyticsTimeframe.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isSyncingProfile = MutableStateFlow(false)
    val isSyncingProfile: StateFlow<Boolean> = _isSyncingProfile.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    fun setAnalyticsTimeframe(timeframe: String) {
        _analyticsTimeframe.value = timeframe
    }

    fun loginOriginalAccount(username: String, fullName: String, category: String) {
        viewModelScope.launch {
            _isSyncingProfile.value = true
            val prof = repository.loginWithOriginalAccount(username, fullName, category)
            _isSyncingProfile.value = false
            if (prof.followersCount > 0) {
                showToast("Logged in & synced @${prof.username} (${prof.followersCount} followers)!")
            } else {
                showToast("Logged in as @${prof.username}!")
            }
        }
    }

    fun syncCurrentProfile() {
        val current = activeProfile.value ?: return
        viewModelScope.launch {
            _isSyncingProfile.value = true
            val result = repository.syncProfile(current.id)
            _isSyncingProfile.value = false
            if (result.success) {
                showToast("✓ Synced with Instagram: ${result.followersCount} followers, ${result.totalPosts} posts")
            } else {
                showToast(result.errorMessage ?: "Instagram sync complete")
            }
        }
    }

    fun updateProfileCustomStats(
        followersCount: Int,
        followingCount: Int,
        totalReels: Int,
        fullName: String,
        category: String
    ) {
        val current = activeProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfileCustomStats(
                profileId = current.id,
                followersCount = followersCount,
                followingCount = followingCount,
                totalReels = totalReels,
                fullName = fullName,
                category = category
            )
            showToast("Updated profile stats!")
        }
    }

    fun removeProfile(profileId: String) {
        viewModelScope.launch {
            repository.deleteProfileById(profileId)
            showToast("Profile removed from device")
        }
    }

    fun logout() {
        val current = activeProfile.value
        viewModelScope.launch {
            if (current != null) {
                repository.logoutProfile(current)
            } else {
                repository.logoutAll()
            }
            showToast("Logged out successfully")
        }
    }

    fun switchProfile(profileId: String) {
        viewModelScope.launch {
            repository.switchProfile(profileId)
            showToast("Switched active profile")
        }
    }

    fun toggleAutoPilot(enabled: Boolean) {
        val current = activeProfile.value ?: return
        viewModelScope.launch {
            repository.toggleAutoPilot(current.id, enabled)
            showToast(if (enabled) "Auto-Pilot enabled: Reels will publish automatically at peak times" else "Auto-Pilot paused")
        }
    }

    fun onFolderSelected(uri: Uri) {
        _selectedFolderUri.value = uri
        val lastSegment = uri.lastPathSegment ?: "Selected Folder"
        val cleanName = lastSegment.substringAfterLast(":").ifEmpty { "Connected Video Folder" }
        _connectedFolderName.value = cleanName

        viewModelScope.launch {
            _isFolderScanning.value = true
            val current = activeProfile.value
            if (current != null) {
                val found = repository.scanFolder(uri, current.id)
                showToast("Connected folder: Scanned $found video clips")
            }
            _isFolderScanning.value = false
        }
    }

    fun scanCurrentFolder() {
        val uri = _selectedFolderUri.value
        val current = activeProfile.value ?: return
        if (uri == null) {
            showToast("Please tap 'Choose Folder' to pick your video folder")
            return
        }
        viewModelScope.launch {
            _isFolderScanning.value = true
            val found = repository.scanFolder(uri, current.id)
            showToast("Refreshed folder: $found videos detected")
            _isFolderScanning.value = false
        }
    }

    fun generateCaptions(videoId: String, tone: String) {
        val current = activeProfile.value ?: return
        viewModelScope.launch {
            repository.generateCaptionsForVideo(videoId, tone, current.category)
            showToast("AI Generated captions & hashtags with '$tone' style")
        }
    }

    fun scheduleAtOptimalTime(videoId: String) {
        viewModelScope.launch {
            repository.scheduleVideoAtOptimalTime(videoId)
            showToast("Scheduled video at predicted peak audience time! ⚡")
        }
    }

    fun scheduleCustomTime(videoId: String, epochMillis: Long) {
        viewModelScope.launch {
            repository.scheduleVideoCustom(videoId, epochMillis)
            showToast("Video scheduled successfully")
        }
    }

    fun publishReelNow(videoId: String) {
        viewModelScope.launch {
            _isUploading.value = true
            _uploadProgress.value = 0.1f
            _uploadStage.value = "Starting Instagram Graph API upload..."

            val success = repository.publishReelNow(videoId) { stage, progress ->
                _uploadStage.value = stage
                _uploadProgress.value = progress
            }

            _isUploading.value = false
            if (success) {
                showToast("🎉 Reel published successfully to your Instagram account!")
            } else {
                showToast("Upload failed: Please check account status")
            }
        }
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            repository.deleteVideo(videoId)
            showToast("Removed video from queue")
        }
    }

    fun toggleCommentReviewed(commentId: String, currentReviewed: Boolean) {
        viewModelScope.launch {
            repository.toggleCommentReviewed(commentId, !currentReviewed)
        }
    }

    fun convertCommentToNextReelIdea(comment: CommentSuggestionEntity) {
        viewModelScope.launch {
            repository.convertCommentToIdea(comment)
            showToast("💡 Converted comment into a new Reel project in queue!")
        }
    }

    fun connectNewProfileOAuth(username: String, fullName: String, category: String) {
        viewModelScope.launch {
            val prof = repository.loginWithOriginalAccount(username, fullName, category)
            showToast("Successfully connected @${prof.username} via Meta OAuth! 🎉")
        }
    }
}
