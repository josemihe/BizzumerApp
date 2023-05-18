package com.je.bizzumer.io.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.je.bizzumer.ui.screens.BodyContent
import com.je.bizzumer.ui.screens.login.LoginScreen
import com.je.bizzumer.ui.screens.login.RegisterScreen
import com.je.bizzumer.ui.screens.ResetPasswordRequestScreen
import com.je.bizzumer.ui.screens.user.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeScreen.route
    ){
        composable(route = AppScreens.HomeScreen.route){
            BodyContent(navController)
        }
        composable(route = AppScreens.LoginScreen.route){
            LoginScreen(navController)
        }
        composable(route = AppScreens.RegisterScreen.route){
            RegisterScreen(navController)
        }
        composable(route = AppScreens.ResetPasswordRequestScreen.route){
            ResetPasswordRequestScreen(navController)
        }
        composable(route = AppScreens.UserMainScreen.route){
            UserMainScreen(navController)
        }
        composable(route = AppScreens.GroupScreen.route){
            GroupScreen(navController)
        }
        composable(route= AppScreens.InviteMemberScreen.route){
            InviteMemberScreen(navController)
        }
        composable(route= AppScreens.JoinGroupScreen.route){
            JoinGroupScreen(navController)
        }
        composable(route = AppScreens.CreateGroupScreen.route){
            CreateGroupScreen(navController)
        }
        composable(route = AppScreens.ExpensesScreen.route){
            UploadExpenseScreen(navController)
        }
        composable(route = AppScreens.ShowExpensesScreen.route){
            ShowExpensesScreen(navController)
        }
    }
}
