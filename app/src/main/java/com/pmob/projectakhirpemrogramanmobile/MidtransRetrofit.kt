package com.pmob.projectakhirpemrogramanmobile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MidtransRetrofit {

    private const val BASE_URL = "https://Bovery.great-site.net/"

    val api: MidtransApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MidtransApiService::class.java)
    }
}
