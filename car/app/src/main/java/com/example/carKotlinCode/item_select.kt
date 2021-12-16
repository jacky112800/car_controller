package com.example.carKotlinCode

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_item_select.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class item_select : AppCompatActivity() {
    var timeU: TimeUnit = TimeUnit.MILLISECONDS
    var receiveCheck = true
    var itemSpinner: Spinner? = null
    var selectSendButton: Button? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_select)
        itemSpinner = findViewById<Spinner>(R.id.item_select_spinner)
        selectSendButton = findViewById<Button>(R.id.btn_select_confirm)
//        val getItemConfigJSONObject = JSONObject()

        val viewItemInfo = thread(start = false) { spinnerChange() }
        viewItemInfo.start()
        itemSelectSwitch()
//        getItemConfigJSONObject.put("CMD", "GET_CONFIGS")
//        sendJsonToByteArray(getItemConfigJSONObject)
        viewItemInfo.join()
    }

    override fun onStart() {
        super.onStart()
        itemConfigSend()
    }

    private fun itemSelectSwitch() {
        item_select_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                false -> MainActivity.doJsonCommand.setInferJSON(false)
                true -> MainActivity.doJsonCommand.setInferJSON(true)
            }
        }
    }


    var stringArray = arrayListOf<String>("")
    fun itemConfigSend() {
        var configJSONObject = JSONObject()
        var spinnerSelectString = ""
        itemSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    this@item_select,
                    "你選的是" + stringArray[position],
                    Toast.LENGTH_SHORT
                ).show()
                spinnerSelectString = stringArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerSelectString = ""
            }
        }
        selectSendButton?.setOnClickListener {
            if (spinnerSelectString != "") {
                MainActivity.doJsonCommand.setConfigJSON(spinnerSelectString)
            } else {
                Toast.makeText(this, "尚未選擇物件", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun spinnerChange() {
        val spinnerChangeTimer = Timer("spinnerChange").schedule(0, 50) {
            val inputString = socket_client.inputCmdString
            if (inputString != "") {
                val inputJsonObject = JSONObject(inputString)
                val configInfo = inputJsonObject.getString("CMD")
                if (configInfo == "CONFIGS") {
                    val configStringArray = inputJsonObject.getString("CONFIGS")
                    stringArray = configStringArray.split(",") as ArrayList<String>
                    val itemAdapter = ArrayAdapter(
                        this@item_select,
                        android.R.layout.simple_spinner_item,
                        stringArray
                    )
                    itemSpinner?.adapter = itemAdapter
                }
            }
            if (!receiveCheck) {
                cancel()
            }
        }
        spinnerChangeTimer.run()
    }

    override fun onDestroy() {
        super.onDestroy()
        receiveCheck = false
    }
}