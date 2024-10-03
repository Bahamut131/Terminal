package com.example.terminal

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.terminal.di.ApplicationComponent
import com.example.terminal.di.DaggerApplicationComponent

class TerminalApp : Application() {

        val component : ApplicationComponent by lazy {
                DaggerApplicationComponent.factory().create(this)
        }
}

@Composable
fun getApplicationComponent() : ApplicationComponent{
        return (LocalContext.current.applicationContext as TerminalApp).component
}