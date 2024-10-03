package com.example.terminal.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminal.data.network.ApiService
import com.example.terminal.data.repository.CoinRepositoryImpl
import com.example.terminal.domain.CoinRepository
import com.example.terminal.domain.entity.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinViewModel @Inject constructor (
     val repository: CoinRepositoryImpl
): ViewModel(){
    val bars = repository.loadBars()

    val timeFrame = repository.timeFrameFlow

    val newState = MutableSharedFlow<TerminalScreenState>()

    val state  = bars
        .filter { it.isNotEmpty()}
        .map { TerminalScreenState.Content(it,timeFrame.value) as TerminalScreenState }
        .onStart { emit(TerminalScreenState.Loading) }
        .mergeWith(newState)


     fun newTimeFrame(timeFrame1: TimeFrame){
         viewModelScope.launch {
             newState.emit(
                 TerminalScreenState.Loading
             )
             repository.newTimeFrame(timeFrame1)
         }
    }

    fun <T> Flow<T>.mergeWith(another: Flow<T>): Flow<T> {
        return merge(this, another)
    }

}