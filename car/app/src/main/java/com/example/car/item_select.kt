package com.example.car

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.activity_item_select.*
import org.json.JSONObject
import java.security.AccessController.getContext
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class item_select : AppCompatActivity() {
    var time_u: TimeUnit = TimeUnit.MILLISECONDS
    var inputstring = ""
    var receiveCheck = true
    var itemSpinner: Spinner? = null
    var selectSendButton: Button? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_select)
        itemSpinner = findViewById<Spinner>(R.id.item_select_spinner)
        selectSendButton = findViewById<Button>(R.id.btn_select_confirm)
        val getItemConfigJSONObject = JSONObject()

        val readByteArray = thread(start = false) { recvByteArrayToString() }
        val viewItemInfo = thread(start = false) { spinnerChange() }
        readByteArray.start()
        viewItemInfo.start()
        itemSelectSwitch()
        getItemConfigJSONObject.put("CMD", "GET_CONFIGS")
        sendJsonToByteArray(getItemConfigJSONObject)
        readByteArray.join()
        viewItemInfo.join()
    }

    override fun onStart() {
        super.onStart()
        itemConfigSend()
    }

    private fun itemSelectSwitch() {
        item_select_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                false -> itemSelectSwitchJsonObject("false")
                true -> itemSelectSwitchJsonObject("true")
            }
        }
    }

    fun itemSelectSwitchJsonObject(itemSelectSwitchString: String) {
        var itemSelectJson = JSONObject()
        itemSelectJson.put("CMD", "SET_INFER")
        itemSelectJson.put("INFER", itemSelectSwitchString)
        sendJsonToByteArray(itemSelectJson)
    }

    var stringArray = arrayListOf<String>("")
    fun itemConfigSend() {
        var configJSONObject=JSONObject()
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
                spinnerSelectString=""
            }
        }
        selectSendButton?.setOnClickListener {
            if (spinnerSelectString!=""){
                configJSONObject.put("CMD","SET_CONFIG")
                configJSONObject.put("CONFIG",spinnerSelectString)
            }else{
                Toast.makeText(this,"尚未選擇物件",Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun spinnerChange() {
        val spinnerChangeTimer = Timer("spinnerChange").schedule(0, 50) {
            if (inputstring != "") {
                val inputJsonObject = JSONObject(inputstring)
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

    fun recvByteArrayToString() {
        val catchTimer = Timer("recvByteArrayToString").schedule(0, 10) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val inputByteArray = socket_client.inputQueue.poll(1000, time_u)
                if (inputByteArray != null) {
                    inputstring = inputByteArray.decodeToString()
                    println("catch:$inputstring")
                }
            }
            if (!receiveCheck) {
                cancel()
            }
        }
        catchTimer.run()
    }

    override fun onDestroy() {
        super.onDestroy()
        receiveCheck = false
    }
}