package com.je.bizzumer.io.navigation

sealed class AppScreens(val route: String) {
    object HomeScreen: AppScreens("home_screen")
    object LoginScreen: AppScreens("login_screen")
    object RegisterScreen: AppScreens("register_screen")
    object ResetPasswordRequestScreen: AppScreens("reset_password_screen")
    object UserMainScreen: AppScreens("user_main_Screen")
    object GroupScreen: AppScreens("group_screen")
    object InviteMemberScreen: AppScreens("invite_member_screen")
    object JoinGroupScreen: AppScreens("join_group_screen")
    object CreateGroupScreen: AppScreens("create_group_screen")
    object ExpensesScreen: AppScreens("expenses_screen")
    object ShowExpensesScreen: AppScreens("show_expenses_screen")
}