package com.app.barterbuddy.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.Users

class ProfileViewModel(private val repo: BarterRepository): ViewModel() {

    suspend fun getDetailUser(userId: String): LiveData<Users> {
        return repo.getDetailUser(userId).asFlow().asLiveData()
    }

}