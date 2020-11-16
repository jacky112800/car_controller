package com.example.car

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.view.*
import android.widget.Button


class camera : AppCompatActivity() {

    private var m_angle_tv: TextView? = null
    private var m_strength_tv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        m_angle_tv = findViewById<View>(R.id.angle_tv) as TextView
        m_strength_tv = findViewById<View>(R.id.strength_tv) as TextView

        val joystick =findViewById<JoystickView>(R.id.joystickView_car)
        joystick.setOnMoveListener { angle, strength ->
            m_angle_tv!!.setText(angle.toString())
            m_strength_tv!!.setText(strength.toString())
        }
    right_left_btn.setOnClickListener(object :View.OnClickListener{
        override fun onClick(v: View?) {
            right_left_btn.setBackgroundResource(R.drawable.round_btn_change_color)
        }
    })

    }
    fun to_setting (view: View){
        val intent_setting=Intent(this,setting::class.java)
        startActivity(intent_setting)
    }

//    fun car_run (view: View){
//        val button : Button =findViewById(R.id.right_left_btn)
//        button.setBackgroundResource(R.drawable.round_btn_change_color)
//    }
}