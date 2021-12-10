package com.example.carKonlinCode

import org.json.JSONObject

class jsonCommand:Thread() {
    fun makeCmdJSON(cmd: String){
        val cmdJSONObject=JSONObject()
        cmdJSONObject.put("CMD",cmd)
        MainActivity.th.sendJsonToByteArray(cmdJSONObject)
    }
    fun loginJSON(pwd:String){
        val loginJSONObject=JSONObject()
        loginJSONObject.put("CMD", "LOGIN")
        loginJSONObject.put("PWD", pwd)
    }
    fun movJSON(L:Double,R:Double){
        val moveJSONObject = JSONObject()
        moveJSONObject.put("CMD", "MOV")
        moveJSONObject.put("L", L)
        moveJSONObject.put("R", R)
    }
    fun setStreamJSON(stream:Boolean){
        val setStreamJSONObject=JSONObject()
        setStreamJSONObject.put("CMD", "SET_STREAM")
        setStreamJSONObject.put("STREAM", stream)
    }
    fun setConfigJSON(config:String){
        val setConfigJSONObject=JSONObject()
        setConfigJSONObject.put("CMD", "SET_CONFIG")
        setConfigJSONObject.put("CONFIG", config)
    }
    fun setInferJSON(stream:Boolean){
        val setInferJSONObject=JSONObject()
        setInferJSONObject.put("CMD", "SET_STREAM")
        setInferJSONObject.put("STREAM", stream)
    }
    fun setQualityJSON(width:Int,hight:Int){
        val qualityJSONObject = JSONObject()
        qualityJSONObject.put("CMD", "SET_QUALITY")
        qualityJSONObject.put("WIDTH", width)
        qualityJSONObject.put("HEIGHT", hight)
    }
}