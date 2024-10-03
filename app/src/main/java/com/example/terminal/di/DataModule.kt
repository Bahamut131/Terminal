package com.example.terminal.di

import androidx.compose.ui.tooling.preview.Preview
import com.example.terminal.data.network.ApiFactory
import com.example.terminal.data.network.ApiService
import com.example.terminal.data.repository.CoinRepositoryImpl
import com.example.terminal.domain.CoinRepository
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindsRepository(impl: CoinRepositoryImpl) : CoinRepository


    companion object{
        @ApplicationScope
        @Provides
        fun provideApiService():ApiService{
            return ApiFactory.apiService
        }
    }

}