package com.example.financaspessoais1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financaspessoais1.model.Expense
import com.example.financaspessoais1.model.ExpenseCategory
import com.example.financaspessoais1.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    editing: Expense?,
    periodYear: Int,
    periodMonth: Int,
    onDismiss: () -> Unit,
    onSave: (name: String, value: Double, category: ExpenseCategory, rawDate: String) -> Unit,
    onDelete: () -> Unit
) {
    val isEdit = editing != null
    var name by remember { mutableStateOf(editing?.name ?: "") }
    var valueInput by remember { mutableStateOf(editing?.value?.let { trimTrailing(it) } ?: "") }
    var category by remember { mutableStateOf(editing?.category ?: ExpenseCategory.ALIMENTACAO) }
    var rawDate by remember { mutableStateOf(editing?.rawDate ?: defaultDateForPeriod(periodYear, periodMonth)) }
    var error by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun submit() {
        val trimmed = name.trim()
        val value = valueInput.replace(",", ".").toDoubleOrNull()
        when {
            trimmed.isEmpty() -> error = "Nome é obrigatório."
            value == null || value <= 0.0 -> error = "Informe um valor válido."
            else -> { error = ""; onSave(trimmed, value, category, rawDate) }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Slate200) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        // Título
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (isEdit) "Editar Despesa" else "Nova Despesa",
                fontFamily = DMSans, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary
            )
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Slate100)
                    .clickableNoRipple { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Fechar", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
        }

        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Nome da despesa *")
                AppTextField(
                    value = name,
                    onValueChange = { name = it; error = "" },
                    placeholder = "Ex: Supermercado, Uber..."
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Valor (R$) *")
                AppTextField(
                    value = valueInput,
                    onValueChange = { valueInput = it.filter { c -> c.isDigit() || c == '.' || c == ',' }; error = "" },
                    placeholder = "0.00",
                    keyboardType = KeyboardType.Decimal
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Categoria")
                CategoryDropdown(selected = category, onSelected = { category = it })
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Data")
                DateField(rawDate = rawDate, onPicked = { rawDate = it })
            }
            if (error.isNotEmpty()) {
                Text(error, fontFamily = DMSans, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Red500)
            }
        }

        // Botões
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isEdit) {
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedBg),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text("Excluir", fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Red500)
                }
                Button(
                    onClick = { submit() },
                    modifier = Modifier.weight(2f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text("Salvar Alterações", fontFamily = DMSans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            } else {
                Button(
                    onClick = { submit() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text("Adicionar Despesa", fontFamily = DMSans, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(selected: ExpenseCategory, onSelected: (ExpenseCategory) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            textStyle = TextStyle(fontFamily = DMSans, fontSize = 14.sp, color = TextPrimary),
            trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue500,
                unfocusedBorderColor = InputBorder,
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = SurfaceWhite) {
            ExpenseCategory.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.label, fontFamily = DMSans, fontSize = 14.sp, color = TextPrimary) },
                    onClick = { onSelected(cat); expanded = false },
                    leadingIcon = { Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(18.dp)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(rawDate: String, onPicked: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = formatDisplayDate(rawDate),
        onValueChange = {},
        readOnly = true,
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple { showPicker = true },
        textStyle = TextStyle(fontFamily = DMSans, fontSize = 14.sp, color = TextPrimary),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = InputBorder,
            disabledContainerColor = SurfaceWhite,
            disabledTextColor = TextPrimary
        )
    )

    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = rawDateToMillis(rawDate))
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onPicked(millisToRawDate(it)) }
                    showPicker = false
                }) { Text("OK", fontFamily = DMSans, color = Blue500) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar", fontFamily = DMSans, color = TextSecondary) }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

// ── helpers de data/valor ─────────────────────────────────────────────────────

private fun trimTrailing(v: Double): String {
    val s = String.format(java.util.Locale.US, "%.2f", v)
    return s.trimEnd('0').trimEnd('.')
}

private fun formatDisplayDate(raw: String): String {
    val p = raw.split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else raw
}

private fun rawDateToMillis(raw: String): Long {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        sdf.parse(raw)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

private fun millisToRawDate(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
    return sdf.format(java.util.Date(millis))
}
