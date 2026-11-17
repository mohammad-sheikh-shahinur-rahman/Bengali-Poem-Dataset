package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = OnTerracottaPrimary,
    primaryContainer = TerracottaContainer,
    onPrimaryContainer = OnTerracottaContainer,
    secondary = OchreSecondary,
    onSecondary = OnOchreSecondary,
    secondaryContainer = OchreContainer,
    onSecondaryContainer = OnOchreContainer,
    tertiary = ForestTealTertiary,
    onTertiary = OnForestTealTertiary,
    tertiaryContainer = ForestTealContainer,
    onTertiaryContainer = OnForestTealContainer,
    background = WarmPaperBackground,
    onBackground = InkOnBackground,
    surface = WarmPaperSurface,
    onSurface = InkOnSurface,
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = MutedInkOnSurfaceVariant,
    outline = WarmOutline,
    outlineVariant = WarmOutlineVariant,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTerracottaPrimary,
    onPrimary = DarkOnTerracottaPrimary,
    primaryContainer = DarkTerracottaContainer,
    onPrimaryContainer = DarkOnTerracottaContainer,
    secondary = DarkOchreSecondary,
    onSecondary = DarkOnOchreSecondary,
    secondaryContainer = DarkOchreContainer,
    onSecondaryContainer = DarkOnOchreContainer,
    tertiary = DarkForestTealTertiary,
    onTertiary = DarkOnForestTealTertiary,
    tertiaryContainer = DarkForestTealContainer,
    onTertiaryContainer = DarkOnForestTealContainer,
    background = MidnightInkBackground,
    onBackground = ParchmentOnDarkBackground,
    surface = MidnightInkSurface,
    onSurface = ParchmentOnDarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MutedParchmentOnDark,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun KobitaArchiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
