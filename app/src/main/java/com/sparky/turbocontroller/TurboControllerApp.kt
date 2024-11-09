package com.sparky.turbocontroller

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sparky.turbocontroller.screens.controller.ControllerScreen
import com.sparky.turbocontroller.screens.controller.ControllerScreenViewModel
import com.sparky.turbocontroller.screens.settings.SettingsScreen

@Composable
fun TurboControllerApp(
    controllerScreenViewModel: ControllerScreenViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SettingsScreen
    ) {
        composable<SettingsScreen> {
            controllerScreenViewModel.stopRepeatingJob()
            controllerScreenViewModel.closeSocket()
            SettingsScreen(navController)
        }
        composable<ControllerScreen> {
            val args = it.toRoute<ControllerScreen>()
            ControllerScreen(args, controllerScreenViewModel)
        }
    }
}