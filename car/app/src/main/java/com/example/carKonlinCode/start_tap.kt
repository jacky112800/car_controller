package com.example.carKonlinCode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast

class start_tap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tap)

    }

    fun detection(view: View) {
        Toast.makeText(this, "detection", Toast.LENGTH_SHORT).show()
        val cameraIntent = Intent(this, camera::class.java)
        startActivity(cameraIntent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (MainActivity.socketIsChecked) {
                val backToStartActivity = Intent(this, MainActivity::class.java)
                startActivity(backToStartActivity)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}