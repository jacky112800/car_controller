package com.example.car

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_infer_change.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.ConnectException
import kotlin.concurrent.thread

class infer_change : AppCompatActivity() {
    var infer_h = ""
    var infer_w = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infer_change)

        infer_h_text.inputType = EditorInfo.TYPE_CLASS_TEXT
        infer_w_text.inputType = EditorInfo.TYPE_CLASS_TEXT

        infer_change_btn.setOnClickListener {
            if (infer_h_text.text.isNullOrEmpty() && infer_w_text.text.isNullOrEmpty()) {
                Toast.makeText(this, "請勿輸入空白", Toast.LENGTH_SHORT).show()
            } else {
                infer_h = infer_h_text.text.toString()
                infer_w = infer_w_text.text.toString()

                try {


                } catch (e: Exception) {
                    Looper.prepare()
                    Toast.makeText(this, "請檢查是否有輸入正確格式", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
            }
        }
    }

    var login_json = JSONObject()

    fun tojson(pwd: String) {
        login_json.put("CMD", "LOGIN")
        login_json.put("PWD", pwd)
        println(login_json)


    }
}