package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val InstagramLightColorScheme = lightColorScheme(
    primary = InstaBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F1FF),
    onPrimaryContainer = Color(0xFF00376B),
    secondary = Color(0xFF262626),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFEFEF),
    onSecondaryContainer = Color(0xFF262626),
    tertiary = InstaRed,
    onTertiary = Color.White,
    background = InstaWhite,
    onBackground = InstaTextPrimary,
    surface = InstaWhite,
    onSurface = InstaTextPrimary,
    surfaceVariant = InstaSecondaryBg,
    onSurfaceVariant = InstaTextSecondary,
    outline = InstaBorder,
    outlineVariant = InstaDivider
)

private val InstagramDarkColorScheme = darkColorScheme(
    primary = InstaBlue,
    onPrimary = Color.White,
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFA8A8A8),
    outline = Color(0xFF262626)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Pure Instagram light theme as requested!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> InstagramDarkColorScheme
        else -> InstagramLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
