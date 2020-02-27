package com.example.eoku.network

import com.user.network.getterSetter.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface serviceApi {

    @POST("/login")
    fun login(@Body request: RequestBody): Call<Login>


    @GET("/user")
    fun getAllUser(@Header("Authorization") token: String): Call<AllUsers>

    @GET("/user/{id}")
    fun getUserById(@Header("Authorization") token: String, @Path("id") id: Int)
            : Call<UserById>

    @Multipart
    @POST("/user/photo/{id}")
    fun updatePhoto(
        @Header("Authorization") token:String,
        @Path("id") id:String,
        @Part photo: MultipartBody.Part
    ): Call<updatePhotos>

    @PUT("/user/{id}")
    fun editUserById(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: RequestBody)
            : Call<EditUserById>

    @POST("/register")
    fun register(@Body request: RequestBody): Call<Register>

    @DELETE("/delete/{id}")
    fun delete(@Header("Authorization") token: String, @Path("id") id: Int): Call<DefaultResponse>

}