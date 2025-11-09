package com.example.devicehealth.ui

import androidx.compose.runtime.*
import com.example.devicehealth.data.local.User
import com.example.devicehealth.ui.auth.AuthScreen
import com.example.devicehealth.ui.home.HomeScreen

@Composable
fun AppRoot() {
    var loggedInUser by remember { mutableStateOf<User?>(null) }

    if (loggedInUser == null) {
        AuthScreen(onAuthSuccess = { user -> loggedInUser = user })
    } else {
        HomeScreen(user = loggedInUser!!, onLogout = { loggedInUser = null })
    }
}
