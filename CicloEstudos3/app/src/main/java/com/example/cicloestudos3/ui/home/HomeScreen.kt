package com.example.cicloestudos3.ui.home

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cicloestudos3.CicloApp
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            (androidx.compose.ui.platform.LocalContext.current.applicationContext as CicloApp).repository
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Hero Header ──────────────────────────────────────────────────────
        item { HeroHeader(state) }

        // ── Stats Row ────────────────────────────────────────────────────────
        item {
            StatsRow(
                streak           = state.streak,
                topicsThisWeek   = state.topicsThisWeek,
                minutesThisWeek  = state.totalMinutesThisWeek,
                modifier         = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // ── Today's Revisions ────────────────────────────────────────────────
        item {
            SectionTitle(
                title    = "Revisões de Hoje",
                subtitle = if (state.todayRevisions.isEmpty()) "Nenhuma agendada" else "${state.todayRevisions.size} agendada(s)",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        if (state.todayRevisions.isEmpty()) {
            item { EmptyRevisionsBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
        } else {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.todayRevisions) { rev -> RevisionCard(rev) }
                }
            }
        }

        // ── Upcoming Revisions ───────────────────────────────────────────────
        if (state.upcomingRevisions.isNotEmpty()) {
            item {
                SectionTitle(
                    title    = "Próximas Revisões",
                    subtitle = "nos próximos dias",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
            }
            items(state.upcomingRevisions.take(3)) { rev ->
                UpcomingRevisionRow(rev, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }

        // ── Subjects Grid ────────────────────────────────────────────────────
        if (state.subjects.isNotEmpty()) {
            item {
                SectionTitle(
                    title    = "Suas Disciplinas",
                    subtitle = "${state.subjects.size} cadastrada(s)",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
            }
            item {
                SubjectsGrid(
                    subjects = state.subjects,
                    onSubjectClick = { id ->
                        navController.navigate("subject/$id")
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ── Recent Topics ────────────────────────────────────────────────────
        if (state.recentTopics.isNotEmpty()) {
            item {
                SectionTitle(
                    title    = "Últimos Estudos",
                    subtitle = "histórico recente",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
            }
            items(state.recentTopics.take(5)) { t ->
                RecentTopicRow(t, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }
    }
}

// ── Hero Header ───────────────────────────────────────────────────────────────

@Composable
private fun HeroHeader(state: HomeUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(ForestDeep, EmeraldPrimary),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column {
            Text(
                text  = state.greeting + "!",
                style = MaterialTheme.typography.labelLarge,
                color = TextOnDarkSub
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Ciclo de Estudos",
                style = MaterialTheme.typography.headlineLarge,
                color = TextOnDark
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint               = AmberStreak,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = if (state.streak > 0) "${state.streak} dia(s) em sequência" else "Comece sua sequência hoje!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnDarkSub
                )
            }
        }
    }
}

// ── Stats Row ─────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(
    streak: Int,
    topicsThisWeek: Int,
    minutesThisWeek: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
            icon  = Icons.Rounded.LocalFireDepartment,
            value = "$streak",
            label = "Dias seguidos",
            tint  = AmberStreak,
            bg    = AmberContainer,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon  = Icons.Rounded.MenuBook,
            value = "$topicsThisWeek",
            label = "Tópicos/semana",
            tint  = EmeraldPrimary,
            bg    = EmeraldContainer,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon  = Icons.Rounded.Schedule,
            value = if (minutesThisWeek >= 60) "${minutesThisWeek / 60}h" else "${minutesThisWeek}min",
            label = "Esta semana",
            tint  = TealSecondary,
            bg    = TealContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

// ── Section Title ─────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
    Spacer(Modifier.height(8.dp))
}

// ── Revision Card (horizontal scroll) ────────────────────────────────────────

@Composable
private fun RevisionCard(revision: Revision) {
    val color = parseColor(revision.subjectColorHex)
    Card(
        modifier  = Modifier.width(180.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                revision.topicTitle,
                style     = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines  = 2
            )
            Spacer(Modifier.height(4.dp))
            Text(revision.subjectName, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = color)
                Spacer(Modifier.width(4.dp))
                Text(
                    formatTime(revision.scheduledAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Upcoming Revision Row ─────────────────────────────────────────────────────

@Composable
private fun UpcomingRevisionRow(revision: Revision, modifier: Modifier = Modifier) {
    val color = parseColor(revision.subjectColorHex)
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(revision.topicTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(revision.subjectName, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatDate(revision.scheduledAt), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(formatTime(revision.scheduledAt), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Empty Revisions Banner ────────────────────────────────────────────────────

@Composable
private fun EmptyRevisionsBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = EmeraldContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Dia livre de revisões!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ForestDeep)
                Text("Agende novas revisões na aba Revisões.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

// ── Subjects Grid ─────────────────────────────────────────────────────────────

@Composable
private fun SubjectsGrid(
    subjects: List<SubjectWithStats>,
    onSubjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = subjects.chunked(2)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { s ->
                    SubjectMiniCard(s, onClick = { onSubjectClick(s.subject.id) }, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SubjectMiniCard(s: SubjectWithStats, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = parseColor(s.subject.colorHex)
    Card(
        modifier  = modifier.clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.height(8.dp))
            Text(s.subject.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Text("${s.topicCount} tópico(s)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                if (s.totalMinutes >= 60) "${s.totalMinutes / 60}h ${s.totalMinutes % 60}min"
                else "${s.totalMinutes}min",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Recent Topic Row ──────────────────────────────────────────────────────────

@Composable
private fun RecentTopicRow(t: TopicWithSubject, modifier: Modifier = Modifier) {
    val color = parseColor(t.subjectColorHex)
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.LibraryBooks, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.topic.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(t.subjectName,  style = MaterialTheme.typography.labelSmall, color = color)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatDate(t.topic.studiedAt), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(
                    if (t.topic.durationMinutes >= 60) "${t.topic.durationMinutes / 60}h ${t.topic.durationMinutes % 60}min"
                    else "${t.topic.durationMinutes}min",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Utils ─────────────────────────────────────────────────────────────────────

private val dateFmt = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
private val timeFmt = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

fun formatDate(ms: Long): String = dateFmt.format(Date(ms))
fun formatTime(ms: Long): String = timeFmt.format(Date(ms))

fun parseColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    EmeraldPrimary
}
