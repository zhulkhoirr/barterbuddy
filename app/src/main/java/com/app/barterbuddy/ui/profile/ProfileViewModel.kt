package com.app.barterbuddy.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.di.models.SuccessPost
import com.app.barterbuddy.di.models.UpdateUser
import com.app.barterbuddy.di.models.Users

class ProfileViewModel(private val repo: BarterRepository): ViewModel() {

    suspend fun getDetailUser(userId: String): LiveData<Users> {
        return repo.getDetailUser(userId).asFlow().asLiveData()
    }

    suspend fun getPostByUser(userId: String): LiveData<List<PostsItem>> {
        return repo.getPostByUser(userId).asFlow().asLiveData()
    }

    suspend fun deletePostById(userId: String, postId: String): LiveData<SuccessPost> {
        return repo.deletePostById(userId, postId).asFlow().asLiveData()
    }

    suspend fun getInterestPost(postId: List<String>): LiveData<List<PostsItem>> {
        return repo.getInterestPost(postId).asFlow().asLiveData()
    }

    suspend fun updateUser(request: UpdateUser): LiveData<SuccessPost> {
        return repo.updateUser(request).asFlow().asLiveData()
    }
}