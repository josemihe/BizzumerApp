package com.je.bizzumer.io.preferences_management

import android.content.Context
import android.util.Log

fun deleteToken(context: Context) {
    val sharedPreferences = context.getSharedPreferences("BizzumerPreferences", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("TokenKey").apply()
}

fun getTokenFromSharedPreferences(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("BizzumerPreferences", Context.MODE_PRIVATE)
    return if (!sharedPreferences.contains("TokenKey")) {
        "Error, no token provided"
    }
    else{
        sharedPreferences.getString("TokenKey", null)
    }
}

fun saveToken(token:String,context:Context){
    val sharedPreferences = context.getSharedPreferences("BizzumerPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("TokenKey", token)
    Log.d("Token",token)
    editor.apply()
}

fun checkToken(context: Context):Boolean{

    val token = getTokenFromSharedPreferences(context)
    if(token!="Error, no token provided"){
        return true
    }
    return false
}