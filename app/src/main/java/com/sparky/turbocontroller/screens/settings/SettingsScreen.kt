package com.sparky.turbocontroller.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sparky.turbocontroller.R
import com.sparky.turbocontroller.screens.controller.ControllerScreen
import com.sparky.turbocontroller.screens.controller.ControllerScreenViewModel
import kotlinx.serialization.Serializable

@Composable
fun SettingsScreen(
    navController: NavHostController,
) {
    val ipAddressInput = remember { mutableStateOf("") }
    val portInput = remember { mutableStateOf("") }
    var checked = remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.backgroundapp),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = ipAddressInput.value,
                onValueChange = { newText: String -> ipAddressInput.value = newText },
                singleLine = true,
                label = { Text("Indirizzo IP") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = portInput.value,
                onValueChange = { newText: String -> portInput.value = newText },
                singleLine = true,
                label = { Text("Porta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Camera",
                fontSize = 18.sp,
                color = Color(255,255,255)
            )

            Switch(checked = checked.value,
                onCheckedChange = {
                    checked.value = it
                })

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate(
                    ControllerScreen(
                        ip = ipAddressInput.value,
                        port = portInput.value,
                        camera = checked.value
                    )
                ) },
                shape = RoundedCornerShape(20),
            ) {
                Text(
                    text = "Conferma",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Serializable
object SettingsScreen