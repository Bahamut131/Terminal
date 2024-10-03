package com.example.terminal.presentation

import com.example.terminal.domain.entity.Result

sealed class TerminalScreenState {

    object Initial : TerminalScreenState()
    object Loading : TerminalScreenState()

    data class Content(val listResult: List<Result>, val timeFrame: TimeFrame) : TerminalScreenState()

}