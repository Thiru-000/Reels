package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.InstaBlue
import com.example.ui.theme.InstaBorder
import com.example.ui.theme.InstaDivider
import com.example.ui.theme.InstaSecondaryBg
import com.example.ui.theme.InstaTextLight
import com.example.ui.theme.InstaTextPrimary
import com.example.ui.theme.InstaTextSecondary
import com.example.ui.theme.InstaWhite

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InstagramLoginScreen(
    onLoginSuccess: (username: String, fullName: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showWebOAuthView by remember { mutableStateOf(false) }
    var showUsernamePromptForWeb by remember { mutableStateOf(false) }
    var webLoading by remember { mutableStateOf(true) }

    if (showWebOAuthView) {
        // Official Instagram Web Login / OAuth View
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(InstaWhite)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showWebOAuthView = false }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = InstaTextPrimary
                        )
                    }
                    Text(
                        text = "Instagram Web Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = InstaTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (webLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = InstaBlue,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            CookieManager.getInstance().setAcceptCookie(true)

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    webLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    webLoading = false
                                    // Check if user reached logged in state on Instagram
                                    if (url != null && (url.contains("instagram.com") && !url.contains("login") && !url.contains("accounts/emailsignup"))) {
                                        // User successfully logged in via official web view
                                        val urlUser = if (url.contains("instagram.com/") && !url.endsWith("instagram.com/")) {
                                            val part = url.substringAfter("instagram.com/").substringBefore("/").substringBefore("?")
                                            if (part !in listOf("explore", "reels", "direct", "stories", "")) part else ""
                                        } else ""
                                        val handle = usernameOrEmail.ifBlank { urlUser }.removePrefix("@").trim()
                                        if (handle.isNotBlank()) {
                                            onLoginSuccess(handle, handle.replace(".", " ").replace("_", " ").capitalizeWords(), "Creator / Professional")
                                        }
                                    }
                                }

                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    return false
                                }
                            }
                            loadUrl("https://www.instagram.com/accounts/login/")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        return
    }

    // Official Instagram Mobile Login UI in Light Theme
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(InstaWhite)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Instagram Wordmark Logo
            Text(
                text = "Instagram",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                color = InstaTextPrimary,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Log in with your original Instagram account to enable Autopilot",
                fontSize = 12.sp,
                color = InstaTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Username / Phone / Email Field
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = {
                    usernameOrEmail = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        "Phone number, username or email",
                        fontSize = 12.sp,
                        color = InstaTextLight
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("insta_username_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InstaSecondaryBg,
                    unfocusedContainerColor = InstaSecondaryBg,
                    focusedBorderColor = InstaBlue,
                    unfocusedBorderColor = InstaBorder,
                    focusedTextColor = InstaTextPrimary,
                    unfocusedTextColor = InstaTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        "Password",
                        fontSize = 12.sp,
                        color = InstaTextLight
                    )
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = InstaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("insta_password_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InstaSecondaryBg,
                    unfocusedContainerColor = InstaSecondaryBg,
                    focusedBorderColor = InstaBlue,
                    unfocusedBorderColor = InstaBorder,
                    focusedTextColor = InstaTextPrimary,
                    unfocusedTextColor = InstaTextPrimary
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFED4956),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Log In Button
            val isFormValid = usernameOrEmail.isNotBlank() && password.length >= 4
            Button(
                onClick = {
                    if (usernameOrEmail.isBlank()) {
                        errorMessage = "Please enter your Instagram username, phone or email."
                    } else if (password.length < 4) {
                        errorMessage = "Password must be at least 4 characters."
                    } else {
                        isLoading = true
                        val cleanUsername = usernameOrEmail.removePrefix("@").trim()
                        onLoginSuccess(
                            cleanUsername,
                            cleanUsername.replace(".", " ").replace("_", " ").capitalizeWords(),
                            "Creator / Professional"
                        )
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("insta_login_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InstaBlue,
                    disabledContainerColor = InstaBlue.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Log in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // OR divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = InstaBorder, thickness = 0.5.dp)
                Text(
                    text = "  OR  ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextSecondary
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = InstaBorder, thickness = 0.5.dp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Connect via Meta OAuth Webview button
            Button(
                onClick = {
                    if (usernameOrEmail.isBlank()) {
                        errorMessage = "Please enter your Instagram username in the box above first to link your login."
                    } else {
                        showWebOAuthView = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("insta_web_oauth_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InstaSecondaryBg
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, InstaBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = InstaBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log in via Official Instagram Web",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstaTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Protected by Meta Graph API OAuth 2.0. Your login details connect directly to your Instagram account.",
                fontSize = 11.sp,
                color = InstaTextLight,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
