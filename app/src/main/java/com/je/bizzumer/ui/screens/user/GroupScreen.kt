package com.je.bizzumer.ui.screens.user


import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.preferences_management.getTokenFromSharedPreferences
import com.je.bizzumer.io.response.*
import com.je.bizzumer.model.Group
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

@Composable
fun GroupScreen(navController: NavController){
    val apiService = ApiService.create()
    val token = getTokenFromSharedPreferences(LocalContext.current).toString()
    val groupId = remember {
        navController.previousBackStackEntry?.savedStateHandle?.get<String>("groupId")
    }
    val context = LocalContext.current
    val groupState = remember { mutableStateOf(emptyList<Group>()) }
    val isDarkTheme = isSystemInDarkTheme()
    val titleColor = if (isDarkTheme) Color.Black else Color.White
    LaunchedEffect(key1 = token) {
        val call = apiService.getGroup(token,groupId.toString())
        call.enqueue(
            object: Callback<GroupsResponse>{
                override fun onResponse(
                    call: Call<GroupsResponse>,
                    response: Response<GroupsResponse>
                ) {
                    val groupRequest = response.body()
                    val group = groupRequest?.groups
                    if(group!=null){
                        groupState.value = group
                    }
                }

                override fun onFailure(call: Call<GroupsResponse>, t: Throwable) {
                    val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(titleColor)
    ) {
        LazyColumn {
            items(groupState.value) { group ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("groupId", group.id.toString())
                            navController.navigate(AppScreens.InviteMemberScreen.route)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        Text(text = "Invite members")
                    }
                    Button(
                        onClick = {
                            val leaveService = ApiService.create()
                            val authToken = getTokenFromSharedPreferences(context)
                            val call = leaveService.leaveGroup(authToken.toString(),group.id)

                            call.enqueue(object: Callback<MessageResponse> {
                                override fun onResponse(
                                    call: Call<MessageResponse>,
                                    response: Response<MessageResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val message = response.body()?.message
                                        if (message != null) {
                                            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            toast.show()
                                            navController.navigate(AppScreens.UserMainScreen.route)
                                        }
                                    } else {
                                        val toast = Toast.makeText(context, "There was an error", Toast.LENGTH_SHORT)
                                        toast.show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<MessageResponse>,
                                    t: Throwable
                                ) {
                                    val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                                    toast.show()
                                }

                            })
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        Text(text = "Leave Group", color = Color.White)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("groupId", group.id.toString())
                            navController.navigate(AppScreens.ExpensesScreen.route)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        Text(text = "Upload Expense")
                    }
                }
                SingleGroupCard(group = group,navController)
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun SingleGroupCard(group: Group, navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme()
    val titleColor = if (isDarkTheme) Color.Black else Color.White
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        elevation = 8.dp,
        backgroundColor = titleColor,
    ) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val formattedDate = dateFormat.format(group.date)
        val context = LocalContext.current
        val groupId = group.id
        val transactionsState = remember { mutableStateOf<List<Transaction>>(emptyList()) }


        LaunchedEffect(groupId) {
            getTransactions(groupId, navController, context) { expenses ->
                transactionsState.value = expenses ?: emptyList()
            }
        }

        val participantsState = remember { mutableStateListOf(*group.participants.toTypedArray()) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "Created: $formattedDate",
                style = MaterialTheme.typography.h6
            )
            group.comment?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h6
                )
            }
            Text(
                text = "Payments:",
                style = MaterialTheme.typography.h5
            )
            if (transactionsState.value.isNotEmpty()) {
                transactionsState.value.forEach { transaction ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = transaction.from,
                            style = MaterialTheme.typography.h6,
                            color = Color.Blue
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "to",
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = transaction.to,
                            style = MaterialTheme.typography.h6,
                            color = Color.Blue
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = transaction.amount.toString() + " €",
                        style = MaterialTheme.typography.h6,
                        color = Color.Red
                    )
                }
            } else {
                Text(
                    text = "No one owes money",
                    style = MaterialTheme.typography.h6
                )
            }
            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("groupId", group.id.toString())
                    navController.navigate(AppScreens.ShowExpensesScreen.route)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Text(text = "Check Expenses")
            }
            Text(
                text = "List of participants:",
                style = MaterialTheme.typography.h5
            )
            for (participant in participantsState) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = participant.name,
                        style = MaterialTheme.typography.h6
                    )
                    if(participant.id != group.ownerId){
                        Button(
                            onClick = {
                                val apiService = ApiService.create()
                                val token = getTokenFromSharedPreferences(context)
                                val call = apiService.removeParticipant(token.toString(),group.id,participant.id)
                                call.enqueue(object : Callback<MessageResponse> {
                                    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                                        val message = response.body()?.message
                                        if (response.isSuccessful) {
                                            if(message.toString() == "Participant successfully removed"){
                                                participantsState.remove(participant)
                                            }
                                        } else {
                                            val toast = Toast.makeText(context, "Error", Toast.LENGTH_SHORT)
                                            toast.show()
                                        }
                                    }
                                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                                        val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                                        toast.show()
                                    }
                                })
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(MaterialTheme.shapes.medium)) {
                            Text(text = "Remove", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun calculateExpenses(groupId: Int, navController: NavController, context: Context, callback: (List<Transaction>?) -> Unit) {
    val apiService = ApiService.create()
    val token = getTokenFromSharedPreferences(context)
    val call = apiService.calculateExpenses(token.toString(), groupId)
    call.enqueue(object : Callback<ExpenseCalculationResult> {
        override fun onResponse(
            call: Call<ExpenseCalculationResult>,
            response: Response<ExpenseCalculationResult>
        ) {
            val expenses = response.body()?.transactions
            callback(expenses ?: emptyList())
        }

        override fun onFailure(call: Call<ExpenseCalculationResult>, t: Throwable) {
            callback(emptyList())
        }
    })
}


private fun getTransactions(
    groupId: Int,
    navController: NavController,
    context: Context,
    callback: (List<Transaction>?) -> Unit
) {
    calculateExpenses(groupId, navController, context) { expenses ->
        val transactions = expenses ?: emptyList()
        callback(transactions)
    }
}