package com.example.carKonlinCode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        
    }

    fun toSelect(view: View){
        Toast.makeText(this,"item_select",Toast.LENGTH_LONG).show()
        val itemSelectIntent = Intent(this,item_select::class.java)
        startActivity(itemSelectIntent)
    }
    fun toQualitySwitch(view: View){
        Toast.makeText(this,"camera_switch",Toast.LENGTH_LONG).show()
        val itemSelectIntent = Intent(this,camera_switch::class.java)
        startActivity(itemSelectIntent)
    }
    fun setInfer (view: View){
        Toast.makeText(this,"infer_change",Toast.LENGTH_LONG).show()
        val itemSelectIntent = Intent(this,qualityChange::class.java)
        startActivity(itemSelectIntent)
    }
}