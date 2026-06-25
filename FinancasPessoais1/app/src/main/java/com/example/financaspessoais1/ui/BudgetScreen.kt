package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

private data class CatBudget(
    val cat: ExpenseCategory,
    val spent: Double,
    val pct: Int,
    val barColor: Color
)

@Composable
fun BudgetScreen(expenses: List<Expense>, budget: Double, periodYear: Int, periodMonth: Int) {
    val total = expenses.sumOf { it.value }
    val remaining = budget - total
    val pct = min((total / budget * 100).roundToInt(), 100)
    val barColor = when {
        pct >= 100 -> Red500
        pct >= 75 -> Amber500
        else -> Blue500
    }
    val pillBg = when {
        pct >= 100 -> RedBg
        pct >= 75 -> AmberBg
        else -> BlueBg
    }

    val catBudgets = ExpenseCategory.entries.map { cat ->
        val sp = expenses.filter { it.category == cat }.sumOf { it.value }
        val p = min((sp / cat.limit * 100).roundToInt(), 100)
        CatBudget(cat, sp, p, when {
            p >= 100 -> Red500
            p >= 75 -> Amber500
            else -> cat.color
        })
    }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            // Header
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 6.dp)) {
                Text(periodLabelUpper(periodYear, periodMonth), fontFamily = DMSans, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted, letterSpacing = 1.2.sp)
                Spacer(Modifier.height(2.dp))
                Text("Orçamento", fontFamily = DMSans, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = (-0.3).sp)
            }

            // Resumo do teto
            Column(
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 14.dp)
                    .fillMaxWidth()
                    .cardSurface(16.dp)
                    .padding(20.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text("Teto Mensal", fontFamily = DMSans, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                        Spacer(Modifier.height(3.dp))
                        Text("R$ ${formatMoney(budget)}", fontFamily = DMSans, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(pillBg)
                            .padding(horizontal = 11.dp, vertical = 5.dp)
                    ) {
                        Text("$pct%", fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = barColor)
                    }
                }
                Spacer(Modifier.height(14.dp))
                ProgressTrack(fraction = pct / 100f, trackColor = Slate100, barColor = barColor, height = 8.dp)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendDot(Red500, "Gasto: R$ ${formatMoney(total)}")
                    LegendDot(Green500, "Restam: R$ ${formatMoney(remaining)}")
                }
            }

            Box(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)) {
                Text("Por Categoria", fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        items(catBudgets, key = { it.cat.id }) { cb ->
            CatBudgetCard(cb)
            Spacer(Modifier.height(8.dp))
        }

        item { Spacer(Modifier.height(92.dp)) }
    }
}

@Composable
private fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(text, fontFamily = DMSans, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
    }
}

@Composable
private fun CatBudgetCard(cb: CatBudget) {
    Column(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .cardSurface(14.dp)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(cb.cat.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(cb.cat.icon, contentDescription = null, tint = cb.cat.color, modifier = Modifier.size(17.dp))
            }
            Text(cb.cat.label, fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.weight(1f))
            Text("R$ ${formatMoney(cb.spent)}", fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(" /${formatMoney(cb.cat.limit)}", fontFamily = DMSans, fontSize = 12.sp, color = TextMuted)
        }
        Spacer(Modifier.height(10.dp))
        ProgressTrack(fraction = cb.pct / 100f, trackColor = Slate100, barColor = cb.barColor, height = 5.dp)
        Spacer(Modifier.height(4.dp))
        Text(
            "${cb.pct}% utilizado",
            fontFamily = DMSans, fontSize = 11.sp, color = TextMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
