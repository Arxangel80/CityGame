import com.example.citygame.data.remote.PersistentCookieJar
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.HttpUrl
import org.json.JSONObject

class SocketIOManager(
    baseUrl: String,
    private val cookieJar: PersistentCookieJar
) {
    private val socket: Socket = IO.socket(baseUrl)

    private fun getCookieHeader(cookieJar: PersistentCookieJar, url: HttpUrl): String? {
        return (cookieJar as? PersistentCookieJar)?.getCookieHeader(cookieJar, url)
    }

    fun onConnect() {
        socket.on(Socket.EVENT_CONNECT) {
            println("Socket.IO connected")
        }
        socket.on("connected") { args ->
            val data = args[0] as JSONObject
            println("Message from server: ${data.getString("message")}")
        }
        socket.connect()
    }


    fun send(message: String) {
        socket.emit("message", message)
    }

    fun onDisconnect() {
        socket.disconnect()
    }
}
