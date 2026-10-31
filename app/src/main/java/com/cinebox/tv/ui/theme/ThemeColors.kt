package com.cinebox.tv.ui.theme

import androidx.compose.ui.graphics.Color
import com.cinebox.tv.domain.model.AppTheme
import com.cinebox.tv.domain.model.CustomThemeColors

data class ThemeColorPalette(
    val secondary: Color,
    val secondaryVariant: Color,
    val onSecondary: Color = CineboxPrimitives.white,
    val onSecondaryVariant: Color = CineboxPrimitives.white,
    val accentGradient: List<Color> = listOf(secondary),
    val focusRing: Color,
    val focusRingGradient: List<Color> = listOf(focusRing),
    val focusBackground: Color,
    val background: Color = CineboxPrimitives.neutral950,
    val backgroundElevated: Color = CineboxPrimitives.neutral900,
    val backgroundCard: Color = CineboxPrimitives.neutral825,
    val surface: Color = CineboxPrimitives.neutral875,
    val surfaceVariant: Color = CineboxPrimitives.neutral800,
    val panel: Color = CineboxPrimitives.neutral900,
    val overlay: Color = Color(0xD9000000),
    val field: Color = CineboxPrimitives.neutral850,
    val menu: Color = CineboxPrimitives.neutral875,
    val modal: Color = CineboxPrimitives.neutral900,
    val playerOverlay: Color = Color(0xCC000000)
)

object ThemeColors {
    val Crimson = ThemeColorPalette(
        secondary = CineboxPrimitives.red500,
        secondaryVariant = CineboxPrimitives.red600,
        focusRing = CineboxPrimitives.red300,
        focusBackground = Color(0xFF3D1A1A),
        backgroundCard = Color(0xFF241A1A)
    )

    val Ocean = ThemeColorPalette(
        secondary = CineboxPrimitives.blue500,
        secondaryVariant = CineboxPrimitives.blue700,
        focusRing = CineboxPrimitives.blue300,
        focusBackground = Color(0xFF1A2D3D),
        background = Color(0xFF0D0D0F),
        backgroundElevated = Color(0xFF1A1A1E),
        backgroundCard = Color(0xFF1A1F24)
    )

    val Violet = ThemeColorPalette(
        secondary = CineboxPrimitives.violet500,
        secondaryVariant = CineboxPrimitives.violet700,
        focusRing = CineboxPrimitives.violet300,
        focusBackground = Color(0xFF2D1A3D),
        background = Color(0xFF0D0D0F),
        backgroundElevated = Color(0xFF1A1A1E),
        backgroundCard = Color(0xFF1F1A24)
    )

    val Emerald = ThemeColorPalette(
        secondary = CineboxPrimitives.green500,
        secondaryVariant = CineboxPrimitives.green700,
        focusRing = CineboxPrimitives.green300,
        focusBackground = Color(0xFF1A3D1E),
        backgroundCard = Color(0xFF1A241A)
    )

    val Amber = ThemeColorPalette(
        secondary = CineboxPrimitives.amber500,
        secondaryVariant = CineboxPrimitives.amber700,
        focusRing = CineboxPrimitives.amber300,
        focusBackground = Color(0xFF3D2D1A),
        background = Color(0xFF0F0D0D),
        backgroundElevated = Color(0xFF1E1A1A),
        backgroundCard = Color(0xFF24201A)
    )

    val Rose = ThemeColorPalette(
        secondary = CineboxPrimitives.rose500,
        secondaryVariant = CineboxPrimitives.rose700,
        focusRing = CineboxPrimitives.rose300,
        focusBackground = Color(0xFF3D1A2D),
        backgroundCard = Color(0xFF241A1F)
    )

    val White = ThemeColorPalette(
        secondary = CineboxPrimitives.neutral100,
        secondaryVariant = CineboxPrimitives.neutral200,
        onSecondary = CineboxPrimitives.neutral925,
        onSecondaryVariant = CineboxPrimitives.neutral925,
        focusRing = CineboxPrimitives.white,
        focusBackground = Color(0xFF303030),
        backgroundCard = CineboxPrimitives.neutral850
    )

    fun getColorPalette(
        theme: AppTheme,
        customColors: CustomThemeColors = CustomThemeColors.Default
    ): ThemeColorPalette {
        return when (theme) {
            AppTheme.CUSTOM -> customColors.toColorPalette()
            AppTheme.GOLD -> SupporterThemeColors.Gold
            AppTheme.JADE -> SupporterThemeColors.Jade
            AppTheme.ROSE_GOLD -> SupporterThemeColors.RoseGold
            AppTheme.ARCTIC_BLUE -> SupporterThemeColors.ArcticBlue
            AppTheme.GRAPHITE -> SupporterThemeColors.Graphite
            AppTheme.CRIMSON -> Crimson
            AppTheme.OCEAN -> Ocean
            AppTheme.VIOLET -> Violet
            AppTheme.EMERALD -> Emerald
            AppTheme.AMBER -> Amber
            AppTheme.ROSE -> Rose
            AppTheme.WHITE -> White
        }
    }
}
