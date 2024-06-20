package com.app.barterbuddy.di

import com.app.barterbuddy.di.api.ApiCore

object Injection {
    fun provideRepo(): BarterRepository {
        val apiService = ApiCore().getApiService()
        return BarterRepository.getInstance(apiService)
    }
}