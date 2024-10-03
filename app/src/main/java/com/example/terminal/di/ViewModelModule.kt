package com.example.terminal.di

import androidx.lifecycle.ViewModel
import com.example.terminal.presentation.CoinViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(CoinViewModel::class)
    fun bindsCoinViewModel(viewModel: CoinViewModel) : ViewModel


}