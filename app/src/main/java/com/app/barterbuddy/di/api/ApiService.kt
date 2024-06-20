package com.app.barterbuddy.di.api

import com.app.barterbuddy.di.models.LoginRequest
import com.app.barterbuddy.di.models.LoginResponse
import com.app.barterbuddy.di.models.Posts
import com.app.barterbuddy.di.models.RegisterRequest
import com.app.barterbuddy.di.models.RegisterResponse
import com.app.barterbuddy.di.models.SuccessPost
import com.app.barterbuddy.di.models.Users
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("register")
    suspend fun postRegister(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("posts")
    suspend fun getPost(): Response<Posts>

    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<Users>

    @Multipart
    @POST("{userId}/post")
    suspend fun postPosts(
        @Path("userId") userId: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("type") type: RequestBody,
        @Part("status") status: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<SuccessPost>
}