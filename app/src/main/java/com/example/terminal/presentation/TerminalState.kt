package com.example.terminal.presentation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.terminal.domain.entity.Result
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt


@Parcelize
data class TerminalState(
    val listBar : List<Result>,
    var visibleBars : Int = 100,
    var terminalWidth : Float = 1f,
    var panChangeState : Float =1f,
    var terminalHeight : Float = 1f
) : Parcelable {

    val barWidth : Float get() = terminalWidth/visibleBars


    private val visibleBarsOnScreen : List<Result> get() {
        val start = (panChangeState / barWidth).roundToInt().coerceAtLeast(0)
        val end = (start + visibleBars).coerceAtMost(listBar.size)
        return listBar.subList(start,end)
    }

    val barMax get() = visibleBarsOnScreen.maxOf { it.highPrice }
    val barMin get() =  visibleBarsOnScreen.minOf { it.lowPrice }
    val pxMaxHigh get() = terminalHeight / (barMax-barMin)

}

@Composable
fun rememberTerminalState(bars : List<Result>) : MutableState<TerminalState>{
    return rememberSaveable {
        mutableStateOf(TerminalState(bars))
    }
}