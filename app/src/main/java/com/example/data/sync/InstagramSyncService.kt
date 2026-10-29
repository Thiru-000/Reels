package com.example.data.sync

import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class InstagramProfileSyncResult(
    val success: Boolean,
    val username: String,
    val fullName: String,
    val bio: String,
    val followersCount: Int,
    val followingCount: Int,
    val totalPosts: Int,
    val avatarUrl: String,
    val isVerified: Boolean,
    val errorMessage: String? = null
)

class InstagramSyncService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun syncProfileFromInstagram(username: String): InstagramProfileSyncResult = withContext(Dispatchers.IO) {
        val cleanUser = username.removePrefix("@").trim()
        if (cleanUser.isBlank()) {
            return@withContext InstagramProfileSyncResult(
                success = false,
                username = "",
                fullName = "",
                bio = "",
                followersCount = 0,
                followingCount = 0,
                totalPosts = 0,
                avatarUrl = "",
                isVerified = false,
                errorMessage = "Invalid username"
            )
        }

        // Try extracting cookies from WebView if user logged in via Web
        val cookie = try {
            CookieManager.getInstance().getCookie("https://www.instagram.com") ?: ""
        } catch (e: Exception) {
            ""
        }

        // Method 1: Web profile info endpoint
        try {
            val url = "https://www.instagram.com/api/v1/users/web_profile_info/?username=$cleanUser"
            val requestBuilder = Request.Builder()
                .url(url)
                .header("User-Agent", "Instagram 219.0.0.12.117 Android")
                .header("x-ig-app-id", "936619743392459")
                .header("Accept", "*/*")

            if (cookie.isNotBlank()) {
                requestBuilder.header("Cookie", cookie)
            }

            val response = client.newCall(requestBuilder.build()).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val data = json.optJSONObject("data")
                    val user = data?.optJSONObject("user")
                    if (user != null) {
                        val fullName = user.optString("full_name", cleanUser)
                        val bio = user.optString("biography", "")
                        val followers = user.optJSONObject("edge_followed_by")?.optInt("count", 0) ?: 0
                        val following = user.optJSONObject("edge_follow")?.optInt("count", 0) ?: 0
                        val posts = user.optJSONObject("edge_owner_to_timeline_media")?.optInt("count", 0) ?: 0
                        val avatar = user.optString("profile_pic_url_hd").ifBlank {
                            user.optString("profile_pic_url")
                        }
                        val isVerified = user.optBoolean("is_verified", false)

                        return@withContext InstagramProfileSyncResult(
                            success = true,
                            username = cleanUser,
                            fullName = if (fullName.isBlank()) cleanUser else fullName,
                            bio = bio,
                            followersCount = followers,
                            followingCount = following,
                            totalPosts = posts,
                            avatarUrl = if (avatar.isBlank()) defaultAvatar(cleanUser) else avatar,
                            isVerified = isVerified
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("InstagramSyncService", "Endpoint 1 failed: ${e.message}")
        }

        // Method 2: Alternate JSON endpoint
        try {
            val url2 = "https://www.instagram.com/$cleanUser/?__a=1&__d=dis"
            val request = Request.Builder()
                .url(url2)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36")
                .apply {
                    if (cookie.isNotBlank()) header("Cookie", cookie)
                }
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val graphql = json.optJSONObject("graphql")
                    val user = graphql?.optJSONObject("user")
                    if (user != null) {
                        val fullName = user.optString("full_name", cleanUser)
                        val bio = user.optString("biography", "")
                        val followers = user.optJSONObject("edge_followed_by")?.optInt("count", 0) ?: 0
                        val following = user.optJSONObject("edge_follow")?.optInt("count", 0) ?: 0
                        val posts = user.optJSONObject("edge_owner_to_timeline_media")?.optInt("count", 0) ?: 0
                        val avatar = user.optString("profile_pic_url_hd").ifBlank {
                            user.optString("profile_pic_url")
                        }
                        val isVerified = user.optBoolean("is_verified", false)

                        return@withContext InstagramProfileSyncResult(
                            success = true,
                            username = cleanUser,
                            fullName = if (fullName.isBlank()) cleanUser else fullName,
                            bio = bio,
                            followersCount = followers,
                            followingCount = following,
                            totalPosts = posts,
                            avatarUrl = if (avatar.isBlank()) defaultAvatar(cleanUser) else avatar,
                            isVerified = isVerified
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("InstagramSyncService", "Endpoint 2 failed: ${e.message}")
        }

        // If direct HTTP scrape was blocked or rate limited by Instagram's login-wall:
        // Return a structured sync result using the user handle and fallback avatar
        // so the user can easily review or confirm their stats.
        return@withContext InstagramProfileSyncResult(
            success = false,
            username = cleanUser,
            fullName = cleanUser.replace("_", " ").replace(".", " ").capitalize(),
            bio = "🎬 Automatic Reels Publisher & Content Lab\n⚡ Peak engagement auto-scheduler powered by AI",
            followersCount = 0,
            followingCount = 0,
            totalPosts = 0,
            avatarUrl = defaultAvatar(cleanUser),
            isVerified = false,
            errorMessage = "Instagram requires session confirmation. You can enter or confirm your account numbers below."
        )
    }

    private fun defaultAvatar(username: String): String {
        return "https://ui-avatars.com/api/?name=${username}&background=E1306C&color=fff&size=200&bold=true"
    }

    private fun String.capitalize(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
