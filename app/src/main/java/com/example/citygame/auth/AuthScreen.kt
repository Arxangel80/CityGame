package com.example.citygame.auth

import AuthViewModel
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citygame.R
import com.example.citygame.ui.theme.CityGameTheme

enum class LoginScreens {
    LogIn,
    SignUp,
}

@Composable
fun LoginScreen(onNextButtonClicked: () -> Unit) {
    var loginScreensState by remember { mutableStateOf(LoginScreens.LogIn) }
    val viewModel: AuthViewModel = viewModel()

    CityGameTheme {
        if (loginScreensState == LoginScreens.LogIn) {
            LoginDrawer(
                viewModel,
                onNextButtonClicked,
                onSignUpButtonClicked = { loginScreensState = LoginScreens.SignUp })
        } else if (loginScreensState == LoginScreens.SignUp) {
            BackHandler(enabled = loginScreensState != LoginScreens.LogIn) {
                loginScreensState = LoginScreens.LogIn
            }
            RegisterDrawer(viewModel)
        }
    }
}

@Composable
fun LoginDrawer(
    viewModel: AuthViewModel,
    onSuccessfulLogin: () -> Unit,
    onSignUpButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val loginCreds by viewModel.loginCredentials.collectAsState()

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is AuthViewModel.LoginUiState.Success -> {
                Toast.makeText(
                    context,
                    currentState.message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetLoginState()
                onSuccessfulLogin()
            }

            is AuthViewModel.LoginUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG)
                    .show()
                viewModel.resetLoginState()
            }

            else -> {}
        }
    }

    val isFormEnabled = uiState !is AuthViewModel.LoginUiState.Loading
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
            if (uiState is AuthViewModel.LoginUiState.Loading) {
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
                        if (isButtonEnabled) viewModel.login(onSuccessfulLogin)
                    })
                )

                Button(
                    onClick = {
                        if (loginCreds.isNotEmpty()) {
                            viewModel.login(onSuccessfulLogin)
                        }
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState is AuthViewModel.LoginUiState.Loading) "Loading..." else stringResource(
                            id = R.string.LoginActionText
                        )
                    )
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
    viewModel: AuthViewModel,
    onSuccessfulRegistration: () -> Unit = {},
    onSwitchToLoginClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val registerState by viewModel.registerUiState.collectAsState()
    val registerCreds by viewModel.registerCredentials.collectAsState()

    LaunchedEffect(registerState) {
        when (val currentState = registerState) {
            is AuthViewModel.RegisterUiState.Success -> {
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                viewModel.resetRegisterState()
                onSuccessfulRegistration()
            }

            is AuthViewModel.RegisterUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetRegisterState()
            }

            else -> {
                // Idle, Loading
            }
        }
    }

    val isFormEnabled = registerState !is AuthViewModel.RegisterUiState.Loading
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
            if (registerState is AuthViewModel.RegisterUiState.Loading) {
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
                        if (registerState is AuthViewModel.RegisterUiState.Loading) "Registering..." else stringResource(
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