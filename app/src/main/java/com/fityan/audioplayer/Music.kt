package com.fityan.audioplayer

import android.net.Uri
import java.io.File

class Music internal constructor(
    /**
     * Get the music Uri.
     *
     * @return The music Uri.
     */
    val uri: Uri,

    /**
     * Get the music title.
     *
     * @return The music title.
     */
    val title: String
) : File(uri.toString())