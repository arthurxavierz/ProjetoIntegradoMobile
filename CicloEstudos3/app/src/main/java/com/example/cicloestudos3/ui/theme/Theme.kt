package com.example.cicloestudos3.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EstudosColorScheme = lightColorScheme(
    primary              = EstudosPrimary,
    onPrimary            = Color.White,
    primaryContainer     = EstudosPrimarySoft,
    onPrimaryContainer   = EstudosDark,
    secondary            = EstudosPrimaryDeep,
    onSecondary          = Color.White,
    secondaryContainer   = EstudosPrimarySoft,
    onSecondaryContainer = EstudosDark,
    tertiary             = EstudosDone,
    onTertiary           = Color.White,
    tertiaryContainer    = EstudosDoneSoft,
    onTertiaryContainer  = EstudosDone,
    background           = EstudosBackground,
    onBackground         = EstudosTitle,
    surface              = EstudosSurface,
    onSurface            = EstudosTitle,
    surfaceVariant       = EstudosTrack,
    onSurfaceVariant     = EstudosMuted,
    outline              = EstudosBorder,
    error                = EstudosDanger,
    onError              = Color.White,
    errorContainer       = EstudosDangerSoft,
    onErrorContainer     = EstudosDanger
)

@Composable
fun CicloEstudosTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = EstudosDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = EstudosColorScheme,
        typography  = CicloTypography,
        content     = content
    )
}
