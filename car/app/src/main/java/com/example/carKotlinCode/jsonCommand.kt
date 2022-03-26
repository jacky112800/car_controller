package com.example.carKotlinCode

import org.json.JSONObject
import java.util.concurrent.TimeUnit

class jsonCommand : Thread() {
    val timeU = TimeUnit.MILLISECONDS

    fun loginJSON() {
        val loginJSONObject = JSONObject()
        loginJSONObject.put("CMD", "LOGIN")
        loginJSONObject.put("PWD", MainActivity.PWD)
        sendJsonToByteArray(loginJSONObject)
    }

    fun logoutJSON() {
        val logoutJSONObject = JSONObject()
        logoutJSONObject.put("CMD", "LOGOUT")
        sendJsonToByteArray(logoutJSONObject)
    }

    fun exitJSON() {
        val exitJSONObject = JSONObject()
        exitJSONObject.put("CMD", "EXIT")
        exitJSONObject.put("PWD", MainActivity.PWD)
        sendJsonToByteArray(exitJSONObject)
    }

    fun shutdownJSON() {
        val shutdownJSONObject = JSONObject()
        shutdownJSONObject.put("CMD", "SHUTDOWN")
        shutdownJSONObject.put("PWD", MainActivity.PWD)
        sendJsonToByteArray(shutdownJSONObject)
    }

    fun resetJSON() {
        val resetJSONObject = JSONObject()
        resetJSONObject.put("CMD", "RESET")
        sendJsonToByteArray(resetJSONObject)
    }

    fun getSystemInfo() {
        val getSystemInfoJSONObject = JSONObject()
        getSystemInfoJSONObject.put("CMD", "GET_SYS_INFO")
        sendJsonToByteArray(getSystemInfoJSONObject)
    }

    fun setStreamJSON(stream: Boolean) {
        val setStreamJSONObject = JSONObject()
        setStreamJSONObject.put("CMD", "SET_STREAM")
        setStreamJSONObject.put("STREAM", stream)
        sendJsonToByteArray(setStreamJSONObject)
    }

    fun getConfigJSON() {
        val getConfigJSONObject = JSONObject()
        getConfigJSONObject.put("CMD", "GET_CONFIGS")
        sendJsonToByteArray(getConfigJSONObject)
    }

    fun setConfigJSON(configItem: String) {
        val setConfigJSONObject = JSONObject()
        setConfigJSONObject.put("CMD", "SET_CONFIG")
        setConfigJSONObject.put("CONFIG", configItem)
        sendJsonToByteArray(setConfigJSONObject)
    }

    fun setInferJSON(infer: Boolean) {
        val setInferJSONObject = JSONObject()
        setInferJSONObject.put("CMD", "SET_INFER")
        setInferJSONObject.put("INFER", infer)
        sendJsonToByteArray(setInferJSONObject)
    }

    fun setQualityJSON(width: String, height: String) {
        val qualityJSONObject = JSONObject()
        qualityJSONObject.put("CMD", "SET_QUALITY")
        qualityJSONObject.put("WIDTH", width)
        qualityJSONObject.put("HEIGHT", height)
        sendJsonToByteArray(qualityJSONObject)
    }

    fun movJSON(strength: Float, angle: Int) {
        val moveJSONObject = JSONObject()
        moveJSONObject.put("CMD", "MOV")
        moveJSONObject.put("THETA", angle)
        moveJSONObject.put("R", strength)
        sendJsonToByteArray(moveJSONObject)
    }

    private fun sendJsonToByteArray(jsonObject: JSONObject) {
        socket_client.outputQueue.offer(jsonObject.toString(), 1000, timeU)
    }
}