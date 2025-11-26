package com.example.musicapp

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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


    fun playSong(index: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.release()

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

        startProgressUpdater()
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
        viewModelScope.launch {
            while (mediaPlayer?.isPlaying == true) {
                if (!isDragging) {
                    _currentPosition.value = mediaPlayer?.currentPosition ?: 0
                    _duration.value = mediaPlayer?.duration ?: 0
                    _progress.value =
                        if (_duration.value > 0) _currentPosition.value / _duration.value.toFloat() else 0f
                }
                delay(200)
            }
        }
    }

    override fun onCleared() {
        mediaPlayer?.release()
        super.onCleared()
    }

    fun setProgress(newProgress: Float) {
        _progress.value = newProgress
    }

}
