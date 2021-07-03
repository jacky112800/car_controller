package com.example.car

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {


    companion object {
        var ip: String = ""
        var PWD: String = ""
        var port_car = 65536
        var th: socket_client = socket_client()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_ent()
    }

    fun text_ent() {
        ip_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        PWD_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        start_btn.setOnClickListener {
            if (ip_input.text.isNullOrEmpty() && PWD_input.text.isNullOrEmpty()) {
                Toast.makeText(this, "請勿輸入空白", Toast.LENGTH_SHORT).show()
            } else {
                PWD = PWD_input.text.toString()
                try {
                    val ip_list = ip_input.text.toString().split(":")
                    ip = ip_list[0]
                    port_car = ip_list[1].toInt()
                    println(ip + "\n" + port_car)
                    Thread.sleep(100)
                    sign_in()
//                    tojson()
                } catch (e: Exception) {
                    Toast.makeText(this, "請檢查是否有輸入正確格式", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    Looper.loop()
                }
            }
        }
    }


    fun sign_in() {
        val intent = Intent(this, check::class.java)
        startActivity(intent)

    }




    fun tojson() {
        try {
            var clientThread= thread(start=false) {
                var th=MainActivity.th
                println("1"+th.state)
                if (th.state == Thread.State.RUNNABLE) {
                    th.socketConnect()
                } else if (th.state == Thread.State.NEW) {
                    th.start()
                    th.socketConnect()
                }
            }

            if (th.state==Thread.State.TERMINATED){
                Looper.prepare()
                Toast.makeText(this, "連線失敗，請重新開啟應用程式在嘗試", Toast.LENGTH_SHORT).show()
                restartApp()
                Looper.loop()
            }

            clientThread.start()

            if (th.socketConnection) {
                sign_in()
            }

        } catch (e: ConnectException) {
            Looper.prepare()
            Toast.makeText(this, "請檢查主機是否異常", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            Looper.loop()
        } catch (e: SocketTimeoutException) {
            Looper.prepare()
            Toast.makeText(this, "請檢查主機是否異常", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            Looper.loop()
        } catch (e: IllegalThreadStateException) {

            e.printStackTrace()

        }

    }
    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())

    }

}


