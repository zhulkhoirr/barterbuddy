package com.app.barterbuddy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.PostsItem

class HomeViewModel(private val repo: BarterRepository): ViewModel() {

    suspend fun getPost(): LiveData<List<PostsItem>> {
        return repo.getPost().asFlow().asLiveData()
    }
}