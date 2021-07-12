package com.example.car

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
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
            println("Socket start")
            socketConnection=socket.isConnected
            activate(socket)
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
        println("Socket receive")
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
        var outputStream = ByteArrayOutputStream()
        outputStream.write(receiveAll(inputStream, head))
        var inputStringByteArray = outputStream.toByteArray()
        inputQueue.offer(inputStringByteArray,1000,time_u)
    }

    private fun receiveAll(data_in: InputStream, buffSize: Int): ByteArray {
        /*
        get buffSize byteArray
        todo: throw EOFException when received = -1
         */

        var buffer = ByteArray(buffSize)
        var total = 0
        while (total < buffSize) {
            var received = data_in.read(buffer, total, buffSize - total)
            total += received
        }
        return buffer
    }

    private fun sendMessage(socket: Socket) {
        println("Socket sendMessage")
        var outputStream = DataOutputStream(socket.getOutputStream())
        try {
            while (this.connection) {
                var outputData = outputQueue.poll(1000, time_u)
                if (outputData!=null) {
                    println(outputData.size.toString()+"socket")
                    println(outputData.decodeToString()+"socket")
                    outputStream.writeInt(outputData.size)
                    outputStream.write(outputData)
                }
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

