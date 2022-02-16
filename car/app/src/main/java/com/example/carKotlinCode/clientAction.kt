import com.example.carKotlinCode.MainActivity
import com.example.carKotlinCode.socket_client
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class clientAction : Thread() {
    companion object {
//        val frameBufferQueue = LinkedBlockingQueue<JSONObject>()
    }

    //    private val clientSocket = socket_client()
    private var timeU: TimeUnit = TimeUnit.MILLISECONDS
    private var configJSONArray:JSONArray= JSONArray()


    fun verify(): Boolean {
        try {
            sleep(100)
            return if (!socket_client.inputQueue.isNullOrEmpty()) {
                val loginInfo = socket_client.inputQueue.poll(1000, timeU)
                when (loginInfo.getBoolean("VERIFY")) {
                    true -> {
                        println("驗證成功")
                        true
                    }
                    false -> {
                        println("驗證失敗")
                        MainActivity.th.closeSocket()
                        false
                    }
                }
            }else{
                println("驗證失敗(null)")
                MainActivity.th.closeSocket()
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun isConnection(): Boolean {
        return MainActivity.th.isConnection()
    }

    override fun run() {
        //event loop
        while (this.isConnection()) {
            if (!socket_client.inputQueue.isNullOrEmpty()) {
                val jsonObject = socket_client.inputQueue.poll(1000, timeU)
                event(jsonObject)
            }
            sleep(10)
        }
    }

    private fun event(jsonObject: JSONObject) {
        val jsonObjectCmd = jsonObject.getString("CMD")
        when (jsonObjectCmd) {
            "FRAME" -> frameEvent(jsonObject)
            "CONFIGS" -> configsEvent(jsonObject)
            "SYS_LOGOUT" -> logoutEvent()
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
        configJSONArray = jsonObject.getJSONArray("CLASSES")
    }

    private fun logoutEvent() {

    }

    fun getConfigJSONArrayEvent(): JSONArray {
        return configJSONArray
    }

}