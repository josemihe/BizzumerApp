package com.je.bizzumer.ui.screens.user

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.response.GroupsResponse
import com.je.bizzumer.io.preferences_management.getTokenFromSharedPreferences
import com.je.bizzumer.model.Group
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

@Composable
fun UserMainScreen(navController: NavController) {
    val apiService = ApiService.create()
    val token = getTokenFromSharedPreferences(LocalContext.current).toString()
    val context = LocalContext.current

    // Hold the state of the groups data
    val groupsState = remember { mutableStateOf(emptyList<Group>()) }

    // Retrieve the list of groups from the API
    LaunchedEffect(key1 = token) {
        val call = apiService.getAllGroups(token)
        call.enqueue(object:  Callback<GroupsResponse> {
            override fun onResponse(
                call: Call<GroupsResponse>,
                response: Response<GroupsResponse>
            ) {
                val groupsRequest = response.body()
                val groups = groupsRequest?.groups
                if(groups!=null){
                    groupsState.value = groups
                }
            }

            override fun onFailure(call: Call<GroupsResponse>, t: Throwable) {
                val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                toast.show()
            }

        })

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { navController.navigate(AppScreens.CreateGroupScreen.route) },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        Text(text = "Create a Group")
                    }

                    Button(
                        onClick = { navController.navigate(AppScreens.JoinGroupScreen.route) },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        Text(text = "Join a Group")
                    }
                }
                Text(
                    text = "Groups you participate in: ",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(groupsState.value) { group ->
                GroupCard(group = group,navController)
            }
        }
    }
}


@SuppressLint("SimpleDateFormat")
@Composable
private fun GroupCard(group: Group, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        elevation = 8.dp,
        backgroundColor = Color.LightGray,
    ) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val formattedDate = dateFormat.format(group.date)
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "Created: $formattedDate",
                style = MaterialTheme.typography.body1
            )
            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("groupId", group.id.toString())
                    navController.navigate(AppScreens.GroupScreen.route)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Text(text = "View")
            }
        }
    }
}