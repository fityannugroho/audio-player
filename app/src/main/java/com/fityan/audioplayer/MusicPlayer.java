package com.fityan.audioplayer;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends MediaPlayer {
    /* Properties */
    Runnable runnable;
    Handler handler = new Handler();

    /* Control Properties */



    MusicPlayer() {
        init();
    }


    public void init() {
        setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
    }


    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
    }


    @SuppressLint("DefaultLocale")
    private String seekBarTimeFormat(int durationInMs) {
        long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(durationInMs);
        long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(durationInMs);

        return String.format("%02d:%02d",
                minutesDuration,
                secondsDuration - TimeUnit.MINUTES.toSeconds(minutesDuration));
    }
}


//    Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.usa_for_africa_we_are_the_world);
//    File file = new File(url.toString());