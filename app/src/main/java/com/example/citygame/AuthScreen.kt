package com.example.citygame

import AuthViewModel
import RegisterUiState
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citygame.ui.theme.CityGameTheme

enum class LoginScreens {
    logIn,
    signUp,
}

@Composable
fun LoginScreen(onNextButtonClicked: () -> Unit) {
    var loginScreensState by remember { mutableStateOf(LoginScreens.logIn) }
    val context = LocalContext.current
    val viewModel = remember { AuthViewModel(context) }

    CityGameTheme {
        if (loginScreensState == LoginScreens.logIn) {
            LoginDrawer(
                viewModel,
                onNextButtonClicked,
                onSignUpButtonClicked = { loginScreensState = LoginScreens.signUp })
        } else if (loginScreensState == LoginScreens.signUp) {
            BackHandler(enabled = loginScreensState != LoginScreens.logIn) {
                loginScreensState = LoginScreens.logIn
            }
            RegisterDrawer(viewModel)
        }
    }
}

@Composable
fun LoginDrawer(
    viewModel: AuthViewModel,
    onSuccesfullLogin: () -> Unit,
    onSignUpButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val loginCreds by viewModel.loginCredentials.collectAsState()

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is LoginUiState.Success -> {
                Toast.makeText(
                    context,
                    currentState.message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetLoginState()
                onSuccesfullLogin()
            }

            is LoginUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG)
                    .show()
                viewModel.resetLoginState()
            }

            else -> {}
        }
    }

    val isFormEnabled = uiState !is LoginUiState.Loading
    val isButtonEnabled by remember(loginCreds, uiState) {
        mutableStateOf(loginCreds.isNotEmpty() && isFormEnabled)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator()
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to the Quest",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                // Login field
                OutlinedTextField(
                    value = loginCreds.login,
                    onValueChange = { viewModel.updateLoginUsername(it) },
                    label = { Text("Login") },
                    singleLine = true,
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password Field
                OutlinedTextField(
                    value = loginCreds.pwd,
                    onValueChange = { viewModel.updateLoginPassword(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = KeyboardActions(onDone = {
                        if (isButtonEnabled) viewModel.login(onSuccesfullLogin)
                    })
                )

                Button(
                    onClick = {
                        if (loginCreds.isNotEmpty()) {
                            viewModel.login(onSuccesfullLogin)
                        }
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState is LoginUiState.Loading) "Loading..." else stringResource(id = R.string.LoginActionText))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.SignUp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable(enabled = isFormEnabled) { onSignUpButtonClicked() }
                )
            }
        }
    }
}


@Composable
fun RegisterDrawer(
    viewModel: AuthViewModel, // Используем AuthViewModel (или LoginViewModel, если не переименовали)
    onSuccessfulRegistration: () -> Unit = {}, // Для навигации после успеха
    onSwitchToLoginClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val registerState by viewModel.registerUiState.collectAsState()
    val registerCreds by viewModel.registerCredentials.collectAsState()

    LaunchedEffect(registerState) {
        when (val currentState = registerState) {
            is RegisterUiState.Success -> {
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                viewModel.resetRegisterState() // Сбрасываем состояние
                onSuccessfulRegistration()    // Выполняем действие после успеха
            }

            is RegisterUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetRegisterState() // Сбрасываем состояние
            }

            else -> {
                // Idle, Loading
            }
        }
    }

    val isFormEnabled = registerState !is RegisterUiState.Loading
    val isButtonEnabled by remember(registerCreds, registerState) {
        mutableStateOf(registerCreds.isNotEmpty() && isFormEnabled)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (registerState is RegisterUiState.Loading) {
                CircularProgressIndicator()
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign up",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                // login field
                OutlinedTextField(
                    value = registerCreds.login,
                    onValueChange = { viewModel.updateRegisterLogin(it) },
                    label = { Text("Login") },
                    singleLine = true,
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                // Email field
                OutlinedTextField(
                    value = registerCreds.email,
                    onValueChange = { viewModel.updateRegisterEmail(it) },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password field
                OutlinedTextField(
                    value = registerCreds.password,
                    onValueChange = { viewModel.updateRegisterPassword(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                // repeat password field
                OutlinedTextField(
                    value = registerCreds.confirmPassword,
                    onValueChange = { viewModel.updateConfirmRegisterPassword(it) },
                    label = { Text("Repeat password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = isFormEnabled,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isButtonEnabled) {
                                viewModel.register()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (registerCreds.isNotEmpty()) {
                            viewModel.register()
                        }
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (registerState is RegisterUiState.Loading) "Registering..." else stringResource(
                            id = R.string.RegisterActionText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.LoginInsteadText),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable(enabled = isFormEnabled) { onSwitchToLoginClicked() }
                )
            }
        }
    }
}