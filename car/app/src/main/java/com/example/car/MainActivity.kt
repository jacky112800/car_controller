package com.example.car

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.ConnectException
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    var PWD: String = ""

    companion object {
        var ip: String = ""
        var port_car = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_ent()

    }

    fun text_ent() {
        ip_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        PWD_input.inputType = EditorInfo.TYPE_CLASS_TEXT
        start_btn.setOnClickListener {

            if (ip_input.text.isNullOrEmpty() && PWD_input.text.isNullOrEmpty()) {
                Toast.makeText(this, "請勿輸入空白", Toast.LENGTH_SHORT).show()
            } else {
                PWD = PWD_input.text.toString()
                try {
                    val ip_list = ip_input.text.toString().split(":")
                    ip = ip_list[0]
                    port_car = ip_list[1]
                    println(ip + "/n" + port_car)
                    tojson(PWD, ip)
                    sign_in()
                } catch (e: Exception) {
                    Toast.makeText(this, "請檢查是否有輸入正確格式", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sign_in() {
        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, check::class.java)
        startActivity(intent)
    }

    var login_json = JSONObject()
    fun tojson(accout: String, ip: String) {
        login_json.put("CMD", "LOGIN")
        login_json.put("PWD", accout)
        login_json.put("LOGIN_ip", ip)
        println(login_json)

        thread {
            try {
                var th = client_th_string()
                th.start()
                th.send_data(login_json.toString().toByteArray())
            } catch (e: ConnectException) {
                Looper.prepare()
                Toast.makeText(this, "請檢查主機是否異常", Toast.LENGTH_SHORT).show()
                println("請檢查主機是否異常")
                Looper.loop()
            }
        }
    }
}


