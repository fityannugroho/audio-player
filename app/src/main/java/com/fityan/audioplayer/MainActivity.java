package com.fityan.audioplayer;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    /* View properties */
    TextView tvAudioName, tvAudioPosition, tvAudioDuration;
    SeekBar seekBarAudio;
    ImageButton btnPrevious, btnNext, btnPlay, btnPause, btnStop;

    /* Media player properties */
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;

    /* initialize the music playlist */
    ArrayList<Integer> listOfMusicId = new ArrayList<>();

    /* initialize the nowPlayingMusic */
    int nowPlaying = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Assign view properties */
        tvAudioName = findViewById(R.id.tvAudioName);
        tvAudioPosition = findViewById(R.id.tvAudioPosition);
        tvAudioDuration = findViewById(R.id.tvAudioDuration);
        seekBarAudio = findViewById(R.id.seekBarAudio);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);


        /* load the music playlist */
        listOfMusicId.add(R.raw.maroon_5_sugar);
        listOfMusicId.add(R.raw.maroon5_girls_like_you);
        listOfMusicId.add(R.raw.maroon_5_memories);
        listOfMusicId.add(R.raw.over_the_horizon);


        /* load the first music */
        loadMusic(listOfMusicId.get(nowPlaying), "Music " + (nowPlaying+1));


        /* When Play Button is clicked */
        btnPlay.setOnClickListener(view -> playMusic());


        /* When Pause Button is clicked */
        btnPause.setOnClickListener(view -> pauseMusic());


        /* When Stop Button is clicked */
        btnStop.setOnClickListener(view -> {
            /* Stop the media player & handler */
            stopMusic();

            /* Preparing the media player */
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Back to start */
            mediaPlayer.seekTo(0);

            /* Handler post delay for 0.5s */
            handler.postDelayed(runnable, 500);
        });


        /* When Previous Button is clicked */
        btnPrevious.setOnClickListener(view -> {
            /* Get the previous music in playlist */
            if (nowPlaying != 0) {
                nowPlaying -= 1;

                /* Stop the media player & handler */
                stopMusic();

                /* Preparing the new music */
                loadMusic(listOfMusicId.get(nowPlaying), "Music " + (nowPlaying+1));

                /* Playing the new music */
                playMusic();
            } else {
                showToast("This is the first music.");
            }
        });


        /* When Next Button is clicked */
        btnNext.setOnClickListener(view -> {
            /* Get the next music in playlist */
            if (nowPlaying != listOfMusicId.size()-1) {
                nowPlaying += 1;

                /* Stop the media player & handler */
                stopMusic();

                /* Preparing the new music */
                loadMusic(listOfMusicId.get(nowPlaying), "Music " + (nowPlaying+1));

                /* Playing the new music */
                playMusic();
            } else {
                showToast("This is the last music.");
            }
        });


        /* When Seek Bar is scrolled */
        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    /* When drag on the seek bar, set progress to the seek bar */
                    mediaPlayer.seekTo(i);
                }

                /* Update the current position on display */
                tvAudioPosition.setText(seekBarTimeFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    /**
     * Load a music to audio player.
     * @param musicId The music id.
     * @param musicName The music name.
     */
    private void loadMusic(int musicId, String musicName) {
        /* Initialize media player */
        mediaPlayer = MediaPlayer.create(getApplicationContext(), musicId);

        /* Initialize runnable */
        runnable = new Runnable() {
            @Override
            public void run() {
                /* Set progress on seek bar */
                seekBarAudio.setProgress(mediaPlayer.getCurrentPosition());

                /* Handler post delay for 0.5s */
                handler.postDelayed(this, 500);
            }
        };

        /* Set seek bar max */
        seekBarAudio.setMax(mediaPlayer.getDuration());

        /* Get duration of media player, convert it to minutes:seconds format, then displaying it. */
        tvAudioDuration.setText(seekBarTimeFormat(mediaPlayer.getDuration()));

        /* Update the music name */
        tvAudioName.setText(musicName);

        /* Set autoplay to next music after the music is finished. */
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            /* Get the next music in playlist */
            if (nowPlaying != listOfMusicId.size()-1) {
                nowPlaying += 1;

                /* Stop the media player & handler */
                stopMusic();

                /* Preparing the new music */
                loadMusic(listOfMusicId.get(nowPlaying), "Music " + (nowPlaying+1));

                /* Playing the new music */
                playMusic();
            } else {
                /* Hide the Pause Button & show the Play Button  */
                showPlayButton();

                /* Reset media player position */
                mediaPlayer.seekTo(0);
            }
        });
    }


    /**
     * Play the audio player.
     */
    private void playMusic() {
        /* Hide the Play Button & show the Pause Button */
        showPauseButton();

        /* Start the media player */
        mediaPlayer.start();

        /* Start handler */
        handler.postDelayed(runnable, 0);
    }


    /**
     * Pause the audio player.
     */
    private void pauseMusic() {
        /* Hide the Pause Button & show the Play Button */
        showPlayButton();

        /* Pause the media player */
        mediaPlayer.pause();

        /* Stop handler */
        handler.removeCallbacks(runnable);
    }


    /**
     * Stop the audio player.
     */
    private void stopMusic() {
        /* Hide the Pause Button & show the Play Button */
        showPlayButton();

        /* Stop the media player */
        mediaPlayer.stop();

        /* Stop handler */
        handler.removeCallbacks(runnable);
    }


    /**
     * Convert time in milliseconds to seek bar format (mm:ss).
     * @param durationInMs The time in milliseconds.
     * @return The time in string with seek bar format.
     */
    @SuppressLint("DefaultLocale")
    private String seekBarTimeFormat(int durationInMs) {
        long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(durationInMs);
        long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(durationInMs);

        return String.format("%02d:%02d",
                minutesDuration,
                secondsDuration - TimeUnit.MINUTES.toSeconds(minutesDuration));
    }


    /**
     * Display the Play Button and hide the Pause Button.
     */
    private void showPlayButton() {
        btnPlay.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
    }


    /**
     * Display the Pause Button and hide the Play Button.
     */
    private void showPauseButton() {
        btnPause.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);
    }


    /**
     * Display a message with toast.
     * @param message The message to displayed.
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }
}