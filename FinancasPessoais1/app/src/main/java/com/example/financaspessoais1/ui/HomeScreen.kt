package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory
import com.example.financaspessoais1.ui.theme.*
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    expenses: List<Expense>,
    budget: Double,
    periodYear: Int,
    periodMonth: Int,
    onSeeAll: () -> Unit
) {
    val total = expenses.sumOf { it.value }
    val remaining = budget - total
    val pct = min((total / budget * 100).roundToInt(), 100)
    val showAlert = remaining < 200

    val catTotals = ExpenseCategory.entries
        .map { cat -> cat to expenses.filter { it.category == cat }.sumOf { it.value } }
        .filter { it.second > 0 }
    val recent = expenses.take(4)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 6.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(periodLabelUpper(periodYear, periodMonth), fontFamily = DMSans, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted, letterSpacing = 1.2.sp)
                Spacer(Modifier.height(2.dp))
                Text("Finanças", fontFamily = DMSans, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = (-0.3).sp)
            }
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlueBg)
                    .clickableNoRipple { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Notifications, contentDescription = "Notificações", tint = Blue500, modifier = Modifier.size(19.dp))
            }
        }

        // Balance card
        Column(
            Modifier
                .padding(start = 20.dp, end = 20.dp, top = 6.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(brandGradient())
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 18.dp)
        ) {
            Text("Saldo Restante", fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.7f))
            Spacer(Modifier.height(4.dp))
            Text("R$ ${formatMoney(remaining)}", fontFamily = DMSans, fontSize = 33.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.5).sp)
            Spacer(Modifier.height(14.dp))
            ProgressTrack(
                fraction = pct / 100f,
                trackColor = Color.White.copy(alpha = 0.2f),
                barColor = Color.White.copy(alpha = 0.85f),
                height = 6.dp
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("R$ ${formatMoney(total)} gastos", fontFamily = DMSans, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.7f))
                Text("Teto R$ ${formatMoney(budget)}", fontFamily = DMSans, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.7f))
            }
        }

        // Alert
        if (showAlert) {
            val isOver = remaining < 0
            val alertColor = if (isOver) Red600 else Amber600
            val alertBg = if (isOver) RedBg else AmberBg
            val alertMsg = if (isOver) "Limite mensal ultrapassado!" else "Atenção: menos de R$ 200 restantes."
            Row(
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(alertBg)
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Error, contentDescription = null, tint = alertColor, modifier = Modifier.size(17.dp))
                Text(alertMsg, fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = alertColor)
            }
        }

        if (expenses.isEmpty()) {
            EmptyHint(
                title = "Nenhuma despesa ainda",
                subtitle = "Toque em + para registrar seu primeiro gasto.",
                top = 24.dp
            )
        } else {
            // Por Categoria
            SectionHeader("Por Categoria", top = 16.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                catTotals.forEach { (cat, amount) -> CategoryChip(cat, amount) }
            }

            // Recentes
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recentes", fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "Ver todos", fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    color = Blue500, modifier = Modifier.clickableNoRipple { onSeeAll() }
                )
            }
            Column(
                Modifier.padding(start = 20.dp, end = 20.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recent.forEach { RecentRow(it) }
            }
        }
    }
}

@Composable
private fun EmptyHint(title: String, subtitle: String, top: androidx.compose.ui.unit.Dp) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp, top = top, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(Modifier.height(6.dp))
        Text(
            subtitle, fontFamily = DMSans, fontSize = 13.sp, color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeader(title: String, top: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = top, bottom = 8.dp)
    ) {
        Text(title, fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun CategoryChip(cat: ExpenseCategory, amount: Double) {
    Column(
        Modifier
            .widthIn(min = 106.dp)
            .cardSurface(14.dp)
            .padding(14.dp)
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(cat.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(cat.label, fontFamily = DMSans, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextMuted)
        Spacer(Modifier.height(2.dp))
        Text("R$ ${formatMoney(amount)}", fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun RecentRow(exp: Expense) {
    Row(
        Modifier
            .fillMaxWidth()
            .cardSurface(14.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(exp.category.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(exp.category.icon, contentDescription = null, tint = exp.category.color, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(exp.name, fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text("${exp.category.label} · ${exp.dateLabel}", fontFamily = DMSans, fontSize = 12.sp, color = TextMuted)
        }
        Text("-R$ ${formatMoney(exp.value)}", fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Red500)
    }
}
