import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.citygame.R
import okhttp3.HttpUrl

data class GridItem(
    val picture: Int,
    val title: String,
    val description: String
)

class QuestsViewModel(
    private val socketManager: SocketIOManager
) : ViewModel() {

    val items = mutableStateListOf<GridItem>()

    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    private val url = HttpUrl.Builder()
        .scheme("http")
        .host("192.168.0.17")
        .port(5000)
        .build()

    init {
        startSocket()
        loadGridItems()
    }

    private fun loadGridItems() {
        val pictures = listOf(R.drawable.cit, R.drawable.pp, R.drawable.cit)
        val titles = listOf("EiT Faculty game", "Campus game", "Text 2", "Text 3")
        val descriptions = listOf(
            "Experience an immersive mobile quest through the rich history of your alma mater.",
            "Dive into a captivating journey through history of PUT.",
            "Description Text 2",
            "Description Text 3"
        )

        repeat(10) { index ->
            items.add(
                GridItem(
                    picture = pictures[index % pictures.size],
                    title = titles[index % titles.size],
                    description = descriptions[index % descriptions.size]
                )
            )
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun startSocket() {
        socketManager.connect(url) { message ->
            _toastMessage.value = message
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }
}
