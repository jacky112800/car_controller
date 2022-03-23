package com.example.carKotlinCode

import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


@RequiresApi(Build.VERSION_CODES.O)

class camera : AppCompatActivity() {

    var itemTextView: TextView? = null

    var receiveCheck = false
    var timeU: TimeUnit = TimeUnit.MILLISECONDS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onStart() {
        super.onStart()
        val joystick = findViewById<JoystickView>(R.id.joystickView_car)
        val joystickThread = thread(start = false) { joystickListen(joystick) }
        val drawThread = thread(start = false) { drawJson() }
        receiveCheck = true
        pollFrameQueueToInputCMDString()
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
                val strengthCarRun: Float = (strength.toFloat() / 100)

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

    fun drawJson() {
        val imgViewCar = findViewById<ImageView>(R.id.img_view_car_to_iphone)
        try {
            //將全域變數的圖像資料取用
            val drawTimer = Timer("draw").schedule(0, 16) {
                val frameString = socket_client.inputFrameString
                if (frameString != "") {
                    val frameObject = JSONObject(frameString)
                    //如果CMD標籤內的字串為FRAME時
                    //將IMAGE內的圖像資料先Base64解碼
                    //再以裡面的圖像資料做成bitmap
                    if (frameObject.getString("CMD") == "FRAME") {
                        if (!frameObject.getString("IMAGE")
                                .isNullOrEmpty() && frameObject.getString("IMAGE") != ""
                        ) {
                            val imgBase64 = frameObject.getString("IMAGE")
                            val jpgData = Base64.getDecoder().decode(imgBase64)
                            val bitmap = BitmapFactory.decodeByteArray(jpgData, 0, jpgData.size)
                            val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                            if (frameObject.getJSONArray("BBOX").length() != 0) {
                                val getBBoxJSONArray = frameObject.getJSONArray("BBOX")
                                val bBoxArray = Array(getBBoxJSONArray.length()) { FloatArray(4) }
                                val canvas = Canvas(copyBitmap)
                                val paintTest = Paint()
                                paintTest.strokeWidth = 2f
                                paintTest.color = Color.RED
                                paintTest.style = Paint.Style.STROKE
                                if (getBBoxJSONArray.length() > 0) {
                                    for (i in 0 until getBBoxJSONArray.length()) {
                                        val getJSONArray: JSONArray =
                                            getBBoxJSONArray.getJSONArray(i)
                                        for (j in 0 until getJSONArray.length() - 1) {
                                            bBoxArray[i][j] = getJSONArray.getInt(j).toFloat()
                                        }
                                        canvas.drawRect(
                                            bBoxArray[i][0],
                                            bBoxArray[i][1],
                                            bBoxArray[i][2] - bBoxArray[i][0],
                                            bBoxArray[i][3] - bBoxArray[i][1],
                                            paintTest
                                        )
                                    }
                                }
                            }

                            //如果bitmap不為空就顯示圖片
                            //由於bitmap為空會產生錯誤,所以必須要有這一步驟
                            if (copyBitmap != null) {
                                runOnUiThread { imgViewCar.setImageBitmap(copyBitmap) }
                            }
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

    fun pollFrameQueueToInputCMDString() {
        val pollFrameQueue = thread(start = false) {
            socket_client.frameBufferQueue.clear()
            while(true){
                if (!socket_client.frameBufferQueue.isEmpty()) {
                    val inputJSONObject = socket_client.frameBufferQueue.poll()
                    if (inputJSONObject != null) {
                        socket_client.inputFrameString = inputJSONObject.toString()
                        println("catch:${socket_client.inputFrameString}")
                    }
                }
                Thread.sleep(1)
            }
        }
        pollFrameQueue.start()
    }

    override fun onPause() {
        super.onPause()
        MainActivity.doJsonCommand.setStreamJSON(false)
        receiveCheck = false
    }
}