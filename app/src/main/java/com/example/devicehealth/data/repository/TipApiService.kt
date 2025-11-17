package com.example.devicehealth.repository

import retrofit2.http.GET

data class TipResponse(
    val phrase: String
)

interface TipApiService {
    @GET("phrases/random")
    suspend fun fetchTip(): TipResponse
}
