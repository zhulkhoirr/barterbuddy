package com.app.barterbuddy.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.RegisterRequest
import com.app.barterbuddy.di.models.RegisterResponse

class SignUpViewModel(private val repo: BarterRepository): ViewModel(){
    suspend fun postRegister(request: RegisterRequest): LiveData<RegisterResponse>{
        return repo.postRegister(request)
    }
}