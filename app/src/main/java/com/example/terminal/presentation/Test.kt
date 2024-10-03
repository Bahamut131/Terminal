package com.example.terminal.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Test(){

    var testData by rememberSaveable(saver = TestDataClass1.Saver) {
        mutableStateOf(TestDataClass1(0))
    }
    Column {
        Box(modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()
            .clickable {
                testData = testData.copy(number = testData.number + 1)
            },
            contentAlignment = Alignment.Center)
        {
            Text(text = "text ${testData.number}")
        }

        var testData1 by rememberSaveable(saver = TestDataClass2.Saver) {
            mutableStateOf(TestDataClass2(0, ""))
        }

        Box(modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()
            .clickable {
                testData1 = testData1.copy(number = testData1.number + 1, text = testData1.text + "a")
            },
            contentAlignment = Alignment.Center) {
            Text(text = "Number: ${testData1.number}  Text: ${testData1.text}")
        }
    }


}

data class TestDataClass1(val number : Int){

    companion object{

        val Saver : Saver<MutableState<TestDataClass1>,Int> = Saver(
            save = {
                it.value.number
            },
            restore = {
                mutableStateOf(TestDataClass1(it))
            }
        )
    }

}

data class TestDataClass2(val number : Int, val text : String){
    companion object{

        val Saver : Saver<MutableState<TestDataClass2>,Any> = listSaver(
            save = {
                val number = it.value.number
                val text = it.value.text
                listOf(number,text)
            },
            restore = {
                val testData = TestDataClass2(
                    number = it[0] as Int,
                    text = it[1] as String
                )

                mutableStateOf(testData)
            }
        )
    }
}