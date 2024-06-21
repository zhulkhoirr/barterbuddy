package com.app.barterbuddy.ui.detailPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.di.models.SuccessPost
import com.app.barterbuddy.di.models.Users

class DetailPostViewModel(private val repo: BarterRepository): ViewModel() {

    suspend fun getDetailPost(postId: String): LiveData<PostsItem> {
        return repo.getDetailPost(postId).asFlow().asLiveData()
    }

    suspend fun getDetailUser(userId: String): LiveData<Users> {
        return repo.getDetailUser(userId).asFlow().asLiveData()
    }

    suspend fun postInterest(userId: String, postId: String): LiveData<SuccessPost> {
        return repo.postInterest(userId, postId).asFlow().asLiveData()
    }

    suspend fun postUninterest(userId: String, postId: String): LiveData<SuccessPost> {
        return repo.postUninterest(userId, postId).asFlow().asLiveData()
    }

}