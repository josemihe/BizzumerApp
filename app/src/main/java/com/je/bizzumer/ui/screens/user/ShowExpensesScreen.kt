package com.je.bizzumer.ui.screens.user

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.ImageLoader
import coil.request.ImageRequest
import com.je.bizzumer.io.ApiService
import com.je.bizzumer.io.navigation.AppScreens
import com.je.bizzumer.io.preferences_management.getTokenFromSharedPreferences
import com.je.bizzumer.io.response.ExpensesList
import com.je.bizzumer.io.response.MessageResponse
import com.je.bizzumer.model.Expense
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.DateFormat


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
        val expenseDateTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(expense.created_at)
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Amount: ${expense.amount}", style = MaterialTheme.typography.h6)
            Text(text = "Description: ${expense.description ?: "No description"}", style = MaterialTheme.typography.h6)
            Text(text = "Expense made at: $expenseDateTime", style = MaterialTheme.typography.h6)
            Text(text = "Paid by: ${expense.user_name}", style = MaterialTheme.typography.h6)

            ImageOrUploadButton(groupId = expense.group_id, expenseId = expense.id, navController)

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

private fun uploadImage(
    token: String,
    groupId: Int,
    expenseId: Int,
    imageUri: Uri,
    context: Context,
    navController: NavController
) {
    val apiService = ApiService.create()
    val imageFile = imageUri.path?.let { File(it) }
    val imageRequestBody = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
    val imagePart = imageRequestBody?.let { MultipartBody.Part.createFormData("image",
        imageFile.name, it) }

    val groupIdRequestBody = groupId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
    val expenseIdRequestBody = expenseId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    imagePart?.let { apiService.uploadImage(token, groupIdRequestBody, expenseIdRequestBody, it) }
        ?.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    val toast = Toast.makeText(context, response.body()?.message,Toast.LENGTH_SHORT)
                    toast.show()
                    navController.navigate(AppScreens.ShowExpensesScreen.route) {
                        popUpTo(AppScreens.ShowExpensesScreen.route) {
                            inclusive = true
                        }
                    }
                } else {
                    val toast = Toast.makeText(
                        context,
                        "Couldn't upload the image",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                val toast = Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                toast.show()
            }
        })

}

fun getFilePathFromContentUri(uri: Uri, context: Context): String? {
    var filePath: String? = null
    val inputStream = context.contentResolver.openInputStream(uri)
    if (inputStream != null) {
        val tempFile = File(context.cacheDir, "temp_image_file")
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        filePath = tempFile.absolutePath
        inputStream.close()
    }
    return filePath
}

@Composable
fun ImageOrUploadButton(groupId: Int, expenseId: Int, navController: NavController) {

    val apiService = ApiService.create()
    val context = LocalContext.current
    val imageState = remember { mutableStateOf<Bitmap?>(null) }
    val token = getTokenFromSharedPreferences(context).toString()
    var isImageExpanded by remember { mutableStateOf(false) }

    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val filePath = getFilePathFromContentUri(uri, context)
            if (filePath != null) {
                uploadImage(token, groupId, expenseId, Uri.fromFile(File(filePath)), context, navController)
            }
        }
    }

    LaunchedEffect(key1 = token, key2 = groupId, key3 = expenseId) {
        val call = apiService.viewImage(token, groupId, expenseId)
        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                val messageResponse = response.body()
                val fileUrl = messageResponse?.message
                if (!fileUrl.isNullOrEmpty()) {
                    loadImageFromUrl(fileUrl,context) { bitmap ->
                        imageState.value = bitmap
                    }
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                val toast =
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT)
                toast.show()
            }
        })
    }

    val imageModifier = if (isImageExpanded) {
        Modifier
            .absoluteOffset(x = 0.dp, y = 0.dp)
            .fillMaxSize()
            .clickable { isImageExpanded = false }
    } else {
        Modifier
            .clickable { isImageExpanded = true }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Other content here

        if (imageState.value != null && isImageExpanded) {
            // Expanded image overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { isImageExpanded = false },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = imageState.value!!.asImageBitmap(),
                    contentDescription = "Expense Ticket",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                )
            }
        } else if (imageState.value != null) {
            // Image preview
            Image(
                bitmap = imageState.value!!.asImageBitmap(),
                contentDescription = "Expense Ticket",
                modifier = imageModifier
            )
        } else {
        // Image failed to load or is still loading, show upload button
        Button(
            onClick = {
                getContent.launch("image/*")
            },
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Upload Ticket", style = MaterialTheme.typography.h5)
        }
    }
    }
}

private fun loadImageFromUrl(url: String,context: Context, onImageLoaded: (Bitmap) -> Unit) {
    Log.d("RESPONSE", url)
    val request = ImageRequest.Builder(context)
        .data(url)
        .target { result ->
            onImageLoaded(result.toBitmap())
        }
        .build()
    val imageLoader = ImageLoader(context)
    imageLoader.enqueue(request)
}
