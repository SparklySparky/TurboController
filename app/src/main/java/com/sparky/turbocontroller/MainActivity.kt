package com.sparky.turbocontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sparky.turbocontroller.screens.controller.ControllerScreenViewModel
import com.sparky.turbocontroller.ui.theme.TurboControllerTheme

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