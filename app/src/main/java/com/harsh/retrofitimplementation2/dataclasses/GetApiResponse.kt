package com.harsh.retrofitimplementation2.dataclasses


import com.google.gson.annotations.SerializedName

class GetApiResponse : ArrayList<GetApiResponseItem>()
    data class GetApiResponseItem(
        @SerializedName("email")
        val email: String? = null, // sinha_atmaja@anderson.test
        @SerializedName("gender")
        val gender: String? = null, // male
        @SerializedName("id")
        val id: Int? = null, // 6880153
        @SerializedName("name")
        val name: String? = null, // Atmaja Sinha
        @SerializedName("status")
        val status: String? = null // active
    )
