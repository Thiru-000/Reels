package com.example.data.saf

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import com.example.data.local.VideoEntity
import java.util.UUID

class FolderMediaScanner(private val context: Context) {

    data class ScannedVideo(
        val fileName: String,
        val fileUri: String,
        val fileSize: Long,
        val durationSeconds: Int,
        val resolution: String
    )

    fun scanFolder(treeUri: Uri): List<ScannedVideo> {
        val results = mutableListOf<ScannedVideo>()
        try {
            val treeDocId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, treeDocId)
            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE
            )

            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)

                while (cursor.moveToNext()) {
                    val docId = if (idIndex != -1) cursor.getString(idIndex) else null
                    val name = if (nameIndex != -1) cursor.getString(nameIndex) ?: "video.mp4" else "video.mp4"
                    val mime = if (mimeIndex != -1) cursor.getString(mimeIndex) else ""
                    val size = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 15_000_000L

                    val isVideo = (mime != null && mime.startsWith("video/")) ||
                            name.endsWith(".mp4", ignoreCase = true) ||
                            name.endsWith(".mov", ignoreCase = true) ||
                            name.endsWith(".mkv", ignoreCase = true)

                    if (isVideo && docId != null) {
                        val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
                        var duration = 15
                        var resolution = "1080x1920"

                        try {
                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(context, fileUri)
                            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)

                            if (durationStr != null) {
                                duration = (durationStr.toLongOrNull() ?: 15000L).toInt() / 1000
                            }
                            if (widthStr != null && heightStr != null) {
                                resolution = "${widthStr}x${heightStr}"
                            }
                            retriever.release()
                        } catch (e: Exception) {
                            Log.w("FolderMediaScanner", "Metadata retrieval error for $name", e)
                        }

                        results.add(
                            ScannedVideo(
                                fileName = name,
                                fileUri = fileUri.toString(),
                                fileSize = size,
                                durationSeconds = duration.coerceAtLeast(3),
                                resolution = resolution
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FolderMediaScanner", "Error scanning folder tree", e)
        }
        return results
    }

    fun generateSampleVideos(folderUri: String, profileId: String): List<VideoEntity> {
        val samples = listOf(
            SampleClip(
                name = "cinematic_drone_shot_01.mp4",
                duration = 24,
                res = "1080x1920",
                size = 18_400_000L,
                tone = "Aesthetic / Cinematic",
                hook = "Wait till the sunset hit the cliff edge...",
                caption = "The best sunset in the archipelago. Captured with neutral density filters at 24fps. Save this spot for your next trip!",
                tags = "#dronephotography #cinematicvideo #reelsviral #travelcreators #sunsetmagic #filmmakerslife #landscape4k #explorepage #voyaged",
                cta = "Would you visit here? Tell me in the comments!"
            ),
            SampleClip(
                name = "tech_desk_setup_tour_2026.mp4",
                duration = 38,
                res = "1080x1920",
                size = 32_100_000L,
                tone = "Tech / Modern",
                hook = "3 hidden desk gadgets that actually increased my productivity by 40%",
                caption = "Leveling up the studio workstation. The wireless macro pad and magnetic cable dock are game changers for daily video editing workflows.",
                tags = "#desksetup #workspacegoals #minimalistsetup #techgadgets #productivityhacks #creativespace #cleandesk #techreels #gadgetgram",
                cta = "Drop a 'LINKS' below and I'll DM you the gear breakdown!"
            ),
            SampleClip(
                name = "quick_broll_tutorial.mp4",
                duration = 18,
                res = "1080x1920",
                size = 14_800_000L,
                tone = "Educational / Creator",
                hook = "Stop shooting flat b-roll. Use this 3-second camera whip trick instead!",
                caption = "Simple lighting & movement will turn ordinary phone clips into commercial-looking b-roll. Bookmark this for your next reel shoot.",
                tags = "#contentcreator #videotips #mobilevideography #brolltips #reelsediting #capcuttutorial #cameratricks #creatorhacks",
                cta = "Try this on your next reel and tag me!"
            ),
            SampleClip(
                name = "behind_the_scenes_brand_shoot.mp4",
                duration = 29,
                res = "1080x1920",
                size = 26_500_000L,
                tone = "Storytelling",
                hook = "What brands don't tell you about 5-figure commercial shoots...",
                caption = "From storyboard breakdown to final color grading. Client requested 3 deliverables in 48 hours, here is how we structured the sprint.",
                tags = "#behindthescenes #productioncompany #videographer #contentmarketing #commercialshoot #creativeagency #reelsinstagram",
                cta = "What's the hardest part of freelance video production for you?"
            )
        )

        val now = System.currentTimeMillis()
        return samples.mapIndexed { index, clip ->
            VideoEntity(
                id = UUID.randomUUID().toString(),
                folderUri = folderUri,
                fileUri = "content://media/external/video/media/${1000 + index}",
                fileName = clip.name,
                fileSizeBytes = clip.size,
                durationSeconds = clip.duration,
                resolution = clip.res,
                detectedAt = now - (index * 3600_000L),
                status = if (index == 0) "CAPTIONS_READY" else if (index == 1) "QUEUED" else "DISCOVERED",
                scheduledAt = if (index == 1) now + 7200_000L else null,
                publishedAt = null,
                caption = clip.caption,
                hook = clip.hook,
                hashtags = clip.tags,
                callToAction = clip.cta,
                tone = clip.tone,
                targetProfileId = profileId,
                uploadProgress = 0f
            )
        }
    }

    private data class SampleClip(
        val name: String,
        val duration: Int,
        val res: String,
        val size: Long,
        val tone: String,
        val hook: String,
        val caption: String,
        val tags: String,
        val cta: String
    )
}
