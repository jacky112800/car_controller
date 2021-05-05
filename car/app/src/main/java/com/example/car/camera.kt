package com.example.car

import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_camera.*
import android.os.Vibrator
import android.view.MotionEvent
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


@RequiresApi(Build.VERSION_CODES.O)

class camera : AppCompatActivity() {

    private var m_angle_tv: TextView? = null
    private var m_strength_tv: TextView? = null
    var img_byte = null
    val img_view_car = null
    var car_run: Int = 0
    var th: client_th_string? = null

    init {
        thread {
            try {
                th = client_th_string()
                th?.start()
            } catch (e: Exception) {

            }

        }
    }

    var receive_check = false
    var angle_run: Double = 0.0
    var angle_back = 0
    var angle_json_L = 0.0
    var angle_json_R = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        receive_check = true
        thread {
            th?.send_cmd("FRAME")
            while (receive_check) {
                draw_json()
            }
            while (true) {
                Thread.sleep(100)
                if (car_run == 1) {
                    th?.send_move("MOVE", angle_json_L, angle_json_R)
                }
            }
        }
        //joystick
        m_angle_tv = findViewById<View>(R.id.angle_tv) as TextView
        m_strength_tv = findViewById<View>(R.id.strength_tv) as TextView
        val joystick = findViewById<JoystickView>(R.id.joystickView_car)

        joystick.setOnMoveListener { angle, strength ->
            m_angle_tv!!.setText(angle.toString())
            m_strength_tv!!.setText(strength.toString())
            if ((angle > 0) && (angle < 180)) {
                angle_run = ((angle.toDouble() - 90) / 90)
                angle_run = Math.floor((angle_run * 10))
                angle_run= -(angle_run/10)
                if (angle_run>0){
                    angle_json_R=angle_run
                    angle_json_L=1-angle_run
                }
                if (angle_run<0){
                    angle_json_R=-angle_run
                    angle_json_L=1+angle_run
                }
            }
            if ((angle > 180) && (angle < 360)) {
                angle_run = ((angle.toDouble() - 270) / 90)
                angle_run = Math.floor((angle_run * 10))
                angle_run= angle_run/10
                if (angle_run>0){
                    angle_json_R=angle_run
                    angle_json_L=1-angle_run
                }
                if (angle_run<0){
                    angle_json_R=-angle_run
                    angle_json_L=1+angle_run
                }
            }
            if (angle == 0) {
                angle_json_R = 1.0
                angle_json_L = 0.0
            }
            if (angle == 180) {
                angle_json_R = 0.0
                angle_json_L = 1.0
            }
            println(angle_run)
        }

        right_left_btn.setOnTouchListener(
            object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            car_run = 1
                            println(car_run)
                        }
                        MotionEvent.ACTION_UP -> {
                            car_run = 0
                            println(car_run)
                        }
                    }
                    return onTouchEvent(event)
                }
            })
    }

    fun to_setting(view: View) {
        val intent_setting = Intent(this, setting::class.java)
        startActivity(intent_setting)
        th?.send_cmd("CONFIG")

    }

    fun draw_jpg() {
        val img_view_car = findViewById<ImageView>(R.id.img_view_car_to_iphone)

        try {
            var th = client_th_jpg()
            th.start()
            var jpg_data = client_th_jpg.get_jpg
            if (jpg_data.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(jpg_data, 0, jpg_data.size)
                img_view_car.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            println("出不來啦")
        }
    }

    fun draw_json() {
        val img_view_car = findViewById<ImageView>(R.id.img_view_car_to_iphone)

        try {
            var json_data = client_th_string.get_data

            if (json_data.isNotEmpty()) {
                var img_json = json_data.decodeToString()
                var js_ob = JSONObject(img_json)
                var img_b64 = js_ob.getString("img")
                var jpg_data = Base64.getDecoder().decode(img_b64)
                val bitmap = BitmapFactory.decodeByteArray(jpg_data, 0, jpg_data.size)
                img_view_car.setImageBitmap(bitmap)
            }

        } catch (e: Exception) {
            println("出不來啦")
        }
    }

    fun draw_img() {
        val img_view_car = findViewById<ImageView>(R.id.img_view_car_to_iphone)
        try {
            var pixel_data: UByteArray = UByteArray((800 * 800 * 3) + 16)
//            var pixel_data: UByteArray = UByteArray((800 * 800 * 3) + 16)

            var th = client_th()
            th.start()
            pixel_data = client_th.get_data

            val w: Int = 800
            val h: Int = 800
            val compare = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

            for (x in 0 until w) {
                for (y in 0 until h) {
                    compare.setPixel(
                        x,
                        y,
                        Color.rgb(
                            pixel_data[17].toInt(),
                            pixel_data[18].toInt(),
                            pixel_data[19].toInt()
                        )
                    )
                }
            }
            img_view_car.setImageBitmap(compare)

        } catch (e: Exception) {
            println("出不來啦")
        }
    }

    override fun onPause() {
        super.onPause()
        th?.send_cmd("WAIT")
        Thread.sleep(100)
        th?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        th?.send_cmd("LOGOUT")
        th?.close()
    }
}