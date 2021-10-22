package com.fityan.audioplayer;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
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


        /* Initialize media player */
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.maroon5_girls_like_you);


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


        /* Get duration of media player,
            Convert ms to minutes & seconds, then displaying it */
        tvAudioDuration.setText(seekBarTimeFormat(mediaPlayer.getDuration()));


        /* When Play Button is clicked */
        btnPlay.setOnClickListener(view -> {
            /* Hide the Play Button & show the Pause Button */
            showPauseButton();

            /* Start the media player */
            mediaPlayer.start();

            /* Set seek bar max */
            seekBarAudio.setMax(mediaPlayer.getDuration());

            /* Start handler */
            handler.postDelayed(runnable, 0);
        });


        /* When Pause Button is clicked */
        btnPause.setOnClickListener(view -> {
            /* Hide the Pause Button & show the Play Button */
            showPlayButton();

            /* Pause the media player */
            mediaPlayer.pause();

            /* Stop handler */
            handler.removeCallbacks(runnable);
        });


        /* When Stop Button is clicked */
        btnStop.setOnClickListener(view -> {
            /* Hide the Pause Button & show the Play Button */
            showPlayButton();

            /* Stop the media player & the handler */
            mediaPlayer.stop();
            handler.removeCallbacks(runnable);

            /* Preparing the media player */
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.postDelayed(runnable, 500);
        });


        /* When Previous Button is clicked */
        btnPrevious.setOnClickListener(view -> {
        });


        /* When Next Button is clicked */
        btnNext.setOnClickListener(view -> {
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
                tvAudioPosition.setText(seekBarTimeFormat(
                        mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            /* Hide the Pause Button & show the Play Button  */
            showPlayButton();

            /* Reset media player position */
            mediaPlayer.seekTo(0);
        });
    }


    @SuppressLint("DefaultLocale")
    private String seekBarTimeFormat(int durationInMs) {
        long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(durationInMs);
        long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(durationInMs);

        return String.format("%02d:%02d",
                minutesDuration,
                secondsDuration - TimeUnit.MINUTES.toSeconds(minutesDuration));
    }


    private void showPlayButton() {
        btnPlay.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
    }


    private void showPauseButton() {
        btnPause.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);
    }
}