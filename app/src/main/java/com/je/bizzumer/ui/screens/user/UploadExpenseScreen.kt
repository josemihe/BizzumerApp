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
import com.je.bizzumer.io.preferences_management.getTokenFromSharedPreferences
import com.je.bizzumer.io.response.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UploadExpenseScreen(navController: NavController){
    val token = getTokenFromSharedPreferences(LocalContext.current).toString()
    val groupId = remember {
        navController.previousBackStackEntry?.savedStateHandle?.get<String>("groupId")
    }
    val context = LocalContext.current
    val amount = remember { mutableStateOf(TextFieldValue()) }
    val description = remember { mutableStateOf(TextFieldValue()) }

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
                value = amount.value,
                onValueChange = { amount.value = it },
                label = {
                    Text(
                        text = "Amount spent",
                        style = MaterialTheme.typography.h6
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = {
                    Text(
                        text = "Description of the expense",
                        style = MaterialTheme.typography.h6
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val apiService = ApiService.create()
                    val call = apiService.uploadExpense(
                        token,
                        groupId.toString().toInt(),
                        amount.value.text.toDouble(),
                        description.value.text
                    )
                    call.enqueue(object : Callback<MessageResponse> {
                        override fun onResponse(
                            call: Call<MessageResponse>,
                            response: Response<MessageResponse>
                        ) {
                            if (response.isSuccessful) {
                                navController.popBackStack()
                            } else {
                                    val toast = Toast.makeText(context,"Couldn't upload the expense",Toast.LENGTH_SHORT)
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
                Text(text = "Upload Expense", style = MaterialTheme.typography.h5)
            }
        }
    }
}