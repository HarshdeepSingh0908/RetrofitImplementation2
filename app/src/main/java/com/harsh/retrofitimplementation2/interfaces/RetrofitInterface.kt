package com.harsh.retrofitimplementation2.interfaces

import com.harsh.retrofitimplementation2.dataclasses.GetApiResponse
import com.harsh.retrofitimplementation2.dataclasses.GetApiResponseItem
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("users")
   fun getApiResponse(): Call<List<GetApiResponseItem>>

    @GET("users/2731664")
    fun getSingleUser(): Call<GetApiResponseItem>

    @GET("users/{id}")
    fun getSingleUserPath(@Path("id") string: String): Call<GetApiResponseItem>

    @POST("users")
    @FormUrlEncoded
    fun postUser(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("gender") gender: String,
        @Field("status") status: String,
    ): Call<GetApiResponseItem>
    @GET("users")
    fun getUsersPerPage(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int) : Call<GetApiResponse>
}