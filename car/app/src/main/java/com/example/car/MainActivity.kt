package com.example.car

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_camera.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val joystick =joystickView

    }
    fun sign_in(view: View){
        Toast.makeText(this,"hi",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,check::class.java)
        startActivity(intent)
    }
}