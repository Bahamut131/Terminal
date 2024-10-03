package com.example.terminal.domain

import com.example.terminal.domain.entity.CoinInfo
import com.example.terminal.domain.entity.Result
import com.example.terminal.presentation.TimeFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CoinRepository {

    fun loadBars(timeFrame: TimeFrame = TimeFrame.HOUR_1): Flow<List<Result>>

     fun newTimeFrame(timeFrame: TimeFrame)

}