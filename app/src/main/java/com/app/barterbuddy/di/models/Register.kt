package com.app.barterbuddy.di.models

import com.google.gson.annotations.SerializedName

data class RegisterRequest (
	val username: String,
	val email: String,
	val password: String,
	val confirmPassword: String,
	val city: String
)

data class RegisterResponse(
	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("error")
	val error: String
)
