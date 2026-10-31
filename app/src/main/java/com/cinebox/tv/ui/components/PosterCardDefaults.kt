package com.cinebox.tv.ui.components

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import com.cinebox.tv.ui.theme.CineboxComponents

val LocalLandscapePosterMode = staticCompositionLocalOf { false }
val LocalAlwaysBackdropWithLogo = staticCompositionLocalOf { false }

@Immutable
data class PosterCardStyle(
    val width: Dp = CineboxComponents.tokens.posterCard.width,
    val height: Dp = CineboxComponents.tokens.posterCard.height,
    val cornerRadius: Dp = CineboxComponents.tokens.posterCard.cornerRadius,
    val focusedBorderWidth: Dp = CineboxComponents.tokens.posterCard.focusedBorderWidth,
    val focusedScale: Float = CineboxComponents.tokens.posterCard.focusedScale
) {
    val aspectRatio: Float
        get() = width.value / height.value
}

object PosterCardDefaults {
    val Style = PosterCardStyle()
}
