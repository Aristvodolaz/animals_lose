package com.application.lose_animals.data.api

import com.application.lose_animals.data.model.DaDataRequest
import com.application.lose_animals.data.model.DaDataResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DaDataApiService {
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Authorization: Token 85dcdbb9b5754718170d0fd0141f6833bbaa4623" // Замените на ваш API ключ
    )
    @POST("api/v1/suggest/address")
    suspend fun suggestAddress(@Body request: DaDataRequest): DaDataResponse
} 