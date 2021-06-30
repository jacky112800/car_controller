package com.example.car

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


class check : AppCompatActivity() {
    var socket_check = 0
    var count = 0
    var back_cd = Timer().schedule(1000, 1000) {
        count++
        println(count)
        if (count >= 10) {
            go_back()
        }
    }
    var ch = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        socket_check = 1
        ch = true

        var client = thread(start = false) {
            try {

                MainActivity.th.start()
            } catch (e: ConnectException) {
                Looper.prepare()
                Toast.makeText(this, "請檢查主機是否異常", Toast.LENGTH_SHORT).show()
                Looper.loop()
                println("請檢查主機是否異常")
            } catch (e: SocketTimeoutException) {
                Looper.prepare()
                Toast.makeText(this, "Time out 請檢查主機是否異常", Toast.LENGTH_SHORT).show()
                println("Time out 請檢查主機是否異常")
                Looper.loop()
            }
        }

        client.start()
        client.join()


    }

    fun check() {
        try {

            var login_check = client_th_string.get_data
            var js_ob = JSONObject(login_check)
            var log_info = js_ob.getString("CMD")

            if (log_info == "LOG_INFO") {
                var log_ch = js_ob.getString("VERIFY").toBoolean()
                if (log_ch) {
                    socket_check = 1
                }
                if (!log_ch) {
                    socket_check = 2
                }
            }

            if (socket_check == 1) {
                println("驗證成功")
                ch = false
                back_cd.cancel()
                Toast.makeText(this, "check", Toast.LENGTH_SHORT).show()
                val check_intent = Intent(this, start_tap::class.java)
                startActivity(check_intent)
            }
            if (socket_check == 2) {
                go_back()
                println("驗證錯誤")
            }
        } catch (e: JSONException) {

        }

    }

//    fun test() {
//        val check_intent = Intent(this, start_tap::class.java)
//        startActivity(check_intent)
//        back_cd.cancel()
//    }

    fun go_back() {
        back_cd.cancel()
        ch = false
        val check_intent = Intent(this, MainActivity::class.java)
        startActivity(check_intent)
        Looper.prepare()
        Toast.makeText(this, "主機無回應\r\n請檢查主機是否異常", Toast.LENGTH_SHORT).show()
        Looper.loop()
    }
}