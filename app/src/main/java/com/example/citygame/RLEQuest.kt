package com.example.citygame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BoardBase(height:Int, width: Int) {
    val truePattern = listOf(true, true, false, false, false, false, false, false, false)
    var textState by remember {mutableStateOf("")}
    val columnState: SnapshotStateList<Boolean> = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(height*width) { add(false) }
        }
    }
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(width)
        ) {
            for (i in 0 until height*width) {
                item {
                    Column(modifier = Modifier
                        .clickable {
                            columnState[i] = !columnState[i]
                            println(columnState)
                            textState = if (columnState == truePattern)
                                "True Pattern"
                            else
                                "False Pattern"
                        }
                        .background(if (columnState[i]) Color.Black else Color.Green)
                        .border(border = BorderStroke(1.dp, Color.Red))
                        .fillMaxWidth()
                        .aspectRatio(1f)
                    )
                    {
                    }
                }
            }
        }
        Text(textState)
    }
}


@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
@Composable
fun BoardBasePreview() {
    BoardBase(3, 3)
}
