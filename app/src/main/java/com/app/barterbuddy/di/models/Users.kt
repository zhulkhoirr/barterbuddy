package com.app.barterbuddy.di.models

import com.google.gson.annotations.SerializedName

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
