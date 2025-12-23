package com.example.musicapp

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null
    private var callback: PlayerCallback? = null
    private var currentIndex: Int = -1
    private var isRepeat: Boolean = false
    private var isDragging = false
    private var dragProgress: Float? = null

    interface PlayerCallback {
        fun onStateChanged(state: MusicPlayerState)
    }

    fun setCallback(cb: PlayerCallback?) {
        callback = cb
        notifyState()
    }

    fun playerState(): MusicPlayerState {
        val duration = mediaPlayer?.duration ?: 0
        val position = mediaPlayer?.currentPosition ?: 0
        val progress = if (duration > 0) position / duration.toFloat() else 0f

        return MusicPlayerState(
            currentSongIndex = currentIndex,
            isPlaying = mediaPlayer?.isPlaying ?: false,
            isRepeatOn = isRepeat,
            progress = progress,
            currentPosition = position,
            duration = duration
        )
    }

    fun playSong(index: Int) {
        prepareAndStart(index)
    }

    private fun prepareAndStart(index: Int) {
        try {
            val afd: AssetFileDescriptor = resources.openRawResourceFd(AppData.songsResId[index])
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer?.prepare()
            currentIndex = index
            mediaPlayer?.start()
            startProgressUpdates()
            mediaPlayer?.setOnCompletionListener {
                if (isRepeat) {
                    it.seekTo(0)
                    it.start()
                } else {
                    val next = if (currentIndex < AppData.songsResId.size - 1) currentIndex + 1 else 0
                    playSong(next)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isActive) {
                if (mediaPlayer != null && !isDragging) {
                    notifyState()
                }
                delay(200)
            }
        }
    }

    private fun notifyState() {
        val duration = mediaPlayer?.duration ?: 0
        val position = if (isDragging && dragProgress != null) {
            (dragProgress!! * duration).toInt()
        } else {
            mediaPlayer?.currentPosition ?: 0
        }
        val progress = if (duration > 0) position / duration.toFloat() else 0f

        val state = MusicPlayerState(
            currentSongIndex = currentIndex,
            isPlaying = mediaPlayer?.isPlaying ?: false,
            isRepeatOn = isRepeat,
            progress = progress,
            currentPosition = position,
            duration = duration
        )
        callback?.onStateChanged(state)
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause() else it.start()
            notifyState()
        }
    }

    fun nextSong() {
        val next = if (currentIndex < AppData.songsResId.size - 1) currentIndex + 1 else 0
        playSong(next)
    }

    fun previousSong() {
        val prev = if (currentIndex > 0) currentIndex - 1 else AppData.songsResId.size - 1
        playSong(prev)
    }

    fun toggleRepeat() {
        isRepeat = !isRepeat
        notifyState()
    }

    fun startDragging() {
        isDragging = true
    }

    fun stopDragging() {
        isDragging = false
        notifyState()
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        dragProgress = null
        notifyState()
    }

    fun setProgressValue(progress: Float) {
        dragProgress = progress
        notifyState()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun onDestroy() {
        progressJob?.cancel()
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
            } catch (_: Exception) {}
            reset()
            release()
        }
        mediaPlayer = null
        super.onDestroy()
    }
}
