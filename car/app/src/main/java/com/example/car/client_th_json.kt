package com.example.car

import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.*

class client_th_json : Thread() {
    companion object {
        var get_json: ByteArray = byteArrayOf()
    }

    var send_json = JSONObject()

    override fun run() {
        val address = MainActivity.ip
        val port = 5050
        val connection: Socket = Socket(address, port)
        var connected: Boolean = true
        var data_in: DataInputStream = DataInputStream(connection.getInputStream())
        var data_out: DataOutputStream = DataOutputStream(connection.getOutputStream())
        val reader: Scanner = Scanner(connection.getInputStream())
        var img_bt: ByteArray = ByteArray(0)
        var lenght: Int

        if (connected) {
            data_out.write(tojson("ready to receive"))
        }

        while (connected) {

            lenght = data_in.readInt()
            img_bt = ByteArray(lenght)

            if (lenght > 0) {
                data_in.readFully(img_bt, 0, img_bt.size)
                print(img_bt.decodeToString())
            }

            if (img_bt[img_bt.size - 1] != null) {
                data_out.write(tojson("get data"))
                connected = false
                reader.close()
                connection.close()
            }
            get_json = img_bt
        }

    }

    fun tojson(frame: String): ByteArray {
        send_json.put("FRAME", frame)
        val j_to_b = send_json.toString().toByteArray()
        return j_to_b
    }

}