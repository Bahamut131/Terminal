package com.example.bookshelf

import com.google.gson.annotations.SerializedName


data class CoinInfoDto (
  @SerializedName("results") var results: ArrayList<ResultsDto> = arrayListOf()
)