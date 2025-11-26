package com.example.musicapp

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MusicPlayerState(
    val currentSongIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isRepeatOn: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Int = 0,
    val duration: Int = 0
)

class MusicViewModel( application: Application ) : AndroidViewModel(application) {
    private val _playerState = MutableStateFlow(MusicPlayerState())
    val playerState: StateFlow<MusicPlayerState> = _playerState

    private val appContext get() = getApplication<Application>().applicationContext
    private var mediaPlayer: MediaPlayer? = null
    var isDragging = false
    private var progressJob: Job? = null

    init {
        startProgressUpdater()
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setOnCompletionListener {
                    val state = _playerState.value
                    if (state.isRepeatOn) {
                        seekTo(0)
                        start()
                    } else {
                        val nextIndex = if (state.currentSongIndex < AppData.songsResId.size - 1)
                            state.currentSongIndex + 1 else 0
                        playSong(nextIndex)
                    }
                }
            }
        }
    }

    fun playSong(index: Int) {
        val player = mediaPlayer ?: return

        player.reset()
        val afd = appContext.resources.openRawResourceFd(AppData.songsResId[index])
        player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
        player.prepare()

        player.start()
        _playerState.value = _playerState.value.copy(
            currentSongIndex = index,
            isPlaying = true,
            duration = player.duration,
            currentPosition = player.currentPosition,
            progress = 0f
        )

    }

    fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            } else {
                player.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
            }
        }
    }

    fun nextSong() {
        val nextIndex = if (_playerState.value.currentSongIndex < AppData.songsResId.size - 1)
        _playerState.value.currentSongIndex + 1 else 0
        playSong(nextIndex)
    }

    fun previousSong() {
        val prevIndex = if (_playerState.value.currentSongIndex > 0)
            _playerState.value.currentSongIndex - 1 else AppData.songsResId.size - 1
        playSong(prevIndex)
    }

    fun toggleRepeat() {
        _playerState.value = _playerState.value.copy(
            isRepeatOn = !_playerState.value.isRepeatOn
        )
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _playerState.value = _playerState.value.copy(currentPosition = position)
    }

    private fun startProgressUpdater() {
        if (progressJob != null) return

        progressJob = viewModelScope.launch {
            while (true) {
                val player = mediaPlayer
                if (player != null && player.isPlaying && !isDragging) {
                    _playerState.value = _playerState.value.copy(
                        currentPosition = player.currentPosition,
                        duration = player.duration,
                        progress = if (player.duration > 0)
                            player.currentPosition / player.duration.toFloat() else 0f
                    )
                }
                delay(200)
            }
        }
    }

    override fun onCleared() {
        progressJob?.cancel()
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        super.onCleared()
    }

    fun setProgress(newProgress: Float) {
        _playerState.value = _playerState.value.copy(progress = newProgress)
    }

}
