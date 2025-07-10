import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import okhttp3.HttpUrl

class QuestsViewModel(
    private val socketManager: SocketIOManager
) : ViewModel() {

    private val _messages = mutableStateListOf<String>()
    val messages: List<String> = _messages

    init {
        startSocket()
    }

    private fun startSocket() {
        val url = HttpUrl.Builder()
            .scheme("http")
            .host("192.168.0.17")
            .port(5000)
            .build()

        socketManager.onConnect()
    }
}
