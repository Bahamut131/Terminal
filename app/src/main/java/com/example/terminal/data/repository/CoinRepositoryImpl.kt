package com.example.terminal.data.repository

import android.util.Log
import com.example.terminal.data.mapTimestampToDate
import com.example.terminal.data.network.ApiService
import com.example.terminal.data.toResult
import com.example.terminal.domain.CoinRepository
import com.example.terminal.domain.entity.CoinInfo
import com.example.terminal.domain.entity.Result
import com.example.terminal.presentation.TimeFrame
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.sql.Time
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    val apiService: ApiService
): CoinRepository {

    private val exceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        Log.d("CoinViewModel", "Exception was handle $throwable")
    }

    private val _coin = mutableListOf<Result>()
    private val coin: List<Result>
        get() = _coin.toList()




    private val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)

    val nextBar = MutableSharedFlow<Unit>(replay = 1)

    val timeFrameFlow =  MutableStateFlow<TimeFrame>(TimeFrame.MIN_5)

    val loadedListFlow  = flow{
        nextBar.emit(Unit)
        nextBar.collect{
            Log.d("CoinRepositoryImpl" ,"CoinRepositoryImpl: ${ timeFrameFlow.value.time}")

            val resultDto = apiService.loadBars(timeFrameFlow.value.time).results
            val result  = resultDto.map {
                it.toResult()
            }

            _coin.clear()
            _coin.addAll(result)
            Log.d("CoinRepositoryImpl" ,"${result}")
            Log.d("CoinRepositoryImpl" ,"${coin}")
            emit(coin)
        }
    }

    val bars : Flow<List<Result>> = loadedListFlow.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )


    override fun loadBars(timeFrame: TimeFrame) : Flow<List<Result>> = bars


    override fun newTimeFrame(timeFrame: TimeFrame) {
        scope.launch {
            timeFrameFlow.value = timeFrame
            Log.d("CoinRepositoryImpl" ," newTimeFrame ${timeFrameFlow.value.time}")
            nextBar.emit(Unit)
        }

    }

    private fun Flow<List<Result>>.mergeWith(flow: Flow<List<Result>>) = merge(this,flow)
}