@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.cinebox.tv.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import com.cinebox.tv.R
import com.cinebox.tv.ui.theme.CineboxTheme
import kotlinx.coroutines.delay

/**
 * The file's chapters, from the right edge like the episodes panel. Focus opens on the chapter
 * playing, so the next and previous ones are one key away; selecting one seeks to its start.
 */
@Composable
internal fun ChaptersSidePanel(
    chapters: List<PlayerChapter>,
    positionMs: Long,
    durationMs: Long,
    focusRequester: FocusRequester,
    onChapterSelected: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    // Fixed while the panel is open: it marks where focus starts, not a live highlight.
    val openedAtIndex = remember(chapters) {
        PlayerChapters.indexAt(chapters, positionMs).coerceAtLeast(0)
    }
    val currentIndex = PlayerChapters.indexAt(chapters, positionMs)

    LaunchedEffect(chapters) {
        runCatching {
            listState.scrollToItem((openedAtIndex - 2).coerceAtLeast(0))
            delay(32)
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(520.dp)
            .clip(RoundedCornerShape(topStart = CineboxTheme.spacing.lg, bottomStart = CineboxTheme.spacing.lg))
            .background(CineboxTheme.colors.BackgroundElevated)
    ) {
        Column(modifier = Modifier.padding(CineboxTheme.spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.player_chapters_title, chapters.size),
                    style = MaterialTheme.typography.headlineSmall,
                    color = CineboxTheme.colors.TextPrimary
                )
                DialogButton(
                    text = stringResource(R.string.episodes_panel_close),
                    onClick = onClose,
                    isPrimary = false
                )
            }

            Spacer(modifier = Modifier.height(CineboxTheme.spacing.lg))

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(CineboxTheme.spacing.sm),
                contentPadding = PaddingValues(vertical = CineboxTheme.spacing.xs),
                modifier = Modifier.fillMaxHeight()
            ) {
                itemsIndexed(chapters, key = { _, chapter -> chapter.startMs }) { index, chapter ->
                    val endMs = chapters.getOrNull(index + 1)?.startMs ?: durationMs
                    ChapterItem(
                        number = index + 1,
                        chapter = chapter,
                        isCurrent = index == currentIndex,
                        progress = if (index == currentIndex && endMs > chapter.startMs) {
                            ((positionMs - chapter.startMs).toFloat() / (endMs - chapter.startMs)).coerceIn(0f, 1f)
                        } else {
                            null
                        },
                        remainingMs = if (index == currentIndex) (endMs - positionMs).coerceAtLeast(0L) else null,
                        modifier = if (index == openedAtIndex) Modifier.focusRequester(focusRequester) else Modifier,
                        onClick = { onChapterSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterItem(
    number: Int,
    chapter: PlayerChapter,
    isCurrent: Boolean,
    progress: Float?,
    remainingMs: Long?,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.colors(
            containerColor = CineboxTheme.colors.BackgroundCard,
            focusedContainerColor = CineboxTheme.colors.FocusBackground
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = CineboxTheme.focusRing.border(CineboxTheme.spacing.xxs),
                shape = RoundedCornerShape(CineboxTheme.radii.xl)
            )
        ),
        scale = CardDefaults.scale(focusedScale = 1.01f),
        shape = CardDefaults.shape(shape = RoundedCornerShape(CineboxTheme.radii.xl))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.width(28.dp), contentAlignment = Alignment.Center) {
                    if (isCurrent) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.cd_current),
                            tint = CineboxTheme.colors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = number.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = CineboxTheme.extendedColors.textTertiary
                        )
                    }
                }
                Text(
                    text = chapter.title ?: stringResource(R.string.player_chapter_number, number),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCurrent) CineboxTheme.colors.Primary else CineboxTheme.colors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatTime(chapter.startMs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineboxTheme.extendedColors.textSecondary
                )
            }
            if (progress != null && remainingMs != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .background(CineboxTheme.colors.Primary)
                        )
                    }
                    Text(
                        text = stringResource(R.string.player_chapter_remaining, formatTime(remainingMs)),
                        style = MaterialTheme.typography.bodySmall,
                        color = CineboxTheme.extendedColors.textTertiary
                    )
                }
            }
        }
    }
}
