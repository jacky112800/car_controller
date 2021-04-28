package com.example.car

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.ConnectException
import java.util.*
import kotlin.concurrent.thread

class check : AppCompatActivity() {
    var socket_check = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        thread {
            try {
                var th = client_th_string()
                th.start()
                var login_check = client_th_string.get_data.decodeToString()
                var js_ob = JSONObject(login_check)
                var log_info = js_ob.getString("CMD")
                if (log_info == "LOG_INFO") {
                    var log_ch = js_ob.getString("LOGIN").toBoolean()
                    if (log_ch) {
                        socket_check = 1
                    } else {
                        socket_check = 2
                    }
                }
            } catch (e: ConnectException) {
                Toast.makeText(this, "請檢查主機是否異常", Toast.LENGTH_SHORT).show()
                println("請檢查主機是否異常")
            }
        }

        if (socket_check == 1) {
            check_func()
        } else if (socket_check == 2) {
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