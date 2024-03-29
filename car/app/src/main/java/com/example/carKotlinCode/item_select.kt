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
    var receiveCheck = true
    var itemSpinner: Spinner? = null
    var selectSendButton: Button? = null
    var selectClassesString=""

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_select)
        itemSpinner = findViewById<Spinner>(R.id.item_select_spinner)
        selectSendButton = findViewById<Button>(R.id.btn_select_confirm)

        itemSelectSwitch()

    }

    override fun onStart() {
        super.onStart()
        spinnerChange(itemSpinner)
        itemConfigSend()
        showClasses()
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
                selectClassesString=spinnerSelectString
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerSelectString = ""
                selectClassesString=spinnerSelectString
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

        if (configJSONArray.isNullOrEmpty()){
            val sampleArray = arrayListOf<String>()
            val sampleJSONObject=JSONObject()
            val sampleJSONArray=JSONArray()
            sampleJSONArray.put("沒有接收到可用選項")
            sampleJSONObject.put("TEST",sampleJSONArray)
            println(sampleJSONObject.toString())

            val getJSONArrayNull=sampleJSONObject.getJSONArray("TEST")

            for (i in 0 until getJSONArrayNull.length()){
                val arrayString=getJSONArrayNull.getString(i)
                sampleArray.add(i,arrayString)
            }

            stringArray=sampleArray
        }
        else{
            stringArray=configJSONArray
        }
        val adapterTest = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stringArray
        )
        spinner?.adapter=adapterTest
    }

    fun showClasses(){
        val getJSONObject=MainActivity.doClientAction.getConfigJSONObjectEvent()
        btn_select_check.setOnClickListener{
            if (getJSONObject.length()>0){
                val modelJSONObject= getJSONObject.getJSONObject(selectClassesString)
                val modelClassesJSONArray=modelJSONObject.getJSONArray("CLASSES")
                Toast.makeText(this, "選擇物件:$modelClassesJSONArray", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "尚未選擇", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiveCheck = false
    }
}