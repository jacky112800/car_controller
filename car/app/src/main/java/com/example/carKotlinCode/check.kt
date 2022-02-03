package com.example.carKotlinCode

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit


class check : AppCompatActivity() {
    var count = 0

    var timeU: TimeUnit = TimeUnit.MILLISECONDS
    var backCdBoolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        Thread.sleep(1000)
    }

    override fun onStart() {
        super.onStart()
//        checkInfo()
        testCheck()
    }

    private fun checkInfo() {
        val timeOutEvent: TimeOutEvent = object : TimeOutEvent() {
            override fun timeOutFunction() {
                //TODO: overwrite this
                try {
                    when (MainActivity.doClientAction.verify()) {
                        true -> {
                            nextActivity()
                        }
                        false -> goBack()
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        timeOutEvent.start()
        println(timeOutEvent.wait(5000, TimeUnit.MILLISECONDS))
        try {
            timeOutEvent.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    fun nextActivity() {
        MainActivity.doClientAction.start()
        val checkIntent = Intent(this, start_tap::class.java)
        startActivity(checkIntent)
    }

    private fun testCheck() {
        MainActivity.socketIsChecked = true
        nextActivity()//進入下一個頁面 start_tap
    }

    fun goBack() {
        Looper.prepare()
        Toast.makeText(this, "主機無回應\r\n請檢查主機是否異常", Toast.LENGTH_SHORT).show()
        val checkIntent = Intent(this, MainActivity::class.java)
        startActivity(checkIntent)
        backCdBoolean = true
        Looper.loop()

    }

}