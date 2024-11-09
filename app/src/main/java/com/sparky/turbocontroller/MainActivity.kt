package com.sparky.turbocontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sparky.turbocontroller.screens.controller.ControllerScreenViewModel
import com.sparky.turbocontroller.ui.theme.TurboControllerTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private val controllerScreenViewModel = ControllerScreenViewModel();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            TurboControllerTheme {
                TurboControllerApp(controllerScreenViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controllerScreenViewModel.stopRepeatingJob()
        controllerScreenViewModel.closeSocket()
    }
}