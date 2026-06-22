package com.example.gestaoacademica2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.EventCategory
import com.example.gestaoacademica2.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory((requireActivity().application as AcademicApp).repository)
    }

    private lateinit var adapter: EventAdapter

    // IDs dos chips de filtro (criados dinamicamente)
    private var chipIdAll       = View.generateViewId()
    private var chipIdFavorites = View.generateViewId()
    private val chipIdByCategory = mutableMapOf<EventCategory, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()
        setupFilterChips()
        setupFab()
        observeEvents()
    }

    // ── Toolbar com busca ────────────────────────────────────────────────────

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_home)
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.hint_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearch(newText.orEmpty())
                return true
            }
        })
    }

    // ── RecyclerView ─────────────────────────────────────────────────────────

    private fun setupAdapter() {
        adapter = EventAdapter(
            onItemClick = { event ->
                findNavController().navigate(
                    R.id.action_home_to_detail,
                    bundleOf("eventId" to event.id)
                )
            },
            onFavoriteToggle = { event -> viewModel.toggleFavorite(event) }
        )
        binding.recyclerView.adapter = adapter
    }

    // ── Chips de filtro ───────────────────────────────────────────────────────

    private fun setupFilterChips() {
        val group = binding.chipGroupFilter

        fun makeChip(label: String, id: Int): Chip = Chip(requireContext()).apply {
            this.id        = id
            text           = label
            isCheckable    = true
            chipStartPadding = 8f
            chipEndPadding   = 8f
        }

        // "Todos"
        chipIdAll = View.generateViewId()
        val chipAll = makeChip(getString(R.string.filter_all), chipIdAll).apply { isChecked = true }
        group.addView(chipAll)

        // "Favoritos"
        chipIdFavorites = View.generateViewId()
        group.addView(makeChip(getString(R.string.filter_favorites), chipIdFavorites))

        // Uma chip por categoria
        EventCategory.entries.forEach { category ->
            val id = View.generateViewId()
            chipIdByCategory[category] = id
            val chip = makeChip(category.label, id).apply {
                setChipStrokeColorResource(category.colorRes)
                chipStrokeWidth = 1.5f
            }
            group.addView(chip)
        }

        group.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: chipIdAll
            when (checkedId) {
                chipIdAll       -> viewModel.filterByCategory(null)
                chipIdFavorites -> viewModel.filterFavorites(true)
                else -> {
                    val cat = chipIdByCategory.entries
                        .firstOrNull { it.value == checkedId }?.key
                    viewModel.filterByCategory(cat)
                }
            }
        }
    }

    // ── FAB ──────────────────────────────────────────────────────────────────

    private fun setupFab() {
        binding.fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_home_to_addEdit,
                bundleOf("eventId" to -1)
            )
        }
    }

    // ── Observação do StateFlow ───────────────────────────────────────────────

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { events ->
                    adapter.submitList(events)
                    val isEmpty = events.isEmpty()
                    binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                    binding.layoutEmpty.visibility  = if (isEmpty) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
