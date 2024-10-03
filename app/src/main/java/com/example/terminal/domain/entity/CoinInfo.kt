package com.example.terminal.domain.entity

import com.example.bookshelf.ResultsDto
import com.google.gson.annotations.SerializedName

data class CoinInfo(

    var results      : ArrayList<ResultsDto> = arrayListOf(),

) {
}