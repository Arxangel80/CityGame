import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.CityGameApp
import com.example.citygame.data.remote.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


class AuthViewModel(
    app: Application
) : AndroidViewModel(app) {
    val apiService = (app as CityGameApp).apiService

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
                if (response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = LoginUiState.Success(response.body()?.message ?: "Logged in!")
                    onSuccess()
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


    // --- Состояние и данные для РЕГИСТРАЦИИ ---
    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerUiState: StateFlow<RegisterUiState> =
        _registerUiState.asStateFlow() // Используем RegisterUiState

    private var _registerCredentials =
        MutableStateFlow(RegisterCredentials()) // Используем MutableStateFlow для Compose
    val registerCredentials: StateFlow<RegisterCredentials> = _registerCredentials.asStateFlow()


    fun updateRegisterLogin(login: String) {
        _registerCredentials.value = _registerCredentials.value.copy(login = login)
    }

    fun updateRegisterEmail(email: String) {
        _registerCredentials.value = _registerCredentials.value.copy(email = email)
    }

    fun updateRegisterPassword(password: String) {
        _registerCredentials.value = _registerCredentials.value.copy(password = password)
    }

    fun updateConfirmRegisterPassword(confirmPassword: String) {
        _registerCredentials.value =
            _registerCredentials.value.copy(confirmPassword = confirmPassword)
    }

    fun register() { // onSuccess коллбэк для регистрации будет обрабатываться через State в UI
        viewModelScope.launch {
            _registerUiState.value = RegisterUiState.Loading
            val currentRegisterCreds = _registerCredentials.value

            // --- Валидация (пример) ---
            if (!currentRegisterCreds.isNotEmpty()) {
                _registerUiState.value = RegisterUiState.Error("Please fill in all fields.")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentRegisterCreds.email)
                    .matches()
            ) {
                _registerUiState.value = RegisterUiState.Error("Invalid email format.")
                return@launch
            }
            if (currentRegisterCreds.password.length < 6) {
                _registerUiState.value =
                    RegisterUiState.Error("Password must be at least 6 characters.")
                return@launch
            }
            if (!currentRegisterCreds.passwordsMatch()) {
                _registerUiState.value = RegisterUiState.Error("Passwords do not match.")
                return@launch
            }

//            try {
//                val response = apiService.registerUser(
//                    RegisterRequest(
//                        name = currentRegisterCreds.login,
//                        email = currentRegisterCreds.email,
//                        password = currentRegisterCreds.password
//                    )
//                )
//                if (response.isSuccessful && response.body()?.status == "success") {
//                    _registerUiState.value = RegisterUiState.Success
//                    // Опционально: сбросить поля после успешной регистрации
//                    // _registerCredentials.value = RegisterCredentials()
//                } else {
//                    val errorJson = response.errorBody()?.string()
//                    val errorMessage = try {
//                        JSONObject(errorJson!!).getString("message")
//                    } catch (e: Exception) {
//                        "Registration error (${response.code()})"
//                    }
//                    _registerUiState.value = RegisterUiState.Error(errorMessage)
//                }
//            } catch (e: Exception) {
//                _registerUiState.value =
//                    RegisterUiState.Error(e.message ?: "Network error during registration")
//            }
        }
    }

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState.Idle
        _registerCredentials.value = RegisterCredentials()
    }

    // --- Data models ---
    data class LoginCredentials(
        val login: String = "Dias",
        val pwd: String = "Loh"
    ) {
        fun isNotEmpty(): Boolean {
            return login.isNotBlank() && pwd.isNotBlank()
        }
    }

    data class RegisterCredentials(
        val login: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = ""
    ) {
        fun isNotEmpty(): Boolean {
            return login.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
        }

        fun passwordsMatch(): Boolean {
            return password == confirmPassword
        }
    }


    // --- UI State ---
    sealed class LoginUiState {
        object Idle : LoginUiState()
        object Loading : LoginUiState()
        data class Success(val message: String) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

    sealed class RegisterUiState {
        object Idle : RegisterUiState()
        object Loading : RegisterUiState()
        data class Success(val message: String) : RegisterUiState()
        data class Error(val message: String) : RegisterUiState()
    }
}