package com.je.bizzumer.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.je.bizzumer.io.navigation.AppNavigation
import com.je.bizzumer.R
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.navigation.Destinations
import com.je.bizzumer.io.navigation.LoginItems
import com.je.bizzumer.io.navigation.MenuItems
import com.je.bizzumer.io.response.MessageResponse
import com.je.bizzumer.io.preferences_management.checkToken
import com.je.bizzumer.io.preferences_management.deleteToken
import com.je.bizzumer.io.preferences_management.getTokenFromSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val hostController = navController as NavHostController
    val context = LocalContext.current
    val menuItems = listOf(
        MenuItems.UserMainItem,
        MenuItems.JoinGroupItem,
        MenuItems.CreateGroupItem,
        MenuItems.LogoutItem,
    )
    val loginItems = listOf<LoginItems>(
        LoginItems.LoginItem
    )
    LaunchedEffect(Unit) {
        if (!permissionState.hasPermission) {
            permissionState.launchPermissionRequest()
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {TopBar(scope, scaffoldState, hostController)},
        drawerContent = {
            Drawer(
                scope,
                scaffoldState,
                hostController,
                menu_items = menuItems,
                login_items = loginItems
            )
        }
    ) {
        AppNavigation(hostController)
        if (checkToken(context)){
            hostController.navigate(AppScreens.UserMainScreen.route)
        }
    }
}


@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
    val context = LocalContext.current
    TopAppBar(
        title = { Text(text = "Bizzumer") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Icon"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (checkToken(context)){
                        navController.navigate(AppScreens.UserMainScreen.route) {
                            launchSingleTop = true
                        }
                    }
                    else{
                        navController.navigate(AppScreens.HomeScreen.route) {
                            launchSingleTop = true
                        }
                    }

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Go to home screen"
                )
            }
        }
    )
}

@Composable
fun Drawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    menu_items: List<MenuItems>,
    login_items: List<LoginItems>
) {
    val context = LocalContext.current
    Column {
        Image(
            painterResource(id = R.drawable.logo),
            contentDescription = "Menu",
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
        )
        val currentRoute = currentRoute(navController)
        if(checkToken(context)){
            menu_items.forEach { item ->
                when (item) {
                    is MenuItems.CreateGroupItem -> {
                        CreateGroupItem(
                            item = item,
                            selected = currentRoute == item.route,
                            navController = navController
                        )
                    }
                    is MenuItems.JoinGroupItem -> {
                        JoinGroupItem(
                            item = item,
                            selected = currentRoute == item.route,
                            navController = navController
                        )
                    }
                    is MenuItems.UserMainItem -> {
                        UserMainItem(
                            item = item,
                            selected = currentRoute == item.route,
                            navController = navController
                        )
                    }
                    is MenuItems.LogoutItem -> {
                        LogoutItem(
                            item = item,
                            selected = currentRoute == item.route,
                            context = LocalContext.current,
                        ) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                        }
                    }
                }
            }
        }
        else{
            login_items.forEach { item ->
                LoginItem(
                    item = LoginItems.LoginItem,
                    selected = currentRoute == item.route ,
                    navController = navController
                )
            }
        }

    }
}

@Composable
fun LogoutItem(
    item: MenuItems.LogoutItem,
    selected: Boolean,
    context: Context,
    onItemClick: (MenuItems) -> Unit
){
    val (isLoading) = remember { mutableStateOf(false) }
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(6.dp)
                .clip(RoundedCornerShape(12))
                .background(
                    if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.25f)
                    else Color.Transparent
                )
                .padding(8.dp)
                .clickable {
                    performLogout(context) {
                    }
                    onItemClick(item)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(id = item.icon),
                contentDescription = item.title
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = if (selected) MaterialTheme.colors.secondaryVariant
                else MaterialTheme.colors.onBackground
            )
        }
    }
}
@Composable
fun LoginItem(
    item: LoginItems.LoginItem,
    selected: Boolean,
    navController: NavController
){
    val (isLoading) = remember { mutableStateOf(false) }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(12))
                .background(
                    if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.25f)
                    else Color.Transparent
                )
                .padding(8.dp)
                .clickable {
                    navController.navigate(AppScreens.LoginScreen.route)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(id = item.icon),
                contentDescription = item.title
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.body1,
                color = if (selected) MaterialTheme.colors.secondaryVariant
                else MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
fun CreateGroupItem(
    item: MenuItems.CreateGroupItem,
    selected: Boolean,
    navController: NavController
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12))
            .background(
                if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .padding(8.dp)
            .clickable {
                //setIsLoading(true)
                navController.navigate(AppScreens.CreateGroupScreen.route)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(id = item.icon),
            contentDescription = item.title
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = if (selected) MaterialTheme.colors.secondaryVariant
            else MaterialTheme.colors.onBackground
        )
    }
}

@Composable
fun JoinGroupItem(
    item: MenuItems.JoinGroupItem,
    selected: Boolean,
    navController: NavController
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12))
            .background(
                if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .padding(8.dp)
            .clickable {
                //setIsLoading(true)
                navController.navigate(AppScreens.JoinGroupScreen.route)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(id = item.icon),
            contentDescription = item.title
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = if (selected) MaterialTheme.colors.secondaryVariant
            else MaterialTheme.colors.onBackground
        )
    }
}

@Composable
fun UserMainItem(
    item: MenuItems.UserMainItem,
    selected: Boolean,
    navController: NavController
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12))
            .background(
                if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .padding(8.dp)
            .clickable {
                //setIsLoading(true)
                navController.navigate(AppScreens.UserMainScreen.route)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(id = item.icon),
            contentDescription = item.title
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = if (selected) MaterialTheme.colors.secondaryVariant
            else MaterialTheme.colors.onBackground
        )
    }
}

private fun performLogout(context: Context, callback: (Boolean) -> Unit) {
    val apiService = ApiService.create()
    val token = getTokenFromSharedPreferences(context).toString()
    val call = apiService.postLogout("Bearer $token")
    call.enqueue(object : Callback<MessageResponse>{
        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
            val logoutResponse = response.body()
            val message = logoutResponse?.message
            if(message!=null){
                callback(true) // API call was successful
            }
            else {
                val toast = Toast.makeText(context, "There was an error", Toast.LENGTH_SHORT)
                toast.show()
                callback(false) // API call failed
            }
        }
        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
            // Network or server error, show an error message
            val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            callback(false) // API call failed
        }
    })
    deleteToken(context)
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun BodyContent(navController: NavController) {
    val isDarkTheme = isSystemInDarkTheme()
    val titleColor = if (isDarkTheme) Color.White else Color.Black
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Bizzumer",
            color = titleColor,
            fontSize = 48.sp,
            fontFamily = FontFamily(Font(R.font.proxima_nova_font)),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "The app to never have to think again when splitting your expenses",
            color = titleColor,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.proxima_nova_font)),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(R.drawable.bizzumer),
            contentDescription = "Bizzumer Logo",
            modifier = Modifier.size(320.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate(Destinations.LoginScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Log in")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { navController.navigate(Destinations.RegisterScreen.route)},
                modifier = Modifier.weight(1f)
            ) {
                Text("Register")
            }
        }
    }
}


