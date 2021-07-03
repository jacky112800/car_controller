package com.example.car

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

    private var m_angle_tv: TextView? = null
    private var m_strength_tv: TextView? = null
    var time_u: TimeUnit = TimeUnit.MILLISECONDS

    var th = MainActivity.th

    var inputstring=""
    var receive_check = false
    var angle_run: Double = 0.0
    var strength_run = 0.0
    var angle_json_L = 0.0
    var angle_json_R = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        receive_check = true
        to_stream(true)

        val joystick = findViewById<JoystickView>(R.id.joystickView_car)
        m_angle_tv = findViewById<View>(R.id.angle_tv) as TextView
        m_strength_tv = findViewById<View>(R.id.strength_tv) as TextView
        var joystickThread = thread(start = false) {
            Thread.sleep(100)
            joystickListen(joystick)
        }
        var drawThread = thread(start = false) {
            while (receive_check) {
                Thread.sleep(100)
                draw_json()
            }
        }
        //joystick




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

            println("LLLL " + angle_json_L + " RRRR " + angle_json_R + " aaaa " + angle_run)
        }
    }

    fun to_setting(view: View) {
        val intent_setting = Intent(this, setting::class.java)
        startActivity(intent_setting)
        to_stream(false)
    }

    fun draw_json() {
        val img_view_car = findViewById<ImageView>(R.id.img_view_car_to_iphone)

        try {
            //將全域變數的圖像資料取用
            var json_data = client_th_string.get_data

            var js_ob = JSONObject(json_data)
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
                    img_view_car.setImageBitmap(bitmap)
                }
            }

        } catch (e: JSONException) {
            println("錯誤")
        }
    }

    var count = 0
    var back_cd = Timer().schedule(0, 5000) {
        count = 0
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        back_cd.run()

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (count == 0) {
                Toast.makeText(this, "按三次即關閉APP", Toast.LENGTH_SHORT).show()
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

    fun close_app() {

        to_stream(false)
        Thread.sleep(100)
        receive_check = false
        Thread.sleep(100)

        Thread.sleep(100)
        finishAffinity()

    }

    override fun onPause() {
        super.onPause()
        to_stream(false)
        receive_check = false
    }

    fun to_stream(str_stream: Boolean) {
        var stream_json = JSONObject()
        stream_json.put("CMD", "IS_STREAM")
        stream_json.put("IS_STREAM", str_stream)
        println(stream_json)
        sendStringToByteArray(stream_json)
    }

    fun sendStringToByteArray(jsonObject: JSONObject) {
        var strTobyte = thread(start = false) {
            var string = jsonObject.toString()
            var bytearrayString = string.encodeToByteArray()
            socket_client.outputQueue.offer(bytearrayString, 1000, time_u)
        }
        strTobyte.start()
        strTobyte.join()
    }

    fun recvByteArrayToString() {
        while (receive_check) {
            var inputByteArray = socket_client.inputQueue.poll(1000, time_u)
            Thread.sleep(100)
            inputstring = inputByteArray.decodeToString()
            Thread.sleep(100)
        }
    }
}