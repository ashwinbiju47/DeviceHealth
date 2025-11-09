package com.example.devicehealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.devicehealth.ui.AppRoot
import com.example.devicehealth.ui.theme.DeviceHealthTheme   // ✅ import your custom theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceHealthTheme(    // ✅ apply your green eco theme here
                darkTheme = true,   // or true / isSystemInDarkTheme()
                dynamicColor = false // disable wallpaper colors
            ) {
                AppRoot()
            }
        }
    }
}
