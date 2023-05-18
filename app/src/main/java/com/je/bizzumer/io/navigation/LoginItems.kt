package com.je.bizzumer.io.navigation

import com.je.bizzumer.R

sealed class LoginItems(
    val icon: Int,
    val title: String,
    val route: String
){
    object LoginItem: LoginItems(R.drawable.login,"login","login_screen")
}

