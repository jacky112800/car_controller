package com.example.car

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun sign_in(view: View){
        Toast.makeText(this,"hi",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,check::class.java)
        startActivity(intent)
    }
}