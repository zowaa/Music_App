package com.example.musicapp

import android.content.Context
import android.content.ServiceConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicRepository (
    private val context: Context
) : MusicService.PlayerCallback {
    private val _playerState = MutableStateFlow(MusicPlayerState())
    val playerState: StateFlow<MusicPlayerState> = _playerState

    private var currentSongIndex = 0
    private var isRepeatOn = false
    private var currentPosition = 0
    private var duration = 0
    private var isPlaying = false

    @Suppress("StaticFieldLeak")
    private var musicService: MusicService? = null
    private var isServiceBound = false

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
            playSong(currentSongIndex)
        } else {
            playNext()
        }
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


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: android.content.ComponentName?,
            binder: android.os.IBinder?
        ) {
            val localBinder = binder as? MusicService.LocalBinder
            musicService = localBinder?.getService()
            isServiceBound = true
            musicService?.setCallback(this@MusicRepository)

        }

        override fun onServiceDisconnected(name: android.content.ComponentName?) {
            isServiceBound = false
            musicService = null
        }
    }

    init {
        val intent = android.content.Intent(context, MusicService::class.java)
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun playSong(index: Int) {
        currentSongIndex = index
        musicService?.playSong(index)
        publishState()
    }

    fun playNext() {
        currentSongIndex = (currentSongIndex + 1) % AppData.songsResId.size
        playSong(currentSongIndex)
    }

    fun playPrevious() {
        currentSongIndex =
            if (currentSongIndex > 0)
                currentSongIndex - 1
            else
                AppData.songsResId.lastIndex

        playSong(currentSongIndex)
    }
    fun toggleRepeat() {
        isRepeatOn = !isRepeatOn
        publishState()
    }



    fun togglePlayPause() {
        if (isPlaying) {
            musicService?.pause()
        } else {
            musicService?.play()
        }
    }

    fun seekTo(positionMs: Int) {
        musicService?.seekTo(positionMs)
    }


    fun clear() {
        if (isServiceBound) {
            context.unbindService(connection)
            isServiceBound = false
        }
        musicService?.setCallback(null)
        musicService = null
    }


}