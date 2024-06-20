package com.app.barterbuddy.di.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class Posts(

	@field:SerializedName("posts")
	val posts: List<PostsItem>
)

data class SuccessPost(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("id")
	val id: String
)

data class AddPost(
	val userId: String,
	val title: RequestBody,
	val description: RequestBody,
	val type: RequestBody,
	val status: RequestBody,
	val image: MultipartBody.Part
)

data class PostsItem(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("interest_count")
	val interestCount: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("status")
	val status: String
)
