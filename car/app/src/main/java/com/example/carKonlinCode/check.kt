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
    var infoCheck = true
    var backCdBoolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        Thread.sleep(1000)
    }

    override fun onStart() {
        super.onStart()
        MainActivity.th.pollJSONQueueToInputCMDString()
        sendInfo()
    }


    fun sendInfo() {
        val checkThread = thread(start = false) { check() }
        val backMainActivityTimer = thread(start = false) {
            val backCd = Timer("backCd").schedule(1000, 1000) {
                count++
                println(count)
                if (count >= 6) {
                    go_back()
                }
                if (backCdBoolean) {
                    cancel()
                }
            }//六秒後沒有回應返回登入頁面
            backCd.run()
        }
        checkThread.start()
        backMainActivityTimer.start()
        MainActivity.doJsonCommand.loginJSON()
        checkThread.join()
    }

    fun check() {
        try {
//            while (infoCheck) {
            val infoCheckTimer = Timer("recvByteArrayToString").schedule(0, 10) {
                val inputString = socket_client.inputCmdString
                if (inputString != "") {
                    val jsonObject = JSONObject(inputString)
                    val logInfo = jsonObject.getString("CMD")
                    if (logInfo == "LOGIN_INFO") {
                        val logCheck = jsonObject.getString("VERIFY").toBoolean()
                        if (logCheck) {
                            infoCheck = false
                            MainActivity.socketIsChecked = true
                            println("驗證成功")
                            backCdBoolean = true
                            NextActivity()//進入下一個頁面 start_tap
                            cancel()//取消本timer
                        }
                        if (!logCheck) {
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
        val checkIntent = Intent(this, start_tap::class.java)
        startActivity(checkIntent)
    }


    fun go_back() {
        Looper.prepare()
        infoCheck = false
        Toast.makeText(this, "主機無回應\r\n請檢查主機是否異常", Toast.LENGTH_SHORT).show()
        val checkIntent = Intent(this, MainActivity::class.java)
        startActivity(checkIntent)
        backCdBoolean = true
        Looper.loop()

    }

//    fun recvByteArrayToString() {
//        val catchTimer = Timer("recvByteArrayToString").schedule(0, 10) {
//            if (!socket_client.inputQueue.isNullOrEmpty()) {
//                val inputJSONObject = socket_client.inputQueue.poll(1000, timeU)
//                if (inputJSONObject != null) {
//                    inputString = inputJSONObject.toString()
//                    println("catch:$inputString")
//                }
//            }
//            if (!infoCheck) {
//                cancel()
//            }
//        }
//        catchTimer.run()
//    }
}