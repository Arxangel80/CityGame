import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.nfc.Tag

class NFCViewModel : ViewModel() {
    private var _readedMsg = MutableLiveData<String>()
    val readedMsg = _readedMsg

    fun setReadedMsg(msg: String) {
        _readedMsg.value = msg
    }

    
}
