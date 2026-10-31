package com.cinebox.tv.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class CineboxRadiusTokens(
    val none: Dp,
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val panel: Dp,
    val full: Dp
)

@Immutable
data class CineboxShapeTokens(
    val posterCard: Shape,
    val backdropCard: Shape,
    val collectionCard: Shape,
    val button: Shape,
    val iconButton: Shape,
    val chip: Shape,
    val badge: Shape,
    val dialog: Shape,
    val sidePanel: Shape,
    val sidebar: Shape,
    val navItem: Shape,
    val progress: Shape,
    val slider: Shape,
    val field: Shape,
    val menu: Shape,
    val circle: Shape
)

object CineboxRadii {
    val tokens = CineboxRadiusTokens(
        none = 0.dp,
        xxs = 2.dp,
        xs = 4.dp,
        sm = 8.dp,
        md = 12.dp,
        lg = 14.dp,
        xl = 16.dp,
        xxl = 20.dp,
        panel = 28.dp,
        full = 999.dp
    )
}

object CineboxShapes {
    val tokens = CineboxShapeTokens(
        posterCard = RoundedCornerShape(CineboxRadii.tokens.md),
        backdropCard = RoundedCornerShape(CineboxRadii.tokens.xl),
        collectionCard = RoundedCornerShape(CineboxRadii.tokens.xl),
        button = RoundedCornerShape(CineboxRadii.tokens.md),
        iconButton = RoundedCornerShape(CineboxRadii.tokens.md),
        chip = RoundedCornerShape(CineboxRadii.tokens.full),
        badge = RoundedCornerShape(CineboxRadii.tokens.xs),
        dialog = RoundedCornerShape(CineboxRadii.tokens.xl),
        sidePanel = RoundedCornerShape(CineboxRadii.tokens.xxl),
        sidebar = RoundedCornerShape(30.dp),
        navItem = RoundedCornerShape(CineboxRadii.tokens.full),
        progress = RoundedCornerShape(CineboxRadii.tokens.xxs),
        slider = RoundedCornerShape(CineboxRadii.tokens.full),
        field = RoundedCornerShape(CineboxRadii.tokens.md),
        menu = RoundedCornerShape(CineboxRadii.tokens.lg),
        circle = CircleShape
    )
}
