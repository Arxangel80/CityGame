package com.example.citygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun QuestsScreen(navigateToMain: () -> Unit) {
    QuestsDrawer(navigateToMain)
}

data class GridItemData(val picture: Int, val title: String, val description: String)

@Composable
fun QuestsDrawer(navigateToMain: () -> Unit) {
    val pictures = listOf(R.drawable.cit, R.drawable.pp, R.drawable.cit)
    val titles =
        listOf("EiT Faculty game", "Campus game", "Text 2", "Text 3")
    val desctiption = listOf(
        "Experience an immersive mobile quest through the rich history of your alma mater, uncovering hidden stories.",
        "Dive into a captivating journey through history of poznan university of technology with our interactive quest.",
        "Description Text 2",
        "Description Text 3"
    )


    val items = List(10) { index ->
        GridItemData(
            picture = pictures[index % pictures.size],
            title = titles[index % titles.size],
            description = desctiption[index % desctiption.size]
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(items) { item ->
            GridItem(item, onClick = { navigateToMain() })
        }
    }
}


@Composable
fun GridItem(item: GridItemData, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = item.picture),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f))
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

