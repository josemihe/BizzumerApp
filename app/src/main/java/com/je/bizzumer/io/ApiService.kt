package com.je.bizzumer.io

import com.je.bizzumer.io.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @POST(value = "/api/login")
    fun postLogin(@Query(value = "email") email:String, @Query(value = "password") password:String):
            Call<LoginResponse>

    @POST(value = "/api/register")
    fun postRegister(@Query(value="name")name:String, @Query(value = "email") email:String, @Query(value = "password") password:String, @Query(value = "password_confirmation") confirm:String):
            Call<LoginResponse>

    @PUT(value="/api/password-reset")
    fun postResetMail(@Query(value="email")email: String):
            Call<MessageResponse>

    @PUT(value="/api/password-update")
    fun updatePassword(@Query(value="password")password: String, @Query(value="password_confirmation")confirmPassword:String, @Query(value="reset_token")resetToken:String):
            Call<MessageResponse>

    @DELETE("/api/logout")
    fun postLogout(@Header("Authorization") token: String): Call<MessageResponse>

    @GET("/api/v2/groups")
    fun getAllGroups(@Header("Authorization") token: String): Call<GroupsResponse>

    @GET("/api/v2/group")
    fun getGroup(@Header("Authorization") token: String, @Query(value="id") id: String): Call<GroupsResponse>

    @DELETE("/api/v2/groups/participant/")
    fun removeParticipant(
        @Header("Authorization") token: String,
        @Query(value = "group_id") groupId: Int,
        @Query(value = "delete_id") userId: Int
    ): Call<MessageResponse>

    @DELETE("/api/v2/groups/leave/")
    fun leaveGroup(
        @Header("Authorization") token: String,
        @Query(value = "group_id") groupId: Int,
    ): Call<MessageResponse>

    @POST("/api/v2/send-invite")
    fun inviteMembers(
        @Header("Authorization") token: String,
        @Query(value= "group_id") groupId: Int,
        @Query(value = "email") email: String
    ): Call<MessageResponse>

    @POST("/api/v2/join")
    fun joinGroup(
        @Header("Authorization") token: String,
        @Query(value= "access_code") accessCode: String,
    ): Call<MessageResponse>

    @POST("api/v1/group/create")
    fun createGroup(
        @Header("Authorization") token: String,
        @Query(value= "name") name: String,
        @Query(value= "comment") comment: String,
    ): Call<MessageResponse>

    @GET("api/v2/group/calculate-expenses")
    fun calculateExpenses(
        @Header("Authorization") token: String,
        @Query(value= "group_id") groupId: Int
    ): Call<ExpenseCalculationResult>

    @POST("api/v2/group/upload-expense")
    fun uploadExpense(
        @Header("Authorization") token: String,
        @Query(value= "group_id") groupId: Int,
        @Query(value = "amount") amount: Double,
        @Query(value = "description") description: String,
    ): Call<MessageResponse>

    @GET("api/v2/group/expenses")
    fun showExpenses(
        @Header("Authorization") token: String,
        @Query(value= "group_id") groupId: Int,
    ): Call<ExpensesList>

    @DELETE("api/v2/group/delete-expense")
    fun deleteExpense(
        @Header("Authorization") token: String,
        @Query(value= "expense_id") expenseId: Int,
    ): Call<MessageResponse>

    @Multipart
    @POST("api/v2/expense/upload-image")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part("group_id") groupId: RequestBody,
        @Part("expense_id") expenseId: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<MessageResponse>

    @GET("api/v2/expense/view-image")
    fun viewImage(
        @Header("Authorization") token: String,
        @Query("group_id") groupId: Int,
        @Query("expense_id") expenseId: Int
    ): Call<MessageResponse>

    companion object Factory{
        private const val BASE_URL = "http://192.168.77.21:8000"
        fun create(): ApiService{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}