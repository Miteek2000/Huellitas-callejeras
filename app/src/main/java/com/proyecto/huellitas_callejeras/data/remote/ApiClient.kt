package com.proyecto.huellitas_callejeras.data.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalStateException

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var okHttpClient: OkHttpClient? = null


    fun initialize(client: OkHttpClient) {
        okHttpClient = client
    }

    val instance: ApiService by lazy {
        if (okHttpClient == null) {
            throw IllegalStateException("ApiClient must be initialized in the Application class.")
        }

        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient!!)
            .build()

        retrofit.create(ApiService::class.java)
    }
}
