package com.example.data.ai

import com.example.data.local.OptimalSlotEntity
import java.util.Calendar
import java.util.UUID

object AIEngine {

    data class CaptionResult(
        val hook: String,
        val caption: String,
        val hashtags: String,
        val cta: String
    )

    fun generateCaptionAndHashtags(
        fileName: String,
        category: String,
        tone: String
    ): CaptionResult {
        val cleanName = fileName
            .replace(".mp4", "", ignoreCase = true)
            .replace(".mov", "", ignoreCase = true)
            .replace("_", " ")
            .replace("-", " ")
            .trim()

        val hook = when (tone) {
            "Viral / High-Energy" -> "🔥 Don't scroll past this if you want to master $cleanName in 2026!"
            "Educational / Tech" -> "💡 The exact step-by-step breakdown of $cleanName that pros keep secret."
            "Storytelling / Aesthetic" -> "✨ Sometimes the most memorable moments begin with $cleanName..."
            "Humor / Relatable" -> "😂 Nobody talks about how chaotic $cleanName actually gets behind the camera."
            else -> "🚀 Everything you need to know about $cleanName right now."
        }

        val body = when (tone) {
            "Viral / High-Energy" ->
                "Tested this strategy over the last 30 days and the results are unmatched. Notice how seamless the transition is? Make sure your sound is ON and pay close attention to the second half!"
            "Educational / Tech" ->
                "Three key fundamentals to elevate your game:\n1. Timing and pacing are 80% of retention\n2. Clean framing without visual clutter\n3. High-contrast color grading that pops on mobile displays.\nSave this for your next project."
            "Storytelling / Aesthetic" ->
                "Captured during the golden hour breeze. The light hit just right and the atmosphere was pure magic. Filmed in 4K 60fps and graded with custom teal & amber LUTs."
            "Humor / Relatable" ->
                "Expectation: Smooth, effortless cinematic genius.\nReality: 47 failed takes, coffee spilled everywhere, and my battery died at 1%. Send this to someone who relates!"
            else ->
                "Breaking down this creative workflow. Consistency and clean execution beat complex equipment every single time. Hope this sparks some inspiration for your next creation!"
        }

        val cta = when (tone) {
            "Viral / High-Energy" -> "👉 Double tap if you felt that and share with your creative circle!"
            "Educational / Tech" -> "📌 Save this reel so you don't forget it, and drop your questions below!"
            "Storytelling / Aesthetic" -> "💬 Which part was your favorite? Let me know in the comments."
            "Humor / Relatable" -> "👇 Tag a friend who needs to see this right now!"
            else -> "🚀 Follow for daily creator insights and comment below your thoughts!"
        }

        val baseTags = "#reels #reelsinstagram #instareels #viralvideos #explorepage #trendingreels #fyp"
        val nicheTags = when (category.lowercase()) {
            "tech", "technology" -> "#techreels #gadgets #desksetup #softwaredeveloper #ai #techtrends #creatorlife"
            "travel", "lifestyle" -> "#travelgram #wanderlust #cinematicvideo #earthfocus #aesthetic #roamtheplanet #traveler"
            "fitness", "health" -> "#fitnessmotivation #gymreels #workouttips #healthylifestyle #trainingday #gains"
            "art", "design" -> "#graphicdesign #digitalart #motiongraphics #creatives #artistsoninstagram #designinspo"
            else -> "#contentcreator #videography #filmmaking #reelsgrowth #creatoreconomy #socialmediatips"
        }

        val customKeywordTags = cleanName.split(" ")
            .filter { it.length > 3 }
            .take(3)
            .joinToString(" ") { "#${it.lowercase()}" }

        val allHashtags = "$baseTags $nicheTags $customKeywordTags".trim()

        return CaptionResult(
            hook = hook,
            caption = body,
            hashtags = allHashtags,
            cta = cta
        )
    }

    data class CommentAnalysis(
        val category: String,
        val sentiment: String,
        val suggestedAiReply: String
    )

    fun analyzeComment(text: String): CommentAnalysis {
        val lower = text.lowercase()
        return when {
            lower.contains("please make") || lower.contains("can you") || lower.contains("next video") ||
                    lower.contains("part 2") || lower.contains("tutorial on") || lower.contains("show us") -> {
                CommentAnalysis(
                    category = "IDEA",
                    sentiment = "POSITIVE",
                    suggestedAiReply = "Brilliant suggestion! Just added it to our filming queue for next week. 🎬🔥"
                )
            }
            lower.contains("how") || lower.contains("what") || lower.contains("where") ||
                    lower.contains("why") || lower.contains("?") -> {
                CommentAnalysis(
                    category = "QUESTION",
                    sentiment = "NEUTRAL",
                    suggestedAiReply = "Thanks for asking! I'll break that down in detail in the next reel. Stay tuned! 🙌"
                )
            }
            lower.contains("too fast") || lower.contains("too quiet") || lower.contains("too loud") ||
                    lower.contains("audio") || lower.contains("sound") || lower.contains("blurry") ||
                    lower.contains("could be better") -> {
                CommentAnalysis(
                    category = "FEEDBACK",
                    sentiment = "CONSTRUCTIVE",
                    suggestedAiReply = "Appreciate the honest feedback! Will calibrate the audio mix and pacing for upcoming drops. 💯"
                )
            }
            else -> {
                CommentAnalysis(
                    category = "PRAISE",
                    sentiment = "POSITIVE",
                    suggestedAiReply = "Thank you so much! Really appreciate the love and support! ❤️🚀"
                )
            }
        }
    }

    fun calculateNextOptimalTime(): Long {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        
        // Optimal engagement windows: 9:15 AM, 12:45 PM, 18:30 (6:30 PM), 20:45 (8:45 PM)
        val optimalHours = listOf(9, 12, 18, 20)
        val optimalMins = listOf(15, 45, 30, 45)

        for (i in optimalHours.indices) {
            val h = optimalHours[i]
            val m = optimalMins[i]
            if (currentHour < h || (currentHour == h && cal.get(Calendar.MINUTE) < m)) {
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, m)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return cal.timeInMillis
            }
        }

        // Otherwise schedule for tomorrow at 9:15 AM
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 9)
        cal.set(Calendar.MINUTE, 15)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getSampleOptimalSlots(profileId: String): List<OptimalSlotEntity> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val currentDay = days[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]
        
        return listOf(
            OptimalSlotEntity(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                dayOfWeek = currentDay,
                timeLabel = "9:15 AM",
                hourOfDay = 9,
                minuteOfHour = 15,
                engagementScore = 92,
                audienceActivePercent = 84,
                isRecommended = true
            ),
            OptimalSlotEntity(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                dayOfWeek = currentDay,
                timeLabel = "12:45 PM",
                hourOfDay = 12,
                minuteOfHour = 45,
                engagementScore = 88,
                audienceActivePercent = 79,
                isRecommended = false
            ),
            OptimalSlotEntity(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                dayOfWeek = currentDay,
                timeLabel = "6:30 PM",
                hourOfDay = 18,
                minuteOfHour = 30,
                engagementScore = 98,
                audienceActivePercent = 95,
                isRecommended = true
            ),
            OptimalSlotEntity(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                dayOfWeek = currentDay,
                timeLabel = "8:45 PM",
                hourOfDay = 20,
                minuteOfHour = 45,
                engagementScore = 94,
                audienceActivePercent = 91,
                isRecommended = true
            )
        )
    }
}
