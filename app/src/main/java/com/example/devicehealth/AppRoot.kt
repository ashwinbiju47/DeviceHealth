package com.example.devicehealth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicehealth.ServiceLocator
import com.example.devicehealth.data.local.User
import com.example.devicehealth.ui.auth.AuthScreen
import com.example.devicehealth.ui.auth.AuthViewModel
import com.example.devicehealth.ui.home.HomeScreen
import com.example.devicehealth.ui.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel()

    val auth = FirebaseAuth.getInstance()
    var loggedInUser by remember { mutableStateOf<User?>(null) }
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(300)
        showSplash = false

        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val localUser = viewModel.getLocalUser(firebaseUser.uid)
            loggedInUser = localUser
        }
    }

    AnimatedVisibility(
        visible = showSplash,
        exit = fadeOut()
    ) {
        SplashScreen()
    }


    if (!showSplash) {
        if (loggedInUser == null) {
            AuthScreen(onAuthSuccess = { loggedInUser = it })
        } else {
            var currentScreen by remember { mutableStateOf("home") }

            when (currentScreen) {
                "home" -> {
                    HomeScreen(
                        user = loggedInUser!!,
                        onLogout = {
                            viewModel.logout()
                            loggedInUser = null
                        },
                        onNavigateToSensors = { currentScreen = "sensors" }
                    )
                }
                "sensors" -> {
                    com.example.devicehealth.ui.sensors.SensorDataScreen(
                        onBack = { currentScreen = "home" }
                    )
                }
            }
        }
    }
}