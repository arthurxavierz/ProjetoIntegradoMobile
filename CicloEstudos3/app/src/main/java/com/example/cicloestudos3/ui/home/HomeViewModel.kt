package com.example.cicloestudos3.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cicloestudos3.data.model.Revision
import com.example.cicloestudos3.data.model.SubjectWithStats
import com.example.cicloestudos3.data.model.Topic
import com.example.cicloestudos3.data.model.TopicWithSubject
import com.example.cicloestudos3.data.repository.StudyRepository
import kotlinx.coroutines.flow.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class HomeUiState(
    val greeting: String                   = "Bom dia",
    val todayRevisions: List<Revision>     = emptyList(),
    val upcomingRevisions: List<Revision>  = emptyList(),
    val recentTopics: List<TopicWithSubject> = emptyList(),
    val subjects: List<SubjectWithStats>   = emptyList(),
    val streak: Int                        = 0,
    val topicsThisWeek: Int                = 0,
    val totalMinutesThisWeek: Int          = 0
)

class HomeViewModel(private val repo: StudyRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repo.getTodayRevisions(),
        repo.getUpcomingRevisions(5),
        repo.getRecentTopics(15),
        repo.subjectsWithStats,
        repo.getTopicsSince(sevenDaysAgo())
    ) { todayRevs, upcoming, recent, subjects, weekTopics ->
        HomeUiState(
            greeting             = greeting(),
            todayRevisions       = todayRevs,
            upcomingRevisions    = upcoming,
            recentTopics         = recent,
            subjects             = subjects,
            streak               = calculateStreak(weekTopics),
            topicsThisWeek       = weekTopics.size,
            totalMinutesThisWeek = weekTopics.sumOf { it.durationMinutes }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun greeting(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11  -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else      -> "Boa noite"
        }
    }

    private fun calculateStreak(weekTopics: List<Topic>): Int {
        val today    = todayMidnight()
        val studyDays = weekTopics
            .map { (it.studiedAt / 86_400_000L) * 86_400_000L }
            .toSet()
        var streak = 0
        var day    = today
        while (studyDays.contains(day)) {
            streak++
            day -= 86_400_000L
        }
        return streak
    }

    private fun todayMidnight(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun sevenDaysAgo(): Long =
        System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)

    class Factory(private val repo: StudyRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repo) as T
    }
}
