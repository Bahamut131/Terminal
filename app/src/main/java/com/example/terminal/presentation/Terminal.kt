package com.example.terminal.presentation

import android.annotation.SuppressLint
import java.util.Calendar
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terminal.R
import com.example.terminal.data.mapTimestampToDate
import com.example.terminal.domain.entity.Result
import com.example.terminal.getApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS = 20

@Composable
fun Terminal(
    modifier: Modifier = Modifier
) {

    val component = getApplicationComponent()
    val viewModel: CoinViewModel = viewModel(factory = component.getViewModelFactory())
    val coinState = viewModel.state.collectAsState(TerminalScreenState.Initial)

    TerminalScreenContent(coinState, modifier, viewModel)


}

@Composable
private fun TerminalScreenContent(
    coinState: State<TerminalScreenState>,
    modifier: Modifier,
    viewModel: CoinViewModel
) {

    when (val currentState = coinState.value) {

        is TerminalScreenState.Content -> {
            val terminalState = rememberTerminalState(bars = currentState.listResult)
            TerminalGraph(
                modifier = modifier,
                terminalState = terminalState,
                timeFrame = currentState.timeFrame
            ) {
                terminalState.value = it
            }

            currentState.listResult.firstOrNull()?.let {
                Prices(
                    modifier = modifier,
                    terminalState = terminalState,
                    currentPrice = it.close
                )
            }

            TimeFrame(selectedTimeFrame = currentState.timeFrame) {
                viewModel.newTimeFrame(it)
            }
        }

        TerminalScreenState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        TerminalScreenState.Initial -> {}
    }
}

@Composable
private fun TerminalGraph(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    timeFrame: TimeFrame,
    terminalStateChange: (TerminalState) -> Unit,
) {
    val currentState = terminalState.value
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBars = (currentState.visibleBars / zoomChange)
            .roundToInt()
            .coerceIn(MIN_VISIBLE_BARS, currentState.listBar.size)

        val panChangeState = (currentState.panChangeState + panChange.x)
            .coerceIn(
                0f,
                currentState.listBar.size * currentState.barWidth - currentState.terminalWidth
            )

        terminalStateChange(
            currentState.copy(
                visibleBars = visibleBars,
                panChangeState = panChangeState
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(top = 40.dp, bottom = 40.dp, end = 32.dp)
            .transformable(transformableState)
            .onSizeChanged {
                terminalStateChange(
                    currentState.copy(
                        terminalWidth = it.width.toFloat(),
                        terminalHeight = it.height.toFloat()
                    )
                )
            }
    ) {

        val barMin = currentState.barMin
        val pxMaxHigh = currentState.pxMaxHigh

        translate(left = currentState.panChangeState) {
            currentState.listBar.forEachIndexed { index, bar ->

                val offsetX = size.width - index * currentState.barWidth

                timeFrameLine(
                    bar = bar,
                    nextBar = if (index < currentState.listBar.size - 1) {
                        currentState.listBar[index + 1]
                    }else {null},
                    timeFrame = timeFrame,
                    offsetX = offsetX,
                    textMeasurer = textMeasurer
                )

                val barsColor = if (bar.open < bar.close) Color.Green else Color.Red

                drawLine(
                    color = Color.White,
                    start = Offset(offsetX, size.height - (bar.lowPrice - barMin) * pxMaxHigh),
                    end = Offset(offsetX, size.height - (bar.highPrice - barMin) * pxMaxHigh),
                    strokeWidth = 1f
                )

                drawLine(
                    color = barsColor,
                    start = Offset(offsetX, size.height - (bar.open - barMin) * pxMaxHigh),
                    end = Offset(offsetX, size.height - (bar.close - barMin) * pxMaxHigh),
                    strokeWidth = currentState.barWidth / 2
                )
            }
        }

    }

}



private fun DrawScope.timeFrameLine(
    bar: Result,
    nextBar: Result?,
    timeFrame: TimeFrame,
    offsetX: Float,
    textMeasurer: TextMeasurer
) {

    val calendar = bar.calendar
    val minute = calendar.get(Calendar.MINUTE)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_WEEK)

    val shouldDrawDelimiter = when (timeFrame) {
        TimeFrame.MIN_5 -> {
            minute == 0
        }

        TimeFrame.MIN_15 -> {
            minute == 0 && hour % 2 == 0
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            val nextDay = nextBar?.mapTimestampToDate()?.get(Calendar.DAY_OF_WEEK)
            day != nextDay
        }
    }

    if (!shouldDrawDelimiter) return

    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )

    val nameOfMonth =
        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val timeText = when (timeFrame) {
        TimeFrame.MIN_5, TimeFrame.MIN_15 -> {
            String.format("%02d:00", hour)
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            String.format("%s %s", day, nameOfMonth)
        }
    }

    val textLayoutResult = textMeasurer.measure(
        text = timeText,
        style = TextStyle(
            fontSize = 14.sp,
            color = Color.White
        )

    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
    )

}

@Composable
private fun TimeFrame(
    selectedTimeFrame: TimeFrame,
    onSelectedTime: (TimeFrame) -> Unit
) {

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFrame.entries.forEach { timeFrame ->
            val labelResId = when (timeFrame) {
                TimeFrame.MIN_5 -> R.string.time_5_min
                TimeFrame.MIN_15 -> R.string.time_15_min
                TimeFrame.MIN_30 -> R.string.time_30_min
                TimeFrame.HOUR_1 -> R.string.time_1_hour
            }

            val isSelected = selectedTimeFrame == timeFrame

            AssistChip(onClick = {
                onSelectedTime(timeFrame)
            }, label = {
                Text(text = stringResource(id = labelResId))
            }, colors = AssistChipDefaults.assistChipColors(
                containerColor = if (isSelected) Color.White else Color.Black,
                labelColor = if (isSelected) Color.Black else Color.White
            )
            )
        }
    }


}


@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    currentPrice: Float
) {
    val currentState = terminalState.value
    val terminalMeasure = rememberTextMeasurer()

    val max = currentState.barMax
    val min = currentState.barMin
    val pxPerPoint = currentState.pxMaxHigh

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(top = 40.dp, bottom = 40.dp)
    ) {
        crawPrice(max, min, currentPrice, pxPerPoint, terminalMeasure)
    }

}


private fun DrawScope.crawPrice(
    max: Float,
    min: Float,
    currentPrice: Float,
    pxPerPoint: Float,
    textMeasurer: TextMeasurer
) {

    drawDashedLine(
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
    )
    terminalMeasure(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = size.height
    )


    drawDashedLine(
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
    )
    terminalMeasure(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = 0f
    )


    drawDashedLine(
        start = Offset(0f, size.height - (currentPrice - min) * pxPerPoint),
        end = Offset(size.width, size.height - (currentPrice - min) * pxPerPoint),
    )
    terminalMeasure(
        textMeasurer = textMeasurer,
        price = currentPrice,
        offsetY = size.height - (currentPrice - min) * pxPerPoint
    )


}

private fun DrawScope.terminalMeasure(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            fontSize = 14.sp,
            color = Color.White
        )

    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width, offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {

    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )

}