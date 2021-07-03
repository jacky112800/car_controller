package com.example.car

import java.io.EOFException
import java.io.InputStream
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class socket_client : Thread() {
    companion object {
        var get_json: String = ""
        val inputQueue = LinkedBlockingQueue<ByteArray>()
        val outputQueue = LinkedBlockingQueue<ByteArray>()
    }


    var connection = true
    var time_u: TimeUnit = TimeUnit.MILLISECONDS
    var socketConnection = false


    override fun run() {
        println("Socket connect")
        this.socketConnect()
        println("Socket end")
    }

    fun socketConnect() {
        try {
            var socket = Socket(MainActivity.ip, MainActivity.port_car)
            sleep(100)
            socketConnection = socket.isConnected
            if (!socket.isConnected) {
                closeSocket(socket)
            }
            if (socket.isConnected) {
                activate(socket)
            }
        } catch (e: ConnectException) {
            this.connection = false
            e.printStackTrace()
        }
    }

    fun activate(socket: Socket) {

        var recv = thread(start = false) { this.receive(socket) }
        var send = thread(start = false) { this.sendMessage(socket) }

        sleep(100)

        recv.start()
        send.start()
        recv.join()
        send.join()
        socket.close()


    }

    private fun receive(socket: Socket) {
        var inputStream = socket.getInputStream()
        while (this.connection) {
            try {
                this.receiveMessage(inputStream)
            } catch (e: EOFException) {
                this.connection = false
                socket.close()
                e.printStackTrace()
            } catch (e: SocketException) {
                this.connection = false
                socket.close()
                e.printStackTrace()
            }
        }
    }

    private fun receiveMessage(inputStream: InputStream) {
        /*
        get message and put to input_stream
        todo: decode String to JsonString
         */
        var head = ByteBuffer.wrap(receiveAll(inputStream, 4)).int
        var message = receiveAll(inputStream, head)
        inputQueue.offer(message, 1000, time_u)
    }

    private fun receiveAll(inputStream: InputStream, buffSize: Int): ByteArray? {
        /*
        get buffSize byteArray
        todo: throw EOFException when received = -1
         */

        var buffer = ByteArray(buffSize)
        var total = 0
        while (total < buffSize) {
            var received = inputStream.read(buffer, total, buffSize - total)
            total += received
        }
        return buffer
    }

    private fun sendMessage(socket: Socket) {
        var outputStream = socket.getOutputStream()
        try {
            while (this.connection) {
                var outputData = outputQueue.poll(1000, time_u)
                var outputDataSize = ByteBuffer.allocate(4).putInt(outputData.size).array()
                outputStream.write(outputDataSize)
                outputStream.write(outputData)
            }
        } catch (e: EOFException) {
            closeSocket(socket)
            e.printStackTrace()
        } catch (e: SocketException) {
            closeSocket(socket)
            e.printStackTrace()
        }
    }

    fun closeSocket(socket: Socket) {
        this.connection = false
        socket.close()
    }

}

