package com.wordsbyfarber.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

suspend fun downloadAndParseJson(language: String): Map<String, String> {
    val url = "https://wordsbyfarber.com/Consts-$language.js"
    val response = Retrofit.Builder()
        .baseUrl("https://wordsbyfarber.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
        .getJson(url)

    return response
}

interface ApiService {
    @GET
    suspend fun getJson(@Url url: String): Map<String, String>
}
