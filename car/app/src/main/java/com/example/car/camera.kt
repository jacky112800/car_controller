package com.example.car

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


@RequiresApi(Build.VERSION_CODES.O)

class camera : AppCompatActivity() {

    private var m_angle_tv: TextView? = null
    private var m_strength_tv: TextView? = null
    var time_u: TimeUnit = TimeUnit.MILLISECONDS


    var inputstring = ""
    var receiveCheck = false
    var angle_run: Double = 0.0
    var strength_run = 0.0
    var angle_json_L = 0.0
    var angle_json_R = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        
        m_angle_tv = findViewById<View>(R.id.angle_tv) as TextView
        m_strength_tv = findViewById<View>(R.id.strength_tv) as TextView


    }

    override fun onStart() {
        super.onStart()
        val joystick = findViewById<JoystickView>(R.id.joystickView_car)
        val joystickThread = thread(start = false) { joystickListen(joystick) }
        val readByteArray = thread(start = false) { recvByteArrayToString() }
        val drawThread = thread(start = false) { draw_json() }
        receiveCheck = true
        joystickThread.start()
        readByteArray.start()
        drawThread.start()
        to_stream(true)
        joystickThread.join()
        readByteArray.join()
        drawThread.join()
    }

    override fun onRestart() {
        super.onRestart()
        to_stream(true)
    }

    fun joystickListen(joystick: JoystickView) {
        joystick.setOnMoveListener { angle, strength ->
            m_angle_tv!!.setText(angle.toString())
            m_strength_tv!!.setText(strength.toString())

            strength_run = (strength.toDouble() / 100)
            angle_run = Math.floor((angle_run * 10))
            angle_run = angle_run / 10

            if ((angle > 0) && (angle < 180)) {

                val angle_c = angle
                angle_run = ((angle_c.toDouble() - 90) / 90)
                angle_run = Math.floor((angle_run * 10))
                angle_run = angle_run / 10


                if ((angle > 0) && (angle < 90)) {
                    angle_run = 1 + angle_run
                    angle_run = Math.floor((angle_run * 10))
                    angle_run = angle_run / 10
                    angle_json_R = angle_run * strength_run
                    angle_json_L = 1.0 * strength_run
                }

                if ((angle > 90) && (angle < 180)) {
                    angle_run = 1 - angle_run
                    angle_run = Math.floor((angle_run * 10))
                    angle_run = -(angle_run / 10)
                    angle_json_R = 1.0 * strength_run
                    angle_json_L = -angle_run * strength_run
                }
            }
            if ((angle > 180) && (angle < 360)) {
                val angle_c = angle
                angle_run = ((angle_c.toDouble() - 270) / 90)
                angle_run = Math.floor((angle_run * 10))
                angle_run = angle_run / 10

                if ((angle > 180) && (angle < 270)) {
                    angle_run = 1 + angle_run
                    angle_run = Math.floor((angle_run * 10))
                    angle_run = -(angle_run / 10)
                    angle_json_R = -1.0 * strength_run
                    angle_json_L = angle_run * strength_run
                }

                if ((angle > 270) && (angle < 360)) {
                    angle_run = 1 - angle_run
                    angle_run = Math.floor((angle_run * 10))
                    angle_run = angle_run / 10
                    angle_json_R = -angle_run * strength_run
                    angle_json_L = -1.0 * strength_run
                }

            }
            if (angle == 90) {
                angle_json_R = 1.0 * strength_run
                angle_json_L = 1.0 * strength_run
            }
            if (angle == 180) {
                angle_json_R = -1.0 * strength_run
                angle_json_L = -1.0 * strength_run
            }

//            println("LLLL " + angle_json_L + " RRRR " + angle_json_R + " aaaa " + angle_run)
            send_move(angle_json_L, angle_json_R)
        }
    }

    fun send_move(L: Double, R: Double) {
        //將要傳送的字串(指令)放進標籤為CMD的JSON
        //此為專為移動數值所設
        var sendMoveThread = thread(start = false) {
            var moveJson = JSONObject()
            moveJson.put("CMD", "MOV")
            moveJson.put("L", L)
            moveJson.put("R", R)
            println(moveJson)
            sendJsonToByteArray(moveJson)
        }
        sendMoveThread.start()
        sendMoveThread.join()
    }

    fun to_setting(view: View) {
        val intent_setting = Intent(this, setting::class.java)
        startActivity(intent_setting)
    }

    fun draw_json() {
        val img_view_car = findViewById<ImageView>(R.id.img_view_car_to_iphone)

        try {
//            while (receiveCheck) {
            //將全域變數的圖像資料取用
            val drawTimer = Timer("draw").schedule(0, 30) {
                if (inputstring != "") {
                    val json_data = inputstring
                    val js_ob = JSONObject(json_data)
                    //如果CMD標籤內的字串為FRAME時
                    //將IMAGE內的圖像資料先Base64解碼
                    //再以裡面的圖像資料做成bitmap

                    if (js_ob.getString("CMD") == "FRAME") {
                        var img_b64 = js_ob.getString("IMAGE")
                        var jpg_data = Base64.getDecoder().decode(img_b64)
                        val bitmap =
                            BitmapFactory.decodeByteArray(jpg_data, 0, jpg_data.size)
                        //如果bitmap不為空就顯示圖片
                        //由於bitmap為空會產生錯誤,所以必須要有這一步驟
                        if (bitmap != null) {
                            runOnUiThread { img_view_car.setImageBitmap(bitmap) }
                        }
                    }

                }
                if(!receiveCheck){
                    cancel()
                }
            }

//            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    var count = 0


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

//            val back_cd = Timer().schedule(6000) {
//                count = 0
//                cancel()
//            }
//            back_cd.run()

            println(count)
            if (count == 0) {
                Toast.makeText(this, "按兩次即關閉APP", Toast.LENGTH_SHORT).show()
            }
            if (count == 1) {
                Toast.makeText(this, "再按一次即關閉APP", Toast.LENGTH_SHORT).show()
            }
            if (count == 2) {
                close_app()
            }
            count++
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    var logoutJson = JSONObject()
    var exitJson = JSONObject()

    fun close_app() {
        logoutJson.put("CMD", "LOGOUT")
        exitJson.put("CMD", "EXIT")
        to_stream(false)
        Thread.sleep(100)
        sendJsonToByteArray(logoutJson)
        Thread.sleep(100)
        sendJsonToByteArray(exitJson)
        Thread.sleep(100)
        receiveCheck = false
        finishAffinity()
        System.exit(0)
    }

    override fun onPause() {
        super.onPause()
        to_stream(false)
        receiveCheck = false
    }

    fun to_stream(str_stream: Boolean) {
        var stream_json = JSONObject()
        stream_json.put("CMD", "IS_STREAM")
        stream_json.put("IS_STREAM", str_stream)
        println(stream_json)
        sendJsonToByteArray(stream_json)
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
        val catchTimer = Timer("recvByteArrayToString").schedule(0, 10) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val inputByteArray = socket_client.inputQueue.poll(1000, time_u)
                if (inputByteArray != null) {
                    inputstring = inputByteArray.decodeToString()
                    println(inputstring)
                }
            }
            if(!receiveCheck){
                cancel()
            }
        }
        catchTimer.run()

    }

}