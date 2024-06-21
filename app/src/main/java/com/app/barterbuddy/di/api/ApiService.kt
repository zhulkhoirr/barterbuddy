package com.app.barterbuddy.di.api

import com.app.barterbuddy.di.models.Posts
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.di.models.RegisterRequest
import com.app.barterbuddy.di.models.RegisterResponse
import com.app.barterbuddy.di.models.SuccessPost
import com.app.barterbuddy.di.models.Users
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun postRegister(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("posts")
    suspend fun getPost(): Response<Posts>

    @GET("post/{postId}")
    suspend fun getDetailPost(@Path("postId") postId: String): Response<PostsItem>

    @Multipart
    @PUT("{userId}/profile")
    suspend fun updateProfile(
        @Path("userId") userId: String,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("city") city: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<SuccessPost>

    @POST("{userId}/post/{postId}/interest")
    suspend fun postInterest(
        @Path("userId") userId: String,
        @Path("postId") postId: String
    ): Response<SuccessPost>

    @POST("{userId}/post/{postId}/uninterest")
    suspend fun postUninterest(
        @Path("userId") userId: String,
        @Path("postId") postId: String
    ): Response<SuccessPost>

    @GET("{userId}/search")
    suspend fun searchPosts(
        @Path("userId") userId: String,
        @Query("keyword") keyword: String
    ): Response<Posts>

    @DELETE("{userId}/delete/{postId}")
    suspend fun deletePostById(
        @Path("userId") userId: String,
        @Path("postId") postId: String
    ): Response<SuccessPost>

    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<Users>

    @GET("user/{userId}/posts")
    suspend fun getPostUser(@Path("userId") userId: String): Response<Posts>

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