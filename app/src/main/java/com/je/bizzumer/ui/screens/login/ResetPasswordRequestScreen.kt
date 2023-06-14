package com.je.bizzumer.ui.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.response.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ResetPasswordRequestScreen(navController: NavController) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val newPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val newPasswordConfirmationState = remember { mutableStateOf(TextFieldValue()) }
    val passwordResetToken = remember { mutableStateOf(TextFieldValue()) }
    var passwordVisibility by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text(text = "Email Address", style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { sendEmail(context, emailState.value.text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Send reset email", style = MaterialTheme.typography.h5)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPasswordState.value,
                onValueChange = { newPasswordState.value = it },
                label = { Text(text = "New Password", style = MaterialTheme.typography.h6) },
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

            OutlinedTextField(
                value = newPasswordConfirmationState.value,
                onValueChange = { newPasswordConfirmationState.value = it },
                label = { Text(text = "Repeat the new password", style = MaterialTheme.typography.h6) },
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

            OutlinedTextField(
                value = passwordResetToken.value,
                onValueChange = { passwordResetToken.value = it },
                label = { Text(text = "Put your reset token here", style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { changePassword(context, newPasswordState.value.text, newPasswordConfirmationState.value.text, passwordResetToken.value.text,navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Change Password", style = MaterialTheme.typography.h5)
            }
        }
    }
}

fun changePassword(context: Context, password: String, confirmPassword: String, resetToken:String, navController: NavController) {
    val apiService = ApiService.create()
    val call = apiService.updatePassword(password,confirmPassword,resetToken)

    call.enqueue(object: Callback<MessageResponse> {
        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>){
            val resetRequestResponse = response.body()
            val message = resetRequestResponse?.message
            if(message!=null){
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreens.HomeScreen.route)
            }
            else{
                Toast.makeText(context, "The token was no valid or passwords didn't match", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
            val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
            toast.show()
        }
    })
}

private fun sendEmail(context: Context, email: String){
    val apiService = ApiService.create()
    val call = apiService.postResetMail(email)
    call.enqueue(object: Callback<MessageResponse> {
        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>){
            val resetRequestResponse = response.body()
            val message = resetRequestResponse?.message
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, "There was an error", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
            val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
            toast.show()
        }
    })
}