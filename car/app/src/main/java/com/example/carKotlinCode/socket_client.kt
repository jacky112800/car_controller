package com.example.carKotlinCode

import org.json.JSONObject
import java.io.*
import java.net.ConnectException
import java.net.InetSocketAddress
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
    var socket = Socket()

//    override fun run() {
//        println("Socket connect")
//        this.socketConnect()
//        println("Socket end")
//    }

    fun isConnection(): Boolean {
        return this.socket.isConnected
    }

    override fun run() {
        println("Socket connect")
        this.socketConnect()
        println("Socket end")
    }

    fun socketConnect() {
        try {
            val inetSocketAddress = InetSocketAddress(MainActivity.ip, MainActivity.port_car)
            socket.connect(inetSocketAddress)
            sleep(100)

            println("Socket start")
            active()
        } catch (e: ConnectException) {
            closeSocket()
            e.printStackTrace()
        } catch (e: IOException) {
            closeSocket()
            e.printStackTrace()
        }
    }

    fun closeSocket() {
        this.connection = false
        socket.close()
    }

    //    @Override
    private fun active() {
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
        val inputStream = socket.getInputStream()
        while (this.connection) {
            try {
                this.receiveMessage(inputStream)
            } catch (e: EOFException) {
                closeSocket()
                e.printStackTrace()
            } catch (e: SocketException) {
                closeSocket()
                e.printStackTrace()
            } catch (e: InterruptedException) {
                closeSocket()
                e.printStackTrace()
            } catch (e: IOException) {
                closeSocket()
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
            throw EOFException()
        }
        val inputStringJSONObject = JSONObject(inputStringJSON)

        inputQueue.offer(inputStringJSONObject, 1000, timeU)

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
            closeSocket()
            e.printStackTrace()
        } catch (e: SocketException) {
            closeSocket()
            e.printStackTrace()
        } catch (e: InterruptedException) {
            closeSocket()
            e.printStackTrace()
        } catch (e: IOException) {
            closeSocket()
            e.printStackTrace()
        }
    }

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
                if (!isConnection()) {
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
                if (!isConnection()) {
                    cancel()
                }
            }
            catchTimer.run()
        }
        pollFrameQueue.start()
    }


}