package com.example.cicloestudos3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cicloestudos3.ui.theme.*
import java.util.Calendar
import java.util.TimeZone

// ── Shared date-picker dialog (same UTC→local handling as SessionSheet) ───────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickDateDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onPicked: (Long) -> Unit
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = it }
                    val local = Calendar.getInstance().apply {
                        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH), 9, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onPicked(local.timeInMillis)
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) { DatePicker(state = state) }
}

// ── DISCIPLINA sheet (create / edit / delete) ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DisciplineSheet(
    initial: SubjectForm,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (SubjectForm) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(initial.name) }
    var color by remember { mutableStateOf(initial.colorHex) }
    var localError by remember { mutableStateOf<String?>(null) }
    val shownError = localError ?: error

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EstudosSurface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)
        ) {
            Text(
                text = if (initial.isEdit) "Editar Disciplina" else "Nova Disciplina",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = EstudosTitle
            )
            Spacer(Modifier.height(18.dp))

            LabeledField(
                label = "Nome *",
                value = name,
                onValueChange = { name = it; localError = null },
                placeholder = "Ex: Cálculo I, Física..."
            )
            Spacer(Modifier.height(16.dp))

            FieldLabel("Cor")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                subjectColorPalette.forEach { hex ->
                    val c = parseColor(hex)
                    val selected = hex.equals(color, ignoreCase = true)
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(c)
                            .then(
                                if (selected) Modifier.border(3.dp, EstudosTitle, CircleShape)
                                else Modifier
                            )
                            .clickable { color = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            shownError?.let {
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
                        colors = ButtonDefaults.buttonColors(containerColor = EstudosDangerSoft, contentColor = EstudosDanger),
                        elevation = null
                    ) { Text("Excluir", fontWeight = FontWeight.SemiBold) }
                }
                Button(
                    onClick = {
                        if (name.isBlank()) localError = "Nome é obrigatório."
                        else onSave(initial.copy(name = name, colorHex = color))
                    },
                    modifier = Modifier.weight(if (initial.isEdit) 2f else 1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EstudosPrimary, contentColor = Color.White)
                ) {
                    Text(
                        if (initial.isEdit) "Salvar Alterações" else "Criar Disciplina",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── REVISÃO sheet (create / edit / delete) ────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionSheet(
    initial: RevisionForm,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (RevisionForm) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var subjectName by remember { mutableStateOf(initial.subjectName) }
    var topicTitle by remember { mutableStateOf(initial.topicTitle) }
    var scheduledAt by remember { mutableStateOf(initial.scheduledAt) }
    var localError by remember { mutableStateOf<String?>(null) }
    val shownError = localError ?: error

    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        PickDateDialog(
            initialMillis = scheduledAt,
            onDismiss = { showDatePicker = false },
            onPicked = { scheduledAt = it }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EstudosSurface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)
        ) {
            Text(
                text = if (initial.isEdit) "Editar Revisão" else "Nova Revisão",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = EstudosTitle
            )
            Spacer(Modifier.height(18.dp))

            LabeledField(
                label = "Disciplina *",
                value = subjectName,
                onValueChange = { subjectName = it; localError = null },
                placeholder = "Ex: Cálculo I, Física..."
            )
            Spacer(Modifier.height(14.dp))

            LabeledField(
                label = "Tópico *",
                value = topicTitle,
                onValueChange = { topicTitle = it; localError = null },
                placeholder = "Ex: Derivadas, Matrizes..."
            )
            Spacer(Modifier.height(14.dp))

            FieldLabel("Data da Revisão")
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EstudosTitle)
            ) { Text(fullDate(scheduledAt), style = MaterialTheme.typography.bodyMedium) }

            shownError?.let {
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
                        colors = ButtonDefaults.buttonColors(containerColor = EstudosDangerSoft, contentColor = EstudosDanger),
                        elevation = null
                    ) { Text("Excluir", fontWeight = FontWeight.SemiBold) }
                }
                Button(
                    onClick = {
                        when {
                            subjectName.isBlank() -> localError = "Disciplina é obrigatória."
                            topicTitle.isBlank() -> localError = "Tópico é obrigatório."
                            else -> onSave(
                                initial.copy(
                                    subjectName = subjectName,
                                    topicTitle = topicTitle,
                                    scheduledAt = scheduledAt
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(if (initial.isEdit) 2f else 1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EstudosPrimary, contentColor = Color.White)
                ) {
                    Text(
                        if (initial.isEdit) "Salvar Alterações" else "Agendar Revisão",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
