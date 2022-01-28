package com.example.myapplication

import com.example.carKotlinCode.MainActivity
import com.example.carKotlinCode.socket_client
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class clientAction : Thread() {
    companion object{
//        val frameBufferQueue = LinkedBlockingQueue<JSONObject>()
    }
    private val clientSocket = socket_client()
    private var timeU: TimeUnit = TimeUnit.MILLISECONDS

    fun verify(passWord: String) {
        if (!clientSocket.isConnection()) {
            return
        }
//        val login: JSONObject = CommandFactory.LOGIN()
//        login.put("PWD", passWord)
        try {
            MainActivity.doJsonCommand.loginJSON()
//            if (!loginInfo.getBoolean("VERIFY")) {
//                this.closeSocket(socket)
//            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isConnection(): Boolean {
        return clientSocket.isConnection()
    }

    override fun run() {
        //event loop
        while (this.isConnection()) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val jsonObject = socket_client.inputQueue.poll(1000,timeU)
                event(jsonObject)
            }
        }
    }

    private fun event(jsonObject: JSONObject) {
        val jsonObjectCmd = jsonObject.getString("CMD")
        when(jsonObjectCmd){
            "FRAME" -> frameEvent(jsonObject)

        }
        if (jsonObjectCmd == "FRAME") {
            frameEvent(jsonObject)
        } else if (jsonObjectCmd == "CONFIGS") {
            configsEvent(jsonObject)
        }
    }

    private fun frameEvent(jsonObject: JSONObject) {
        try {
            socket_client.frameBufferQueue.offer(jsonObject, 1000, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun configsEvent(jsonObject: JSONObject) {
//        this.configs = jsonObject
    }

//    fun getFrame(jsonObject: JSONObject?) {
//        try {
//            this.frameBuffer.poll(1000, TimeUnit.MILLISECONDS)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//    }

//    fun putCommand(jsonObject: JSONObject?) {
//        this.clientConnection.put(jsonObject)
//    }


//    fun getConfigs(): JSONObject? {
//        return this.configs
//    }


}