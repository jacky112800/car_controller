package com.example.carKotlinCode

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class check : AppCompatActivity() {
    var count = 0

    var timeU: TimeUnit = TimeUnit.MILLISECONDS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
    }

    override fun onStart() {
        super.onStart()
//        val cheakInfoTwoThread = thread(start = false) {
//            cheakInfoTwo()
//        }
//        Thread.sleep(1000)
//        cheakInfoTwoThread.start()
//        checkInfo()
        testCheck()
    }

    private fun checkInfo() {
        val timeOutEvent: TimeOutEvent = object : TimeOutEvent() {
            override fun timeOutFunction() {
                //TODO: overwrite this
                try {
                    MainActivity.doJsonCommand.loginJSON()
                    when (MainActivity.doClientAction.verify()) {
                        true -> {
                            nextActivity()
                        }
                        false -> goBack()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        timeOutEvent.start()
        println(timeOutEvent.wait(3000, TimeUnit.MILLISECONDS))
        try {
            timeOutEvent.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun cheakInfoTwo() {
        try {
            MainActivity.doJsonCommand.loginJSON()
            Thread.sleep(5000)
            when (MainActivity.doClientAction.verify()) {
                true -> nextActivity()
                false -> goBack()
            }
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
        Thread.sleep(1000)
        val backToStartActivity = Intent(this, MainActivity::class.java)
        startActivity(backToStartActivity)
        System.exit(0)

    }

}