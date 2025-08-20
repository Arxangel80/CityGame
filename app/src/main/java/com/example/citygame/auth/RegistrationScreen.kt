package com.example.citygame.auth

import LoginViewModel
import RegisterViewModel
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citygame.R

@Composable
fun RegistrationScreen(
    onSuccessfulRegistration: () -> Unit = {},
    onLoginClicked: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val context = LocalContext.current

    val registerState by viewModel.registerUiState.collectAsState()
    val registerCreds by viewModel.registerCredentials.collectAsState()

    LaunchedEffect(key1 = registerState) {
        when (val currentState = registerState) {
            is RegisterViewModel.RegisterUiState.Success -> {
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                viewModel.resetRegisterState()
                onSuccessfulRegistration()
            }

            is RegisterViewModel.RegisterUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetRegisterState()
            }

            else -> { /* Idle or Loading */
            }
        }
    }

    val isFormEnabled = registerState !is RegisterViewModel.RegisterUiState.Loading
    val isButtonEnabled = registerCreds.isNotEmpty() && isFormEnabled

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
            if (registerState is RegisterViewModel.RegisterUiState.Loading) {
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

                OutlinedTextField(
                    value = registerCreds.login,
                    onValueChange = { viewModel.updateRegisterLogin(it) },
                    label = { Text("Login") },
                    singleLine = true,
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = registerCreds.email,
                    onValueChange = { viewModel.updateRegisterEmail(it) },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = registerCreds.password,
                    onValueChange = { viewModel.updateRegisterPassword(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth()
                )

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
                            if (isButtonEnabled) viewModel.register()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { viewModel.register() },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (registerState is RegisterViewModel.RegisterUiState.Loading) "Registering..."
                        else stringResource(id = R.string.RegisterActionText)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.LoginInsteadText),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable(enabled = isFormEnabled) {
                        onLoginClicked()
                    }
                )
            }
        }
    }
}
