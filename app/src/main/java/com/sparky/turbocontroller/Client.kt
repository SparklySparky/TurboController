package com.sparky.turbocontroller

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

interface Client {
    suspend fun send(message: String)
    suspend fun close()
}

class UDPClient(private val ipAddress: String, private val port: Int): Client {
    private val udpSocket = DatagramSocket()

    override suspend fun send(message: String) {
        val buffer = message.toByteArray()
        val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(ipAddress), port)

        withContext(Dispatchers.IO) {
            udpSocket.send(packet)
        }
    }

    override suspend fun close() {
        withContext(Dispatchers.IO) { udpSocket.close() }
    }
}

class TCPClient(ipAddress: String, port: Int) : Client {
    private val tcpSocket = Socket()

    init {
        tcpSocket.soTimeout = 1000
        tcpSocket.connect(InetSocketAddress(ipAddress, port))
    }


    override suspend fun send(message: String) {
        withContext(Dispatchers.IO) {
            tcpSocket.outputStream.write(message.toByteArray())
        }
    }

    override suspend fun close() {
        withContext(Dispatchers.IO) {
            tcpSocket.close()
        }
    }
}