package com.app.barterbuddy.di.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val idToken: String
)

data class LoginResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("userId")
    val userId: String
)
