package com.example.carKotlinCode

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import clientAction
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
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
        var doJsonCommand: jsonCommand = jsonCommand()
        var doClientAction: clientAction = clientAction()
        var socketIsChecked = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textEvent()
        if (socketIsChecked) {
            Toast.makeText(this, "登出中請稍候", Toast.LENGTH_SHORT).show()
            logout()
        }
    }

    override fun onStart() {
        super.onStart()
//        MainActivity.th.pollJSONQueueToInputCMDString()
    }

    fun textEvent() {
        ip_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        PWD_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        start_btn.setOnClickListener {
            if (ip_input.text.isNullOrEmpty() && PWD_input.text.isNullOrEmpty()) {
                Toast.makeText(this, "請勿輸入空白", Toast.LENGTH_SHORT).show()
            } else {
                PWD = PWD_input.text.toString()
                try {
                    val ipList = ip_input.text.toString().split(":")
                    ip = ipList[0]
                    port_car = ipList[1].toInt()
                    println(ip + "\n" + port_car)
                    Thread.sleep(100)
                    toJson()
                } catch (e: Exception) {
                    Toast.makeText(this, "請檢查是否有輸入正確格式", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    Looper.loop()
                }
            }
        }
    }

    private fun toJson() {
        try {
            var ctBoolean = true
            val clientThread = thread(start = false) {
                println("1" + th.state)
                if (th.state == Thread.State.RUNNABLE) {//偵測socket class狀態以免重複start
                    th.socketConnect()
                } else if (th.state == Thread.State.NEW) {
                    th.start()
                }
            }

            val clientThreadCheck = thread(start = false) {
                var count = 0
                while (ctBoolean) {
                    if (th.isConnection() && !socketIsChecked) {//連線成功時進入下一個頁面
                        ctBoolean = false
                        val intent = Intent(this, check::class.java)
                        startActivity(intent)//進入驗證頁面 check.kt
                    }

                    if (th.state == Thread.State.TERMINATED) {//避免重複start class當狀態為終止時重啟app
                        Looper.prepare()
                        ctBoolean = false
                        Toast.makeText(this, "連線失敗，請檢查主機是否異常。", Toast.LENGTH_SHORT)
                            .show()
                        restartApp()
                        Looper.loop()
                    }
                    if (count >= 5) {
                        ctBoolean = false
                        restartApp()
                    } else {
                        count++
                    }
                    Thread.sleep(1000)
                }
            }

            clientThread.start()
            clientThreadCheck.start()
            
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

    private fun restartApp() {
        Looper.prepare()
        Toast.makeText(this, "連線失敗，請確認IP是否輸入正確或主機異常，重啟APP中", Toast.LENGTH_SHORT).show()
//        Thread.sleep(1000)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }


    private fun logout() {
        val checkSeverLogoutThread = thread(start = false) { checkServerLogout() }
        doJsonCommand.logoutJSON()
        checkSeverLogoutThread.start()
        Thread.sleep(100)
        Thread.sleep(100)
    }

    private fun checkServerLogout() {
        try {
            //將全域變數的圖像資料取用
            val drawTimer = Timer("checkServer").schedule(0, 30) {
                if (socket_client.inputCmdString != "") {
                    val jsonData = socket_client.inputCmdString
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            println("press")
//            jsonObjectTest()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun jsonObjectTest(){
        val testJSONObject=JSONObject()
        val inTestJSONObject=JSONObject()
        inTestJSONObject.put("in1",111)
        inTestJSONObject.put("in2",222)
        inTestJSONObject.put("in3",333)
        testJSONObject.put("CMD","CMD")
        testJSONObject.put("CONFIG",inTestJSONObject)
        val aaa=testJSONObject.getJSONObject("CONFIG")
        var iteratarTest= iterator<String> {  }
        iteratarTest=aaa.keys()
        println(aaa.toString())
        val configsJSONArray=JSONArray()
        while (iteratarTest.hasNext()) {
            val key: String = iteratarTest.next()
            configsJSONArray.put(key)
            println(configsJSONArray)
        }

    }

}
