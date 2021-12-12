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
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


@RequiresApi(Build.VERSION_CODES.O)

class camera : AppCompatActivity() {

    var itemTextView: TextView? = null

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
        val drawThread = thread(start = false) { draw_json() }
        receiveCheck = true
        MainActivity.th.pollJSONQueueToInputCMDString()
        joystickThread.start()
        drawThread.start()
        MainActivity.doJsonCommand.setStreamJSON(true)
//        drawThread.join()
    }

    override fun onRestart() {
        super.onRestart()
        MainActivity.doJsonCommand.setStreamJSON(true)
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
                MainActivity.doJsonCommand.movJSON(angleJsonLeft,angleJsonRight)
                if (angle == 0 && strength == 0) {
                    Timer("returnToZero", false).schedule(200) {
                        MainActivity.doJsonCommand.movJSON(0.0,0.0)
                    }
                }
            }
        }
    }

    fun toSettingIntent(view: View) {
        val intentSetting = Intent(this, setting::class.java)
        startActivity(intentSetting)
    }

    fun draw_json() {
        val imgViewCar = findViewById<ImageView>(R.id.img_view_car_to_iphone)
        try {
            //將全域變數的圖像資料取用
            val drawTimer = Timer("draw").schedule(0, 30) {
                val frameString = socket_client.inputFrameString
                if (frameString != "") {
                    val frameObject = JSONObject(frameString)
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
                            runOnUiThread { imgViewCar.setImageBitmap(bitmap) }
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
        MainActivity.doJsonCommand.setStreamJSON(false)
        receiveCheck = false
    }
}