package com.example.car

import android.content.Intent
import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Socket
import java.io.OutputStream

import java.nio.charset.Charset
import java.util.*
import javax.xml.transform.sax.TemplatesHandler
import kotlin.collections.HashMap
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_ent()
    }

    fun sign_in(view: View) {
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, check::class.java)
        startActivity(intent)
    }

    fun text_ent(){
        ip_input.inputType=EditorInfo.TYPE_CLASS_TEXT
        account_input.inputType=EditorInfo.TYPE_CLASS_TEXT
        start_btn.setOnClickListener {
            if (ip_input.text.isNullOrEmpty()&& account_input.text.isNullOrEmpty()){
                println("error")
            }else{
                var accout_car=account_input.text.toString()
                var ip=ip_input.text.toString()
            }
        }
    }
    val map =HashMap<String,String>()

    fun tojson(a: String){
        val map =HashMap<String,String>()
        map.put("LOGIN",a)

    }



}


