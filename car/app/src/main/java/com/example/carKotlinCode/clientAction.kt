package com.example.myapplication

import com.example.carKotlinCode.jsonCommand
import com.example.carKotlinCode.socket_client
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashMap

class clientAction:Thread() {
    private val clientSocket = ClientSocket()
    private var serverConfigs: HashMap<String, Any>? = null
    private val frameBuffer = LinkedBlockingQueue<HashMap<String, Any>>()
    private val eventThread = Thread()

    fun activate() {
        this.eventThread.start()
    }

    private fun eventLoop() {
        while (this.clientSocket.isConnect()) {
            //預計更改為從buffer中poll出
            val commands = this.clientSocket.getCommand()
            if (commands.isEmpty()) {
                continue
            }
            this.event(commands)
        }
        /*
         * after while loop
         * shutdown app or back to homepage
         */
    }

    private fun event(command: HashMap<String, Any>) {
        /*
         * event function maybe return null or Map (send to server)
         */
        var returnValue: HashMap<String, Any>? = null
        when (command["CMD"]) {
            "FRAME" ->
                returnValue = this.frameEvent(command)
            "CONFIG" ->
                this.configsEvent(command)
            else -> return
            //escape this function

        }
        if (returnValue != null) {
            this.clientSocket.putCommand(returnValue)
        }
    }

    private fun frameEvent(command: HashMap<String, Any>): HashMap<String, Any>? {
        val jsonObject:JSONObject=JSONObject(command as HashMap<*, *>?)
        socket_client.frameBufferQueue.offer(jsonObject)
        return null
    }

    private fun configsEvent(command: HashMap<String, Any>): HashMap<String, Any>? {
        this.serverConfigs = command
        return null
    }

    fun getConfigsFromServer(): HashMap<String, Any>? {
        return this.serverConfigs
    }

    fun getFrameFromServer(): HashMap<String, Any>? {
        return null
    }

    fun isConnect(): Boolean {
        return this.clientSocket.isConnect()
    }

}