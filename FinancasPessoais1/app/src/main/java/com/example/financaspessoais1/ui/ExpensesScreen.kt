package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.ui.theme.*

@Composable
fun ExpensesScreen(
    expenses: List<Expense>,
    periodYear: Int,
    periodMonth: Int,
    onEdit: (Expense) -> Unit,
    onDelete: (Long) -> Unit
) {
    val total = expenses.sumOf { it.value }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            // Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Despesas", fontFamily = DMSans, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = (-0.3).sp)
                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(periodLabelShort(periodYear, periodMonth), fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Blue500)
                }
            }

            // Summary
            Row(
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 14.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlueBg)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TOTAL GASTO", fontFamily = DMSans, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Blue300, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(3.dp))
                    Text("R$ ${formatMoney(total)}", fontFamily = DMSans, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Blue700)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("REGISTROS", fontFamily = DMSans, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Blue300, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(3.dp))
                    Text("${expenses.size}", fontFamily = DMSans, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Blue700)
                }
            }
        }

        if (expenses.isEmpty()) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nenhuma despesa ainda", fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Toque em + para registrar seu primeiro gasto.",
                        fontFamily = DMSans, fontSize = 13.sp, color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        items(expenses, key = { it.id }) { exp ->
            ExpenseRow(exp, onEdit = { onEdit(exp) }, onDelete = { onDelete(exp.id) })
            Spacer(Modifier.height(8.dp))
        }

        item { Spacer(Modifier.height(92.dp)) }
    }
}

@Composable
private fun ExpenseRow(exp: Expense, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .cardSurface(14.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(exp.category.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(exp.category.icon, contentDescription = null, tint = exp.category.color, modifier = Modifier.size(18.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(exp.name, fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text("${exp.category.label} · ${exp.dateLabel}", fontFamily = DMSans, fontSize = 11.sp, color = TextMuted)
        }
        Text(
            "-R$ ${formatMoney(exp.value)}",
            fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Red500,
            modifier = Modifier.padding(end = 4.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate100)
                    .clickableNoRipple { onEdit() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = "Editar", tint = TextSecondary, modifier = Modifier.size(14.dp))
            }
            Box(
                Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RedBg)
                    .clickableNoRipple { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = "Excluir", tint = Red500, modifier = Modifier.size(14.dp))
            }
        }
    }
}
