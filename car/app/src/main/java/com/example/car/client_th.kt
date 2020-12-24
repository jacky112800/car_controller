package com.example.car

import java.io.DataInputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.max

class client_th : Thread() {
    var get_data: ByteArray = byteArrayOf()
    override fun run() {
        val address = "172.25.128.1"
        val port = 5050
        val client = client_controller(address, port)
        client.run()
        var get_data: ByteArray = client.img_bt
    }
}


class client_controller(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true
    var data_in: DataInputStream = DataInputStream(connection.getInputStream())

    init {
        println("Connected to server at $address on port $port")
    }

    private val reader: Scanner = Scanner(connection.getInputStream())
    private val writer: OutputStream = connection.getOutputStream()
    var img_bt = byteArrayOf()
    fun run() {
        val length: Int = 2764816
        var img_bt = ByteArray(length)
        while (connected) {
            data_in.readFully(img_bt, 0, img_bt.size)
            if (img_bt[length] != null) {
                connected = false
                reader.close()
                connection.close()
            }

        }

    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun read() {
        while (connected)
            println(reader.nextLine())
    }
}




