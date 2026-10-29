package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY isCurrentActive DESC, username ASC")
    fun getAllProfilesFlow(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE isCurrentActive = 1 LIMIT 1")
    fun getActiveProfileFlow(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE username = :username LIMIT 1")
    suspend fun getProfileByUsername(username: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProfiles(profiles: List<ProfileEntity>)

    @Query("UPDATE profiles SET isCurrentActive = CASE WHEN id = :profileId THEN 1 ELSE 0 END")
    suspend fun setActiveProfile(profileId: String)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id IN ('prof_tech_flow', 'prof_vlogs', 'prof_art')")
    suspend fun deleteDemoProfiles()

    @Query("DELETE FROM profiles")
    suspend fun deleteAllProfiles()
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE targetProfileId = :profileId ORDER BY detectedAt DESC")
    fun getVideosForProfileFlow(profileId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos ORDER BY detectedAt DESC")
    fun getAllVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE status = 'SCHEDULED' ORDER BY scheduledAt ASC")
    fun getScheduledVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE status = 'PUBLISHED' ORDER BY publishedAt DESC")
    fun getPublishedVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id")
    suspend fun getVideoById(id: String): VideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVideos(videos: List<VideoEntity>)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideoById(id: String)

    @Query("DELETE FROM videos WHERE targetProfileId IN ('prof_tech_flow', 'prof_vlogs', 'prof_art')")
    suspend fun deleteDemoVideos()

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideos()
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments_suggestions WHERE profileId = :profileId ORDER BY timestamp DESC")
    fun getCommentsForProfileFlow(profileId: String): Flow<List<CommentSuggestionEntity>>

    @Query("SELECT * FROM comments_suggestions ORDER BY timestamp DESC")
    fun getAllCommentsFlow(): Flow<List<CommentSuggestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateComment(comment: CommentSuggestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<CommentSuggestionEntity>)

    @Query("UPDATE comments_suggestions SET isReviewed = :isReviewed WHERE id = :id")
    suspend fun updateReviewedStatus(id: String, isReviewed: Boolean)

    @Query("UPDATE comments_suggestions SET isConvertedToNextReel = 1 WHERE id = :id")
    suspend fun markConvertedToIdea(id: String)

    @Query("DELETE FROM comments_suggestions WHERE profileId IN ('prof_tech_flow', 'prof_vlogs', 'prof_art')")
    suspend fun deleteDemoComments()

    @Query("DELETE FROM comments_suggestions")
    suspend fun deleteAllComments()
}

@Dao
interface OptimalSlotDao {
    @Query("SELECT * FROM optimal_slots WHERE profileId = :profileId ORDER BY hourOfDay ASC")
    fun getOptimalSlotsFlow(profileId: String): Flow<List<OptimalSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptimalSlots(slots: List<OptimalSlotEntity>)

    @Query("DELETE FROM optimal_slots WHERE profileId IN ('prof_tech_flow', 'prof_vlogs', 'prof_art')")
    suspend fun deleteDemoSlots()

    @Query("DELETE FROM optimal_slots")
    suspend fun deleteAllSlots()
}
