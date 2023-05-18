package com.je.bizzumer.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.preferences_management.getTokenFromSharedPreferences
import com.je.bizzumer.io.response.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun JoinGroupScreen(navController: NavController) {
    val token = getTokenFromSharedPreferences(LocalContext.current).toString()
    val accessCodeState = remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = accessCodeState.value,
                onValueChange = { accessCodeState.value = it },
                label = {
                    Text(
                        text = "Put the access code here",
                        style = MaterialTheme.typography.h6
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val apiService = ApiService.create()
                    val call = apiService.joinGroup(token, accessCodeState.value.text)
                    call.enqueue(object : Callback<MessageResponse> {
                        override fun onResponse(
                            call: Call<MessageResponse>,
                            response: Response<MessageResponse>
                        ) {
                            if (response.isSuccessful) {
                                val message = response.body()?.message
                                val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                toast.show()
                                navController.navigate(AppScreens.UserMainScreen.route)
                            }
                            else{
                                val toast = Toast.makeText(context, "Couldn't join the group", Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }

                        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                            val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                            toast.show()
                        }

                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Join Group", style = MaterialTheme.typography.h5)
            }
        }
    }
}

