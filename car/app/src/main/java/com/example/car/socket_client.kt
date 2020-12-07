package com.example.car

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.logging.Handler
import kotlin.concurrent.thread

class socket_client:Thread() {
    private lateinit var handler:Handler
    var writer:PrintWriter?=null

    fun main(args: Array<String>){
        val address="127.0.0.1"
        val port =9999
        val client =Client(address,port)
        client.run()
    }

    class Client(address:String,port:Int) {
        val socket=Socket(address,port)
        private var socket_check:Boolean=true
        init{
            println("連接至$address 之 $port")
        }
        val reader:Scanner= Scanner(socket.getInputStream())
        val writer:OutputStream=socket.getOutputStream()

        fun run() {
            thread { read() }
            while (socket_check) {
                val input = readLine() ?: ""
                if ("exit" in input) {
                    socket_check=false
                    reader.close()
                    socket.close()

                } else {
                    write(input)
                }
            }

        }

        private fun write(message: String) {
            writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
        }

        private fun read() {
            while (socket_check)
                println(reader.nextLine())
        }
    }