package com.example.car

import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

class client_th_string : Thread() {
    companion object {
        var get_cmd: ByteArray = byteArrayOf()
        var get_data: ByteArray = byteArrayOf()
    }

    var send_json = JSONObject()

    val address = MainActivity.ip
    val port = MainActivity.port_car.toInt()
    val connection: Socket = Socket(address, port)
    var connected: Boolean = true
    var send_check = false
    var data_in: DataInputStream = DataInputStream(connection.getInputStream())
    var data_out: DataOutputStream = DataOutputStream(connection.getOutputStream())
    val reader: Scanner = Scanner(connection.getInputStream())

    override fun run() {

        while (connected) {
            receive_data()
        }

    }

    fun receive_data() {
        var img_bt: ByteArray = ByteArray(0)
        var lenght: Int
        lenght = data_in.readInt()
        img_bt = ByteArray(lenght)

        if (lenght > 0) {
            data_in.readFully(img_bt, 0, img_bt.size)
            print(img_bt.decodeToString())
        }

        if (img_bt[img_bt.size - 1] != null) {
            connected = false
            println("ok")

        }
        get_data = img_bt
    }

    fun send_cmd(cmd: String) {
        send_json.put("CMD", cmd)
        val j_to_b = send_json.toString().toByteArray()
        send_data(j_to_b)
    }

    fun send_move(cmd: String, L: Int, R: Int) {
        send_json.put("CMD", cmd)
        send_json.put("MOVE_L", L)
        send_json.put("MOVE_R", R)
        println(this.send_json)
        val j_to_b = send_json.toString().toByteArray()
        send_data(j_to_b)
    }

    fun send_data(send_cmd: ByteArray) {
        thread {
            data_out.writeInt(send_cmd.size)
            data_out.write(send_cmd)
        }
    }

    fun close() {
        reader.close()
        connection.close()
    }
}