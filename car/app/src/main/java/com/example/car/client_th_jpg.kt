package com.example.car

import android.widget.ImageView
import java.io.DataInputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

class client_th_jpg : Thread(){
    companion object  {var get_jpg:ByteArray= byteArrayOf()}
    override fun run() {
        val address = "192.168.100.26"
        val port = 5050
        val connection: Socket = Socket(address, port)
        var connected: Boolean = true
        var data_in: DataInputStream = DataInputStream(connection.getInputStream())
        val reader: Scanner = Scanner(connection.getInputStream())
        val writer: OutputStream = connection.getOutputStream()

        var img_bt: ByteArray

        while (connected) {
            if (data_in.available()>0){
                img_bt= ByteArray(data_in.readInt())
                data_in.read(img_bt)
                get_jpg=img_bt

            }


                connected = false
                reader.close()
                connection.close()


        }

//        img_bt= byteArrayOf()


    }
}