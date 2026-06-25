package com.example.cicloestudos3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cicloestudos3.data.AppSettings
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.ui.theme.*

// ── Shared header bits ──────────────────────────────────────────────────────────

@Composable
private fun ScreenTitle(eyebrow: String?, title: String, trailing: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp, 20.dp, 20.dp, 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            if (eyebrow != null) {
                Text(
                    eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = EstudosEyebrow,
                    letterSpacing = 1.2.sp
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(title, style = MaterialTheme.typography.headlineLarge, color = EstudosTitle)
        }
        trailing?.invoke()
    }
}

@Composable
private fun CountPill(text: String) {
    Surface(shape = RoundedCornerShape(10.dp), color = EstudosPrimarySoft) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = EstudosPrimaryDeep
        )
    }
}

@Composable
private fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp, 16.dp, 20.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EstudosTitle)
        if (action != null && onAction != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = EstudosPrimary,
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickableNoRipple(onAction).padding(4.dp)
            )
        }
    }
}

// ── HOME / INÍCIO ───────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    state: StudyUiState,
    onSeeRevisions: () -> Unit,
    onSeeHistory: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(EstudosBackground)) {
        item {
            ScreenTitle(eyebrow = monthYearEyebrow(), title = "Meus Estudos")
        }
        item { StatsCard(state) }

        item {
            SectionHeader("Próximas Revisões", action = "Ver todas", onAction = onSeeRevisions)
        }
        val next = state.pendingRevisions.sortedBy { it.scheduledAt }.take(3)
        if (next.isEmpty()) {
            item { EmptyHint("Nenhuma revisão pendente.") }
        } else {
            items(next) { rev -> NextRevisionCard(rev) }
        }

        item {
            SectionHeader("Sessões Recentes", action = "Ver histórico", onAction = onSeeHistory)
        }
        val recent = state.sessions.take(3)
        if (recent.isEmpty()) {
            item { EmptyHint("Nenhuma sessão registrada. Toque em + para começar.") }
        } else {
            items(recent) { sess -> RecentSessionRow(sess) }
        }
        item { Spacer(Modifier.height(110.dp)) }
    }
}

@Composable
private fun StatsCard(state: StudyUiState) {
    Column(
        modifier = Modifier
            .padding(20.dp, 10.dp, 20.dp, 0.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(EstudosPrimary, EstudosDark)))
            .padding(20.dp)
    ) {
        Text(
            "Resumo de ${monthLabel()}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatNumber(state.totalSessions.toString(), "Sessões", Modifier.weight(1f))
            VDivider()
            StatNumber("${state.totalHours}h", "Estudadas", Modifier.weight(1f))
            VDivider()
            StatNumber(state.totalDisciplines.toString(), "Matérias", Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatNumber(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = DmSans)
        Spacer(Modifier.height(3.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.65f), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun VDivider() {
    Box(Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha = 0.2f)))
}

@Composable
private fun NextRevisionCard(rev: Revision) {
    SoftCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            RoundIcon(Icons.Filled.Autorenew, EstudosPrimarySoft, EstudosPrimary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${rev.subjectName} · ${rev.topicTitle}",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    color = EstudosTitle, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text("Revisão em ${shortDate(rev.scheduledAt)}", style = MaterialTheme.typography.labelMedium, color = EstudosMuted)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = EstudosPrimarySoft) {
                Text(
                    daysLabel(rev.scheduledAt),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EstudosPrimary
                )
            }
        }
    }
}

@Composable
private fun RecentSessionRow(sess: TopicWithSubject) {
    SoftCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Row(Modifier.padding(14.dp, 14.dp, 16.dp, 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(parseColor(sess.subjectColorHex)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${sess.subjectName} · ${sess.topic.title}",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    color = EstudosTitle, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text("${shortDate(sess.topic.studiedAt)} · ${sess.topic.durationMinutes} min", style = MaterialTheme.typography.labelMedium, color = EstudosMuted)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = EstudosPrimarySoft) {
                Text(
                    "${sess.topic.durationMinutes}min",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = EstudosPrimary
                )
            }
        }
    }
}

// ── DISCIPLINAS ─────────────────────────────────────────────────────────────────

@Composable
fun DisciplinasScreen(
    state: StudyUiState,
    onEditSession: (TopicWithSubject) -> Unit,
    onDeleteSession: (TopicWithSubject) -> Unit,
    onEditDiscipline: (SubjectWithStats) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(EstudosBackground)) {
        item {
            ScreenTitle(eyebrow = null, title = "Disciplinas") {
                CountPill("${state.totalDisciplines} matérias")
            }
        }
        if (state.subjects.isEmpty()) {
            item { EmptyHint("Nenhuma disciplina ainda. Toque em + para criar a primeira.") }
        } else {
            items(state.subjects) { s -> DisciplineCard(s, state.sessions, onEditDiscipline) }
        }

        item {
            Text(
                "Histórico de Sessões",
                style = MaterialTheme.typography.titleLarge, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EstudosTitle,
                modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 8.dp)
            )
        }
        if (state.sessions.isEmpty()) {
            item { EmptyHint("Sem sessões no histórico.") }
        } else {
            items(state.sessions) { sess -> HistorySessionRow(sess, onEditSession, onDeleteSession) }
        }
        item { Spacer(Modifier.height(110.dp)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DisciplineCard(
    s: SubjectWithStats,
    sessions: List<TopicWithSubject>,
    onEdit: (SubjectWithStats) -> Unit
) {
    val color = parseColor(s.subject.colorHex)
    // Distinct topic titles for this subject; "studied" = topics with at least one session.
    val topicTitles = sessions.filter { it.topic.subjectId == s.subject.id }
        .map { it.topic.title }.distinct()
    val total = topicTitles.size.coerceAtLeast(s.topicCount)
    val studied = topicTitles.size
    val pct = if (total > 0) studied.toFloat() / total else 0f

    SoftCard(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp).clickableNoRipple { onEdit(s) },
        corner = 16.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(color))
                Spacer(Modifier.width(10.dp))
                Text(s.subject.name, style = MaterialTheme.typography.titleLarge, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EstudosTitle, modifier = Modifier.weight(1f))
                Text("$studied/$total tópicos", style = MaterialTheme.typography.labelMedium, color = EstudosMuted, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(8.dp))
                SquareButton(Icons.Filled.Edit, EstudosTrack, Color(0xFF64748B)) { onEdit(s) }
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(EstudosTrack)) {
                Box(Modifier.fillMaxWidth(pct).height(6.dp).clip(RoundedCornerShape(3.dp)).background(color))
            }
            if (topicTitles.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    topicTitles.take(8).forEach { name ->
                        Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.13f)) {
                            Text(
                                name,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySessionRow(
    sess: TopicWithSubject,
    onEdit: (TopicWithSubject) -> Unit,
    onDelete: (TopicWithSubject) -> Unit
) {
    SoftCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        Row(Modifier.padding(14.dp, 12.dp, 14.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
            RoundIcon(Icons.Filled.MenuBook, EstudosPrimarySoft, EstudosPrimary, size = 36.dp, iconSize = 17.dp, corner = 10.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${sess.subjectName} · ${sess.topic.title}",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    color = EstudosTitle, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text("${shortDate(sess.topic.studiedAt)} · ${sess.topic.durationMinutes} min", style = MaterialTheme.typography.labelSmall, color = EstudosMuted)
            }
            SquareButton(Icons.Filled.Edit, EstudosTrack, Color(0xFF64748B)) { onEdit(sess) }
            Spacer(Modifier.width(8.dp))
            SquareButton(Icons.Filled.Delete, EstudosDangerSoft, EstudosDanger) { onDelete(sess) }
        }
    }
}

// ── REVISÕES ────────────────────────────────────────────────────────────────────

@Composable
fun RevisionsScreen(
    state: StudyUiState,
    onToggleDone: (Revision) -> Unit,
    onEditRevision: (Revision) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(EstudosBackground)) {
        item {
            ScreenTitle(eyebrow = null, title = "Revisões") {
                CountPill("${state.pendingRevisions.size} pendentes")
            }
        }
        if (state.revisions.isEmpty()) {
            item { EmptyHint("Nenhuma revisão. Toque em + para agendar ou registre sessões para gerá-las automaticamente.") }
        } else {
            items(state.revisions.sortedWith(compareBy({ it.isCompleted }, { it.scheduledAt }))) { rev ->
                RevisionRow(rev, onToggleDone, onEditRevision)
            }
        }
        item { Spacer(Modifier.height(110.dp)) }
    }
}

@Composable
private fun RevisionRow(
    rev: Revision,
    onToggleDone: (Revision) -> Unit,
    onEdit: (Revision) -> Unit
) {
    val done = rev.isCompleted
    SoftCard(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp).clickableNoRipple { onEdit(rev) }) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            RoundIcon(
                Icons.Filled.Autorenew,
                if (done) EstudosDoneSoft else EstudosPrimarySoft,
                if (done) EstudosDone else EstudosPrimary
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(rev.subjectName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = EstudosTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${rev.topicTitle} · ${shortDate(rev.scheduledAt)}", style = MaterialTheme.typography.labelMedium, color = EstudosMuted)
            }
            SquareButton(Icons.Filled.Edit, EstudosTrack, Color(0xFF64748B)) { onEdit(rev) }
            Spacer(Modifier.width(8.dp))
            // Match the design's compact pill (7×12 padding, no Material min-height).
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (done) EstudosDoneSoft else EstudosPrimary,
                modifier = Modifier.clickableNoRipple { onToggleDone(rev) }
            ) {
                Text(
                    if (done) "Feita" else "Marcar feita",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (done) EstudosDone else Color.White
                )
            }
        }
    }
}

// ── PERFIL ──────────────────────────────────────────────────────────────────────

@Composable
fun PerfilScreen(
    state: StudyUiState,
    userName: String,
    userEmail: String,
    intervalDays: Int,
    notificationsEnabled: Boolean,
    onSetInterval: (Int) -> Unit,
    onSetNotifications: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    var showIntervalDialog by remember { mutableStateOf(false) }
    if (showIntervalDialog) {
        IntervalPickerDialog(
            current = intervalDays,
            onPick = { onSetInterval(it); showIntervalDialog = false },
            onDismiss = { showIntervalDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(EstudosBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp, 28.dp, 20.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(80.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(EstudosPrimary, EstudosDark))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(38.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(userName.ifBlank { "Estudante" }, style = MaterialTheme.typography.headlineMedium, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EstudosTitle)
                Spacer(Modifier.height(3.dp))
                Text(userEmail.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge, fontSize = 14.sp, color = EstudosMuted)
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStat(state.totalSessions.toString(), "Sessões", Modifier.weight(1f))
                ProfileStat("${state.totalHours}h", "Estudadas", Modifier.weight(1f))
                ProfileStat(state.totalDisciplines.toString(), "Matérias", Modifier.weight(1f))
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            SoftCard(modifier = Modifier.padding(horizontal = 20.dp), corner = 16.dp) {
                Column {
                    SettingsRow(
                        Icons.Filled.CalendarMonth, "Intervalo de Revisão",
                        intervalSubtitle(intervalDays), EstudosPrimarySoft, EstudosPrimary,
                        divider = true, onClick = { showIntervalDialog = true }
                    )
                    SettingsRow(
                        Icons.Filled.Notifications, "Notificações de Revisão",
                        if (notificationsEnabled) "Lembretes locais ativados" else "Lembretes desativados",
                        EstudosPrimarySoft, EstudosPrimary, divider = true,
                        onClick = { onSetNotifications(!notificationsEnabled) },
                        trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = onSetNotifications,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = EstudosPrimary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = EstudosChevron,
                                    uncheckedBorderColor = EstudosChevron
                                )
                            )
                        }
                    )
                    SettingsRow(Icons.Filled.Logout, "Sair da Conta", null, EstudosDangerSoft, EstudosDanger, danger = true, chevron = false, divider = false, onClick = onLogout)
                }
            }
        }
        item { Spacer(Modifier.height(110.dp)) }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, modifier: Modifier = Modifier) {
    SoftCard(modifier = modifier) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = EstudosPrimary)
            Spacer(Modifier.height(3.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = EstudosMuted)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    iconBg: Color,
    iconTint: Color,
    danger: Boolean = false,
    chevron: Boolean = true,
    divider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .then(if (onClick != null) Modifier.clickableNoRipple(onClick) else Modifier)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIcon(icon, iconBg, iconTint, size = 36.dp, iconSize = 18.dp, corner = 10.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = if (danger) EstudosDanger else EstudosTitle)
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.labelMedium, color = EstudosMuted)
            }
            when {
                trailing != null -> trailing()
                chevron -> Icon(Icons.Filled.ChevronRight, null, tint = EstudosChevron, modifier = Modifier.size(18.dp))
            }
        }
        if (divider) HorizontalDivider(color = EstudosBackground, thickness = 1.dp)
    }
}

private fun intervalSubtitle(days: Int): String =
    if (days == 1) "Revisão 1 dia após estudar" else "Revisão $days dias após estudar"

@Composable
private fun IntervalPickerDialog(
    current: Int,
    onPick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar", color = EstudosPrimary) }
        },
        title = {
            Text("Intervalo de Revisão", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = EstudosTitle)
        },
        text = {
            Column {
                Text(
                    "Quantos dias após estudar a revisão deve ser agendada.",
                    style = MaterialTheme.typography.bodyMedium, color = EstudosMuted
                )
                Spacer(Modifier.height(8.dp))
                AppSettings.INTERVAL_OPTIONS.forEach { d ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickableNoRipple { onPick(d) }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = d == current,
                            onClick = { onPick(d) },
                            colors = RadioButtonDefaults.colors(selectedColor = EstudosPrimary, unselectedColor = EstudosChevron)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (d == 1) "1 dia" else "$d dias", style = MaterialTheme.typography.bodyLarge, color = EstudosTitle)
                    }
                }
            }
        },
        containerColor = EstudosSurface
    )
}

// ── Reusable primitives ───────────────────────────────────────────────────────────

@Composable
fun SoftCard(modifier: Modifier = Modifier, corner: Dp = 14.dp, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(corner)
    Surface(
        // Match the design's very soft "0 1px 4px rgba(0,0,0,.05)" shadow: a low-alpha
        // custom shadow instead of M3's elevation shadow, which renders much darker.
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.10f),
                spotColor = Color.Black.copy(alpha = 0.13f)
            ),
        shape = shape,
        color = EstudosSurface,
        shadowElevation = 0.dp
    ) { content() }
}

@Composable
private fun RoundIcon(
    icon: ImageVector,
    bg: Color,
    tint: Color,
    size: Dp = 42.dp,
    iconSize: Dp = 20.dp,
    corner: Dp = 12.dp
) {
    Box(
        Modifier.size(size).clip(RoundedCornerShape(corner)).background(bg),
        contentAlignment = Alignment.Center
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize)) }
}

@Composable
private fun SquareButton(icon: ImageVector, bg: Color, tint: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = Modifier.size(30.dp).clickableNoRipple(onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = EstudosMuted,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick)
