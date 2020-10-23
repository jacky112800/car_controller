package com.example.car

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.view.*


class camera : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val joystick =findViewById<JoystickView>(R.id.joystickView)
        joystick.setOnMoveListener{
            
        }

    }
    fun to_setting (view: View){
        val intent_setting=Intent(this,setting::class.java)
        startActivity(intent_setting)
    }
}