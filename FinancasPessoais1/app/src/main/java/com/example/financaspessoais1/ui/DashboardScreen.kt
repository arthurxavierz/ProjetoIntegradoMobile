package com.example.financaspessoais1.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory
import com.example.financaspessoais1.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun Double.toCurrency(): String =
    "R$ " + String.format(Locale("pt", "BR"), "%,.2f", this)

private fun Long.toFormattedDate(): String =
    SimpleDateFormat("dd/MM", Locale("pt", "BR")).format(Date(this))

// ─────────────────────────────────────────────────────────────────────────────
// Tela principal — Dashboard
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    budget: Double,
    expenses: List<Expense>,
    onAddExpense: () -> Unit,
    onDeleteExpense: (Int) -> Unit,
    onResetBudget: () -> Unit
) {
    val spent     = expenses.sumOf { it.value }
    val remaining = budget - spent
    val progress  = if (budget > 0) (spent / budget).coerceIn(0.0, 1.0).toFloat() else 0f

    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(700),
        label         = "progress"
    )

    val monthYear = remember {
        val cal    = Calendar.getInstance()
        val months = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        "${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Visão Geral",
                            style      = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text  = monthYear,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onResetBudget) {
                        Icon(
                            imageVector        = Icons.Rounded.Settings,
                            contentDescription = "Redefinir orçamento",
                            tint               = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddExpense,
                containerColor = Primary,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Adicionar gasto")
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // ── Hero card ────────────────────────────────────────────────────
            item {
                BudgetHeroCard(
                    budget          = budget,
                    spent           = spent,
                    remaining       = remaining,
                    animatedProgress = animatedProgress
                )
            }

            // ── Alerta de status ─────────────────────────────────────────────
            val showAlert = expenses.isNotEmpty() && progress >= 0.7f
            if (showAlert) {
                item {
                    Spacer(Modifier.height(12.dp))
                    StatusAlertBanner(spent = spent, budget = budget, remaining = remaining)
                }
            }

            // ── Stats e breakdown só com despesas ────────────────────────────
            if (expenses.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(12.dp))
                    StatsRow(expenses)
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    CategoryBreakdownCard(expenses = expenses, totalSpent = spent)
                }
            }

            // ── Cabeçalho da lista ───────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Lançamentos",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = "${expenses.size} item${if (expenses.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Lista ou estado vazio ────────────────────────────────────────
            if (expenses.isEmpty()) {
                item { EmptyState() }
            } else {
                items(
                    items = expenses.sortedByDescending { it.timestamp },
                    key   = { it.id }
                ) { expense ->
                    ExpenseItem(expense = expense, onDelete = { onDeleteExpense(expense.id) })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero card — gradiente navy com resumo do orçamento
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BudgetHeroCard(
    budget: Double,
    spent: Double,
    remaining: Double,
    animatedProgress: Float
) {
    val progressColor = when {
        animatedProgress >= 1f   -> AccentRed
        animatedProgress >= 0.7f -> AccentAmber
        else                     -> AccentGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(NavyDeep, NavyMid),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column {
            Text(
                text  = "Orçamento do Mês",
                style = MaterialTheme.typography.labelLarge,
                color = TextOnNavySub
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = budget.toCurrency(),
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color      = TextOnNavy
            )

            Spacer(Modifier.height(24.dp))

            LinearProgressIndicator(
                progress    = { animatedProgress },
                modifier    = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color       = progressColor,
                trackColor  = Color.White.copy(alpha = 0.2f)
            )

            Spacer(Modifier.height(6.dp))
            Text(
                text  = "${(animatedProgress * 100).toInt()}% utilizado",
                style = MaterialTheme.typography.labelSmall,
                color = TextOnNavySub
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeroStat(label = "Gasto", value = spent.toCurrency(), valueColor = AccentRed.copy(alpha = 0.9f))
                HeroStat(
                    label      = "Disponível",
                    value      = remaining.toCurrency(),
                    valueColor = if (remaining >= 0) AccentGreen else AccentRed,
                    align      = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun HeroStat(
    label: String,
    value: String,
    valueColor: Color,
    align: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = align) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextOnNavySub)
        Text(
            text       = value,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color      = valueColor
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Banner de alerta
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatusAlertBanner(spent: Double, budget: Double, remaining: Double) {
    val isOver      = remaining < 0
    val alertColor  = if (isOver) AccentRed else AccentAmber
    val alertIcon   = if (isOver) Icons.Rounded.Warning else Icons.Rounded.Info
    val alertText   = if (isOver)
        "Orçamento excedido em ${(-remaining).toCurrency()}"
    else
        "Atenção: apenas ${remaining.toCurrency()} restantes (${((remaining / budget) * 100).toInt()}%)"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape  = RoundedCornerShape(12.dp),
        color  = alertColor.copy(alpha = 0.10f)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = alertIcon,
                contentDescription = null,
                tint               = alertColor,
                modifier           = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text       = alertText,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color      = alertColor
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Faixa de estatísticas
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(expenses: List<Expense>) {
    val maxExpense = expenses.maxOf { it.value }
    val avgExpense = expenses.sumOf { it.value } / expenses.size

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MiniStatCard(modifier = Modifier.weight(1f), label = "Total",    value = "${expenses.size} itens")
        MiniStatCard(modifier = Modifier.weight(1f), label = "Maior",    value = maxExpense.toCurrency())
        MiniStatCard(modifier = Modifier.weight(1f), label = "Média",    value = avgExpense.toCurrency())
    }
}

@Composable
private fun MiniStatCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = value,
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                textAlign  = TextAlign.Center,
                maxLines   = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text      = label,
                style     = MaterialTheme.typography.labelSmall,
                color     = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Breakdown por categoria
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CategoryBreakdownCard(expenses: List<Expense>, totalSpent: Double) {
    val byCategory = ExpenseCategory.entries
        .mapNotNull { cat ->
            val sum = expenses.filter { it.category == cat }.sumOf { it.value }
            if (sum > 0) cat to sum else null
        }
        .sortedByDescending { it.second }

    if (byCategory.isEmpty()) return

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = "Por categoria",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            byCategory.forEachIndexed { index, (category, amount) ->
                CategoryRow(category = category, amount = amount, totalSpent = totalSpent)
                if (index < byCategory.lastIndex) Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun CategoryRow(category: ExpenseCategory, amount: Double, totalSpent: Double) {
    val fraction   = if (totalSpent > 0) (amount / totalSpent).toFloat() else 0f
    val percentage = (fraction * 100).toInt()

    val animatedFraction by animateFloatAsState(
        targetValue   = fraction,
        animationSpec = tween(600),
        label         = "cat_progress_${category.name}"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier         = Modifier
                .size(38.dp)
                .background(category.color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = category.icon,
                contentDescription = null,
                tint               = category.color,
                modifier           = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = category.label,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color      = TextPrimary
                )
                Text(
                    text       = amount.toCurrency(),
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }
            Spacer(Modifier.height(5.dp))
            LinearProgressIndicator(
                progress   = { animatedFraction },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color      = category.color,
                trackColor = category.color.copy(alpha = 0.12f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "$percentage% do total",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Item de transação
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpenseItem(expense: Expense, onDelete: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone da categoria
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .background(expense.category.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = expense.category.icon,
                    contentDescription = null,
                    tint               = expense.category.color,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = expense.name,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color      = TextPrimary,
                    maxLines   = 1
                )
                Text(
                    text  = "${expense.category.label}  ·  ${expense.timestamp.toFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text       = expense.value.toCurrency(),
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color      = AccentRed
            )

            IconButton(
                onClick  = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Delete,
                    contentDescription = "Remover",
                    tint               = TextSecondary.copy(alpha = 0.6f),
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Estado vazio
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector        = Icons.Rounded.ReceiptLong,
            contentDescription = null,
            tint               = TextSecondary.copy(alpha = 0.35f),
            modifier           = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text       = "Nenhum lançamento ainda",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color      = TextSecondary,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text      = "Toque em + para registrar seu primeiro gasto",
            style     = MaterialTheme.typography.bodyMedium,
            color     = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
