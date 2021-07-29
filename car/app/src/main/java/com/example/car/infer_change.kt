package com.example.car

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_infer_change.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class infer_change : AppCompatActivity() {
    var inferH = ""
    var inferW = ""
    var time_u: TimeUnit = TimeUnit.MILLISECONDS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infer_change)
        val inferChangeButton = findViewById<Button>(R.id.infer_change_btn)
        infer_h_text.inputType = EditorInfo.TYPE_CLASS_TEXT
        infer_w_text.inputType = EditorInfo.TYPE_CLASS_TEXT

        inferChangeButton.setOnClickListener {
            if (infer_h_text.text.isNullOrEmpty() && infer_w_text.text.isNullOrEmpty()) {
                Toast.makeText(this, "請勿輸入空白", Toast.LENGTH_SHORT).show()
            } else {
                inferH = infer_h_text.text.toString()
                inferW = infer_w_text.text.toString()
                toJson(inferH, inferW)

            }
        }
    }

    fun toJson(inferH: String, inferW: String) {
        var inferJson = JSONObject()
        inferJson.put("CMD", "SET_QUALITY")
        inferJson.put("WIDTH", inferW)
        inferJson.put("HEIGHT", inferH)

        sendJsonToByteArray(inferJson)
    }

    fun sendJsonToByteArray(jsonObject: JSONObject) {
        var strTobyte = thread(start = false) {
            var string = jsonObject.toString()
            println(string)
            var bytearrayString = string.encodeToByteArray()
            socket_client.outputQueue.offer(bytearrayString, 1000, time_u)
        }
        strTobyte.start()
        strTobyte.join()
    }
}