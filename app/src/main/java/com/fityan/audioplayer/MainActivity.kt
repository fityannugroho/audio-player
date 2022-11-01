package com.fityan.audioplayer

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    /* View properties */
    private lateinit var tvAudioName: TextView
    private lateinit var tvAudioPosition: TextView
    private lateinit var tvAudioDuration: TextView
    private lateinit var seekBarAudio: SeekBar
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnStop: ImageButton

    /* Media player properties */
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler()
    private var runnable: Runnable? = null

    /* initialize the music playlist */
    private val musicList = ArrayList<Music>()

    /* initialize the nowPlayingMusic */
    private var nowPlaying = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Assign view properties */
        tvAudioName = findViewById(R.id.tvAudioName)
        tvAudioPosition = findViewById(R.id.tvAudioPosition)
        tvAudioDuration = findViewById(R.id.tvAudioDuration)
        seekBarAudio = findViewById(R.id.seekBarAudio)
        btnPlay = findViewById(R.id.btnPlay)
        btnPause = findViewById(R.id.btnPause)
        btnStop = findViewById(R.id.btnStop)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)

        /* load the music playlist */
        musicList.add(
            Music(
                getRawUri(R.raw.maroon_5_sugar),
                "Maroon 5 - Sugar"
            )
        )
        musicList.add(Music(getRawUri(R.raw.maroon5_girls_like_you), "Maroon 5 - Girls Like You"))
        musicList.add(Music(getRawUri(R.raw.maroon_5_memories), "Maroon 5 - Memories"))
        musicList.add(Music(getRawUri(R.raw.over_the_horizon), "Over The Horizon"))


        /* Initialize the audio player */
        init()

        /* load the first music */
        try {
            loadMusic(musicList[nowPlaying])
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /* When Play Button is clicked */
        btnPlay.setOnClickListener { playMusic() }

        /* When Pause Button is clicked */
        btnPause.setOnClickListener { pauseMusic() }

        /* When Stop Button is clicked */
        btnStop.setOnClickListener {
            /* Stop the media player & handler */
            stopMusic()

            /* Preparing the media player */
            try {
                mediaPlayer.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            /* Back to start */
            mediaPlayer.seekTo(0)

            /* Handler post delay for 0.5s */
            handler.postDelayed(runnable!!, 500)
        }

        /* When Previous Button is clicked */
        btnPrevious.setOnClickListener {
            /* Get the previous music in playlist */
            try {
                goToPreviousMusic()
            } catch (e: IndexOutOfBoundsException) {
                showToast("This is the first music.")
            }
        }

        /* When Next Button is clicked */
        btnNext.setOnClickListener {
            /* Get the next music in playlist */
            try {
                goToNextMusic()
            } catch (e: IndexOutOfBoundsException) {
                showToast("This is the last music.")
            }
        }

        /* When Seek Bar is scrolled */
        seekBarAudio.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    /* When drag on the seek bar, set progress to the seek bar */
                    mediaPlayer.seekTo(i)
                }

                /* Update the current position on display */
                tvAudioPosition.text = seekBarTimeFormat(mediaPlayer.currentPosition)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onDestroy() {
        /* Stop the media player */
        stopMusic()

        /* Destroy the media player. */
        mediaPlayer.release()

        /* Destroy the activity */
        super.onDestroy()
    }

    /**
     * Initialize the audio player.
     */
    private fun init() {
        /* Initialize media player */
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        /* Set autoplay to next music after the music is finished. */
        mediaPlayer.setOnCompletionListener { mediaPlayer: MediaPlayer ->
            try {
                goToNextMusic()
            } catch (e: IndexOutOfBoundsException) {
                /* Hide the Pause Button & show the Play Button  */
                showPlayButton()

                /* Reset media player position */
                mediaPlayer.seekTo(0)
            }
        }
    }

    /**
     * Load a music to audio player.
     *
     * @param music The music.
     */
    @Throws(IOException::class)
    private fun loadMusic(music: Music) {
        /* Reset the media player to idle. */
        mediaPlayer.reset()

        /* Set the music to media player */
        mediaPlayer.setDataSource(applicationContext, music.uri)

        /* Preparing the media player */
        mediaPlayer.prepare()

        /* Initialize runnable */runnable = object : Runnable {
            override fun run() {
                /* Set progress on seek bar */
                seekBarAudio.progress = mediaPlayer.currentPosition

                /* Handler post delay for 0.5s */
                handler.postDelayed(this, 500)
            }
        }

        /* Set seek bar max */
        seekBarAudio.max = mediaPlayer.duration

        /* Get duration of media player, convert it to Seek Bar time format, then displaying it. */
        tvAudioDuration.text = seekBarTimeFormat(mediaPlayer.duration)

        /* Update the music name */
        tvAudioName.text = music.title
    }

    /**
     * Play the previous music
     */
    private fun goToPreviousMusic() {
        if (nowPlaying == 0) {
            throw IndexOutOfBoundsException()
        }

        /* Stop the media player & handler */
        stopMusic()

        /* Preparing the new music */
        nowPlaying -= 1
        try {
            loadMusic(musicList[nowPlaying])
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /* Playing the new music */
        playMusic()
    }

    /**
     * Play the next music.
     */
    private fun goToNextMusic() {
        if (nowPlaying == musicList.size - 1) {
            throw IndexOutOfBoundsException()
        }

        /* Stop the media player & handler */
        stopMusic()

        /* Preparing the new music */
        nowPlaying += 1
        try {
            loadMusic(musicList[nowPlaying])
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /* Playing the new music */
        playMusic()
    }

    /**
     * Play the audio player.
     */
    private fun playMusic() {
        /* Hide the Play Button & show the Pause Button */
        showPauseButton()

        /* Start the media player */
        mediaPlayer.start()

        /* Start handler */
        handler.postDelayed(runnable!!, 0)
    }

    /**
     * Pause the audio player.
     */
    private fun pauseMusic() {
        /* Hide the Pause Button & show the Play Button */
        showPlayButton()

        /* Pause the media player */
        mediaPlayer.pause()

        /* Stop handler */
        handler.removeCallbacks(runnable!!)
    }

    /**
     * Stop the audio player.
     */
    private fun stopMusic() {
        /* Hide the Pause Button & show the Play Button */
        showPlayButton()

        /* Stop the media player */
        mediaPlayer.stop()

        /* Stop handler */
        handler.removeCallbacks(runnable!!)
    }

    /**
     * Convert time in milliseconds to seek bar format (mm:ss).
     *
     * @param durationInMs The time in milliseconds.
     * @return The time in string with seek bar format.
     */
    @SuppressLint("DefaultLocale")
    private fun seekBarTimeFormat(durationInMs: Int): String {
        val minutesDuration = TimeUnit.MILLISECONDS.toMinutes(durationInMs.toLong())
        val secondsDuration = TimeUnit.MILLISECONDS.toSeconds(durationInMs.toLong())
        return String.format(
            "%02d:%02d",
            minutesDuration,
            secondsDuration - TimeUnit.MINUTES.toSeconds(minutesDuration)
        )
    }

    /**
     * Display the Play Button and hide the Pause Button.
     */
    private fun showPlayButton() {
        btnPlay.visibility = View.VISIBLE
        btnPause.visibility = View.GONE
    }

    /**
     * Display the Pause Button and hide the Play Button.
     */
    private fun showPauseButton() {
        btnPause.visibility = View.VISIBLE
        btnPlay.visibility = View.GONE
    }

    /**
     * Display a message with toast.
     *
     * @param message The message to displayed.
     */
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
            .show()
    }

    private fun getRawUri(rawId: Int): Uri {
        return Uri.parse("android.resource://$packageName/$rawId")
    }
}