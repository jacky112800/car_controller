package com.example.car

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import org.json.JSONObject
import java.net.ConnectException
import kotlin.concurrent.thread

class start_tap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tap)

    }
    fun detection (view: View) {
        Toast.makeText(this, "detection", Toast.LENGTH_SHORT).show()
        val camera_intent = Intent(this, camera::class.java)
        startActivity(camera_intent)
    }


}