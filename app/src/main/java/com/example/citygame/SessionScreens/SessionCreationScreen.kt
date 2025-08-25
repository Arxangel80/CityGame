package com.example.citygame.SessionScreens

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SessionCreationScreen(
    navigateToMain: () -> Unit,
) {
    val viewModel: SessionCreationViewModel = viewModel()
    val context = LocalContext.current

    val toastMessage by viewModel.toastMessage

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(viewModel.items) { item ->
            GridItem(item, onClick = { viewModel.createSession() { navigateToMain() } })
        }
    }
}


@Composable
fun GridItem(item: SessionCreationViewModel.GridItem, onClick: () -> Unit) {
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