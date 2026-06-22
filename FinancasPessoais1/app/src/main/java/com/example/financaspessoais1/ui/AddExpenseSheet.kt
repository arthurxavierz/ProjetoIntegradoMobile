package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financaspessoais1.model.ExpenseCategory
import com.example.financaspessoais1.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseSheet(
    onDismiss: () -> Unit,
    onAdd: (name: String, value: Double, category: ExpenseCategory) -> Unit
) {
    var name             by remember { mutableStateOf("") }
    var valueInput       by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.OUTROS) }
    var nameError        by remember { mutableStateOf("") }
    var valueError       by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Título ───────────────────────────────────────────────────────
            Text(
                text       = "Novo Lançamento",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )

            Spacer(Modifier.height(24.dp))

            // ── Nome da despesa ──────────────────────────────────────────────
            OutlinedTextField(
                value        = name,
                onValueChange = { name = it; nameError = "" },
                label        = { Text("Descrição") },
                placeholder  = { Text("Ex: Supermercado") },
                singleLine   = true,
                isError      = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError) }
                } else null,
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ── Valor ────────────────────────────────────────────────────────
            OutlinedTextField(
                value        = valueInput,
                onValueChange = { valueInput = it; valueError = "" },
                label        = { Text("Valor") },
                prefix       = { Text("R$  ", fontWeight = FontWeight.Medium) },
                placeholder  = { Text("0,00") },
                singleLine   = true,
                isError      = valueError.isNotEmpty(),
                supportingText = if (valueError.isNotEmpty()) {
                    { Text(valueError) }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // ── Categoria ────────────────────────────────────────────────────
            Text(
                text  = "Categoria",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )

            Spacer(Modifier.height(10.dp))

            FlowRow(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    val isSelected = category == selectedCategory
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(
                                width = 1.5.dp,
                                color = if (isSelected) category.color
                                        else DividerColor,
                                shape = RoundedCornerShape(50)
                            )
                            .background(
                                if (isSelected) category.color.copy(alpha = 0.12f)
                                else SurfaceElevated
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector        = category.icon,
                            contentDescription = null,
                            tint               = if (isSelected) category.color else TextSecondary,
                            modifier           = Modifier.size(16.dp)
                        )
                        Text(
                            text  = category.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) category.color else TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Botão adicionar ──────────────────────────────────────────────
            Button(
                onClick = {
                    val trimmed = name.trim()
                    val value   = valueInput.replace(",", ".").toDoubleOrNull()
                    var valid   = true
                    if (trimmed.isEmpty()) { nameError  = "Informe a descrição."; valid = false }
                    if (value == null || value <= 0.0) { valueError = "Informe um valor válido."; valid = false }
                    if (valid) onAdd(trimmed, value!!, selectedCategory)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Adicionar lançamento",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
