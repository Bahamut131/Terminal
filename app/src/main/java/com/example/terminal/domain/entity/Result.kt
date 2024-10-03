package com.example.terminal.domain.entity

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.Date

@Parcelize
@Immutable
data class Result(
    var open  : Float,
    var close  : Float,
    var highPrice  : Float,
    var lowPrice  : Float,
    var time  : Long,
) : Parcelable {

    val calendar: Calendar
        get() {
            return Calendar.getInstance().apply {
                time = Date(this@Result.time)
            }
        }

}