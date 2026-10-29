package com.example

import com.example.data.ai.AIEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun captionGeneration_createsHookAndHashtags() {
        val result = AIEngine.generateCaptionAndHashtags(
            fileName = "desk_setup_workspace.mp4",
            category = "tech",
            tone = "Viral / High-Energy"
        )

        assertNotNull(result.hook)
        assertTrue(result.hook.contains("desk setup workspace"))
        assertTrue(result.hashtags.contains("#reels"))
        assertTrue(result.hashtags.contains("#techreels"))
        assertNotNull(result.cta)
    }

    @Test
    fun commentAnalysis_categorizesIdeasAndQuestions() {
        val ideaComment = AIEngine.analyzeComment("Can you please make a part 2 tutorial on color grading?")
        assertEquals("IDEA", ideaComment.category)
        assertTrue(ideaComment.suggestedAiReply.isNotEmpty())

        val questionComment = AIEngine.analyzeComment("What lens did you use at 0:14?")
        assertEquals("QUESTION", questionComment.category)

        val feedbackComment = AIEngine.analyzeComment("Great visuals but background audio was too loud")
        assertEquals("FEEDBACK", feedbackComment.category)
    }

    @Test
    fun optimalTimeCalculator_returnsFutureEpoch() {
        val nextTime = AIEngine.calculateNextOptimalTime()
        assertTrue(nextTime > System.currentTimeMillis() - 60000L)
    }
}
