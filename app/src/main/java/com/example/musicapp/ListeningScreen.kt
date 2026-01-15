package com.example.musicapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun ListeningScreen(
    viewModel: MusicViewModel = viewModel()
) {
    val playerState by viewModel.playerState.collectAsState()


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
                SongsList(
                    currentSongIndex = playerState.currentSongIndex,
                    playSong = { index -> viewModel.playSong(index) }
                )
            }

            Box(
                modifier = Modifier.weight(0.2f)
            ) {
                MiniPlayer(
                    playerState = playerState,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.playNext() },
                    onPrevious = { viewModel.playPrevious() },
                    onRepeatToggle = { viewModel.toggleRepeat() },
                    onDragEnd = { pos -> viewModel.seekTo(pos.toFloat() / playerState.duration) },
                )
            }
        }
    }
}

@Composable
fun SongsList(
    currentSongIndex: Int,
    playSong: (Int) -> Unit
) {

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
        itemsIndexed(
            items = AppData.songsName,
            key = { index, _ -> index }
        ) { index, song ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = if (index == currentSongIndex) 2.dp else 0.dp,
                        color = if (index == currentSongIndex) Color.Black else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        if (index == currentSongIndex) Color.Gray.copy(alpha = 0.7f)
                        else Color.White.copy(alpha = 0.8f)
                    )
                    .padding(12.dp)
                    .clickable { playSong(index) }
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
fun MiniPlayer(
    playerState: MusicPlayerState,
    onPlayPause: () -> Unit,
    onNext : () -> Unit,
    onPrevious : () -> Unit,
    onRepeatToggle: (Boolean) -> Unit,
    onDragEnd: (Int) -> Unit,
) {
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
                PlayerIcon(R.drawable.shuffle, "Shuffle", 28.dp) { }
                PlayerIcon(R.drawable.previous, "Previous", 34.dp, onPrevious)
                PlayerIcon(if (playerState.isPlaying) R.drawable.pause else R.drawable.play, "Play/Pause", 50.dp, onPlayPause)
                PlayerIcon(R.drawable.next, "Next", 34.dp, onNext)
                PlayerIcon(if (playerState.isRepeatOn) R.drawable.repeaton else R.drawable.repeat, "Repeat", 28.dp){ onRepeatToggle(!playerState.isRepeatOn)}
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ProgressBar(
                    progress = playerState.progress,
                    onDragEnd = onDragEnd,
                    duration = playerState.duration,
                    songIndex = playerState.currentSongIndex
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(playerState.currentPosition), color = Color.Black, fontFamily = AppData.myFont)
                    Text(formatTime(playerState.duration), color = Color.Black, fontFamily = AppData.myFont)
                }
            }
        }
    }
}

@Composable
fun PlayerIcon(resId: Int, description: String, size: Dp, onClick: () -> Unit) {
    Icon(
        painter = painterResource(id = resId),
        contentDescription = description,
        modifier = Modifier.size(size).clickable { onClick() },
    )
}


fun formatTime(
    ms: Int
): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%02d:%02d".format(min, sec)
}

@Composable
fun ProgressBar(
    progress: Float,
    onDragEnd: (Int) -> Unit,
    duration: Int,
    songIndex: Int
) {
    var barWidth by remember { mutableFloatStateOf(0f) }
    var localProgress by remember { mutableFloatStateOf(progress) }
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(progress) {
        if (!isDragging) localProgress = progress
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 8.dp)
            .onGloballyPositioned { barWidth = it.size.width.toFloat() }
            .pointerInput(songIndex) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    isDragging = true
                    val newProgress = (down.position.x / barWidth).coerceIn(0f, 1f)
                    localProgress = newProgress
                    drag(down.id) { change ->
                        val x = change.position.x.coerceIn(0f, barWidth)
                        localProgress = (x / barWidth).coerceIn(0f, 1f)
                        change.consume()
                    }
                    isDragging = false
                    onDragEnd((localProgress * duration).toInt())
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray.copy(alpha = 0.7f))
                .align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(localProgress)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black)
                .align(Alignment.CenterStart)
        )

        val knobOffset = with(density) { (localProgress * barWidth).toDp() - 6.dp }

        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black)
                .align(Alignment.CenterStart)
        )
    }
}
