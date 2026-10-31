package com.cinebox.tv.ui.components

import com.cinebox.tv.ui.theme.CineboxTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.tv.material3.Icon

@Composable
fun MonochromePosterPlaceholder(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val base = CineboxTheme.colors.BackgroundCard
    val strokeColor = CineboxTheme.colors.TextTertiary.copy(alpha = 0.28f)
    val centerButtonBorder = CineboxTheme.colors.TextTertiary.copy(alpha = 0.18f)
    val backgroundGradient = remember(base) {
        Brush.verticalGradient(
            colors = listOf(
                base.copy(alpha = 0.92f),
                base.copy(alpha = 0.98f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                contentDescription?.let { description ->
                    Modifier.semantics { this.contentDescription = description }
                } ?: Modifier
            )
            .background(backgroundGradient)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(46.dp)
                .border(width = CineboxTheme.spacing.hairline, color = strokeColor, shape = CircleShape)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(42.dp)
                .background(Color.White.copy(alpha = 0.92f), CircleShape)
                .border(BorderStroke(CineboxTheme.spacing.hairline, centerButtonBorder), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = CineboxTheme.colors.BackgroundCard.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(CineboxTheme.spacing.xl)
                    .offset(x = CineboxTheme.spacing.hairline)
            )
        }
    }
}
