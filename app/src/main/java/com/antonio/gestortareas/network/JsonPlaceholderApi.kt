package com.antonio.gestortareas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonPlaceholderApi {

    @GET("todos")
    suspend fun obtenerTareas(@Query("userId") userId: Int): List<TareaRemota>
}

object ClienteRetrofit {
    private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

    val api: JsonPlaceholderApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonPlaceholderApi::class.java)
    }
}

