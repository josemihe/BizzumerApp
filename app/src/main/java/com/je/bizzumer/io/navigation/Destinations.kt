package com.je.bizzumer.io.navigation


sealed class Destinations(
    val icon: Int,
    val title: String,
    val route: String
){
    object LoginScreen: Destinations(0,"login","login_screen")
    object RegisterScreen: Destinations(0,"register","register_screen")
    object ResetPasswordRequestScreen: Destinations(0,"reset_password", "reset_password_screen")
}
