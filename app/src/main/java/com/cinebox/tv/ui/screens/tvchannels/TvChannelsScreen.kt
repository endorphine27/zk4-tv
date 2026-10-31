package com.cinebox.tv.ui.screens.tvchannels

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import java.text.Normalizer
import com.cinebox.tv.R
import com.cinebox.tv.ui.screens.account.InputField
import com.cinebox.tv.ui.theme.CineboxTheme
import com.cinebox.tv.ui.util.rememberLongPressKeyTracker

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvChannelsScreen(
    showBuiltInHeader: Boolean = true,
    onOpenChannel: (channel: TvChannel) -> Unit,
    viewModel: TvChannelsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var focusedId by remember { mutableStateOf<String?>(null) }
    val visibleChannels = remember(state.channels, query) {
        val needle = query.trim().foldForSearch()
        if (needle.isEmpty()) state.channels else state.channels.filter { it.name.foldForSearch().contains(needle) }
    }
    val favorites = remember(visibleChannels, state.favorites, query) {
        if (query.isNotBlank()) emptyList() else visibleChannels.filter { it.id in state.favorites }
    }
    val focusedChannel = state.channels.firstOrNull { it.id == focusedId }
    LaunchedEffect(focusedId) {
        val channel = focusedChannel ?: return@LaunchedEffect
        delay(250)
        viewModel.loadUpNext(channel)
    }
    val firstTile = remember { FocusRequester() }
    var focusRequested by remember { mutableStateOf(false) }
    LaunchedEffect(state.channels.isNotEmpty()) {
        if (state.channels.isNotEmpty() && !focusRequested) {
            focusRequested = true
            runCatching { firstTile.requestFocus() }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {
        if (showBuiltInHeader) {
            Text(
                text = stringResource(R.string.nav_tv_channels),
                style = MaterialTheme.typography.headlineMedium,
                color = CineboxTheme.colors.TextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.tv_channels_hint),
                style = MaterialTheme.typography.bodySmall,
                color = CineboxTheme.colors.TextSecondary,
            )
            Spacer(Modifier.height(16.dp))
        }

        if (state.channels.isNotEmpty()) {
            InputField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.tv_channels_search),
                modifier = Modifier.fillMaxWidth(0.5f),
            )
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                if (focusedChannel != null) {
                    Text(
                        text = listOfNotNull(focusedChannel.name, focusedChannel.programme).joinToString("  —  "),
                        style = MaterialTheme.typography.titleSmall,
                        color = CineboxTheme.colors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val next = state.upNext[focusedChannel.id].orEmpty()
                    if (next.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.tv_channels_next) + ": " + next.joinToString("   ·   "),
                            style = MaterialTheme.typography.bodySmall,
                            color = CineboxTheme.colors.TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        when {
            state.channels.isEmpty() -> Text(
                text = stringResource(if (state.isLoading) R.string.tv_channels_loading else R.string.tv_channels_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = CineboxTheme.colors.TextSecondary,
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 210.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                if (favorites.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }, key = "h-fav") {
                        SectionTitle(stringResource(R.string.tv_channels_favorites))
                    }
                    items(favorites, key = { "fav-${it.id}" }) { channel ->
                        ChannelTile(
                            channel = channel,
                            isFavorite = true,
                            focusRequester = if (channel == favorites.first()) firstTile else null,
                            onFocused = { focusedId = channel.id },
                            onClick = { onOpenChannel(channel) },
                            onToggleFavorite = { viewModel.toggleFavorite(channel.id) },
                        )
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }, key = "h-all") {
                    SectionTitle(stringResource(R.string.tv_channels_all))
                }
                items(visibleChannels, key = { "all-${it.id}" }) { channel ->
                    ChannelTile(
                        channel = channel,
                        isFavorite = channel.id in state.favorites,
                        focusRequester = if (favorites.isEmpty() && channel == visibleChannels.first()) firstTile else null,
                        onFocused = { focusedId = channel.id },
                        onClick = { onOpenChannel(channel) },
                        onToggleFavorite = { viewModel.toggleFavorite(channel.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = CineboxTheme.colors.TextPrimary,
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ChannelTile(
    channel: TvChannel,
    isFavorite: Boolean,
    focusRequester: FocusRequester?,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    val longPressTracker = rememberLongPressKeyTracker()
    var longPressHandled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .onFocusChanged { if (it.isFocused) onFocused() }
                .onPreviewKeyEvent { keyEvent ->
                    val native = keyEvent.nativeKeyEvent
                    // Menu key or a long press on OK adds/removes the channel from favourites
                    if (native.action == AndroidKeyEvent.ACTION_DOWN && native.keyCode == AndroidKeyEvent.KEYCODE_MENU) {
                        longPressHandled = true
                        onToggleFavorite()
                        return@onPreviewKeyEvent true
                    }
                    if (longPressTracker.handle(native, ::isSelectKey) {
                            longPressHandled = true
                            onToggleFavorite()
                        }
                    ) {
                        if (native.action == AndroidKeyEvent.ACTION_UP) longPressHandled = false
                        return@onPreviewKeyEvent true
                    }
                    if (native.action == AndroidKeyEvent.ACTION_UP && longPressHandled &&
                        (isSelectKey(native.keyCode) || native.keyCode == AndroidKeyEvent.KEYCODE_MENU)
                    ) {
                        longPressHandled = false
                        return@onPreviewKeyEvent true
                    }
                    false
                },
            shape = CardDefaults.shape(shape = shape),
            colors = CardDefaults.colors(
                containerColor = CineboxTheme.colors.BackgroundCard,
                focusedContainerColor = CineboxTheme.colors.BackgroundCard,
            ),
            border = CardDefaults.border(
                focusedBorder = Border(border = CineboxTheme.focusRing.border(3.dp), shape = shape)
            ),
            scale = CardDefaults.scale(focusedScale = 1.06f),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = CineboxTheme.colors.Secondary,
                        modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    )
                }
            }
        }
        Text(
            text = channel.name,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            color = CineboxTheme.colors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        if (channel.programme != null) {
            Text(
                text = channel.programme,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = CineboxTheme.colors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Lower-case and strip accents, so "pro" finds "PRO TV" and "stiri" finds "Știri". */
private fun String.foldForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").lowercase()

private fun isSelectKey(keyCode: Int): Boolean =
    keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
