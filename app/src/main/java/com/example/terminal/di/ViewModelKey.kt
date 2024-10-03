package com.example.terminal.di

import android.view.View
import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass


@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val key : KClass<out ViewModel>)
