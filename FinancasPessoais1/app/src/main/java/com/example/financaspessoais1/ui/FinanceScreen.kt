package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financaspessoais1.FinancasApp
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.ui.theme.*

/** Estado da gaveta (drawer) de adicionar/editar despesa. */
private sealed interface SheetMode {
    data object Add : SheetMode
    data class Edit(val expense: Expense) : SheetMode
}

@Composable
fun FinanceApp() {
    val context = LocalContext.current.applicationContext
    val repo = (context as FinancasApp).repository
    val vm: FinanceViewModel = viewModel(factory = FinanceViewModel.Factory(repo, context))

    val settings by vm.settings.collectAsState()
    val expenses by vm.expenses.collectAsState()

    if (!settings.loggedIn) {
        LoginScreen(
            onLogin = { email, pass -> vm.login(email, pass) },
            onRegister = { name, email, pass -> vm.register(name, email, pass) }
        )
        return
    }

    val budget = settings.monthlyBudget
    val periodYear = settings.periodYear
    val periodMonth = settings.periodMonth
    val visibleExpenses = remember(expenses, periodYear, periodMonth) {
        expenses.filter { it.inPeriod(periodYear, periodMonth) }
    }
    var tab by rememberSaveable { mutableStateOf("home") }
    var sheet by remember { mutableStateOf<SheetMode?>(null) }

    Box(Modifier.fillMaxSize().background(ScreenBg)) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (tab) {
                    "home" -> HomeScreen(
                        expenses = visibleExpenses,
                        budget = budget,
                        periodYear = periodYear,
                        periodMonth = periodMonth,
                        onSeeAll = { tab = "expenses" }
                    )
                    "expenses" -> ExpensesScreen(
                        expenses = visibleExpenses,
                        periodYear = periodYear,
                        periodMonth = periodMonth,
                        onEdit = { sheet = SheetMode.Edit(it) },
                        onDelete = { id -> vm.deleteExpenseById(id) }
                    )
                    "budget" -> BudgetScreen(
                        expenses = visibleExpenses,
                        budget = budget,
                        periodYear = periodYear,
                        periodMonth = periodMonth
                    )
                    "profile" -> ProfileScreen(
                        expenses = visibleExpenses,
                        budget = budget,
                        userName = settings.userName,
                        userEmail = settings.userEmail,
                        periodYear = periodYear,
                        periodMonth = periodMonth,
                        onSetBudget = { vm.setMonthlyBudget(it) },
                        onSetPeriod = { y, m -> vm.setPeriod(y, m) },
                        onLogout = {
                            vm.logout()
                            tab = "home"
                        }
                    )
                }

                // FAB — só em Início e Despesas
                if (tab == "home" || tab == "expenses") {
                    FloatingActionButton(
                        onClick = { sheet = SheetMode.Add },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 16.dp)
                            .size(56.dp)
                            .shadow(12.dp, CircleShapeFab, spotColor = Blue500),
                        shape = CircleShapeFab,
                        containerColor = Blue500,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Adicionar despesa", modifier = Modifier.size(26.dp))
                    }
                }
            }

            BottomNav(current = tab, onSelect = { tab = it })
        }
    }

    val current = sheet
    if (current != null) {
        AddExpenseSheet(
            editing = (current as? SheetMode.Edit)?.expense,
            periodYear = periodYear,
            periodMonth = periodMonth,
            onDismiss = { sheet = null },
            onSave = { name, value, category, rawDate ->
                when (current) {
                    is SheetMode.Edit -> vm.updateExpense(current.expense.id, name, value, category, rawDate)
                    SheetMode.Add -> vm.addExpense(name, value, category, rawDate)
                }
                sheet = null
            },
            onDelete = {
                if (current is SheetMode.Edit) vm.deleteExpense(current.expense)
                sheet = null
            }
        )
    }
}

private val CircleShapeFab = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)

@Composable
private fun BottomNav(current: String, onSelect: (String) -> Unit) {
    Surface(color = SurfaceWhite, tonalElevation = 0.dp) {
        Column {
            HorizontalDivider(thickness = 1.dp, color = Slate100)
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .navigationBarsPadding()
            ) {
                NavItem("home", "Início", Icons.Rounded.Home, current, onSelect)
                NavItem("expenses", "Despesas", Icons.AutoMirrored.Rounded.FormatListBulleted, current, onSelect)
                NavItem("budget", "Orçamento", Icons.Rounded.BarChart, current, onSelect)
                NavItem("profile", "Perfil", Icons.Rounded.Person, current, onSelect)
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    id: String,
    label: String,
    icon: ImageVector,
    current: String,
    onSelect: (String) -> Unit
) {
    val active = current == id
    val tint = if (active) Blue500 else TextMuted
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickableNoRipple { onSelect(id) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            fontFamily = DMSans,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = tint
        )
    }
}
