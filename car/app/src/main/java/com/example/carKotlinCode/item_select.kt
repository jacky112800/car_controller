package com.example.carKotlinCode

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_item_select.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit


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

//        val viewItemInfo = thread(start = false) { spinnerChange() }
//        viewItemInfo.start()
        itemSelectSwitch()

    }

    override fun onStart() {
        super.onStart()
        spinnerChange(itemSpinner)
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

    fun testSpinner(spinner: Spinner) {
        val lunch = arrayOf("one", "two", "three", "four", "five")
        val lunchArray = arrayListOf<String>()

        val lunchJSONObject=JSONObject()

        val lunchJSONArray=JSONArray()
        lunchJSONArray.put("one")
        lunchJSONArray.put("two")
        lunchJSONArray.put("three")

        lunchJSONObject.put("CLASSES",lunchJSONArray)
        println(lunchJSONObject.toString())

        val getJSONArray=lunchJSONObject.getJSONArray("CLASSES")

        for (i in 0 until getJSONArray.length()){
            val arrayString=getJSONArray.getString(i)
            lunchArray.add(i,arrayString)
        }

        stringArray=lunchArray
        val adapterTest = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stringArray
        )
        spinner.adapter=adapterTest
    }

    fun itemConfigSend() {
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

    fun spinnerChange(spinner: Spinner?) {
        val configJSONArray = arrayListOf<String>()

        val getJSONArray=MainActivity.doClientAction.getConfigJSONArrayEvent()

        for (i in 0 until getJSONArray.length()){
            val arrayString=getJSONArray.getString(i)
            configJSONArray.add(i,arrayString)
        }

        stringArray=configJSONArray
        val adapterTest = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stringArray
        )
        spinner?.adapter=adapterTest
    }

    override fun onDestroy() {
        super.onDestroy()
        receiveCheck = false
    }
}