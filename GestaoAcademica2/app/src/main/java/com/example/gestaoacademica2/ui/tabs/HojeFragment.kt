package com.example.gestaoacademica2.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.databinding.FragmentHojeBinding
import com.example.gestaoacademica2.ui.EventAdapter
import com.example.gestaoacademica2.ui.MainViewModel
import com.example.gestaoacademica2.ui.addedit.AddEditBottomSheet
import com.example.gestaoacademica2.util.DateUtils
import kotlinx.coroutines.launch

class HojeFragment : Fragment() {

    private var _binding: FragmentHojeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity().application as AcademicApp).repository, requireContext())
    }

    private val todayAdapter by lazy {
        EventAdapter(
            variant = EventAdapter.Variant.CARD,
            onFav = { viewModel.toggleFavorite(it) },
            onPrimary = { openEdit(it) },
            meta = { ev -> listOf(ev.time, ev.location).filter { it.isNotBlank() }.joinToString("    ") }
        )
    }

    private val upcomingAdapter by lazy {
        EventAdapter(
            variant = EventAdapter.Variant.CARD,
            onFav = { viewModel.toggleFavorite(it) },
            onPrimary = { openEdit(it) },
            meta = { ev -> "${DateUtils.shortLabel(ev.date)}    ${ev.time}" }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHojeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvTodayLabel.text = DateUtils.longLabel(DateUtils.today())
        binding.statToday.tvStatLabel.text = getString(R.string.stat_today)
        binding.statUpcoming.tvStatLabel.text = getString(R.string.stat_upcoming)
        binding.statFav.tvStatLabel.text = getString(R.string.stat_fav)
        binding.rvToday.adapter = todayAdapter
        binding.rvUpcoming.adapter = upcomingAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.settings.collect { s ->
                        val first = s.userName.trim().substringBefore(' ').ifBlank { "estudante" }
                        binding.tvGreeting.text = getString(R.string.greeting_user, first)
                    }
                }
                launch {
                    viewModel.todayEvents.collect { list ->
                        binding.statToday.tvStatNumber.text = list.size.toString()
                        binding.cardEmptyToday.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        todayAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.upcomingEvents.collect { list ->
                        binding.statUpcoming.tvStatNumber.text = list.size.toString()
                        upcomingAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.favoriteEvents.collect { binding.statFav.tvStatNumber.text = it.size.toString() }
                }
            }
        }
    }

    private fun openEdit(event: AcademicEvent) {
        AddEditBottomSheet.newInstance(event.id).show(parentFragmentManager, "addEdit")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
