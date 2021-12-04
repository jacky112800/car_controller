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
    var receiveCheck = false
    var angle_run: Double = 0.0
    var strength_run = 0.0
    var angle_json_L = 0.0
    var angle_json_R = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


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
        Timer("delaySendMove", false).schedule(200) {
            joystick.setOnMoveListener { angle, strength ->
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
                send_move(angle_json_L, angle_json_R)

                if (angle == 0 && strength == 0) {
                    Timer("returnToZero",false).schedule(200){
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
                    val json_data = inputString
                    val js_ob = JSONObject(json_data)

                    //如果CMD標籤內的字串為FRAME時
                    //將IMAGE內的圖像資料先Base64解碼
                    //再以裡面的圖像資料做成bitmap
                    if (js_ob.getString("CMD") == "FRAME") {
                        var img_b64 = js_ob.getString("IMAGE")
                        var jpg_data = Base64.getDecoder().decode(img_b64)
                        val bitmap = BitmapFactory.decodeByteArray(jpg_data, 0, jpg_data.size)
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
                    inputString = inputJSONObject
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