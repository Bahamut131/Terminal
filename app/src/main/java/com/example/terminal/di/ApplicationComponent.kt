package com.example.terminal.di

import android.app.Application
import com.example.terminal.presentation.ViewModelFactory
import dagger.BindsInstance
import dagger.Component
import dagger.Component.Factory

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {


    fun getViewModelFactory() : ViewModelFactory

    @Component.Factory
    interface Factory{
        fun create(
            @BindsInstance application: Application
        ):ApplicationComponent
    }

}