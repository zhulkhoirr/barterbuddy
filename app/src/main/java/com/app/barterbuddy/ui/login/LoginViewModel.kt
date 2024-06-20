package com.app.barterbuddy.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.models.LoginRequest
import com.app.barterbuddy.di.models.LoginResponse
import com.app.barterbuddy.di.models.UserModel

class LoginViewModel(private val repo: BarterRepository): ViewModel() {

}