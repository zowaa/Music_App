package com.example.musicapp

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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

class MusicViewModel(application: Application) : AndroidViewModel(application), MusicService.PlayerCallback {
    private val _playerState = MutableStateFlow(MusicPlayerState())
    val playerState: StateFlow<MusicPlayerState> = _playerState
    private val appContext get() = getApplication<Application>().applicationContext
    @Suppress("StaticFieldLeak")
    private var musicService: MusicService? = null
    private var isServiceBound = false
    private var currentSongIndex = 0
    private var isRepeatOn = false
    private var currentPosition = 0
    private var duration = 0
    private var isPlaying = false


    override fun onPlaybackUpdate(
        position: Int,
        duration: Int,
        isPlaying: Boolean
    ) {
        currentPosition = position
        this.duration = duration
        this.isPlaying = isPlaying

        publishState()
    }

    override fun onSongCompleted() {
        if (isRepeatOn) {
            musicService?.playSong(currentSongIndex)
        } else {
            playNext()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? MusicService.LocalBinder
            musicService = localBinder?.getService()
            isServiceBound = true

            musicService?.setCallback(this@MusicViewModel)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            musicService = null
        }
    }

    init {
        val intent = Intent(appContext, MusicService::class.java)
        appContext.startService(intent)
        appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun publishState() {
        _playerState.value = MusicPlayerState(
            currentSongIndex = currentSongIndex,
            isPlaying = isPlaying,
            isRepeatOn = isRepeatOn,
            progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
            currentPosition = currentPosition,
            duration = duration
        )
    }


    fun playSong(index: Int) {
        currentSongIndex = index
        musicService?.playSong(index)
        publishState()
    }

    fun togglePlayPause() {
        musicService?.togglePlayPause()
    }

    fun playNext() {
        currentSongIndex = (currentSongIndex + 1) % AppData.songsResId.size
        musicService?.playSong(currentSongIndex)
        publishState()
    }

    fun playPrevious() {
        currentSongIndex =
            if (currentSongIndex > 0)
                currentSongIndex - 1
            else
                AppData.songsResId.lastIndex

        musicService?.playSong(currentSongIndex)
        publishState()
    }


    fun toggleRepeat() {
        isRepeatOn = !isRepeatOn
        publishState()
    }


    fun seekTo(progress: Float) {
        val positionMs = (progress * duration).toInt()
        musicService?.seekTo(positionMs)
    }


    override fun onCleared() {
        if (isServiceBound) {
            appContext.unbindService(connection)
            isServiceBound = false
        }
        musicService?.setCallback(null)
        musicService = null
        super.onCleared()
    }
}
