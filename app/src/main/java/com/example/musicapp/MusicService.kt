package com.example.musicapp

import android.app.Service
import android.content.Intent
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

    interface PlayerCallback {
        fun onPlaybackUpdate(
            position: Int,
            duration: Int,
            isPlaying: Boolean
        )

        fun onSongCompleted()
    }

    fun setCallback(cb: PlayerCallback?) {
        callback = cb
    }

    fun playSong(index: Int) {
        try {
            val afd = resources.openRawResourceFd(AppData.songsResId[index])

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            mediaPlayer?.prepare()
            mediaPlayer?.start()

            startProgressUpdates()

            mediaPlayer?.setOnCompletionListener {
                callback?.onSongCompleted()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isActive && mediaPlayer != null) {
                val position = mediaPlayer?.currentPosition ?: 0
                val duration = mediaPlayer?.duration ?: 0
                val isPlaying = mediaPlayer?.isPlaying ?: false

                callback?.onPlaybackUpdate(
                    position = position,
                    duration = duration,
                    isPlaying = isPlaying
                )

                delay(200)
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { if (it.isPlaying) it.pause() else it.start() }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
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
