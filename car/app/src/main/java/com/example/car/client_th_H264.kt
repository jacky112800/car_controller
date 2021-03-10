package com.example.car

import java.io.DataInputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

class client_th_H264 : Thread(){
    companion object  {var get_data_H264: ByteArray = ByteArray((800 * 800 * 3) + 16)}
    override fun run() {
        val address = "192.168.0.44"
        val port = 5050
        val connection: Socket = Socket(address, port)
        var connected: Boolean = true
        var data_in: DataInputStream = DataInputStream(connection.getInputStream())
        val reader: Scanner = Scanner(connection.getInputStream())
        val writer: OutputStream = connection.getOutputStream()
        val length: Int = (800 * 800 * 3) + 16
        var img_bt = ByteArray(length)

        while (connected) {
            data_in.readFully(img_bt, 0, img_bt.size)

            if (img_bt[length - 1] != null) {
                connected = false
                reader.close()
                connection.close()
            }
            get_data_H264=img_bt
        }

//        img_bt= byteArrayOf()

        return
    }
}