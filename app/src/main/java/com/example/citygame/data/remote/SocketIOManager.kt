import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.HttpUrl
import org.json.JSONObject

class SocketIOManager(
    private val baseUrl: String,
) {
    private var socket: Socket? = null


    fun send(message: String) {
        socket?.emit("message", message)
    }

    fun disconnect() {
        socket?.disconnect()
    }
}
