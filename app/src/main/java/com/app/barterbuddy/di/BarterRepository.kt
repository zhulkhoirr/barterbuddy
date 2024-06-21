package com.app.barterbuddy.di

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.barterbuddy.di.api.ApiService
import com.app.barterbuddy.di.models.AddPost
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.di.models.RegisterRequest
import com.app.barterbuddy.di.models.RegisterResponse
import com.app.barterbuddy.di.models.SuccessPost
import com.app.barterbuddy.di.models.UpdateUser
import com.app.barterbuddy.di.models.Users

class BarterRepository private constructor(
    private val apiService: ApiService
){
    suspend fun getPost(): LiveData<List<PostsItem>> {
        val data = MutableLiveData<List<PostsItem>>()
        try {
            val res = apiService.getPost()
            if (res.isSuccessful){
                data.postValue(res.body()!!.posts)
            }
        } catch (e: Exception){
            Log.d("error posts", e.message.toString())
        }
        return data
    }

    suspend fun postInterest(userId: String, postId: String): LiveData<SuccessPost> {
        val data = MutableLiveData<SuccessPost>()
        try {
            val res = apiService.postInterest(userId, postId)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error interest", e.message.toString())
        }
        return data
    }

    suspend fun getInterestPost(postId: List<String>): LiveData<List<PostsItem>> = liveData {
        val data = mutableListOf<PostsItem>()
        postId.forEach { postId ->
            try {
                val res = apiService.getDetailPost(postId)
                if (res.isSuccessful){
                    res.body().let {
                        data.add(it!!)
                    }
                }
            } catch (e: Exception){
                Log.d("error list interest", e.message.toString())
            }
        }
       emit(data)
    }

    suspend fun postUninterest(userId: String, postId: String): LiveData<SuccessPost> {
        val data = MutableLiveData<SuccessPost>()
        try {
            val res = apiService.postUninterest(userId, postId)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error uninterest", e.message.toString())
        }
        return data
    }

    suspend fun getPostSearch(userId: String, keyword: String): LiveData<List<PostsItem>> {
        val data = MutableLiveData<List<PostsItem>>()
        try {
            val res = apiService.searchPosts(userId, keyword)
            if (res.isSuccessful){
                data.postValue(res.body()!!.posts)
            }
        } catch (e: Exception){
            Log.d("error posts", e.message.toString())
        }
        return data
    }

    suspend fun deletePostById(userId: String, postId: String): LiveData<SuccessPost>{
        val data = MutableLiveData<SuccessPost>()
        try {
            val res = apiService.deletePostById(userId, postId)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error delete posts", e.message.toString())
        }
        return data
    }

    suspend fun getDetailPost(postId: String): LiveData<PostsItem> {
        val data = MutableLiveData<PostsItem>()
        try {
            val res = apiService.getDetailPost(postId)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error posts", e.message.toString())
        }
        return data
    }

    suspend fun getPostByUser(userId: String): LiveData<List<PostsItem>> {
        val data = MutableLiveData<List<PostsItem>>()
        try {
            val res = apiService.getPostUser(userId)
            if (res.isSuccessful){
                data.postValue(res.body()!!.posts)
            }
        } catch (e: Exception){
            Log.d("error posts", e.message.toString())
        }
        return data
    }

    suspend fun getDetailUser(userId: String): LiveData<Users>{
        val data = MutableLiveData<Users>()
        try {
            val res = apiService.getUser(userId)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error users $userId", e.message.toString())
        }
        return data
    }

    suspend fun postPost(request: AddPost): LiveData<SuccessPost> {
        val data = MutableLiveData<SuccessPost>()
        try {
            val res = apiService.postPosts(request.userId, request.title, request.description, request.type, request.status, request.image)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error add posts", e.message.toString())
        }
        return data
    }

    suspend fun updateUser(request: UpdateUser): LiveData<SuccessPost> {
        val data = MutableLiveData<SuccessPost>()
        try {
            val res = apiService.updateProfile(request.userId, request.username, request.email, request.city, request.image)
            if (res.isSuccessful){
                data.postValue(res.body())
            }
        } catch (e: Exception){
            Log.d("error add posts", e.message.toString())
        }
        return data
    }

    suspend fun postRegister(request: RegisterRequest): LiveData<RegisterResponse> {
        val data = MutableLiveData<RegisterResponse>()
        try {
            val res = apiService.postRegister(request)
            if (res.isSuccessful){
                val response = RegisterResponse(res.body()!!.success, "")
                data.postValue(response)
            } else {
                val response = RegisterResponse(false, "Error")
                data.postValue(response)
            }
        } catch (e: Exception){
            val errorResponse = RegisterResponse(success = false, error = e.message.toString())
            data.postValue(errorResponse)
        }
        return data
    }

    companion object {

        @Volatile
        var instance: BarterRepository? = null
        fun getInstance(
            apiService: ApiService
        ): BarterRepository =
            instance ?: synchronized(this) {
                instance ?: BarterRepository(apiService)
            }.also { instance = it }
    }
}