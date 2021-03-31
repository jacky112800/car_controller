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

    }

}