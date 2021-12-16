package com.example.carKotlinCode

import org.json.JSONObject
import java.io.*
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class socket_client : Thread() {
    companion object {
        val inputQueue = LinkedBlockingQueue<JSONObject>()
        val outputQueue = LinkedBlockingQueue<String>()
        val frameBufferQueue = LinkedBlockingQueue<JSONObject>()
        var inputCmdString = ""
        var inputFrameString = ""
        var configString = ""
    }

    var connection = true
    var timeU: TimeUnit = TimeUnit.MILLISECONDS
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
//            socket.soTimeout = 30000
            println("Socket start")
            socketConnection = socket.isConnected
            activate(socket)
        } catch (e: ConnectException) {
            this.connection = false
            e.printStackTrace()
        } catch (e: IOException) {
            this.connection = false
            e.printStackTrace()
        }
    }

    fun activate(socket: Socket) {

        val recv = thread(start = false) { this.receive(socket) }
        val send = thread(start = false) { this.sendMessage(socket) }

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
        var inputStringJSON = outputStream.toByteArray().decodeToString()
        if (inputStringJSON.toString() == "-1") {
            println("close")
        }
        val inputStringJSONObject = JSONObject(inputStringJSON)
        if (inputStringJSONObject.getString("CMD") == "FRAME") {
            frameBufferQueue.offer(inputStringJSONObject, 1000, timeU)
        } else {
            inputQueue.offer(inputStringJSONObject, 1000, timeU)
        }
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
                if (!outputQueue.isNullOrEmpty()) {
                    val outputData = outputQueue.poll(1000, timeU).encodeToByteArray()
                    if (outputData.isNotEmpty()) {
                        println(outputData.size.toString() + "socket")
                        println(outputData.decodeToString() + "socket")
                        outputStream.writeInt(outputData.size)
                        outputStream.write(outputData)
                    }
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

    //------------------建置中未完成------------------
    fun pollJSONQueueToInputCMDString() {
        val pollCmdQueue = thread(start = false) {
            val catchTimer = Timer("getJSONQueueToString").schedule(0, 10) {
                if (!inputQueue.isNullOrEmpty()) {
                    val inputJSONObject = inputQueue.poll(1000, timeU)
                    if (inputJSONObject != null) {
                        inputCmdString = inputJSONObject.toString()
                        println("catch:$inputCmdString")
                    }
                }
                if (!socketConnection) {
                    cancel()
                }
            }
            catchTimer.run()
        }
        pollCmdQueue.start()
    }

    fun pollFrameQueueToInputCMDString() {
        val pollFrameQueue = thread(start = false) {
            val catchTimer = Timer("getJSONQueueToString").schedule(0, 10) {
                if (!frameBufferQueue.isNullOrEmpty()) {
                    val inputJSONObject = frameBufferQueue.poll(1000, timeU)
                    if (inputJSONObject != null) {
                        inputFrameString = inputJSONObject.toString()
                        println("catch:$inputFrameString")
                    }
                }
                if (!socketConnection) {
                    cancel()
                }
            }
            catchTimer.run()
        }
        pollFrameQueue.start()
    }
//------------------建置中未完成------------------

    fun closeSocket(socket: Socket) {
        this.connection = false
        socket.close()
    }

}

