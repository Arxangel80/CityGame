package com.example.citygame.auth

import LoginViewModel
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citygame.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClicked: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val loginCreds by viewModel.loginCredentials.collectAsState()

    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is LoginViewModel.LoginUiState.Success -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetLoginState()
                onLoginSuccess()
            }

            is LoginViewModel.LoginUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetLoginState()
            }

            else -> {}
        }
    }

    val isFormEnabled = uiState !is LoginViewModel.LoginUiState.Loading
    val isButtonEnabled =
        loginCreds.login.isNotEmpty() && loginCreds.pwd.isNotEmpty() && isFormEnabled

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
            if (uiState is LoginViewModel.LoginUiState.Loading) {
                CircularProgressIndicator()
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        if (isButtonEnabled) viewModel.login(onLoginSuccess)
                    })
                )

                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState is LoginViewModel.LoginUiState.Loading) "Loading..."
                        else stringResource(id = R.string.LoginActionText)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.SignUp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable(enabled = isFormEnabled) {
                        onSignupClicked()
                    }
                )
            }
        }
    }
}
