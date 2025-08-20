import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citygame.data.NetworkModule.apiService
import com.example.citygame.data.remote.ApiService
import com.example.citygame.data.remote.LoginRequest
import com.example.citygame.data.remote.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel(
    app: Application,
) : AndroidViewModel(app) {
    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    private val _registerCredentials = MutableStateFlow(RegisterCredentials())
    val registerCredentials: StateFlow<RegisterCredentials> = _registerCredentials.asStateFlow()

    // --- Field updates ---
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

    // --- Registration ---
    fun register() {
        viewModelScope.launch {
            _registerUiState.value = RegisterUiState.Loading
            val creds = _registerCredentials.value

            if (!creds.isNotEmpty()) {
                _registerUiState.value = RegisterUiState.Error("Please fill in all fields.")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(creds.email).matches()) {
                _registerUiState.value = RegisterUiState.Error("Invalid email format.")
                return@launch
            }
            if (!creds.passwordsMatch()) {
                _registerUiState.value = RegisterUiState.Error("Passwords do not match.")
                return@launch
            }

            try {
                val response = apiService.registerUser(
                    RegisterRequest(
                        name = creds.login,
                        email = creds.email,
                        password = creds.password
                    )
                )
                if (response.isSuccessful && response.body()?.status == "success") {
                    _registerUiState.value = RegisterUiState.Success("Registration successful!")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val message = try {
                        JSONObject(errorJson!!).getString("message")
                    } catch (e: Exception) {
                        "Registration error (${response.code()})"
                    }
                    _registerUiState.value = RegisterUiState.Error(message)
                }
            } catch (e: Exception) {
                _registerUiState.value =
                    RegisterUiState.Error(e.message ?: "Network error during registration")
            }
        }
    }

    // --- Reset state ---
    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState.Idle
    }

    // --- Data classes ---
    data class RegisterCredentials(
        val login: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = ""
    ) {
        fun isNotEmpty(): Boolean {
            return login.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
        }

        fun passwordsMatch(): Boolean = password == confirmPassword
    }

    sealed class RegisterUiState {
        object Idle : RegisterUiState()
        object Loading : RegisterUiState()
        data class Success(val message: String) : RegisterUiState()
        data class Error(val message: String) : RegisterUiState()
    }
}
