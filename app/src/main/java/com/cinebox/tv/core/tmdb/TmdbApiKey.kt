package com.cinebox.tv.core.tmdb

import com.cinebox.tv.BuildConfig

/** TMDB key: the one the user entered in Settings wins; the build-time key is only a fallback. */
object TmdbApiKey {
    @Volatile
    var userKey: String = ""

    fun get(): String = userKey.trim().ifBlank { BuildConfig.TMDB_API_KEY }
}
