package com.example.citygame

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citygame.ui.theme.CityGameTheme

enum class LoginScreens {
    logIn,
    signUp,
    forgotPassword
}
@Composable
fun LoginScreen(onNextButtonClicked: () -> Unit) {
    var LoginScreensState by remember { mutableStateOf(LoginScreens.logIn) }

    if (LoginScreensState == LoginScreens.logIn) {
        LoginDrawer(onNextButtonClicked, {LoginScreensState = LoginScreens.signUp})
    } else if (LoginScreensState == LoginScreens.signUp) {
        RegisterDrawer()
    }
}
data class LoginCredentials(
    var login: String = "",
    var pwd: String = ""
) {
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && pwd.isNotEmpty()
    }
}

data class RegisterCredentials(
    var login: String = "",
    var pwd: String = "",
    var email: String = "",
) {
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && pwd.isNotEmpty()
    }
}

fun checkCredentials(creds: LoginCredentials, context: Context): Boolean{
    return if (creds.isNotEmpty()) {
        true
    } else {
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)?)
{
    TextField(
        value = value,
        placeholder = placeholder,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent, unfocusedTextColor = Color.White)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentPasswordField(
    password: String,
    onProceed: () -> Unit,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)?) {

//    var isPasswordVisible by remember { mutableStateOf(false) }

////    val leadingIcon = @Composable {
////        Icon(
//////            Icons.Default.Key,
////            contentDescription = "",
////            tint = MaterialTheme.colorScheme.primary
////        )
////    }
////    val trailingIcon = @Composable {
////        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
////            Icon(
////                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
////                contentDescription = "",
//////                tint = MaterialTheme.colorScheme.primary
////        }
//    }

    TextField(
        value = password,
        placeholder = placeholder,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { onProceed() }
        ),
        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent,
            unfocusedTextColor = Color.White),
    )
}

fun submit(credentials:LoginCredentials, context:Context, proceed: () -> Unit) {
    if (!checkCredentials(credentials, context)) {
        credentials.login = ""
        credentials.pwd = ""
    }
    else {
        proceed()
    }
}

@Composable
fun LoginDrawer(onNextButtonClicked: () -> Unit, onSignUpButtonClicked: () -> Unit) {
    var credentials by remember { mutableStateOf(LoginCredentials()) }
    val context = LocalContext.current
    var isButtonEnabled by remember { mutableStateOf(credentials.isNotEmpty()) }

    CityGameTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Image(
//                    painter = painterResource(id = R.drawable.loginscreenimage),
//                    contentScale = ContentScale.FillBounds,
//                    contentDescription = "",
//                    modifier = Modifier.fillMaxSize()
//                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(space = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to the Quest",
                        fontSize = 30.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    TransparentTextField(
                        value = credentials.login,
                        onValueChange = { data ->
                            credentials = credentials.copy(login = data)
                            isButtonEnabled = credentials.isNotEmpty()
                        },
                        placeholder = {
                            Text(
                                "Login",
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    )
                    TransparentPasswordField(
                        password = credentials.pwd,
                        onValueChange = { data ->
                            credentials = credentials.copy(pwd = data)
                            isButtonEnabled = credentials.isNotEmpty()
                        },
                        placeholder = {
                            Text(
                                "Password",
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif
                            )
                        },
                        onProceed = { submit(credentials, context, onNextButtonClicked) })
                    Button(
                        onClick = { submit(credentials, context, onNextButtonClicked) },
                        enabled = isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(containerColor = if (isButtonEnabled) Color.Cyan else Color.Gray)
                    )
                    {
                        Text("Let's go!")
                    }
                    ClickableText(
                        style = TextStyle(color = Color.White),
                        text = AnnotatedString(stringResource(id = R.string.SignUp))
                    ) {
                        onSignUpButtonClicked()
                    }
                    ClickableText(
                        style = TextStyle(color = Color.White),
                        text = AnnotatedString(stringResource(id = R.string.ForgotPassword))
                    ) {
                        // Действия, выполняемые при нажатии кнопки

                    }
                }
            }
        }
    }
}

@Composable
fun RegisterDrawer() {
    var credentials by remember { mutableStateOf(RegisterCredentials()) }
    var isButtonEnabled by remember { mutableStateOf(credentials.isNotEmpty()) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Image(
//            painter = painterResource(id = R.drawable.loginscreenimage),
//            contentScale = ContentScale.FillBounds,
//            contentDescription = stringResource(id = R.string.LoginImageDesc),
//            modifier = Modifier.fillMaxSize()
//        )
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign up",
            fontSize = 30.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        TransparentTextField(
            value = credentials.login,
            onValueChange = { data ->
                credentials = credentials.copy(login = data)
                isButtonEnabled = credentials.isNotEmpty()
            },
            placeholder = { Text("Login", color = Color.White, fontFamily = FontFamily.SansSerif) }
        )

        TransparentTextField(
            value = credentials.email,
            onValueChange = { data ->
                credentials = credentials.copy(email = data)
                isButtonEnabled = credentials.isNotEmpty()
            },
            placeholder = { Text("Email", color = Color.White, fontFamily = FontFamily.SansSerif) }
        )
        TransparentPasswordField(
            password = credentials.pwd,
            onValueChange = { data ->
                credentials = credentials.copy(pwd = data)
                isButtonEnabled = credentials.isNotEmpty()
            },
            onProceed = {//TODO
            },
            placeholder = { Text("Password", color = Color.White, fontFamily = FontFamily.SansSerif) }
        )
        TransparentPasswordField(
            //TODO
            password = credentials.pwd,
            onValueChange = { data ->
                credentials = credentials.copy(pwd = data)
                isButtonEnabled = credentials.isNotEmpty()
            },
            onProceed = {//TODO
                        },
            placeholder = { Text("Repeat password", color = Color.White, fontFamily = FontFamily.SansSerif) }
        )
        Button(onClick = {//Todo
             },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonEnabled) Color.Cyan else Color.Gray)) {
            Text("Let's go to the Quest!")
        }
    }
    }
    }
}
//@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
//@Composable
//fun LoginFormPreview() {
//    CityGameTheme {
//        LoginDrawer({})
//        RegisterDrawer()
//    }
//}
//
//@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
//@Composable
//fun LoginFormPreviewDark() {
//    CityGameTheme(darkTheme = true) {
//        LoginDrawer({})
//    }
//}
