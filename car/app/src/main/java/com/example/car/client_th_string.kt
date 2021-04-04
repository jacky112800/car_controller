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
    }

    var send_json = JSONObject()

    val address = MainActivity.ip
    val port = 5050
    val connection: Socket = Socket(address, port)
    var connected: Boolean = true
    var data_in: DataInputStream = DataInputStream(connection.getInputStream())
    var data_out: DataOutputStream = DataOutputStream(connection.getOutputStream())
    val reader: Scanner = Scanner(connection.getInputStream())

    override fun run() {

        if (connected) {
            data_out.write(tojson("ready to receive"))
        }
        thread {
            while (connected) {
                receive_data()
            }
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
            reader.close()
            connection.close()
        }
        get_cmd = img_bt
    }

    fun send_data(send_cmd: ByteArray) {
        data_out.write(send_cmd)
    }

    fun tojson(frame: String): ByteArray {
        send_json.put("FRAME", frame)
        val j_to_b = send_json.toString().toByteArray()
        return j_to_b
    }

}