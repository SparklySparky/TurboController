package com.sparky.turbocontroller.screens.controller

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.viewModelScope
import com.sparky.turbocontroller.Cobs
import com.sparky.turbocontroller.UDPClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.text.toInt

enum class JoystickId {
    LEFT,
    RIGHT
}

data class JoystickInfo(
    val x: Float,
    val y: Float
)

fun Float.roundTo(places: Int): Float {
    if (places <= 0) { throw IllegalArgumentException("The number of places must be positive") }

    return this.times(10f.pow(places)).roundToInt() / (10f.pow(places))
}

class ControllerScreenViewModel: ViewModel() {
    private val _maxRadius = mutableFloatStateOf(0f)
    val maxRadius: MutableState<Float> get() = _maxRadius

    private val _leftJoystickInfo = mutableStateOf(JoystickInfo(0f, 0f))
    val leftJoystickInfo: MutableState<JoystickInfo> get() = _leftJoystickInfo

    private val _rightJoystickInfo = mutableStateOf(JoystickInfo(0f, 0f))
    val rightJoystickInfo: MutableState<JoystickInfo> get() = _rightJoystickInfo

    private val _UDPSocket = mutableStateOf(null as UDPClient?)
    val UDPSocket: MutableState<UDPClient?> get() = _UDPSocket

    private val _messageOld = mutableStateOf("{p: 0, th: 0, tu: 0}")
    val messageOld: MutableState<String> get() = _messageOld

    private var job: Job? = null

    fun updateRadius(radius: Float){
        _maxRadius.floatValue = radius
    }

    fun createSocket(ip: String, port: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _UDPSocket.value = UDPClient(ip, port.toInt())
                Log.d("ControllerViewModel", "UDP Socket created successfully.")
            } catch (e: Exception) {
                Log.e("ControllerViewModel", "Error creating UDP socket: ${e.localizedMessage}")
            }
        }
    }

    fun updateJoystickData(id: JoystickId, x: Float, y: Float)
    {
        viewModelScope.launch {
            val newX = (((x - (-maxRadius.value)) * 2) / (maxRadius.value * 2)) - 1
            val newY = (((y - (-maxRadius.value)) * 2) / (maxRadius.value * 2)) - 1

            when (id) {
                JoystickId.LEFT -> {
                    _leftJoystickInfo.value = JoystickInfo(newX, newY)
                }

                JoystickId.RIGHT -> {
                    _rightJoystickInfo.value = JoystickInfo(newX, newY)
                }
            }
        }
    }

    fun closeSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _UDPSocket.value?.close()
                Log.d("ControllerViewModel", "UDP Socket closed successfully.")
            } catch (e: Exception) {
                Log.e("ControllerViewModel", "Error closing socket: ${e.localizedMessage}")
            }
        }
    }

    fun startRepeatingJob(timeInterval: Long) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val messageOut = JSONObject()
                val messageRaw = buildJsonObject {
                    put("p", Json.parseToJsonElement(hypot(leftJoystickInfo.value.x, leftJoystickInfo.value.y).roundTo(2).toString()))
                    put("th", Json.parseToJsonElement((-atan2(leftJoystickInfo.value.y, leftJoystickInfo.value.x).roundTo(2)).toString()))
                    put("tu", Json.parseToJsonElement(rightJoystickInfo.value.x.roundTo(2).toString()))
                }

                val oldMessage = JSONObject(messageOld.value)

                if (messageRaw["p"]?.toString() != oldMessage.optString("p")) {
                    messageOut.put("p", messageRaw["p"])
                }
                if (messageRaw["th"]?.toString() != oldMessage.optString("th")) {
                    messageOut.put("th", messageRaw["th"])
                }
                if (messageRaw["tu"]?.toString() != oldMessage.optString("tu")) {
                    messageOut.put("tu", messageRaw["tu"])
                }

                val messageBytes = messageOut.toString().toCharArray()
                val encodedMessageBytes = CharArray(Cobs.encodeDstBufMaxLen(messageBytes.size))
                Cobs.encode(encodedMessageBytes, encodedMessageBytes.size, messageBytes, messageBytes.size)
                val encodedMessage = encodedMessageBytes.toString()

                UDPSocket.value?.send(encodedMessage)

                _messageOld.value = messageRaw.toString()

                delay(timeInterval)
            }
        }
    }

    fun sendForceStop()
    {
        viewModelScope.launch(Dispatchers.IO) {
            val message = buildJsonObject {
                put("stop", Json.parseToJsonElement("true"))
            }

            val messageBytes = message.toString().toCharArray()
            val encodedMessageBytes = CharArray(Cobs.encodeDstBufMaxLen(messageBytes.size))
            Cobs.encode(encodedMessageBytes, encodedMessageBytes.size, messageBytes, messageBytes.size)
            val encodedMessage = encodedMessageBytes.toString()

            UDPSocket.value?.send(encodedMessage)
        }
    }

    fun stopRepeatingJob() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                job?.cancelAndJoin()
                job = null
                Log.d("ControllerViewModel", "Stopped job successfully.")
            } catch (e: Exception) {
                Log.e("ControllerViewModel", "Error stopping job: ${e.localizedMessage}")
            }
        }
    }
}