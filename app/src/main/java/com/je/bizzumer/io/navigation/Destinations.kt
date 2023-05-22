package com.je.bizzumer.io.navigation


sealed class Destinations(
    val route: String
){
    object LoginScreen: Destinations("login_screen")
    object RegisterScreen: Destinations("register_screen")
    object ResetPasswordRequestScreen: Destinations("reset_password_screen")
}
