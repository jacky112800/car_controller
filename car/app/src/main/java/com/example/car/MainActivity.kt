package com.example.car

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    var accout_car: String = ""
    var th = client_th_string()

    companion object {
        var ip: String = ""
        var socket_check=0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_ent()
    }

    fun text_ent() {
        ip_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        account_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        start_btn.setOnClickListener {
            if (ip_input.text.isNullOrEmpty() && account_input.text.isNullOrEmpty()) {
                println("error")
            } else {
                accout_car = account_input.text.toString()
                ip = ip_input.text.toString()
                tojson(accout_car, ip)
                sign_in()
            }
        }
    }

    fun sign_in() {
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, check::class.java)
        startActivity(intent)
    }

    var login_json = JSONObject()
    fun tojson(accout: String, ip: String) {

        login_json.put("LOGIN_accout", accout)
        login_json.put("LOGIN_ip", ip)
        println(login_json)

        try {
            th.start()
            th.send_data(login_json.toString().toByteArray())
        } catch (e: Exception) {
            Toast.makeText(this, "帳號或IP位置不正確", Toast.LENGTH_SHORT).show()
            socket_check=1
        }
    }
}


