package com.example.terminal.data

import com.example.bookshelf.ResultsDto
import com.example.terminal.domain.entity.Result
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun ResultsDto.toResult() : Result{
    return Result(
        time = this.t ?: 0,
        highPrice = this.h?: 0f,
        lowPrice = this.l?: 0f,
        open = this.o?: 0f,
        close = this.c?: 0f
    )
}

fun Result.mapTimestampToDate() : Calendar {
    return Calendar.getInstance().apply {
        time = Date(this@mapTimestampToDate.time)
    }

}