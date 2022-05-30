package com.fityan.audioplayer;


import android.net.Uri;

import java.io.File;

public class Music extends File {
    private final Uri uri;
    private final String title;

    Music(Uri uri, String title) {
        super(uri.toString());
        this.uri = uri;
        this.title = title;
    }

    /**
     * Get the music Uri.
     *
     * @return The music Uri.
     */
    public Uri getUri() {
        return uri;
    }


    /**
     * Get the music title.
     *
     * @return The music title.
     */
    public String getTitle() {
        return title;
    }
}
