package com.cinebox.tv.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.cinebox.tv.R
import com.cinebox.tv.ui.theme.CineboxTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CalendarScreen(
    showBuiltInHeader: Boolean = true,
    onOpenSeries: (id: String, type: String, addonBaseUrl: String?) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val today = remember { LocalDate.now() }
    // Two sections: what is coming (soonest first), then what aired recently (latest first, dimmed).
    val upcoming = remember(state.episodes) {
        state.episodes.filter { !it.date.isBefore(today) }.groupBy { it.date }
    }
    val recent = remember(state.episodes) {
        state.episodes.filter { it.date.isBefore(today) }.sortedByDescending { it.date }.groupBy { it.date }
    }
    val firstEpisodeFocus = remember { FocusRequester() }
    LaunchedEffect(state.episodes.isNotEmpty()) {
        if (state.episodes.isNotEmpty()) {
            delay(150)
            runCatching { firstEpisodeFocus.requestFocus() }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {
        if (showBuiltInHeader) {
            Text(
                text = stringResource(R.string.nav_calendar),
                style = MaterialTheme.typography.headlineMedium,
                color = CineboxTheme.colors.TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
        }
        if (state.episodes.isEmpty()) {
            Text(
                text = stringResource(if (state.isLoading) R.string.calendar_loading else R.string.calendar_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = CineboxTheme.colors.TextSecondary,
            )
            return@Column
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "h-upcoming") {
                SectionHeading(stringResource(R.string.calendar_upcoming))
            }
            if (upcoming.isEmpty()) {
                item(key = "h-upcoming-empty") {
                    Text(
                        text = stringResource(R.string.calendar_upcoming_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CineboxTheme.colors.TextSecondary,
                    )
                }
            }
            upcoming.forEach { (date, episodes) ->
                item(key = "u-$date") { DayHeading(date, today) }
                items(episodes, key = { "u-${it.seriesId}-${it.season}-${it.episode}" }) { episode ->
                    EpisodeRow(
                        episode = episode,
                        past = false,
                        whenText = whenLabel(date, today),
                        focusRequester = if (date == upcoming.keys.first() && episode == episodes.first()) firstEpisodeFocus else null,
                    ) { onOpenSeries(episode.seriesId, episode.seriesType, episode.addonBaseUrl) }
                }
            }
            if (recent.isNotEmpty()) {
                item(key = "h-recent") {
                    SectionHeading(stringResource(R.string.calendar_recent), modifier = Modifier.padding(top = 20.dp))
                }
                recent.forEach { (date, episodes) ->
                    item(key = "r-$date") { DayHeading(date, today) }
                    items(episodes, key = { "r-${it.seriesId}-${it.season}-${it.episode}" }) { episode ->
                        EpisodeRow(
                            episode = episode,
                            past = true,
                            whenText = null,
                            focusRequester = if (upcoming.isEmpty() && date == recent.keys.first() && episode == episodes.first()) firstEpisodeFocus else null,
                        ) { onOpenSeries(episode.seriesId, episode.seriesType, episode.addonBaseUrl) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = CineboxTheme.colors.TextPrimary,
        modifier = modifier,
    )
}

@Composable
private fun DayHeading(date: LocalDate, today: LocalDate) {
    Text(
        text = dayLabel(date, today),
        style = MaterialTheme.typography.titleLarge,
        color = when {
            date == today -> CineboxTheme.colors.Primary
            date.isBefore(today) -> CineboxTheme.colors.TextSecondary
            else -> CineboxTheme.colors.TextPrimary
        },
        modifier = Modifier.padding(top = 8.dp),
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeRow(
    episode: CalendarEpisode,
    past: Boolean,
    whenText: String?,
    focusRequester: FocusRequester?,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        shape = CardDefaults.shape(shape = shape),
        colors = CardDefaults.colors(
            containerColor = CineboxTheme.colors.BackgroundCard,
            focusedContainerColor = CineboxTheme.colors.BackgroundCard,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(border = CineboxTheme.focusRing.border(3.dp), shape = shape)
        ),
        scale = CardDefaults.scale(focusedScale = 1.02f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().alpha(if (past) 0.55f else 1f).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.width(190.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(10.dp))) {
                AsyncImage(
                    model = episode.thumbnail ?: episode.poster,
                    contentDescription = episode.seriesName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = episode.seriesName,
                    style = MaterialTheme.typography.titleMedium,
                    color = CineboxTheme.colors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "S%02dE%02d · %s".format(episode.season, episode.episode, episode.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineboxTheme.colors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (whenText != null) {
                    Text(
                        text = whenText,
                        style = MaterialTheme.typography.titleSmall,
                        color = CineboxTheme.colors.Primary,
                    )
                }
                if (episode.watched) {
                    Text(
                        text = "✓ " + stringResource(R.string.calendar_watched),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CineboxTheme.colors.Primary,
                    )
                }
            }
        }
    }
}

private fun dayLabel(date: LocalDate, today: LocalDate): String {
    val locale = Locale.getDefault()
    val base = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", locale))
    val weekday = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
    val prefix = when (date) {
        today -> "Azi · "
        today.plusDays(1) -> "Mâine · "
        today.minusDays(1) -> "Ieri · "
        else -> ""
    }
    return "$prefix$weekday, $base"
}

/** Short badge for upcoming days: "Azi", "Mâine", "peste 3 zile"; nothing for days already gone. */
private fun whenLabel(date: LocalDate, today: LocalDate): String? {
    val days = ChronoUnit.DAYS.between(today, date)
    return when {
        days < 0 -> null
        days == 0L -> "Azi"
        days == 1L -> "Mâine"
        else -> "peste $days zile"
    }
}
