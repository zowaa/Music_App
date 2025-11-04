package com.example.musicapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


@Composable
fun ListeningScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.weight(0.8f)
            ) {
                SongsList()
            }

            Box(
                modifier = Modifier.weight(0.2f)
            ) {
                MiniPlayer()
            }
        }
    }
}

@Composable
fun SongsList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 25.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f)),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        val songs = AppData.songsName
        items(songs) { song ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(12.dp)
            ) {
                Text(
                    text = song,
                    fontSize = 20.sp,
                    fontFamily = AppData.myFont,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun MiniPlayer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shuffle),
                    contentDescription = "Shuffle",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )

                Icon(
                    painter = painterResource(id = R.drawable.previous),
                    contentDescription = "Previous",
                    modifier = Modifier.size(34.dp),
                    tint = Color.Black
                )

                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "Play",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Black
                )

                Icon(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = "Next",
                    modifier = Modifier.size(34.dp),
                    tint = Color.Black
                )

                Icon(
                    painter = painterResource(id = R.drawable.repeat),
                    contentDescription = "Repeat",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ProgressBar()
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("00:00", color = Color.Black, fontFamily = AppData.myFont)
                    Text("00:00", color = Color.Black, fontFamily = AppData.myFont)
                }
            }
        }
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray.copy(alpha = 0.7f))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black)
        )

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black)
        )
    }
}
