package com.example.car

import android.widget.ImageView
import java.io.DataInputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

class client_th_json : Thread() {
    companion object {
        var get_json: ByteArray = byteArrayOf()
    }

    override fun run() {

        val address = "192.168.100.22"
        val port = 5050
        val connection: Socket = Socket(address, port)
        var connected: Boolean = true
        var data_in: DataInputStream = DataInputStream(connection.getInputStream())
        val reader: Scanner = Scanner(connection.getInputStream())
        val writer: OutputStream = connection.getOutputStream()
        val close_message = "ok"
        var bt_buff = ByteArray(1024)
        var img_bt: ByteArray = ByteArray(0)
        var lenght: Int
        while (connected) {
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
            get_json = img_bt
        }

//        img_bt= byteArrayOf()
    }

    fun add_bt(data1: ByteArray, data2: ByteArray): ByteArray {
        val data3 = ByteArray(data1.size + data2.size)
        System.arraycopy(data1, 0, data3, 0, data1.size)
        System.arraycopy(data2, 0, data3, data1.size, data2.size)
        return data3
    }

}