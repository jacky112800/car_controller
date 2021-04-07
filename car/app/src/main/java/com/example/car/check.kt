package com.example.car

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

class check : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        if (MainActivity.socket_check == 0) {
            check_func()
        } else if (MainActivity.socket_check == 1) {
            socket_error()
        }
    }

    fun check_func() {
        Toast.makeText(this, "check", Toast.LENGTH_SHORT).show()
        val check_intent = Intent(this, start_tap::class.java)
        startActivity(check_intent)
    }

    fun socket_error() {
        val check_intent = Intent(this, MainActivity::class.java)
        startActivity(check_intent)
    }

}