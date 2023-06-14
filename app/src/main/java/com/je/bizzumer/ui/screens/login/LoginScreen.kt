package com.je.bizzumer.ui.screens.login

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.je.bizzumer.R
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.navigation.Destinations
import com.je.bizzumer.io.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var passwordVisibility by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primaryVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(text = "Email Address", style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text(text = "Password", style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val visibilityIcon = if (passwordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = visibilityIcon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { performLogin(context, emailState.value.text, passwordState.value.text, navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Login", style = MaterialTheme.typography.h5)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate(Destinations.ResetPasswordRequestScreen.route) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Forgot Password?", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.h5)
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = { navController.navigate(Destinations.RegisterScreen.route) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Register Now", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.h5)
            }
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Bizzumer Logo",
                modifier = Modifier.size(320.dp)
            )
        }
    }
}


private fun performLogin(context: Context, email: String, password: String,navController: NavController) {
    val apiService = ApiService.create()
    val call = apiService.postLogin(email, password)

    call.enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            val loginResponse = response.body()
            val token = loginResponse?.token
            if (token != null) {
                com.je.bizzumer.io.preferences_management.saveToken(token,context)
                navController.navigate(AppScreens.UserMainScreen.route)
            }
            else {
                // Login failed, show an error message
                val toast = Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            // Network or server error, show an error message
            val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
            toast.show()
        }
    })
}
