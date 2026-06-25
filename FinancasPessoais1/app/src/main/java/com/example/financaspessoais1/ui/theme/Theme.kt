package com.example.financaspessoais1.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = Blue500,
    onPrimary        = Color.White,
    background       = ScreenBg,
    onBackground     = TextPrimary,
    surface          = SurfaceWhite,
    onSurface        = TextPrimary,
    error            = Red500,
    onError          = Color.White,
    outline          = InputBorder,
)

@Composable
fun FinancasPessoaisTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Blue700.toArgb()
            window.navigationBarColor = SurfaceWhite.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(colorScheme = LightColorScheme) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(fontFamily = DMSans, color = TextPrimary)
        ) {
            content()
        }
    }
}
