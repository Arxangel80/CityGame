import android.nfc.NdefMessage
import android.nfc.NdefRecord
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.nfc.Tag
import android.nfc.tech.Ndef

class NFCViewModel : ViewModel() {
    private var tagToWrite: Tag? = null
    fun setTagToWrite(tag: Tag?) {
        tagToWrite = tag
    }

    private var _readedMsg = MutableLiveData<String>()
    val readedMsg = _readedMsg

    fun setReadedMsg(msg: String) {
        _readedMsg.value = msg
    }
}
