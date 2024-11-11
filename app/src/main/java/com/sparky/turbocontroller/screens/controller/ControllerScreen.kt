package com.sparky.turbocontroller.screens.controller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewState
import com.manalkaff.jetstick.JoyStick
import com.sparky.turbocontroller.R
import kotlinx.serialization.Serializable

@Composable
fun ControllerScreen(
    args: ControllerScreen,
    controllerScreenViewModel: ControllerScreenViewModel
) {
    val ip = args.ip
    val port = args.port
    val camera = args.camera
    val maxRadius = with(LocalDensity.current) { (150.dp / 2).toPx() }

    controllerScreenViewModel.updateRadius(maxRadius)

    controllerScreenViewModel.createSocket(ip, port)

    controllerScreenViewModel.startRepeatingJob(100)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.backgroundapp),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JoyStick(
                    size = 150.dp,
                    dotSize = 50.dp,
                ){  x: Float, y: Float ->
                    controllerScreenViewModel.updateJoystickData(JoystickId.LEFT, x.coerceIn(-maxRadius, maxRadius), y.coerceIn(-maxRadius, maxRadius))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            controllerScreenViewModel.sendForceStop()
                        }
                    ) {
                        Text("Force Stop")
                    }
                    if(camera == true){
                        WebView(
                            state = rememberWebViewState("http://$ip/camera_stream"),
                            modifier = Modifier
                                .width(450.dp)
                                .aspectRatio(4f / 3f)
                                .padding(10.dp, 0.dp)
                                .clipToBounds()
                        )
                    }
                }
                JoyStick(
                    size = 150.dp,
                    dotSize = 50.dp,
                ){  x: Float, y: Float ->
                    controllerScreenViewModel.updateJoystickData(JoystickId.RIGHT, x.coerceIn(-maxRadius, maxRadius), y.coerceIn(-maxRadius, maxRadius))
                }
            }
        }
    }
}

@Serializable
data class ControllerScreen(
    val ip: String,
    val port: String,
    val camera: Boolean
)
