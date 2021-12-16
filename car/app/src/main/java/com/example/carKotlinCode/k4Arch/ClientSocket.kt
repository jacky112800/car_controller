package com.example.myapplication

import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashMap


class ClientSocket {

    val inputBuffer = LinkedBlockingQueue<HashMap<String, Any>>()
    val outputBuffer = LinkedBlockingQueue<HashMap<String, Any>>()
    var connect = true
    var socket = null


    fun connect(address: String, port: Int) {
        /*
         * create socket class
         * Connect to Server
         * pass socket to activate
         */
    }

    private fun activate(client: Socket) {
        /*
         * create and start io thread
         * * clear buffer(reset function)
         * close socket in the end
         */
    }

    fun isConnect(): Boolean {
        /*
         * Get true if Socket Connecting else false
         * return this.connect
         */
        TODO("Not yet implemented")
    }

    private fun listeningClientSocket(client: Socket) {
        TODO("Not yet implemented")
        /*
         * while this.connect
         *     get message
         *     if message = null
         *        continue
         *
         *     if message["CMD"] = LOGOUT, EXIT, SHUTDOWN
         *        connect = false
         *     else
         */
    }

    private fun receiveMessage(client: Socket): HashMap<String, Any>? {
        TODO("Not yet implemented")
    }

    private fun receiveAll(client: Socket, bufferSize: Int): ByteBuffer {
        TODO("Not yet implemented")
    }

    private fun sending(client: Socket) {
        TODO("Not yet implemented")
    }

    private fun sendMessage(client: Socket) {
        TODO("Not yet implemented")
    }

    private fun logout() {
        TODO("Not yet implemented")
        /*
         * this.connect = false
         * clear I/O Buffer
         */
    }

    private fun reset() {
        /*
         reset
         */
    }

    fun getCommand(): HashMap<String, Any> {
        /*
         * get Command from inputBuffer
         */
        TODO("Not yet implemented")
    }

    fun putCommand(command: HashMap<String, Any>) {
        /*
         * put Command to outputBuffer
         */
        TODO("Not yet implemented")
    }

}