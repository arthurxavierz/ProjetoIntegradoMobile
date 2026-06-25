package com.example.gestaoacademica2.ui.tabs

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.data.model.EventType
import com.example.gestaoacademica2.databinding.FragmentAgendaBinding
import com.example.gestaoacademica2.ui.EventAdapter
import com.example.gestaoacademica2.ui.MainViewModel
import com.example.gestaoacademica2.ui.addedit.AddEditBottomSheet
import com.example.gestaoacademica2.util.DateUtils
import kotlinx.coroutines.launch

class AgendaFragment : Fragment() {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity().application as AcademicApp).repository, requireContext())
    }

    private val adapter by lazy {
        EventAdapter(
            variant = EventAdapter.Variant.AGENDA,
            onFav = { viewModel.toggleFavorite(it) },
            onPrimary = { AddEditBottomSheet.newInstance(it.id).show(parentFragmentManager, "addEdit") }
        )
    }

    // (rótulo, tipo) — null = "Todos"
    private val filters: List<Pair<String, EventType?>> = listOf(
        "Todos" to null,
        "Prova" to EventType.PROVA,
        "Trabalho" to EventType.TRABALHO,
        "Palestra" to EventType.PALESTRA,
        "Reunião" to EventType.REUNIAO,
        "Atividade" to EventType.ATIVIDADE,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvMonthBadge.text = DateUtils.monthBadge(DateUtils.today())
        binding.rvEvents.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.filter.collect { buildChips(it) } }
                launch { viewModel.filteredEvents.collect { render(it) } }
            }
        }
    }

    private fun buildChips(selected: EventType?) {
        binding.chipContainer.removeAllViews()
        filters.forEach { (label, type) ->
            binding.chipContainer.addView(makeChip(label, type, type == selected))
        }
    }

    private fun makeChip(label: String, type: EventType?, active: Boolean): TextView {
        val ctx = requireContext()
        return TextView(ctx).apply {
            text = label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = ResourcesCompat.getFont(ctx, R.font.dm_sans)?.let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
                    android.graphics.Typeface.create(it, 600, false) else it
            }
            gravity = Gravity.CENTER
            setPadding(dp(14), dp(7), dp(14), dp(7))
            background = ContextCompat.getDrawable(ctx, R.drawable.bg_filter_chip)?.mutate()?.apply {
                val bg = if (active) R.color.brand_emerald else R.color.brand_green_50
                setColorFilter(ContextCompat.getColor(ctx, bg), PorterDuff.Mode.SRC_IN)
            }
            setTextColor(
                ContextCompat.getColor(ctx, if (active) R.color.white else R.color.text_gray)
            )
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.marginEnd = dp(8)
            layoutParams = lp
            setOnClickListener { viewModel.setFilter(type) }
        }
    }

    private fun render(events: List<AcademicEvent>) {
        adapter.submitList(events)
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
