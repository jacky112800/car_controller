package com.example.carKonlinCode

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
    var count = 0

    var timeU: TimeUnit = TimeUnit.MILLISECONDS
    var loginJson = JSONObject()
    var infoCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        Thread.sleep(1000)
    }

    override fun onStart() {
        super.onStart()
        sendInfo()
    }

    var backCd = Timer("backCd").schedule(1000, 1000) {
        count++
        println(count)
        if (count >= 6) {
            go_back()
        }
    }//六秒後沒有回應返回登入頁面

    var inputString = ""

    fun sendInfo() {
        var checkThread = thread(start = false) { check() }
        var byteToString = thread(start = false) { recvByteArrayToString() }

        checkThread.start()
        byteToString.start()
        loginJson.put("CMD", "LOGIN")
        loginJson.put("PWD", MainActivity.PWD)
        sendJsonToByteArray(loginJson)
        checkThread.join()
        byteToString.join()
    }

    fun check() {
        try {
//            while (infoCheck) {
            val infoCheckTimer = Timer("recvByteArrayToString").schedule(0, 10) {
                if (inputString != "") {
                    var js_ob = JSONObject(inputString)
                    var log_info = js_ob.getString("CMD")

                    if (log_info == "LOGIN_INFO") {
                        var log_ch = js_ob.getString("VERIFY").toBoolean()
                        if (log_ch) {
                            infoCheck = false
                            MainActivity.socketIsChecked = true
                            println("驗證成功")
                            backCd.cancel()
                            NextActivity()//進入下一個頁面 start_tap
                            cancel()//取消本timer
                        }
                        if (!log_ch) {
                            infoCheck = false
                            go_back()
                            cancel()
                            println("驗證錯誤")
                        }
                    }
                }
            }
            infoCheckTimer.run()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun NextActivity() {
        val check_intent = Intent(this, start_tap::class.java)
        startActivity(check_intent)
    }


    fun go_back() {
        Looper.prepare()
        infoCheck = false
        Toast.makeText(this, "主機無回應\r\n請檢查主機是否異常", Toast.LENGTH_SHORT).show()
        val check_intent = Intent(this, MainActivity::class.java)
        startActivity(check_intent)
        backCd.cancel()
        Looper.loop()

    }

    fun sendJsonToByteArray(jsonObject: JSONObject) {
        var strTobyte = thread(start = false) {
            socket_client.outputQueue.offer(jsonObject.toString(), 1000, timeU)
        }
        strTobyte.start()
        strTobyte.join()
    }

    fun recvByteArrayToString() {
        val catchTimer = Timer("recvByteArrayToString").schedule(0, 10) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val inputJSONObject = socket_client.inputQueue.poll(1000, timeU)
                if (inputJSONObject != null) {
                    inputString = inputJSONObject
                    println("catch:$inputString")
                }
            }
            if (!infoCheck) {
                cancel()
            }
        }
        catchTimer.run()
    }
}