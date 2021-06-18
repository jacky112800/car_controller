package com.example.car

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class socket_client : Thread {
    constructor(address: String, port: Int) {
        this.address = address
        this.port = port
    }
    var address: String? = null
    var port: Int = 65536
    var connection = true
    val input_Stream = LinkedBlockingQueue<String>()
    val output_Stream = LinkedBlockingQueue<String>()
    var time_u: TimeUnit = TimeUnit.MILLISECONDS
    var socket = Socket(this.address, this.port)

    override fun run() {
        this.activate()
    }

    fun activate() {
        var data_in = DataInputStream(socket.getInputStream())
        var data_out = DataOutputStream(socket.getOutputStream())
        var recv = thread(start = false) { this.recv(data_in) }
        var send = thread(start = false) { this.send(data_out) }

        recv.start()
        send.start()
        recv.join()
        send.join()
        socket.close()
    }

    fun recv(data_in: DataInputStream) {
        while (this.connection) {
            try {
                this.recv_msg(data_in)
            } catch (e: Exception) {
                this.connection = false
                println(e)
            }
        }
    }

    fun recv_msg(data_in: DataInputStream) {
        var head = data_in.readInt()
        var msg = ByteArray(head)
        data_in.readFully(msg, 0, head)
        var data = msg.decodeToString()
        input_Stream.offer(data, 1000, time_u)
    }

    fun send(data_out: DataOutputStream) {
        while (this.connection) {
            try {
                this.send_msg(data_out)
            } catch (e: Exception) {
                this.connection = false
                println(e)
            }
        }
    }

    fun send_msg(data_out: DataOutputStream) {

        var data = output_Stream.poll(1000, time_u)
        if (data != null) {
            var data_2 = data.encodeToByteArray()
            data_out.writeInt(data_2.size)
            data_out.write(data_2)
        }

    }
}