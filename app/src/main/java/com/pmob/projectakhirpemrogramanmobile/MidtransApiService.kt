package com.pmob.projectakhirpemrogramanmobile

import retrofit2.Call
import retrofit2.http.GET

interface MidtransApiService {
    @GET("create_transaction.php")
    fun getSnapToken(): Call<MidtransResponse>
}
