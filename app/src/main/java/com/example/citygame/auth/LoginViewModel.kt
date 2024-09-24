import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.NetworkModule.apiService
import com.example.citygame.data.SocketManager
import com.example.citygame.data.local.UserPreferences
import com.example.citygame.data.remote.ApiErrorParser
import com.example.citygame.data.remote.LoginRequest
import com.example.citygame.navigation.AppScreens.routeForQuest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(
    app: Application
) : AndroidViewModel(app) {
    private val context = getApplication<Application>().applicationContext

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginCredentials = MutableStateFlow(LoginCredentials())
    val loginCredentials: StateFlow<LoginCredentials> = _loginCredentials.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    // Remember Me
    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()


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
                    response.body()?.access_token?.let { token ->
                        NetworkModule.setToken(token)

                        if (rememberMe.value) {
                            UserPreferences.saveAccessToken(context, token)
                        }

                        launch {
                            try {
                                val response = NetworkModule.apiService.getCurrentSession()
                                if (response.isSuccessful) {
                                    response.body()?.let { sessionData ->
                                        val sessionActive = sessionData.session_active
                                        val questName = sessionData.quest_name

                                        if (sessionActive) {
                                            _navigationEvent.emit(routeForQuest(questName!!))
                                        }

                                        SocketManager.connect()
                                    }
                                } else {
                                    Log.e("MainActivity", "Server error: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "MainActivity",
                                    "Network error fetching session: ${e.message}"
                                )
                            }
                        }
                    }
                    _uiState.value = LoginUiState.Success("Logged in!")
                    onSuccess()
                } else {
                    _uiState.value = LoginUiState.Error(ApiErrorParser.parseError(response))
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

    fun updateRememberMe(value: Boolean) {
        _rememberMe.value = value
    }

    data class LoginCredentials(
        val login: String = "User1",
        val pwd: String = "password1"
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