package com.cinebox.tv.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** Month grid with a poster strip per day; the panel on the right lists the episodes of the focused day. */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CalendarScreen(
    showBuiltInHeader: Boolean = true,
    onOpenSeries: (id: String, type: String, addonBaseUrl: String?) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val today = remember { LocalDate.now() }
    val locale = Locale.getDefault()
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var selected by remember { mutableStateOf(today) }
    val byDate = remember(state.episodes) { state.episodes.groupBy { it.date } }
    val startFocus = remember { FocusRequester() }
    LaunchedEffect(month) {
        delay(150)
        runCatching { startFocus.requestFocus() }
    }
    val focusDate = if (YearMonth.from(today) == month) today else month.atDay(1)

    Row(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 20.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            if (showBuiltInHeader) {
                Text(
                    text = stringResource(R.string.nav_calendar),
                    style = MaterialTheme.typography.headlineMedium,
                    color = CineboxTheme.colors.TextPrimary,
                )
                Spacer(Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MonthButton("‹") { month = month.minusMonths(1) }
                Column(modifier = Modifier.width(260.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = month.year.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CineboxTheme.colors.TextSecondary,
                    )
                    Text(
                        text = month.month.getDisplayName(TextStyle.FULL_STANDALONE, locale),
                        style = MaterialTheme.typography.headlineMedium,
                        color = CineboxTheme.colors.TextPrimary,
                    )
                }
                MonthButton("›") { month = month.plusMonths(1) }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..7) {
                    Text(
                        text = java.time.DayOfWeek.of(i).getDisplayName(TextStyle.SHORT_STANDALONE, locale),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        color = CineboxTheme.colors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            val offset = month.atDay(1).dayOfWeek.value - 1
            val rows = (offset + month.lengthOfMonth() + 6) / 7
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        for (c in 0..6) {
                            val dayNo = r * 7 + c - offset + 1
                            if (dayNo < 1 || dayNo > month.lengthOfMonth()) {
                                Spacer(Modifier.weight(1f))
                            } else {
                                val date = month.atDay(dayNo)
                                DayCell(
                                    date = date,
                                    isToday = date == today,
                                    episodes = byDate[date].orEmpty(),
                                    onFocused = { selected = date },
                                    onClick = {
                                        byDate[date]?.firstOrNull()?.let { onOpenSeries(it.seriesId, it.seriesType, it.addonBaseUrl) }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(if (date == focusDate) Modifier.focusRequester(startFocus) else Modifier),
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(24.dp))
        Column(modifier = Modifier.width(360.dp)) {
            Text(
                text = stringResource(R.string.calendar_upcoming),
                style = MaterialTheme.typography.headlineSmall,
                color = CineboxTheme.colors.TextPrimary,
            )
            Text(
                text = dayLabel(selected, today),
                style = MaterialTheme.typography.bodyLarge,
                color = CineboxTheme.colors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
            val dayEpisodes = byDate[selected].orEmpty()
            if (dayEpisodes.isEmpty()) {
                Text(
                    text = stringResource(if (state.isLoading) R.string.calendar_loading else R.string.calendar_day_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineboxTheme.colors.TextSecondary,
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(dayEpisodes, key = { "${it.seriesId}-${it.season}-${it.episode}" }) { episode ->
                        EpisodeCard(episode) { onOpenSeries(episode.seriesId, episode.seriesType, episode.addonBaseUrl) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun MonthButton(label: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.width(56.dp).height(44.dp),
        shape = CardDefaults.shape(shape = shape),
        colors = CardDefaults.colors(
            containerColor = CineboxTheme.colors.BackgroundCard,
            focusedContainerColor = CineboxTheme.colors.BackgroundCard,
        ),
        border = CardDefaults.border(focusedBorder = Border(border = CineboxTheme.focusRing.border(3.dp), shape = shape)),
        scale = CardDefaults.scale(focusedScale = 1.05f),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.titleLarge, color = CineboxTheme.colors.TextPrimary)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DayCell(
    date: LocalDate,
    isToday: Boolean,
    episodes: List<CalendarEpisode>,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxSize().onFocusChanged { if (it.isFocused) onFocused() },
        shape = CardDefaults.shape(shape = shape),
        colors = CardDefaults.colors(
            containerColor = CineboxTheme.colors.BackgroundCard,
            focusedContainerColor = CineboxTheme.colors.BackgroundCard,
        ),
        border = CardDefaults.border(
            border = if (isToday) Border(border = androidx.compose.foundation.BorderStroke(2.dp, CineboxTheme.colors.Primary), shape = shape) else Border.None,
            focusedBorder = Border(border = CineboxTheme.focusRing.border(3.dp), shape = shape),
        ),
        scale = CardDefaults.scale(focusedScale = 1.04f),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = if (isToday) CineboxTheme.colors.Primary else CineboxTheme.colors.TextPrimary,
            )
            if (episodes.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    episodes.take(2).forEach { episode ->
                        AsyncImage(
                            model = episode.poster ?: episode.thumbnail,
                            contentDescription = episode.seriesName,
                            modifier = Modifier.fillMaxHeight().aspectRatio(2f / 3f).clip(RoundedCornerShape(5.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeCard(episode: CalendarEpisode, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CardDefaults.shape(shape = shape),
        colors = CardDefaults.colors(
            containerColor = CineboxTheme.colors.BackgroundCard,
            focusedContainerColor = CineboxTheme.colors.BackgroundCard,
        ),
        border = CardDefaults.border(focusedBorder = Border(border = CineboxTheme.focusRing.border(3.dp), shape = shape)),
        scale = CardDefaults.scale(focusedScale = 1.02f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = episode.thumbnail ?: episode.poster,
                contentDescription = episode.seriesName,
                modifier = Modifier.width(120.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "S%02dE%02d".format(episode.season, episode.episode),
                    style = MaterialTheme.typography.labelLarge,
                    color = CineboxTheme.colors.Primary,
                )
                Text(
                    text = episode.seriesName,
                    style = MaterialTheme.typography.titleMedium,
                    color = CineboxTheme.colors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (episode.watched) "✓ ${episode.title}" else episode.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineboxTheme.colors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun dayLabel(date: LocalDate, today: LocalDate): String {
    val locale = Locale.getDefault()
    val base = date.format(DateTimeFormatter.ofPattern("d MMMM", locale))
    val weekday = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
    val prefix = when (date) {
        today -> "Azi · "
        today.plusDays(1) -> "Mâine · "
        today.minusDays(1) -> "Ieri · "
        else -> ""
    }
    return "$prefix$weekday, $base"
}
