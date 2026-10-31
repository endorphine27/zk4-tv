package com.cinebox.tv.core.plugin.cloudstream

import com.lagradost.cloudstream3.TvType

/** Map CloudStream TvType to CineboxTV content type string ("movie" or "tv"). */
fun TvType.toCineboxType(): String = when (this) {
    TvType.Movie, TvType.AnimeMovie, TvType.Documentary, TvType.Torrent -> "movie"
    else -> "tv"
}

/** Parse TvType from string name, case-insensitive. */
fun tvTypeFromString(value: String): TvType? = TvType.entries.firstOrNull {
    it.name.equals(value, ignoreCase = true)
}
