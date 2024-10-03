package com.example.terminal.data.network

import com.example.bookshelf.CoinInfoDto
import com.example.bookshelf.ResultsDto
import com.example.terminal.presentation.TimeFrame
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("aggs/ticker/AAPL/range/{timeframe}/2022-01-09/2023-02-10?adjusted=true&sort=desc&limit=50000&apiKey=2eTYwE5sIQNeLOhBDHjV_jkNeew3AJjQ")
    suspend fun loadBars(
        @Path("timeframe") timeFrame: String
    ) : CoinInfoDto

}