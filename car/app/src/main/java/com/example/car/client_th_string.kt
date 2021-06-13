package com.example.car

import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.concurrent.thread

class client_th_string : Thread() {
    companion object {
        var get_data: String = ""
    }

    var send_json = JSONObject()

    val address = MainActivity.ip
    val port = MainActivity.port_car.toInt()
    val connection: Socket = Socket(address, port)

    var connected: Boolean = true
    var data_in: DataInputStream = DataInputStream(connection.getInputStream())
    var data_out: DataOutputStream = DataOutputStream(connection.getOutputStream())
    val reader: Scanner = Scanner(connection.getInputStream())

    override fun run() {
        connection.soTimeout
        thread(connected) {

            while (connected) {
                try {
                    receive_data()
                }catch (e:SocketException){
                    connection.close()
                    println("主機異常")
                } catch (e: ConnectException) {
                    connection.close()
                    println("主機異常")
                }catch(e: SocketTimeoutException){
                    connection.close()
                    println("主機異常")
                }

            }
        }

    }

    fun receive_data() {
        var img_bt: ByteArray = ByteArray(0)
        var lenght: Int

        //先接收主機傳來的圖片ByteArray大小
        lenght = data_in.readInt()
        img_bt = ByteArray(lenght)

        //接收壓縮過的影像資料
        if (lenght > 0) {
            data_in.readFully(img_bt, 0, img_bt.size)
        }

        //接收到後關閉
        if (img_bt!= null) {
            connected = false
        }

        //將資料放到全域變數,以供其他地方使用
        get_data = img_bt.decodeToString()
    }

    fun send_cmd(cmd: String) {
        //將要傳送的字串(指令)放進標籤為CMD的JSON
        send_json.put("CMD", cmd)
        val j_to_b = send_json.toString().encodeToByteArray()
        send_data(j_to_b)
    }

    fun send_move(L: Double, R: Double) {
        //將要傳送的字串(指令)放進標籤為CMD的JSON
        //此為專為移動數值所設
        send_json.put("CMD", "MOV")
        send_json.put("L", L)
        send_json.put("R", R)
        println(this.send_json)
        val j_to_b = send_json.toString().encodeToByteArray()
        send_data(j_to_b)
    }

    fun send_data(send_cmd: ByteArray) {
        //先關閉接收,再進行傳送,傳送後再開起接收
        connected = false
        thread(!connected) {
            data_out.writeInt(send_cmd.size)
            data_out.write(send_cmd)
        }
        connected = true
    }

    fun close() {
        reader.close()
        connection.close()
    }

}