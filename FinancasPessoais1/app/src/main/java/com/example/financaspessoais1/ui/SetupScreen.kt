package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financaspessoais1.ui.theme.*

@Composable
fun SetupScreen(onBudgetSet: (Double) -> Unit) {
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDeep, NavyMid)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Ícone ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.AccountBalance,
                    contentDescription = null,
                    tint               = PrimaryLight,
                    modifier           = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text       = "Finanças Pessoais",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = TextOnNavy
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text      = "Controle seus gastos com inteligência",
                style     = MaterialTheme.typography.bodyLarge,
                color     = TextOnNavySub,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(56.dp))

            // ── Campo de orçamento ───────────────────────────────────────────
            Text(
                text  = "Defina seu orçamento mensal",
                style = MaterialTheme.typography.labelLarge,
                color = TextOnNavySub
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value        = input,
                onValueChange = {
                    input = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                    error = ""
                },
                prefix          = { Text("R$  ", color = TextOnNavy, fontWeight = FontWeight.Medium) },
                placeholder     = { Text("0,00", color = TextOnNavySub) },
                singleLine      = true,
                isError         = error.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PrimaryLight,
                    unfocusedBorderColor = TextOnNavySub.copy(alpha = 0.5f),
                    errorBorderColor     = AccentRed,
                    focusedTextColor     = TextOnNavy,
                    unfocusedTextColor   = TextOnNavy,
                    cursorColor          = PrimaryLight,
                    errorTextColor       = TextOnNavy,
                ),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = error,
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentRed
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Botão principal ──────────────────────────────────────────────
            Button(
                onClick = {
                    val value = input.replace(",", ".").toDoubleOrNull()
                    when {
                        input.isBlank() -> error = "Informe o valor do orçamento."
                        value == null   -> error = "Digite um número válido."
                        value <= 0.0    -> error = "O valor deve ser maior que zero."
                        else            -> onBudgetSet(value)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text       = "Começar",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
