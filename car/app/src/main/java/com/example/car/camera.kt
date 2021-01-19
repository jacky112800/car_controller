package com.example.car

import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
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


@RequiresApi(Build.VERSION_CODES.O)

class camera : AppCompatActivity() {

    private var m_angle_tv: TextView? = null
    private var m_strength_tv: TextView? = null
    var img_byte = null
    val img_view_car = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        //joystick
        m_angle_tv = findViewById<View>(R.id.angle_tv) as TextView
        m_strength_tv = findViewById<View>(R.id.strength_tv) as TextView
        val joystick = findViewById<JoystickView>(R.id.joystickView_car)
        joystick.setOnMoveListener { angle, strength ->
            m_angle_tv!!.setText(angle.toString())
            m_strength_tv!!.setText(strength.toString())
        }
        right_left_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
//            right_left_btn.setBackgroundResource(R.drawable.round_btn_change_color)
                doVibrate()
                draw_img()

            }
        })
        //joystick
        draw_img()
    }

    fun doVibrate() {
        var button_vibrator = application.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= 26) {
            button_vibrator.vibrate(
                VibrationEffect.createOneShot(
                    200,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            button_vibrator.vibrate(100)
        }

    }

    fun to_setting(view: View) {
        val intent_setting = Intent(this, setting::class.java)
        startActivity(intent_setting)
    }

//    fun car_run (view: View){
//        val button : Button =findViewById(R.id.right_left_btn)
//        button.setBackgroundResource(R.drawable.round_btn_change_color)
//    }


    fun draw_img() {
        val img_view_car = findViewById<ImageView>(R.id.img_view_car_to_iphone)
//        try {
        var pixel_data: UByteArray = UByteArray((800 * 800 * 3) + 16)
        var th = client_th()
        th.start()
        pixel_data=client_th.get_data

//        println("client" + pixel_data)
        val w: Int = 800
        val h: Int = 800
        val compare = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//        var r: Int = (0..255).random()
//        var g: Int = (0..255).random()
//        var b: Int = (0..255).random()
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

//        } catch (e: Exception) {
//            println("出不來啦")
//        }

    }
}