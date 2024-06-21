package com.app.barterbuddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.barterbuddy.di.BarterRepository
import com.app.barterbuddy.di.Injection
import com.app.barterbuddy.ui.addPost.AddViewModel
import com.app.barterbuddy.ui.chat.ChatViewModel
import com.app.barterbuddy.ui.detailPost.DetailPostViewModel
import com.app.barterbuddy.ui.home.HomeViewModel
import com.app.barterbuddy.ui.login.LoginViewModel
import com.app.barterbuddy.ui.profile.ProfileViewModel
import com.app.barterbuddy.ui.register.SignUpViewModel

class ViewModelsFactory(private val repo: BarterRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repo) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repo) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repo) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(repo) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(repo) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repo) as T
            }
            modelClass.isAssignableFrom(DetailPostViewModel::class.java) -> {
                DetailPostViewModel(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelsFactory? = null
        @JvmStatic
        fun getInstance(): ViewModelsFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelsFactory::class.java) {
                    INSTANCE = ViewModelsFactory(Injection.provideRepo())
                }
            }
            return INSTANCE as ViewModelsFactory
        }
    }
}