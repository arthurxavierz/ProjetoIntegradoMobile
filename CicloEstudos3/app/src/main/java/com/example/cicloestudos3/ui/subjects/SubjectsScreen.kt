package com.example.cicloestudos3.ui.subjects

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cicloestudos3.CicloApp
import com.example.cicloestudos3.data.model.Subject
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.data.model.subjectColorPalette
import com.example.cicloestudos3.ui.home.parseColor
import com.example.cicloestudos3.ui.theme.*

// ── Subjects List ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    navController: NavHostController,
    viewModel: SubjectsViewModel = viewModel(
        factory = SubjectsViewModel.Factory(
            (LocalContext.current.applicationContext as CicloApp).repository
        )
    )
) {
    val subjects by viewModel.subjectsWithStats.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disciplinas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick            = { showAddSheet = true },
                icon               = { Icon(Icons.Rounded.Add, null) },
                text               = { Text("Nova Disciplina") },
                containerColor     = EmeraldPrimary,
                contentColor       = Color.White
            )
        }
    ) { padding ->
        if (subjects.isEmpty()) {
            EmptySubjectsState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects, key = { it.subject.id }) { s ->
                    SubjectCard(
                        s          = s,
                        onClick    = { navController.navigate("subject/${s.subject.id}") },
                        onDelete   = { viewModel.deleteSubject(s.subject) }
                    )
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddSheet) {
        AddSubjectSheet(
            onDismiss = { showAddSheet = false },
            onSave    = { name, color ->
                viewModel.insertSubject(name, color)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun SubjectCard(s: SubjectWithStats, onClick: () -> Unit, onDelete: () -> Unit) {
    val color = parseColor(s.subject.colorHex)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Color accent strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Spacer(Modifier.width(14.dp))
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = s.subject.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(s.subject.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoChip(Icons.Rounded.LibraryBooks, "${s.topicCount} tópicos", color)
                    InfoChip(
                        Icons.Rounded.Schedule,
                        if (s.totalMinutes >= 60) "${s.totalMinutes / 60}h ${s.totalMinutes % 60}min"
                        else "${s.totalMinutes}min",
                        TealSecondary
                    )
                }
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Rounded.DeleteOutline, null, tint = RedAlert.copy(alpha = 0.7f))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title   = { Text("Excluir Disciplina?") },
            text    = { Text("Todos os tópicos desta disciplina também serão excluídos.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Excluir", color = RedAlert)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = color)
        Spacer(Modifier.width(3.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun EmptySubjectsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.MenuBook, null, tint = EmeraldPrimary.copy(alpha = 0.4f), modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(16.dp))
            Text("Nenhuma disciplina", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text("Toque em + para adicionar uma.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

// ── Add Subject Sheet ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectSheet(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var name        by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(subjectColorPalette.first()) }
    var nameError   by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, dragHandle = { BottomSheetDefaults.DragHandle() }) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Nova Disciplina", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value       = name,
                onValueChange = { name = it; nameError = false },
                label       = { Text("Nome da Disciplina *") },
                isError     = nameError,
                supportingText = if (nameError) {{ Text("Campo obrigatório") }} else null,
                modifier    = Modifier.fillMaxWidth(),
                shape       = RoundedCornerShape(14.dp),
                singleLine  = true
            )

            Spacer(Modifier.height(20.dp))
            Text("Cor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            // Color picker grid
            val rows = subjectColorPalette.chunked(5)
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { hex ->
                        val c = parseColor(hex)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(c)
                                .clickable { selectedColor = hex }
                                .then(
                                    if (selectedColor == hex)
                                        Modifier.border(3.dp, Color.White, CircleShape)
                                            .border(5.dp, c, CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (name.isBlank()) { nameError = true; return@Button }
                    onSave(name, selectedColor)
                },
                modifier       = Modifier.fillMaxWidth().height(52.dp),
                shape          = RoundedCornerShape(14.dp),
                colors         = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Adicionar Disciplina", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// ── Subject Detail ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: Int,
    navController: NavHostController,
    viewModel: SubjectDetailViewModel = viewModel(
        factory = SubjectDetailViewModel.Factory(
            (LocalContext.current.applicationContext as CicloApp).repository,
            subjectId
        )
    )
) {
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }

    // Get subject info from first topic or default
    val subjectName  = topics.firstOrNull()?.subjectName ?: "Disciplina"
    val subjectColor = topics.firstOrNull()?.let { parseColor(it.subjectColorHex) } ?: EmeraldPrimary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(subjectName, fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = subjectColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = { showAddSheet = true },
                icon           = { Icon(Icons.Rounded.Add, null) },
                text           = { Text("Registrar Estudo") },
                containerColor = subjectColor,
                contentColor   = Color.White
            )
        }
    ) { padding ->
        if (topics.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.LibraryBooks, null, tint = subjectColor.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Nenhum tópico registrado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("Registre o que você estudou hoje.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stats header
                item {
                    SubjectStatsHeader(
                        topicCount     = topics.size,
                        totalMinutes   = topics.sumOf { it.topic.durationMinutes },
                        subjectColor   = subjectColor
                    )
                }
                item { Spacer(Modifier.height(4.dp)) }
                items(topics, key = { it.topic.id }) { t ->
                    TopicItem(t, subjectColor, onDelete = { viewModel.deleteTopic(t.topic) })
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddSheet) {
        AddTopicSheet(
            onDismiss = { showAddSheet = false },
            onSave    = { title, notes, minutes, studiedAt ->
                viewModel.insertTopic(title, notes, minutes, studiedAt)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun SubjectStatsHeader(topicCount: Int, totalMinutes: Int, subjectColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = subjectColor.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$topicCount", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = subjectColor)
                Text("Tópicos", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Divider(modifier = Modifier.width(1.dp).height(40.dp).align(Alignment.CenterVertically))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (totalMinutes >= 60) "${totalMinutes / 60}h ${totalMinutes % 60}min" else "${totalMinutes}min",
                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = subjectColor
                )
                Text("Tempo total", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun TopicItem(t: TopicWithSubject, subjectColor: Color, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(t.topic.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (t.topic.notes.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(t.topic.notes, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 2)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CalendarToday, null, modifier = Modifier.size(11.dp), tint = TextSecondary)
                        Spacer(Modifier.width(3.dp))
                        Text(com.example.cicloestudos3.ui.home.formatDate(t.topic.studiedAt), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(11.dp), tint = subjectColor)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            if (t.topic.durationMinutes >= 60) "${t.topic.durationMinutes / 60}h ${t.topic.durationMinutes % 60}min"
                            else "${t.topic.durationMinutes}min",
                            style = MaterialTheme.typography.labelSmall, color = subjectColor, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            IconButton(onClick = { showDelete = true }) {
                Icon(Icons.Rounded.DeleteOutline, null, tint = RedAlert.copy(alpha = 0.6f))
            }
        }
    }
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title   = { Text("Excluir tópico?") },
            text    = { Text("\"${t.topic.title}\" será removido do histórico.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDelete = false }) { Text("Excluir", color = RedAlert) }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancelar") } }
        )
    }
}

// ── Add Topic Sheet ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicSheet(
    onDismiss: () -> Unit,
    onSave: (title: String, notes: String, durationMinutes: Int, studiedAt: Long) -> Unit
) {
    var title          by remember { mutableStateOf("") }
    var notes          by remember { mutableStateOf("") }
    var durationInput  by remember { mutableStateOf("") }
    var titleError     by remember { mutableStateOf(false) }
    var durationError  by remember { mutableStateOf(false) }

    // DatePicker state
    var showDatePicker    by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val datePickerState   = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    val dateLabel = remember(selectedDateMillis) {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR")).format(java.util.Date(selectedDateMillis))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        // Convert UTC midnight to local midnight
                        val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = it }
                        val localCal = java.util.Calendar.getInstance().apply {
                            set(utcCal.get(java.util.Calendar.YEAR), utcCal.get(java.util.Calendar.MONTH), utcCal.get(java.util.Calendar.DAY_OF_MONTH), 12, 0, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        selectedDateMillis = localCal.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Registrar Estudo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = title, onValueChange = { title = it; titleError = false },
                label = { Text("Tópico / Conteúdo *") }, isError = titleError,
                supportingText = if (titleError) {{ Text("Campo obrigatório") }} else null,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Anotações (opcional)") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 2, maxLines = 4
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = durationInput, onValueChange = { durationInput = it.filter(Char::isDigit); durationError = false },
                label = { Text("Duração (minutos) *") }, isError = durationError,
                supportingText = if (durationError) {{ Text("Informe a duração") }} else null,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(Modifier.height(12.dp))

            // Date selector
            OutlinedButton(
                onClick   = { showDatePicker = true },
                modifier  = Modifier.fillMaxWidth().height(52.dp),
                shape     = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.CalendarToday, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Data do Estudo: $dateLabel")
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank())             { titleError = true;    return@Button }
                    val mins = durationInput.toIntOrNull()
                    if (mins == null || mins <= 0)   { durationError = true; return@Button }
                    onSave(title, notes, mins, selectedDateMillis)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Salvar Estudo", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
