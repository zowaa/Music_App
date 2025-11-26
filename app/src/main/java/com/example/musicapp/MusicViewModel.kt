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

class MusicViewModel( application: Application ) : AndroidViewModel(application) {

    private val appContext get() = getApplication<Application>().applicationContext

    private var mediaPlayer: MediaPlayer? = null

    private val _currentSongIndex = MutableStateFlow(-1)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isRepeatOn = MutableStateFlow(false)
    val isRepeatOn: StateFlow<Boolean> = _isRepeatOn

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    var isDragging = false

    private var progressJob: Job? = null

    init {
        startProgressUpdater()
    }

    fun playSong(index: Int) {
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }


        val player = MediaPlayer.create(appContext, AppData.songsResId[index])
        _duration.value = player.duration

        player.setOnCompletionListener {
            if (_isRepeatOn.value) {
                player.seekTo(0)
                player.start()
            } else {
                val nextIndex = if (index < AppData.songsResId.size - 1) index + 1 else 0
                playSong(nextIndex)
            }
        }

        player.start()
        mediaPlayer = player
        _currentSongIndex.value = index
        _isPlaying.value = true

    }

    fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.start()
                _isPlaying.value = true
            }
        }
    }

    fun nextSong() {
        val index = _currentSongIndex.value
        val newIndex = if (index < AppData.songsResId.size - 1) index + 1 else 0
        playSong(newIndex)
    }

    fun previousSong() {
        val index = _currentSongIndex.value
        val newIndex = if (index > 0) index - 1 else AppData.songsResId.size - 1
        playSong(newIndex)
    }

    fun toggleRepeat() {
        _isRepeatOn.value = !_isRepeatOn.value
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun startProgressUpdater() {
        if (progressJob != null) return

        progressJob = viewModelScope.launch {
            while (true) {
                val player = mediaPlayer
                if (player != null && player.isPlaying && !isDragging) {
                    _currentPosition.value = player.currentPosition
                    _duration.value = player.duration
                    _progress.value =
                        if (player.duration > 0) player.currentPosition / player.duration.toFloat() else 0f
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
        _progress.value = newProgress
    }

}
