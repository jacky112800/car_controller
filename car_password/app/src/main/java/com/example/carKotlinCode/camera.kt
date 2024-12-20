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
    var inputFrameString = ""
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
            while (true) {
                val frameString = inputFrameString
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

//                            val canvas = Canvas(copyBitmap)
//                            val paintTest = Paint()
//                            paintTest.strokeWidth = 2f
//                            paintTest.color = Color.RED
//                            paintTest.style = Paint.Style.STROKE
//                            canvas.drawRect(145f, 167f, 277f, 231f, paintTest)
//                            canvas.drawRect(212f, 47f, 425f, 428f, paintTest)
//                            canvas.drawRect(0f, 0f, 62f, 92f, paintTest)

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
                                            bBoxArray[i][2],
                                            bBoxArray[i][3],
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
                Thread.sleep(10)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e:IllegalArgumentException){
            e.printStackTrace()
            println(inputFrameString)
        }
    }

    fun pollFrameQueueToInputCMDString() {
        val pollFrameQueue = thread(start = false) {
            socket_client.frameBufferQueue.clear()
            var clearQueueCount = 0
            while (true) {

                if (!socket_client.frameBufferQueue.isEmpty()) {
                    val inputJSONObject = socket_client.frameBufferQueue.poll()
                    if (inputJSONObject != null) {
                        inputFrameString = inputJSONObject.toString()
                        println("catch:${inputFrameString}")
                    }
                }
                Thread.sleep(1)
                if (clearQueueCount > 34) {
                    socket_client.frameBufferQueue.clear()
                    clearQueueCount=0
                } else {
                    clearQueueCount++
                }
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