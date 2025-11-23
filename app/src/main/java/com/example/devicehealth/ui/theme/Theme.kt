package com.example.devicehealth.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val MatrixDarkColorScheme = darkColorScheme(
    primary = MatrixGreen,
    secondary = MatrixGreenSecondary,
    tertiary = MatrixGreen,
    background = MatrixDarkBackground,
    surface = MatrixSurface,
    onPrimary = MatrixOnPrimary,
    onSecondary = MatrixOnPrimary,
    onBackground = MatrixOnBackground,
    onSurface = MatrixOnBackground,
    surfaceVariant = MatrixSurface, // Ensure variants also use the dark surface
    onSurfaceVariant = MatrixGreen, // Ensure text on variants is green
    error = MatrixError
)

@Composable
fun DeviceHealthTheme(
    darkTheme: Boolean = true,     // ✅ Always AMOLED dark
    dynamicColor: Boolean = false, // ✅ Disable system colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> MatrixDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
