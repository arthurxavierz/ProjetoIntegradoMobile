package com.example.cicloestudos3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cicloestudos3.CicloApp
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.ui.theme.EstudosBackground
import com.example.cicloestudos3.ui.theme.EstudosMuted
import com.example.cicloestudos3.ui.theme.EstudosPrimary
import com.example.cicloestudos3.ui.theme.EstudosSurface

private enum class Tab(val label: String, val icon: ImageVector) {
    Home("Início", Icons.Filled.Home),
    Disciplinas("Disciplinas", Icons.Filled.MenuBook),
    Revisoes("Revisões", Icons.Filled.Autorenew),
    Perfil("Perfil", Icons.Filled.Person)
}

@Composable
fun EstudosApp() {
    val repo = (LocalContext.current.applicationContext as CicloApp).repository
    val context = LocalContext.current.applicationContext
    val vm: StudyViewModel = viewModel(factory = StudyViewModel.Factory(repo, context))
    val state by vm.uiState.collectAsState()
    val settings by vm.settings.collectAsState()

    if (!settings.loggedIn) {
        LoginScreen(
            onLogin = { email, pass -> vm.login(email, pass) },
            onRegister = { name, email, pass -> vm.register(name, email, pass) }
        )
        return
    }

    var tab by remember { mutableStateOf(Tab.Home) }
    var sheetForm by remember { mutableStateOf<SessionForm?>(null) }
    var subjectForm by remember { mutableStateOf<SubjectForm?>(null) }
    var subjectError by remember { mutableStateOf<String?>(null) }
    var revisionForm by remember { mutableStateOf<RevisionForm?>(null) }
    var revisionError by remember { mutableStateOf<String?>(null) }

    val showFab = tab == Tab.Home || tab == Tab.Disciplinas || tab == Tab.Revisoes
    val fabLabel = when (tab) {
        Tab.Disciplinas -> "Nova disciplina"
        Tab.Revisoes -> "Nova revisão"
        else -> "Nova sessão"
    }

    Scaffold(
        containerColor = EstudosBackground,
        bottomBar = { EstudosBottomBar(tab) { tab = it } },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        when (tab) {
                            Tab.Disciplinas -> { subjectError = null; subjectForm = SubjectForm() }
                            Tab.Revisoes -> { revisionError = null; revisionForm = RevisionForm() }
                            else -> sheetForm = SessionForm()
                        }
                    },
                    containerColor = EstudosPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) { Icon(Icons.Filled.Add, fabLabel) }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.Home -> HomeScreen(
                    state = state,
                    onSeeRevisions = { tab = Tab.Revisoes },
                    onSeeHistory = { tab = Tab.Disciplinas }
                )
                Tab.Disciplinas -> DisciplinasScreen(
                    state = state,
                    onEditSession = { s -> sheetForm = s.toForm() },
                    onDeleteSession = { vm.deleteSession(it) },
                    onEditDiscipline = { sws -> subjectError = null; subjectForm = sws.toForm() }
                )
                Tab.Revisoes -> RevisionsScreen(
                    state = state,
                    onToggleDone = { vm.toggleRevisionDone(it) },
                    onEditRevision = { rev -> revisionError = null; revisionForm = rev.toForm() }
                )
                Tab.Perfil -> PerfilScreen(
                    state = state,
                    userName = settings.userName,
                    userEmail = settings.userEmail,
                    intervalDays = settings.revisionIntervalDays,
                    notificationsEnabled = settings.notificationsEnabled,
                    onSetInterval = { vm.setRevisionInterval(it) },
                    onSetNotifications = { vm.setNotificationsEnabled(it) },
                    onLogout = { vm.logout() }
                )
            }
        }
    }

    sheetForm?.let { form ->
        SessionSheet(
            initial = form,
            onDismiss = { sheetForm = null },
            onSave = { vm.saveSession(it); sheetForm = null },
            onDelete = if (form.isEdit) {
                {
                    form.editingTopicId?.let { id ->
                        state.sessions.firstOrNull { it.topic.id == id }?.let { vm.deleteSession(it) }
                    }
                    sheetForm = null
                }
            } else null
        )
    }

    subjectForm?.let { form ->
        DisciplineSheet(
            initial = form,
            error = subjectError,
            onDismiss = { subjectForm = null; subjectError = null },
            onSave = {
                vm.saveSubject(it) { result ->
                    when (result) {
                        is StudyViewModel.SaveResult.Success -> { subjectForm = null; subjectError = null }
                        is StudyViewModel.SaveResult.Error -> subjectError = result.message
                    }
                }
            },
            onDelete = if (form.isEdit) {
                {
                    form.editingSubjectId?.let { id ->
                        state.subjects.firstOrNull { it.subject.id == id }?.let { vm.deleteSubject(it.subject) }
                    }
                    subjectForm = null; subjectError = null
                }
            } else null
        )
    }

    revisionForm?.let { form ->
        RevisionSheet(
            initial = form,
            error = revisionError,
            onDismiss = { revisionForm = null; revisionError = null },
            onSave = {
                vm.saveRevision(it) { result ->
                    when (result) {
                        is StudyViewModel.SaveResult.Success -> { revisionForm = null; revisionError = null }
                        is StudyViewModel.SaveResult.Error -> revisionError = result.message
                    }
                }
            },
            onDelete = if (form.isEdit) {
                {
                    form.editingRevisionId?.let { id ->
                        state.revisions.firstOrNull { it.id == id }?.let { vm.deleteRevision(it) }
                    }
                    revisionForm = null; revisionError = null
                }
            } else null
        )
    }
}

private fun SubjectWithStats.toForm() = SubjectForm(
    editingSubjectId = subject.id,
    name = subject.name,
    colorHex = subject.colorHex
)

private fun Revision.toForm() = RevisionForm(
    editingRevisionId = id,
    subjectId = subjectId,
    topicId = topicId,
    subjectName = subjectName,
    topicTitle = topicTitle,
    scheduledAt = scheduledAt
)

private fun TopicWithSubject.toForm() = SessionForm(
    editingTopicId = topic.id,
    disc = subjectName,
    topic = topic.title,
    dateMillis = topic.studiedAt,
    duration = topic.durationMinutes.toString(),
    notes = topic.notes
)

@Composable
private fun EstudosBottomBar(current: Tab, onSelect: (Tab) -> Unit) {
  Column {
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF3F0FF))
    NavigationBar(containerColor = EstudosSurface, tonalElevation = 0.dp) {
        Tab.entries.forEach { t ->
            val selected = t == current
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(t) },
                icon = { Icon(t.icon, t.label, modifier = Modifier.size(22.dp)) },
                label = {
                    Text(
                        t.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EstudosPrimary,
                    selectedTextColor = EstudosPrimary,
                    unselectedIconColor = EstudosMuted,
                    unselectedTextColor = EstudosMuted,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
  }
}
