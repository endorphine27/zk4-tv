package com.nuvio.tv.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.network.NetworkResult
import com.nuvio.tv.domain.repository.LibraryRepository
import com.nuvio.tv.domain.repository.MetaRepository
import com.nuvio.tv.domain.repository.WatchProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarEpisode(
    val seriesId: String,
    val seriesType: String,
    val seriesName: String,
    val addonBaseUrl: String?,
    val season: Int,
    val episode: Int,
    val title: String,
    val date: LocalDate,
    val thumbnail: String?,
    val poster: String?,
    val watched: Boolean,
)

data class CalendarUiState(
    val isLoading: Boolean = true,
    val episodes: List<CalendarEpisode> = emptyList(),
)

/** Episodes of the library series that aired recently or air soon, read from the metadata addon. */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val metaRepository: MetaRepository,
    private val watchProgressRepository: WatchProgressRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            libraryRepository.libraryItems.collectLatest { entries ->
                val series = entries.filter { it.type.equals("series", ignoreCase = true) }.distinctBy { it.id }
                _state.value = CalendarUiState(isLoading = true, episodes = _state.value.episodes)
                val today = LocalDate.now()
                val from = today.minusDays(WINDOW_PAST_DAYS)
                val to = today.plusDays(WINDOW_FUTURE_DAYS)
                val episodes = coroutineScope {
                    series.map { entry ->
                        async {
                            val meta = (metaRepository.getMetaFromAllAddons(entry.type, entry.id, entry.addonBaseUrl)
                                .first { it !is NetworkResult.Loading } as? NetworkResult.Success)?.data
                                ?: return@async emptyList()
                            meta.videos.mapNotNull { video ->
                                val season = video.season ?: return@mapNotNull null
                                val number = video.episode ?: return@mapNotNull null
                                if (season <= 0) return@mapNotNull null
                                val date = parseDate(video.released) ?: return@mapNotNull null
                                if (date.isBefore(from) || date.isAfter(to)) return@mapNotNull null
                                val watched = runCatching {
                                    watchProgressRepository.isWatched(entry.id, video.id, season, number).first()
                                }.getOrDefault(false)
                                CalendarEpisode(
                                    seriesId = entry.id,
                                    seriesType = entry.type,
                                    seriesName = entry.name,
                                    addonBaseUrl = entry.addonBaseUrl,
                                    season = season,
                                    episode = number,
                                    title = video.title,
                                    date = date,
                                    thumbnail = video.thumbnail,
                                    poster = entry.poster,
                                    watched = watched,
                                )
                            }
                        }
                    }.awaitAll().flatten()
                }.sortedWith(compareBy({ it.date }, { it.seriesName }, { it.season }, { it.episode }))
                _state.value = CalendarUiState(isLoading = false, episodes = episodes)
            }
        }
    }

    private fun parseDate(value: String?): LocalDate? =
        value?.takeIf { it.length >= 10 }?.let { runCatching { LocalDate.parse(it.substring(0, 10)) }.getOrNull() }

    private companion object {
        const val WINDOW_PAST_DAYS = 14L
        const val WINDOW_FUTURE_DAYS = 60L
    }
}
