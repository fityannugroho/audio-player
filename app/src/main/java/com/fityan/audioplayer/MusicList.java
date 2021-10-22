package com.fityan.audioplayer;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

public class MusicList {
    final private ArrayList<File> musicList = new ArrayList<>();
    private int nowPlayingAt;

    public void add(Uri uriMusic) {
        musicList.add(new File(uriMusic.toString()));
    }

    public int getNowPlayingAt() {
        return nowPlayingAt;
    }

    public File getPlayingMusic() {
        return musicList.get(nowPlayingAt);
    }

    public void setNowPlayingAt(int nowPlayingAt) {
        this.nowPlayingAt = nowPlayingAt;
    }

    public ArrayList<File> getMusicList() {
        return musicList;
    }

    public File getMusic(int index) {
        return musicList.get(index);
    }
}
