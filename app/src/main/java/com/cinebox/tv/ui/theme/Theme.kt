package com.cinebox.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.cinebox.tv.domain.model.AppFont
import com.cinebox.tv.domain.model.AppTheme
import com.cinebox.tv.domain.model.CustomThemeColors
import com.cinebox.tv.domain.model.SettingsUiStyle

data class CineboxExtendedColors(
    val backgroundElevated: Color,
    val backgroundCard: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val focusRing: Color,
    val focusBackground: Color,
    val rating: Color
)

val LocalCineboxColors = staticCompositionLocalOf {
    CineboxColorScheme(ThemeColors.Ocean)
}

val LocalCineboxExtendedColors = staticCompositionLocalOf {
    CineboxExtendedColors(
        backgroundElevated = Color(0xFF1A1A1A),
        backgroundCard = Color(0xFF242424),
        textSecondary = Color(0xFFB3B3B3),
        textTertiary = Color(0xFF808080),
        focusRing = ThemeColors.Ocean.focusRing,
        focusBackground = ThemeColors.Ocean.focusBackground,
        rating = Color(0xFFFFD700)
    )
}

val LocalCineboxTextStyles = staticCompositionLocalOf { CineboxTextStyles }

val LocalAppTheme = staticCompositionLocalOf { AppTheme.WHITE }

val LocalThemePalette = staticCompositionLocalOf { ThemeColors.White }

val LocalSettingsUiStyle = staticCompositionLocalOf { SettingsUiStyle.CLASSIC }

val LocalCineboxFocusRingStyle = staticCompositionLocalOf {
    createFocusRingStyle(ThemeColors.Ocean)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CineboxTheme(
    appTheme: AppTheme = AppTheme.WHITE,
    appFont: AppFont = AppFont.INTER,
    amoledMode: Boolean = false,
    amoledSurfacesMode: Boolean = false,
    settingsUiStyle: SettingsUiStyle = SettingsUiStyle.CLASSIC,
    customThemeColors: CustomThemeColors = CustomThemeColors.Default,
    content: @Composable () -> Unit
) {
    val palette = androidx.compose.runtime.remember(appTheme, customThemeColors) {
        ThemeColors.getColorPalette(appTheme, customThemeColors)
    }
    val focusRingStyle = createFocusRingStyle(palette)
    val colorScheme = CineboxColorScheme(
        palette = palette,
        amoledMode = amoledMode,
        amoledSurfacesMode = amoledSurfacesMode
    )
    val typography = buildCineboxTypography(getFontFamily(appFont))
    val textStyles = buildCineboxTextStyles(typography)

    val materialColorScheme = darkColorScheme(
        primary = colorScheme.Primary,
        onPrimary = colorScheme.OnPrimary,
        secondary = colorScheme.Secondary,
        onSecondary = colorScheme.OnSecondary,
        background = colorScheme.Background,
        surface = colorScheme.Surface,
        surfaceVariant = colorScheme.SurfaceVariant,
        onBackground = colorScheme.TextPrimary,
        onSurface = colorScheme.TextPrimary,
        onSurfaceVariant = colorScheme.TextSecondary,
        error = colorScheme.Error
    )

    val extendedColors = CineboxExtendedColors(
        backgroundElevated = colorScheme.BackgroundElevated,
        backgroundCard = colorScheme.BackgroundCard,
        textSecondary = colorScheme.TextSecondary,
        textTertiary = colorScheme.TextTertiary,
        focusRing = colorScheme.FocusRing,
        focusBackground = colorScheme.FocusBackground,
        rating = colorScheme.Rating
    )

    CompositionLocalProvider(
        LocalCineboxColors provides colorScheme,
        LocalCineboxExtendedColors provides extendedColors,
        LocalCineboxTextStyles provides textStyles,
        LocalAppTheme provides appTheme,
        LocalThemePalette provides palette,
        LocalSettingsUiStyle provides settingsUiStyle,
        LocalCineboxFocusRingStyle provides focusRingStyle
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = typography,
            content = content
        )
    }
}

object CineboxTheme {
    val palette: ThemeColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalThemePalette.current

    val colors: CineboxColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCineboxColors.current

    val extendedColors: CineboxExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCineboxExtendedColors.current

    val textStyles: CineboxTextStyleTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalCineboxTextStyles.current

    val spacing: CineboxSpacingTokens
        get() = CineboxSpacing.tokens

    val radii: CineboxRadiusTokens
        get() = CineboxRadii.tokens

    val shapes: CineboxShapeTokens
        get() = CineboxShapes.tokens

    val sizes: CineboxSizeTokens
        get() = CineboxSizes.tokens

    val strokes: CineboxStrokeTokens
        get() = CineboxStrokes.tokens

    val elevations: CineboxElevationTokens
        get() = CineboxElevations.tokens

    val effects: CineboxEffectTokens
        get() = CineboxEffects.tokens

    val motion: CineboxMotionTokens
        get() = CineboxMotion.tokens

    val focus: CineboxFocusTokens
        get() = CineboxFocus.tokens

    val focusRing: CineboxFocusRingStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalCineboxFocusRingStyle.current

    val layout: CineboxLayoutTokens
        get() = CineboxLayout.tokens

    val media: NuvioMediaTokens
        get() = NuvioMedia.tokens

    val components: CineboxComponentTokens
        get() = CineboxComponents.tokens

    val currentTheme: AppTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current

    val settingsUiStyle: SettingsUiStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalSettingsUiStyle.current
}
