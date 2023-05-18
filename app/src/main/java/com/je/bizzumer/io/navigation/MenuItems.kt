package com.je.bizzumer.io.navigation

import com.je.bizzumer.R

sealed class MenuItems(
    val icon: Int,
    val title: String,
    val route: String
){
    object UserMainItem: MenuItems(R.drawable.user,"home","user_main_screen")
    object CreateGroupItem: MenuItems(R.drawable.create, "Create Group", "create_group_screen")
    object JoinGroupItem: MenuItems(R.drawable.join, "Join Group", "join_group_screen")
    object LogoutItem: MenuItems(R.drawable.logout,"logout","home_screen")
}
