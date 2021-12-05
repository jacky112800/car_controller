package com.example.carKonlinCode

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
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

    var itemTextView: TextView? = null
    var timeU: TimeUnit = TimeUnit.MILLISECONDS

    var inputString = ""
    var frameString = ""
    var receiveCheck = false
    var angleCarRun: Double = 0.0
    var strengthCarRun = 0.0
    var angleJsonLeft = 0.0
    var angleJsonRight = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


    }

    override fun onStart() {
        super.onStart()
        val joystick = findViewById<JoystickView>(R.id.joystickView_car)
        val joystickThread = thread(start = false) { joystickListen(joystick) }
        val readByteArray = thread(start = false) { recvByteArrayToString() }
        val readFrameJSONObject = thread(start = false) { recvFrameToString() }
        val drawThread = thread(start = false) { draw_json() }
        receiveCheck = true
        joystickThread.start()
        readByteArray.start()
        readFrameJSONObject.start()
        drawThread.start()
        to_stream(true)
        joystickThread.join()
        readByteArray.join()
        readFrameJSONObject.join()
        drawThread.join()
    }

    override fun onRestart() {
        super.onRestart()
        to_stream(true)
    }

    fun joystickListen(joystick: JoystickView) {
        Timer("delaySendMove", false).schedule(200) {
            joystick.setOnMoveListener { angle, strength ->
                strengthCarRun = (strength.toDouble() / 100)
                angleCarRun = Math.floor((angleCarRun * 10))
                angleCarRun /= 10

                if ((angle > 0) && (angle < 180)) {
                    val angle_c = angle
                    angleCarRun = ((angle_c.toDouble() - 90) / 90)
                    angleCarRun = Math.floor((angleCarRun * 10))
                    angleCarRun /= 10

                    if ((angle > 0) && (angle < 90)) {
                        angleCarRun += 1
                        angleCarRun = Math.floor((angleCarRun * 10))
                        angleCarRun /= 10
                        angleJsonRight = angleCarRun * strengthCarRun
                        angleJsonLeft = 1.0 * strengthCarRun
                    }

                    if ((angle > 90) && (angle < 180)) {
                        angleCarRun = 1 - angleCarRun
                        angleCarRun = Math.floor((angleCarRun * 10))
                        angleCarRun = -(angleCarRun / 10)
                        angleJsonRight = 1.0 * strengthCarRun
                        angleJsonLeft = -angleCarRun * strengthCarRun
                    }
                }

                if ((angle > 180) && (angle < 360)) {
                    val angle_c = angle
                    angleCarRun = ((angle_c.toDouble() - 270) / 90)
                    angleCarRun = Math.floor((angleCarRun * 10))
                    angleCarRun /= 10

                    if ((angle > 180) && (angle < 270)) {
                        angleCarRun = 1 + angleCarRun
                        angleCarRun = Math.floor((angleCarRun * 10))
                        angleCarRun = -(angleCarRun / 10)
                        angleJsonRight = -1.0 * strengthCarRun
                        angleJsonLeft = angleCarRun * strengthCarRun
                    }

                    if ((angle > 270) && (angle < 360)) {
                        angleCarRun = 1 - angleCarRun
                        angleCarRun = Math.floor((angleCarRun * 10))
                        angleCarRun /= 10
                        angleJsonRight = -angleCarRun * strengthCarRun
                        angleJsonLeft = -1.0 * strengthCarRun
                    }
                }

                if (angle == 90) {
                    angleJsonRight = 1.0 * strengthCarRun
                    angleJsonLeft = 1.0 * strengthCarRun
                }
                if (angle == 180) {
                    angleJsonRight = -1.0 * strengthCarRun
                    angleJsonLeft = -1.0 * strengthCarRun
                }
                send_move(angleJsonLeft, angleJsonRight)

                if (angle == 0 && strength == 0) {
                    Timer("returnToZero", false).schedule(200) {
                        send_move(0.0, 0.0)
                    }
                }
            }
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
            //將全域變數的圖像資料取用
            val drawTimer = Timer("draw").schedule(0, 30) {
                if (inputString != "") {
                    val jsonData = frameString
                    val frameObject = JSONObject(jsonData)

                    //如果CMD標籤內的字串為FRAME時
                    //將IMAGE內的圖像資料先Base64解碼
                    //再以裡面的圖像資料做成bitmap
                    if (frameObject.getString("CMD") == "FRAME") {
                        var imgBase64 = frameObject.getString("IMAGE")
                        var jpgData = Base64.getDecoder().decode(imgBase64)
                        val bitmap = BitmapFactory.decodeByteArray(jpgData, 0, jpgData.size)
                        //如果bitmap不為空就顯示圖片
                        //由於bitmap為空會產生錯誤,所以必須要有這一步驟
                        if (bitmap != null) {
                            runOnUiThread { img_view_car.setImageBitmap(bitmap) }
                        }
                    }
                }
                if (!receiveCheck) {
                    cancel()
                }
            }
            drawTimer.run()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        to_stream(false)
        receiveCheck = false
    }

    fun to_stream(str_stream: Boolean) {
        var stream_json = JSONObject()
        stream_json.put("CMD", "SET_STREAM")
        stream_json.put("SET_STREAM", str_stream)
        println(stream_json)
        sendJsonToByteArray(stream_json)
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
                    inputString = inputJSONObject.toString()
                    println("catch:$inputString")
                }
            }
            if (!receiveCheck) {
                cancel()
            }
        }
        catchTimer.run()
    }

    fun recvFrameToString() {
        val catchTimer = Timer("recvByteArrayToString").schedule(0, 10) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val inputJSONObject = socket_client.inputQueue.poll(1000, timeU)
                if (inputJSONObject != null) {
                    frameString = inputJSONObject.toString()
                    println("catch:$inputString")
                }
            }
            if (!receiveCheck) {
                cancel()
            }
        }
        catchTimer.run()
    }
}