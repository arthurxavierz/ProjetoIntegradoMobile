package com.example.cicloestudos3.ui.revisions

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cicloestudos3.CicloApp
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.ui.home.formatDate
import com.example.cicloestudos3.ui.home.formatTime
import com.example.cicloestudos3.ui.home.parseColor
import com.example.cicloestudos3.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionsScreen(
    navController: NavHostController,
    viewModel: RevisionsViewModel = viewModel(
        factory = RevisionsViewModel.Factory(
            (LocalContext.current.applicationContext as CicloApp).repository,
            LocalContext.current.applicationContext
        )
    )
) {
    val revisions by viewModel.allRevisions.collectAsStateWithLifecycle()
    val subjects  by viewModel.allSubjects.collectAsStateWithLifecycle()
    val topics    by viewModel.recentTopics.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }

    val grouped = remember(revisions) { revisions.groupByDate() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronograma de Revisões", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = { showAddSheet = true },
                icon           = { Icon(Icons.Rounded.Add, null) },
                text           = { Text("Agendar Revisão") },
                containerColor = EmeraldPrimary,
                contentColor   = Color.White
            )
        }
    ) { padding ->
        if (revisions.isEmpty()) {
            EmptyRevisionsState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                grouped.forEach { (dateLabel, dayRevisions) ->
                    item {
                        DateGroupHeader(
                            label = dateLabel,
                            count = dayRevisions.size,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(dayRevisions, key = { it.id }) { rev ->
                        RevisionItem(
                            revision       = rev,
                            onToggleComplete = { viewModel.toggleCompleted(rev) },
                            onDelete       = { viewModel.deleteRevision(rev) }
                        )
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showAddSheet) {
        AddRevisionSheet(
            subjects  = subjects,
            topics    = topics,
            onDismiss = { showAddSheet = false },
            onSave    = { topicId, subjectId, topicTitle, subjectName, colorHex, scheduledAt, notify ->
                viewModel.scheduleRevision(topicId, subjectId, topicTitle, subjectName, colorHex, scheduledAt, notify)
                showAddSheet = false
            }
        )
    }
}

// ── Date Group Header ─────────────────────────────────────────────────────────

@Composable
private fun DateGroupHeader(label: String, count: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Surface(
            shape  = RoundedCornerShape(8.dp),
            color  = EmeraldContainer
        ) {
            Text(
                "$count",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Revision Item ─────────────────────────────────────────────────────────────

@Composable
private fun RevisionItem(revision: Revision, onToggleComplete: () -> Unit, onDelete: () -> Unit) {
    val color        = parseColor(revision.subjectColorHex)
    var showDelete   by remember { mutableStateOf(false) }
    val isCompleted  = revision.isCompleted

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isCompleted) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Checkbox
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isCompleted) EmeraldPrimary else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            // Color dot
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isCompleted) color.copy(alpha = 0.3f) else color)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    revision.topicTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) TextSecondary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(revision.subjectName, style = MaterialTheme.typography.labelSmall, color = if (isCompleted) color.copy(0.5f) else color)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(11.dp), tint = TextSecondary)
                    Spacer(Modifier.width(3.dp))
                    Text(formatTime(revision.scheduledAt), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    if (revision.workerRequestId.isNotBlank() && !isCompleted) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.NotificationsActive, null, modifier = Modifier.size(11.dp), tint = AmberStreak)
                    }
                }
            }
            IconButton(onClick = { showDelete = true }) {
                Icon(Icons.Rounded.DeleteOutline, null, tint = RedAlert.copy(0.6f))
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Excluir Revisão?") },
            text  = { Text("\"${revision.topicTitle}\" será removida do cronograma.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDelete = false }) { Text("Excluir", color = RedAlert) }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancelar") } }
        )
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyRevisionsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.CalendarMonth, null, tint = EmeraldPrimary.copy(0.4f), modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(16.dp))
            Text("Nenhuma revisão agendada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text("Agende revisões dos seus tópicos estudados.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

// ── Add Revision Sheet (DatePicker + TimePicker) ──────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRevisionSheet(
    subjects: List<Subject>,
    topics: List<TopicWithSubject>,
    onDismiss: () -> Unit,
    onSave: (topicId: Int, subjectId: Int, topicTitle: String, subjectName: String, colorHex: String, scheduledAt: Long, notifyEnabled: Boolean) -> Unit
) {
    var selectedSubject  by remember { mutableStateOf<Subject?>(null) }
    var selectedTopic    by remember { mutableStateOf<TopicWithSubject?>(null) }
    var notifyEnabled    by remember { mutableStateOf(true) }
    var subjectExpanded  by remember { mutableStateOf(false) }
    var topicExpanded    by remember { mutableStateOf(false) }
    var subjectError     by remember { mutableStateOf(false) }
    var topicError       by remember { mutableStateOf(false) }

    // Date
    var showDatePicker   by remember { mutableStateOf(false) }
    var selectedDateMs   by remember { mutableStateOf(System.currentTimeMillis()) }
    val datePickerState  = rememberDatePickerState(initialSelectedDateMillis = selectedDateMs)
    val dateLabel = remember(selectedDateMs) {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(selectedDateMs))
    }

    // Time
    var showTimePicker   by remember { mutableStateOf(false) }
    var selectedHour     by remember { mutableStateOf(8) }
    var selectedMinute   by remember { mutableStateOf(0) }
    val timePickerState  = rememberTimePickerState(initialHour = selectedHour, initialMinute = selectedMinute)
    val timeLabel = remember(selectedHour, selectedMinute) {
        String.format("%02d:%02d", selectedHour, selectedMinute)
    }

    val filteredTopics = remember(selectedSubject, topics) {
        if (selectedSubject == null) topics else topics.filter { it.topic.subjectId == selectedSubject!!.id }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val utcCal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = it }
                        val localCal = Calendar.getInstance().apply {
                            set(utcCal.get(Calendar.YEAR), utcCal.get(Calendar.MONTH), utcCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        selectedDateMs = localCal.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    // TimePicker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title   = { Text("Horário da Revisão") },
            text    = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour   = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } }
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Agendar Revisão", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            // Subject dropdown
            ExposedDropdownMenuBox(expanded = subjectExpanded, onExpandedChange = { subjectExpanded = it }) {
                OutlinedTextField(
                    value             = selectedSubject?.name ?: "",
                    onValueChange     = {},
                    readOnly          = true,
                    label             = { Text("Disciplina *") },
                    trailingIcon      = { ExposedDropdownMenuDefaults.TrailingIcon(subjectExpanded) },
                    isError           = subjectError,
                    supportingText    = if (subjectError) {{ Text("Selecione uma disciplina") }} else null,
                    modifier          = Modifier.menuAnchor().fillMaxWidth(),
                    shape             = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                    subjects.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.name) },
                            onClick = {
                                selectedSubject = s
                                selectedTopic   = null
                                subjectError    = false
                                subjectExpanded = false
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape)
                                        .background(parseColor(s.colorHex))
                                )
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Topic dropdown
            ExposedDropdownMenuBox(expanded = topicExpanded, onExpandedChange = { topicExpanded = it }) {
                OutlinedTextField(
                    value          = selectedTopic?.topic?.title ?: "",
                    onValueChange  = {},
                    readOnly       = true,
                    label          = { Text("Tópico *") },
                    trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(topicExpanded) },
                    isError        = topicError,
                    supportingText = if (topicError) {{ Text("Selecione um tópico") }} else null,
                    modifier       = Modifier.menuAnchor().fillMaxWidth(),
                    shape          = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(expanded = topicExpanded, onDismissRequest = { topicExpanded = false }) {
                    if (filteredTopics.isEmpty()) {
                        DropdownMenuItem(text = { Text("Nenhum tópico disponível", color = TextSecondary) }, onClick = {})
                    } else {
                        filteredTopics.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.topic.title) },
                                onClick = {
                                    selectedTopic  = t
                                    topicError     = false
                                    topicExpanded  = false
                                },
                                trailingIcon = { Text(t.subjectName, style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.Gray) }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Date + Time row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick   = { showDatePicker = true },
                    modifier  = Modifier.weight(1f).height(52.dp),
                    shape     = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Rounded.CalendarToday, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(dateLabel)
                }
                OutlinedButton(
                    onClick   = { showTimePicker = true },
                    modifier  = Modifier.weight(1f).height(52.dp),
                    shape     = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(timeLabel)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Notification toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = if (notifyEnabled) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (notifyEnabled) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsOff,
                        null,
                        tint     = if (notifyEnabled) EmeraldPrimary else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Notificação", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Lembrete no horário agendado", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(
                        checked = notifyEnabled,
                        onCheckedChange = { notifyEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val sub = selectedSubject
                    val top = selectedTopic
                    if (sub == null) { subjectError = true; return@Button }
                    if (top == null) { topicError   = true; return@Button }

                    // Build scheduled timestamp: selected date + selected time
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = selectedDateMs
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE,      selectedMinute)
                        set(Calendar.SECOND,      0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onSave(
                        top.topic.id, sub.id,
                        top.topic.title, sub.name, sub.colorHex,
                        cal.timeInMillis, notifyEnabled
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Agendar Revisão", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
