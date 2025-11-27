package com.example.musicapp

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
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
    @Suppress("StaticFieldLeak")
    private var musicService: MusicService? = null
    private var isServiceBound = false
    var isDragging = false



    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? MusicService.LocalBinder
            musicService = localBinder?.getService()
            isServiceBound = true

            musicService?.setCallback(object : MusicService.PlayerCallback {
                override fun onStateChanged(state: MusicPlayerState) {
                    _playerState.value = state
                }
            })

            musicService?.let { _playerState.value = it.playerState() }
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

    fun onStartDragging() {
        isDragging = true
        musicService?.startDragging()
    }

    fun onStopDragging() {
        isDragging = false
        musicService?.stopDragging()
    }


    fun playSong(index: Int) {
        musicService?.playSong(index)
    }

    fun togglePlayPause() {
        musicService?.togglePlayPause()
    }

    fun nextSong() {
        musicService?.nextSong()

    }

    fun previousSong() {
        musicService?.previousSong()
    }

    fun toggleRepeat() {
        musicService?.toggleRepeat()
    }

    fun seekTo(position: Int) {
        musicService?.seekTo(position)
    }

    fun setProgress(newProgress: Float) {
            musicService?.setProgressValue(newProgress)
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
