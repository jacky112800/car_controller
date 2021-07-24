package com.example.car

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
        var ip: String = ""
        var PWD: String = ""
        var port_car = 65536
        var th: socket_client = socket_client()
        var socketIsChecked = false
    }

    var timeU: TimeUnit = TimeUnit.MILLISECONDS
    var inputString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text_ent()
        if (socketIsChecked) {
            Toast.makeText(this, "登出中請稍候", Toast.LENGTH_SHORT).show()
            logout()
        }
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
                    tojson()
                } catch (e: Exception) {
                    Toast.makeText(this, "請檢查是否有輸入正確格式", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    Looper.loop()
                }
            }
        }
    }

    fun tojson() {
        try {
            var ctBoolean = true
            var clientThread = thread(start = false) {
                println("1" + th.state)
                if (th.state == Thread.State.RUNNABLE) {//偵測socket class狀態以免重複start
                    th.socketConnect()
                } else if (th.state == Thread.State.NEW) {
                    th.start()
                }
            }

            var clientThread_check = thread(start = false) {
                while (ctBoolean) {
                    if (th.socketConnection && !socketIsChecked) {//連線成功時進入下一個頁面
                        ctBoolean = false
                        val intent = Intent(this, check::class.java)
                        startActivity(intent)//進入驗證頁面 check.kt
                    }
                    if (th.state == Thread.State.TERMINATED) {//避免重複start class當狀態為終止時重啟app
                        Looper.prepare()
                        ctBoolean = false
                        Toast.makeText(this, "連線失敗，請點擊再次登入鈕，\n以便重新開啟應用程式再嘗試。", Toast.LENGTH_SHORT)
                            .show()
                        restartApp()
                        Looper.loop()
                    }
                }
            }

            clientThread.start()
            clientThread_check.start()
            clientThread.join()
            clientThread_check.join()

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

    var logoutJson = JSONObject()

    private fun logout() {
        val catchLogoutMessage = thread(start = false) { revByteArrayToString() }
        val checkSeverLogoutThread = thread(start = false) { checkServerLogout() }
        logoutJson.put("CMD", "LOGOUT")
        Thread.sleep(100)
        sendJsonToByteArray(logoutJson)
        Thread.sleep(100)
        catchLogoutMessage.start()
        checkSeverLogoutThread.start()
        catchLogoutMessage.join()
        checkSeverLogoutThread.join()
        Thread.sleep(100)
    }

    private fun checkServerLogout() {
        try {
            //將全域變數的圖像資料取用
            val drawTimer = Timer("checkServer").schedule(0, 30) {
                if (inputString != "") {
                    val jsonData = inputString
                    val jsonObject = JSONObject(jsonData)

                    if (jsonObject.getString("CMD") == "SYS_LOGOUT") {
                        println(jsonObject)
                        th.connection = false
                        restartApp()
                    }
                }
                if (!socketIsChecked) {
                    cancel()
                }
            }
            drawTimer.run()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendJsonToByteArray(jsonObject: JSONObject) {
        var strTobyte = thread(start = false) {
            var string = jsonObject.toString()
            var bytearrayString = string.encodeToByteArray()
            socket_client.outputQueue.offer(bytearrayString, 1000, timeU)
        }
        strTobyte.start()
        strTobyte.join()
    }

    private fun revByteArrayToString() {
        val catchTimer = Timer("revByteArrayToString").schedule(0, 10) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val inputByteArray = socket_client.inputQueue.poll(1000, timeU)
                if (inputByteArray != null) {
                    inputString = inputByteArray.decodeToString()
                    println("catch:$inputString")
                }
            }
            if (!socketIsChecked) {
                cancel()
            }
        }
        catchTimer.run()
    }
}


