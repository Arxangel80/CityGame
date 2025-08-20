import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.data.NetworkModule.apiService
import com.example.citygame.data.remote.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


class LoginViewModel(
    app: Application
) : AndroidViewModel(app) {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginCredentials = MutableStateFlow(LoginCredentials())
    val loginCredentials: StateFlow<LoginCredentials> = _loginCredentials.asStateFlow()

    fun updateLoginUsername(login: String) {
        _loginCredentials.value = _loginCredentials.value.copy(login = login)
    }

    fun updateLoginPassword(password: String) {
        _loginCredentials.value = _loginCredentials.value.copy(pwd = password)
    }

    fun login(onSuccess: () -> Unit) {
        val currentLoginCreds = _loginCredentials.value
        if (!currentLoginCreds.isNotEmpty()) {
            _uiState.value = LoginUiState.Error("Enter login and password")
            return
        }
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            // DEBUG ONLY!
            if (currentLoginCreds.login == "Dias" && currentLoginCreds.pwd == "Loh") {
                onSuccess()
            }
            try {
                val response = apiService.loginUser(
                    LoginRequest(
                        name = currentLoginCreds.login,
                        password = currentLoginCreds.pwd
                    )
                )
                if (response.isSuccessful) {
                    response.body()?.accessToken?.let { token ->
                        UserPreferences.saveAccessToken(context, token)
                    }
                    _uiState.value = LoginUiState.Success("Logged in!")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorJson).getString("message")
                    } catch (e: Exception) {
                        "Login error (${response.code()})"
                    }
                    _uiState.value = LoginUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetLoginState() {
        _uiState.value = LoginUiState.Idle
        _loginCredentials.value = LoginCredentials()
    }

    data class LoginCredentials(
        val login: String = "Dias",
        val pwd: String = "Loh"
    ) {
        fun isNotEmpty(): Boolean {
            return login.isNotBlank() && pwd.isNotBlank()
        }
    }

    sealed class LoginUiState {
        object Idle : LoginUiState()
        object Loading : LoginUiState()
        data class Success(val message: String) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }
}