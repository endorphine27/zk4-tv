package com.cinebox.tv.core.trakt

import com.cinebox.tv.BuildConfig

/** Trakt Client ID: the one entered in Settings wins; the build-time one is only a fallback. */
object TraktClientId {
    @Volatile
    var userId: String = ""

    fun get(): String = userId.trim().ifBlank { BuildConfig.TRAKT_CLIENT_ID }

    /** Apps made on the new Trakt portal have no secret, so a user-entered ID never sends one. */
    fun secret(): String? =
        if (userId.isNotBlank()) null else BuildConfig.TRAKT_CLIENT_SECRET.ifBlank { null }
}
