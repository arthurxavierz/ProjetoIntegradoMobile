package com.example.cicloestudos3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.cicloestudos3.ui.theme.*
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSheet(
    initial: SessionForm,
    onDismiss: () -> Unit,
    onSave: (SessionForm) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var disc by remember { mutableStateOf(initial.disc) }
    var topic by remember { mutableStateOf(initial.topic) }
    var dateMillis by remember { mutableStateOf(initial.dateMillis) }
    var duration by remember { mutableStateOf(initial.duration) }
    var notes by remember { mutableStateOf(initial.notes) }
    var error by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = it }
                        val local = Calendar.getInstance().apply {
                            set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH), 12, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        dateMillis = local.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EstudosSurface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (initial.isEdit) "Editar Sessão" else "Nova Sessão",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = EstudosTitle
            )
            Spacer(Modifier.height(18.dp))

            LabeledField(
                label = "Disciplina *",
                value = disc,
                onValueChange = { disc = it; error = null },
                placeholder = "Ex: Cálculo I, Física..."
            )
            Spacer(Modifier.height(14.dp))

            LabeledField(
                label = "Tópico Estudado *",
                value = topic,
                onValueChange = { topic = it; error = null },
                placeholder = "Ex: Derivadas, Matrizes..."
            )
            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f)) {
                    FieldLabel("Data")
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EstudosTitle)
                    ) { Text(fullDate(dateMillis), style = MaterialTheme.typography.bodyMedium) }
                }
                Column(Modifier.weight(1f)) {
                    FieldLabel("Duração (min)")
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { v -> duration = v.filter(Char::isDigit) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors()
                    )
                }
            }
            Spacer(Modifier.height(14.dp))

            LabeledField(
                label = "Notas (opcional)",
                value = notes,
                onValueChange = { notes = it },
                placeholder = "Ex: Revisar exercícios..."
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = EstudosDanger, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (initial.isEdit && onDelete != null) {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EstudosDangerSoft, contentColor = EstudosDanger
                        ),
                        elevation = null
                    ) { Text("Excluir", fontWeight = FontWeight.SemiBold) }
                }
                Button(
                    onClick = {
                        when {
                            disc.isBlank() -> error = "Disciplina é obrigatória."
                            topic.isBlank() -> error = "Tópico é obrigatório."
                            else -> onSave(
                                initial.copy(
                                    disc = disc, topic = topic, dateMillis = dateMillis,
                                    duration = duration.ifBlank { "60" }, notes = notes
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(if (initial.isEdit) 2f else 1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EstudosPrimary, contentColor = androidx.compose.ui.graphics.Color.White)
                ) {
                    Text(
                        if (initial.isEdit) "Salvar Alterações" else "Registrar Sessão",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = EstudosLabel,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = EstudosMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = fieldColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EstudosPrimary,
    unfocusedBorderColor = EstudosBorder,
    focusedTextColor = EstudosTitle,
    unfocusedTextColor = EstudosTitle,
    cursorColor = EstudosPrimary
)
