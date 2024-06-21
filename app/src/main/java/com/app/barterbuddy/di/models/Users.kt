package com.app.barterbuddy.di.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class Users(

    @SerializedName("userId")
    val userId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("profile_img")
    val profileImg: String,

    @SerializedName("interested_posts")
    val interestedPosts: List<String>,

    @SerializedName("search_histories")
    val searchHistories: List<String>
)

data class UpdateUser(
    val userId: String,
    val username: RequestBody,
    val email: RequestBody,
    val city: RequestBody,
    val image: MultipartBody.Part
)
