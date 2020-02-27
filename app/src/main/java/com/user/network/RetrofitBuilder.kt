package com.example.eoku.network

import com.user.management.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder {

    companion object {
        private val BASE_URL = "http://10.0.2.2"
        val BASE_URL_PORT = BASE_URL + ":8080"

        private val BASE_URL_NGROK = "http://f6ddbf31.ngrok.io"

        fun create(): Retrofit {

            val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL_PORT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

    }

}