package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.financaspessoais1.ui.theme.SurfaceWhite

/** clickable sem efeito de ondulação (ripple), para imitar os botões "flat" do protótipo. */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    clickable(interactionSource = interaction, indication = null, onClick = onClick)
}

/** Cartão branco arredondado com sombra suave, como os cards do protótipo. */
fun Modifier.cardSurface(radius: Dp, elevation: Dp = 3.dp): Modifier = this
    .shadow(elevation, RoundedCornerShape(radius), spotColor = Color.Black.copy(alpha = 0.10f))
    .clip(RoundedCornerShape(radius))
    .background(SurfaceWhite)

/** Barra de progresso arredondada (trilho + preenchimento por fração 0..1). */
@Composable
fun ProgressTrack(
    fraction: Float,
    trackColor: Color,
    barColor: Color,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(barColor)
        )
    }
}
