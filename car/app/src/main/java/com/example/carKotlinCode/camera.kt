package com.example.carKotlinCode

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
        MainActivity.th.pollFrameQueueToInputCMDString()
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
                val strengthCarRun = (strength / 100).toFloat()

                MainActivity.doJsonCommand.movJSON(strengthCarRun, angle)
                if (angle == 0 && strength == 0) {
                    Timer("returnToZero", false).schedule(200) {
                        MainActivity.doJsonCommand.movJSON(0f, 0)
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