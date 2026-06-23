package com.example.cicloestudos3.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary              = EmeraldPrimary,
    onPrimary            = TextOnDark,
    primaryContainer     = EmeraldContainer,
    onPrimaryContainer   = ForestDeep,
    secondary            = TealSecondary,
    onSecondary          = TextOnDark,
    secondaryContainer   = TealContainer,
    onSecondaryContainer = ForestMid,
    tertiary             = AmberStreak,
    onTertiary           = TextOnDark,
    tertiaryContainer    = AmberContainer,
    onTertiaryContainer  = Color(0xFF78350F),
    background           = BackgroundLight,
    onBackground         = TextPrimary,
    surface              = SurfaceWhite,
    onSurface            = TextPrimary,
    surfaceVariant       = SurfaceVariant,
    onSurfaceVariant     = TextSecondary,
    outline              = OutlineColor,
    error                = RedAlert,
    errorContainer       = RedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary              = EmeraldDark,
    onPrimary            = ForestDeep,
    primaryContainer     = EmeraldDarkContainer,
    onPrimaryContainer   = EmeraldLight,
    secondary            = TealSecondary,
    onSecondary          = TextOnDark,
    secondaryContainer   = Color(0xFF134E4A),
    onSecondaryContainer = TealContainer,
    background           = ForestDarkBg,
    onBackground         = TextOnDark,
    surface              = ForestDarkSurface,
    onSurface            = TextOnDark,
    surfaceVariant       = Color(0xFF1A3A28),
    onSurfaceVariant     = TextOnDarkSub
)

@Composable
fun CicloEstudosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = CicloTypography,
        content     = content
    )
}
