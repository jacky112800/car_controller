package com.example.carKotlinCode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_infer_change.*
import java.util.concurrent.TimeUnit

class qualityChange : AppCompatActivity() {
    var qualityWidth = ""
    var qualityHeight = ""
    var timeU: TimeUnit = TimeUnit.MILLISECONDS

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
                qualityWidth = infer_w_text.text.toString()
                qualityHeight = infer_h_text.text.toString()
                MainActivity.doJsonCommand.setQualityJSON(qualityWidth, qualityHeight)
            }
        }
    }
}