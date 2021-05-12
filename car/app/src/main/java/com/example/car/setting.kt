package com.example.car

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

    fun to_select(view: View){
        Toast.makeText(this,"item_select",Toast.LENGTH_LONG).show()
        val itemSelect_intent = Intent(this,item_select::class.java)
        startActivity(itemSelect_intent)
    }
    fun to_quality_switch(view: View){
        Toast.makeText(this,"camera_switch",Toast.LENGTH_LONG).show()
        val itemSelect_intent = Intent(this,camera_switch::class.java)
        startActivity(itemSelect_intent)
    }
    fun set_infer (view: View){
        Toast.makeText(this,"infer_change",Toast.LENGTH_LONG).show()
        val itemSelect_intent = Intent(this,infer_change::class.java)
        startActivity(itemSelect_intent)
    }
}