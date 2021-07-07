package com.example.car

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
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
    var time_u: TimeUnit = TimeUnit.MILLISECONDS
    var ch = false
    var login_json = JSONObject()
    var infoCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        socket_check = 1
        Thread.sleep(1000)
        sendInfo()
    }

    var inputstring = ""

    fun sendInfo() {
        var checkThread = thread(start = false) {
            while (infoCheck) {
                if (inputstring != null && inputstring != "") {
                    check()
                }
            }
        }
        var byteToString = thread(start = false) {
            while (infoCheck) {
                recvByteArrayToString()
            }
        }

        checkThread.start()
        byteToString.start()
        println("send")
        login_json.put("CMD", "LOGIN")
        login_json.put("PWD", MainActivity.PWD)
        sendJsonToByteArray(login_json)
    }

    fun check() {
        try {

            var js_ob = JSONObject(inputstring)
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

    fun sendJsonToByteArray(jsonObject: JSONObject) {
        var strTobyte = thread(start = false) {
            var string = jsonObject.toString()
            var bytearrayString = string.encodeToByteArray()
            socket_client.outputQueue.offer(bytearrayString, 1000, time_u)
        }
        strTobyte.start()
        strTobyte.join()
    }

    fun recvByteArrayToString() {
        if (socket_client.inputQueue != null) {
            var inputByteArray = socket_client.inputQueue.poll(1000, time_u)
            if (inputByteArray != null) {
                inputstring = inputByteArray.decodeToString()
            }
        }
    }
}