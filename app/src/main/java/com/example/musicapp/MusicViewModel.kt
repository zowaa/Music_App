package com.example.musicapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow

data class MusicPlayerState(
    val currentSongIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isRepeatOn: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Int = 0,
    val duration: Int = 0
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(getApplication<Application>().applicationContext)
    val playerState: StateFlow<MusicPlayerState> = repository.playerState


    fun playSong(index: Int) {
        repository.playSong(index)
    }

    fun togglePlayPause() {
        repository.togglePlayPause()
    }

    fun seekTo(progress: Float) {
        val duration = playerState.value.duration
        val positionMs = (progress * duration).toInt()
        repository.seekTo(positionMs)
    }

    fun playNext() {
        repository.playNext()
    }

    fun playPrevious() {
        repository.playPrevious()
    }

    fun toggleRepeat() {
        repository.toggleRepeat()
    }




    override fun onCleared() {
        repository.clear()
        super.onCleared()
    }
}
