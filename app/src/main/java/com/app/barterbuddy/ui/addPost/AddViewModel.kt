package com.app.barterbuddy.ui.addPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.AddPost
import com.app.barterbuddy.di.models.SuccessPost

class AddViewModel(private val repo: BarterRepository): ViewModel() {

    suspend fun addPost(request: AddPost): LiveData<SuccessPost>{
        return repo.postPost(request).asFlow().asLiveData()
    }
}