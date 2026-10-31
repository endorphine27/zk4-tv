package com.cinebox.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

@Immutable
data class CineboxSurfaceColors(
    val background: Color,
    val raised: Color,
    val card: Color,
    val default: Color,
    val variant: Color,
    val panel: Color,
    val overlay: Color,
    val field: Color,
    val menu: Color,
    val modal: Color,
    val playerOverlay: Color,
    val divider: Color
)

@Immutable
data class CineboxTextColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val disabled: Color,
    val inverse: Color,
    val onAccent: Color,
    val onOverlay: Color,
    val metadata: Color
)

@Immutable
data class CineboxFocusColors(
    val ring: Color,
    val background: Color,
    val content: Color,
    val accent: Color,
    val scrim: Color
)

@Immutable
data class CineboxSelectionColors(
    val background: Color,
    val foreground: Color,
    val border: Color,
    val mutedBackground: Color,
    val mutedForeground: Color
)

@Immutable
data class NuvioMediaColors(
    val heroScrim: Color,
    val imageScrim: Color,
    val posterFallback: Color,
    val videoControlsScrim: Color,
    val glassPanelTop: Color,
    val glassPanelMiddle: Color,
    val glassPanelBottom: Color,
    val glow: Color
)

@Immutable
data class CineboxStatusColors(
    val rating: Color,
    val error: Color,
    val warning: Color,
    val success: Color,
    val info: Color,
    val watched: Color,
    val unwatched: Color,
    val cached: Color,
    val torrent: Color,
    val premium: Color
)

@Immutable
data class CineboxDisabledColors(
    val container: Color,
    val content: Color,
    val border: Color,
    val overlay: Color
)

@Immutable
data class CineboxSourceColors(
    val trakt: Color,
    val tmdb: Color,
    val imdb: Color,
    val mdblist: Color
)

@Immutable
data class CineboxContrastPair(
    val foreground: Color,
    val background: Color
)

class CineboxColorScheme(
    palette: ThemeColorPalette,
    amoledMode: Boolean = false,
    amoledSurfacesMode: Boolean = false
) {
    private val pureBlack = CineboxPrimitives.black
    private val pureBlackSurfaces = amoledMode && amoledSurfacesMode

    val Background = if (amoledMode) pureBlack else palette.background
    val BackgroundElevated = if (pureBlackSurfaces) pureBlack else palette.backgroundElevated
    val BackgroundCard = if (pureBlackSurfaces) pureBlack else palette.backgroundCard
    val Surface = if (pureBlackSurfaces) pureBlack else palette.surface
    val SurfaceVariant = if (pureBlackSurfaces) pureBlack else palette.surfaceVariant
    val Panel = if (pureBlackSurfaces) pureBlack else palette.panel
    val Overlay = palette.overlay
    val Field = if (pureBlackSurfaces) pureBlack else palette.field
    val Menu = if (pureBlackSurfaces) pureBlack else palette.menu
    val Modal = if (pureBlackSurfaces) pureBlack else palette.modal
    val PlayerOverlay = palette.playerOverlay
    val Divider = CineboxPrimitives.neutral750

    val Primary = CineboxPrimitives.neutral500
    val PrimaryVariant = CineboxPrimitives.neutral650
    val OnPrimary = CineboxPrimitives.white
    val Secondary = palette.secondary
    val SecondaryVariant = palette.secondaryVariant
    val OnSecondary = palette.onSecondary
    val OnSecondaryVariant = palette.onSecondaryVariant

    val TextPrimary = CineboxPrimitives.white
    val TextSecondary = CineboxPrimitives.neutral400
    val TextTertiary = CineboxPrimitives.neutral600
    val TextDisabled = CineboxPrimitives.neutral700
    val TextInverse = CineboxPrimitives.neutral925

    val FocusRing = palette.focusRing
    val FocusBackground = palette.focusBackground
    val FocusContent = CineboxPrimitives.white
    val FocusScrim = CineboxPrimitives.black.copy(alpha = 0.32f)

    val Rating = CineboxPrimitives.rating
    val Error = CineboxPrimitives.error
    val Warning = CineboxPrimitives.warning
    val Success = CineboxPrimitives.success
    val Info = CineboxPrimitives.info
    val Watched = CineboxPrimitives.success
    val Unwatched = CineboxPrimitives.neutral600
    val Cached = CineboxPrimitives.blue300
    val Torrent = CineboxPrimitives.torrent
    val Premium = CineboxPrimitives.premium

    val Border = CineboxPrimitives.neutral750
    val BorderFocused = FocusRing
    val BorderMuted = CineboxPrimitives.neutral750.copy(alpha = 0.58f)

    val Scrim = CineboxPrimitives.black.copy(alpha = 0.62f)
    val ImageScrim = CineboxPrimitives.black.copy(alpha = 0.58f)
    val VideoControlsScrim = CineboxPrimitives.black.copy(alpha = 0.72f)
    val PosterFallback = BackgroundCard

    val DisabledContainer = SurfaceVariant.copy(alpha = 0.42f)
    val DisabledContent = TextDisabled
    val DisabledBorder = Border.copy(alpha = 0.48f)
    val DisabledOverlay = CineboxPrimitives.black.copy(alpha = 0.42f)

    val surfaces = CineboxSurfaceColors(
        background = Background,
        raised = BackgroundElevated,
        card = BackgroundCard,
        default = Surface,
        variant = SurfaceVariant,
        panel = Panel,
        overlay = Overlay,
        field = Field,
        menu = Menu,
        modal = Modal,
        playerOverlay = PlayerOverlay,
        divider = Divider
    )

    val text = CineboxTextColors(
        primary = TextPrimary,
        secondary = TextSecondary,
        tertiary = TextTertiary,
        disabled = TextDisabled,
        inverse = TextInverse,
        onAccent = OnSecondary,
        onOverlay = CineboxPrimitives.white,
        metadata = TextSecondary
    )

    val focus = CineboxFocusColors(
        ring = FocusRing,
        background = FocusBackground,
        content = FocusContent,
        accent = Secondary,
        scrim = FocusScrim
    )

    val selection = CineboxSelectionColors(
        background = Secondary,
        foreground = OnSecondary,
        border = SecondaryVariant,
        mutedBackground = FocusBackground,
        mutedForeground = TextPrimary
    )

    val media = NuvioMediaColors(
        heroScrim = Scrim,
        imageScrim = ImageScrim,
        posterFallback = PosterFallback,
        videoControlsScrim = VideoControlsScrim,
        glassPanelTop = Color(0xD64A4F59),
        glassPanelMiddle = Color(0xCC3F454F),
        glassPanelBottom = Color(0xC640474F),
        glow = FocusRing.copy(alpha = 0.32f)
    )

    val status = CineboxStatusColors(
        rating = Rating,
        error = Error,
        warning = Warning,
        success = Success,
        info = Info,
        watched = Watched,
        unwatched = Unwatched,
        cached = Cached,
        torrent = Torrent,
        premium = Premium
    )

    val disabled = CineboxDisabledColors(
        container = DisabledContainer,
        content = DisabledContent,
        border = DisabledBorder,
        overlay = DisabledOverlay
    )

    val source = CineboxSourceColors(
        trakt = CineboxPrimitives.trakt,
        tmdb = CineboxPrimitives.tmdb,
        imdb = CineboxPrimitives.imdb,
        mdblist = CineboxPrimitives.mdblist
    )

    val contrastPairs = listOf(
        CineboxContrastPair(TextPrimary, Background),
        CineboxContrastPair(TextPrimary, BackgroundCard),
        CineboxContrastPair(TextSecondary, Background),
        CineboxContrastPair(OnSecondary, Secondary),
        CineboxContrastPair(FocusContent, FocusBackground),
        CineboxContrastPair(CineboxPrimitives.white, PlayerOverlay)
    )
}

object CineboxColors {
    val Background: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.Background

    val BackgroundElevated: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.BackgroundElevated

    val BackgroundCard: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.BackgroundCard

    val Surface: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.Surface

    val SurfaceVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.SurfaceVariant

    val Primary = CineboxPrimitives.neutral500
    val PrimaryVariant = CineboxPrimitives.neutral650
    val OnPrimary = CineboxPrimitives.white
    val TextPrimary = CineboxPrimitives.white
    val TextSecondary = CineboxPrimitives.neutral400
    val TextTertiary = CineboxPrimitives.neutral600
    val TextDisabled = CineboxPrimitives.neutral700
    val Rating = CineboxPrimitives.rating
    val Error = CineboxPrimitives.error
    val Success = CineboxPrimitives.success
    val Warning = CineboxPrimitives.warning
    val Info = CineboxPrimitives.info
    val Border = CineboxPrimitives.neutral750

    val Secondary: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.Secondary

    val SecondaryVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.SecondaryVariant

    val OnSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.OnSecondary

    val OnSecondaryVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.OnSecondaryVariant

    val FocusRing: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.FocusRing

    val FocusBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.FocusBackground

    val BorderFocused: Color
        @Composable
        @ReadOnlyComposable
        get() = CineboxTheme.colors.BorderFocused
}
