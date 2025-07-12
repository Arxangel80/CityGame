import android.util.Log
import com.example.citygame.data.remote.PersistentCookieJar
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.HttpUrl
import org.json.JSONObject

class SocketIOManager(
    private val baseUrl: String,
    val cookieJar: PersistentCookieJar
) {
    private var socket: Socket? = null

    private fun getCookieHeader(url: HttpUrl): String? {
        return cookieJar.loadForRequest(url).joinToString("; ") { "${it.name}=${it.value}" }
    }

    fun connect(url: HttpUrl, onMessage: (String) -> Unit) {
        val cookieHeader = getCookieHeader(url) ?: ""

        val opts = IO.Options().apply {
            extraHeaders = mapOf("Cookie" to listOf(cookieHeader))
        }

        socket = IO.socket(baseUrl, opts)

        socket?.apply {
            on(Socket.EVENT_CONNECT) {
                Log.i("SocketIO", "Socket.IO connected")
            }
            on("connected") { args ->
                val data = args.getOrNull(0)
                onMessage(data?.toString() ?: "")
            }
            connect()
        }
    }

    fun send(message: String) {
        socket?.emit("message", message)
    }

    fun disconnect() {
        socket?.disconnect()
    }
}
