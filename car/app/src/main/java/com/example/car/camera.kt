package com.example.car

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class camera : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }
    fun to_setting (view: View){
        val intent_setting=Intent(this,setting::class.java)
        startActivity(intent_setting)
    }
}