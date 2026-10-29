package com.nuvio.tv.ui.screens.calendar

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nuvio.tv.R
import com.nuvio.tv.ui.theme.NuvioTheme
import java.time.LocalDate
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
    val grouped = remember(state.episodes) { state.episodes.groupBy { it.date } }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {
        if (showBuiltInHeader) {
            Text(
                text = stringResource(R.string.nav_calendar),
                style = MaterialTheme.typography.headlineMedium,
                color = NuvioTheme.colors.TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
        }
        if (state.episodes.isEmpty()) {
            Text(
                text = stringResource(if (state.isLoading) R.string.calendar_loading else R.string.calendar_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = NuvioTheme.colors.TextSecondary,
            )
            return@Column
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            grouped.forEach { (date, episodes) ->
                item(key = "d-$date") {
                    Text(
                        text = dayLabel(date, today),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (date == today) NuvioTheme.colors.Primary else NuvioTheme.colors.TextPrimary,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
                items(episodes, key = { "e-${it.seriesId}-${it.season}-${it.episode}" }) { episode ->
                    EpisodeRow(episode) { onOpenSeries(episode.seriesId, episode.seriesType, episode.addonBaseUrl) }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeRow(episode: CalendarEpisode, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CardDefaults.shape(shape = shape),
        colors = CardDefaults.colors(
            containerColor = NuvioTheme.colors.BackgroundCard,
            focusedContainerColor = NuvioTheme.colors.BackgroundCard,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(border = NuvioTheme.focusRing.border(3.dp), shape = shape)
        ),
        scale = CardDefaults.scale(focusedScale = 1.02f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.width(150.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(10.dp))) {
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
                    color = NuvioTheme.colors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "S%02dE%02d · %s".format(episode.season, episode.episode, episode.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (episode.watched) {
                Text(
                    text = "✓ " + stringResource(R.string.calendar_watched),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.Primary,
                )
            }
        }
    }
}

private fun dayLabel(date: LocalDate, today: LocalDate): String {
    val locale = Locale.getDefault()
    val base = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", locale))
    val weekday = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
    return when (date) {
        today -> "$weekday, $base"
        else -> "$weekday, $base"
    }
}
