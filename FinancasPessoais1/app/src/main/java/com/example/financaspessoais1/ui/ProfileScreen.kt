package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.ui.theme.*

@Composable
fun ProfileScreen(
    expenses: List<Expense>,
    budget: Double,
    userName: String,
    userEmail: String,
    periodYear: Int,
    periodMonth: Int,
    onSetBudget: (Double) -> Unit,
    onSetPeriod: (Int, Int) -> Unit,
    onLogout: () -> Unit
) {
    val total = expenses.sumOf { it.value }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showPeriodDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Avatar + nome
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(80.dp)
                    .shadow(18.dp, CircleShape, spotColor = Blue500)
                    .background(brandGradient(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                userName.ifBlank { "Usuário" },
                fontFamily = DMSans, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary
            )
            Spacer(Modifier.height(3.dp))
            Text(userEmail, fontFamily = DMSans, fontSize = 14.sp, color = TextMuted)
        }

        // Stats
        Row(
            Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(Modifier.weight(1f), "${expenses.size}", 22.sp, "Despesas")
            StatCard(Modifier.weight(1f), "R$ ${formatMoney(total)}", 20.sp, "Gasto total")
        }

        // Menu
        Column(
            Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 100.dp)
                .fillMaxWidth()
                .cardSurface(16.dp)
        ) {
            MenuRow(
                Icons.Rounded.AttachMoney, "Teto Orçamentário", "R$ ${formatMoney(budget)}/mês",
                showChevron = true, onClick = { showBudgetDialog = true }
            )
            HorizontalDivider(thickness = 1.dp, color = ScreenBg)
            MenuRow(
                Icons.Rounded.CalendarMonth, "Período", periodLabelFull(periodYear, periodMonth),
                showChevron = true, onClick = { showPeriodDialog = true }
            )
            HorizontalDivider(thickness = 1.dp, color = ScreenBg)
            MenuRow(Icons.AutoMirrored.Rounded.Logout, "Sair da Conta", null, danger = true, onClick = onLogout)
        }
    }

    if (showBudgetDialog) {
        BudgetDialog(
            current = budget,
            onDismiss = { showBudgetDialog = false },
            onConfirm = { onSetBudget(it); showBudgetDialog = false }
        )
    }

    if (showPeriodDialog) {
        PeriodDialog(
            year = periodYear,
            month = periodMonth,
            onDismiss = { showPeriodDialog = false },
            onConfirm = { y, m -> onSetPeriod(y, m); showPeriodDialog = false }
        )
    }
}

@Composable
private fun PeriodDialog(
    year: Int,
    month: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selYear by remember { mutableIntStateOf(year) }
    var selMonth by remember { mutableIntStateOf(month) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        title = { Text("Período", fontFamily = DMSans, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                // Seletor de ano
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    YearArrow("‹") { selYear-- }
                    Text("$selYear", fontFamily = DMSans, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    YearArrow("›") { selYear++ }
                }
                Spacer(Modifier.height(14.dp))
                // Grade de meses (3 colunas)
                for (row in 0 until 4) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (col in 0 until 3) {
                            val m = row * 3 + col + 1
                            val selected = m == selMonth
                            Box(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) Blue500 else Slate100)
                                    .clickableNoRipple { selMonth = m }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    MONTHS_ABBR[m - 1],
                                    fontFamily = DMSans, fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                    if (row < 3) Spacer(Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selYear, selMonth) }) {
                Text("Salvar", fontFamily = DMSans, fontWeight = FontWeight.Bold, color = Blue500)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", fontFamily = DMSans, color = TextSecondary) }
        }
    )
}

@Composable
private fun YearArrow(symbol: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Slate100)
            .clickableNoRipple { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, fontFamily = DMSans, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetDialog(current: Double, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var input by remember { mutableStateOf(trimTrailingZeros(current)) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        title = { Text("Teto Mensal", fontFamily = DMSans, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                Text(
                    "Defina o limite de gastos do mês.",
                    fontFamily = DMSans, fontSize = 13.sp, color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { c -> c.isDigit() || c == '.' || c == ',' }; error = "" },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("R$ ", fontFamily = DMSans, color = TextPrimary) },
                    placeholder = { Text("0,00", fontFamily = DMSans, color = TextMuted) },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = DMSans, fontSize = 14.sp, color = TextPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = InputBorder,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    )
                )
                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(error, fontFamily = DMSans, fontSize = 12.sp, color = Red500)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val v = input.replace(",", ".").toDoubleOrNull()
                if (v == null || v <= 0.0) error = "Informe um valor válido."
                else onConfirm(v)
            }) { Text("Salvar", fontFamily = DMSans, fontWeight = FontWeight.Bold, color = Blue500) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", fontFamily = DMSans, color = TextSecondary) }
        }
    )
}

private fun trimTrailingZeros(v: Double): String {
    val s = String.format(java.util.Locale.US, "%.2f", v)
    return s.trimEnd('0').trimEnd('.')
}

@Composable
private fun StatCard(modifier: Modifier, value: String, valueSize: androidx.compose.ui.unit.TextUnit, label: String) {
    Column(
        modifier
            .cardSurface(14.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontFamily = DMSans, fontSize = valueSize, fontWeight = FontWeight.Bold, color = Blue500, maxLines = 1)
        Spacer(Modifier.height(3.dp))
        Text(label, fontFamily = DMSans, fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    showChevron: Boolean = false,
    danger: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickableNoRipple { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (danger) RedBg else BlueBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (danger) Red500 else Blue500, modifier = Modifier.size(18.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (danger) Red500 else TextPrimary)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontFamily = DMSans, fontSize = 12.sp, color = TextMuted)
            }
        }
        if (showChevron) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Slate300, modifier = Modifier.size(16.dp))
        }
    }
}
