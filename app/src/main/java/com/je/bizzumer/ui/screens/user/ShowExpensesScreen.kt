package com.je.bizzumer.ui.screens.user

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.je.bizzumer.io.response.ExpensesList
import com.je.bizzumer.io.response.MessageResponse
import com.je.bizzumer.model.Expense
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ShowExpensesScreen(navController: NavController){
    val groupId = remember {
        navController.previousBackStackEntry?.savedStateHandle?.get<String>("groupId")
    }
    val token = getTokenFromSharedPreferences(LocalContext.current).toString()
    val context = LocalContext.current
    val expensesState = remember { mutableStateOf(emptyList<Expense>()) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        var expenses: List<Expense>
        val apiService = ApiService.create()
        val call = groupId?.let { apiService.showExpenses(token, it.toInt()) }
        call?.enqueue(object : Callback<ExpensesList> {
            override fun onResponse(call: Call<ExpensesList>, response: Response<ExpensesList>) {
                expenses = response.body()?.expenses ?: emptyList()
                Log.d("EXPENSES",expenses.toString())
                expensesState.value = expenses
            }

            override fun onFailure(call: Call<ExpensesList>, t: Throwable) {
                val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                toast.show()
            }
        })
        GetExpenses(expensesState.value,navController, context)
    }
}
@Composable
private fun GetExpenses(expenses: List<Expense>, navController: NavController, context: Context) {
    LazyColumn {
        items(expenses) { expense ->
            ExpenseItem(expense = expense, navController, context)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, navController: NavController, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Amount: ${expense.amount}", style = MaterialTheme.typography.h6)
            Text(text = "Description: ${expense.description ?: "No description"}", style = MaterialTheme.typography.h6)
            Text(text = "Expense made at: ${expense.created_at}", style = MaterialTheme.typography.h6)
            Text(text = "Paid by: ${expense.user_id}", style = MaterialTheme.typography.h6)
            Button(
                onClick = {
                    deleteExpense(navController, context, expense.id)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier = Modifier
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}

private fun deleteExpense(navController: NavController, context: Context, expenseId: Int){
    val apiService = ApiService.create()
    val token = getTokenFromSharedPreferences(context).toString()
    val call = apiService.deleteExpense(token,expenseId)
    call.enqueue(object: Callback<MessageResponse>{
        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
            val message = response.body()?.message
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
            navController.navigate(AppScreens.ShowExpensesScreen.route) {
                popUpTo(AppScreens.ShowExpensesScreen.route) {
                    inclusive = true
                }
            }
        }

        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
            val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
            toast.show()
        }

    })
}