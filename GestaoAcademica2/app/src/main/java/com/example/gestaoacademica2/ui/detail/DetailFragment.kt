package com.example.gestaoacademica2.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.databinding.FragmentEventDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModel.Factory((requireActivity().application as AcademicApp).repository)
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId", -1) ?: -1

        setupToolbar(eventId)
        observeEvent()

        if (eventId != -1) viewModel.loadEvent(eventId)
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────

    private fun setupToolbar(eventId: Int) {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.inflateMenu(R.menu.menu_detail)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(
                        R.id.action_detail_to_addEdit,
                        bundleOf("eventId" to eventId)
                    )
                    true
                }
                R.id.action_delete -> {
                    confirmDelete()
                    true
                }
                else -> false
            }
        }
    }

    // ── Observar evento ───────────────────────────────────────────────────────

    private fun observeEvent() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event ?: return@observe
            bindEvent(event)
        }
    }

    private fun bindEvent(event: AcademicEvent) {
        val ctx = requireContext()

        // ── Cabeçalho colorido ─────────────────────────────────────────────
        val catColor = ContextCompat.getColor(ctx, event.category.colorRes)
        val catContainerColor = ContextCompat.getColor(ctx, event.category.containerColorRes)
        binding.cardHeader.setCardBackgroundColor(catColor)

        binding.chipDetailCategory.text = event.category.label
        binding.chipDetailCategory.chipBackgroundColor = ColorStateList.valueOf(catContainerColor)
        binding.chipDetailCategory.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))

        binding.tvDetailTitle.text = event.title
        binding.tvDetailSubject.text = event.subject.ifBlank { "" }
        binding.tvDetailSubject.visibility = if (event.subject.isBlank()) View.GONE else View.VISIBLE

        // ── Linhas de informação ───────────────────────────────────────────

        // Data
        binding.rowDate.tvRowLabel.text = getString(R.string.label_detail_date)
        binding.rowDate.tvRowValue.text = dateFormatter.format(Date(event.date))

        // Prioridade
        val prioColor = ContextCompat.getColor(ctx, event.priority.colorRes)
        binding.rowPriority.tvRowLabel.text = getString(R.string.label_detail_priority)
        binding.rowPriority.tvRowValue.text = event.priority.label
        binding.rowPriority.tvRowValue.setTextColor(prioColor)

        // Local (condicional)
        if (event.location.isNotBlank()) {
            binding.dividerLocation.visibility = View.VISIBLE
            binding.rowLocation.root.visibility = View.VISIBLE
            binding.rowLocation.tvRowLabel.text = getString(R.string.label_detail_location)
            binding.rowLocation.tvRowValue.text = event.location
        } else {
            binding.dividerLocation.visibility = View.GONE
            binding.rowLocation.root.visibility = View.GONE
        }

        // Descrição (condicional)
        if (event.description.isNotBlank()) {
            binding.dividerDescription.visibility = View.VISIBLE
            binding.rowDescription.root.visibility = View.VISIBLE
            binding.rowDescription.tvRowLabel.text = getString(R.string.label_detail_description)
            binding.rowDescription.tvRowValue.text = event.description
        } else {
            binding.dividerDescription.visibility = View.GONE
            binding.rowDescription.root.visibility = View.GONE
        }

        // ── Badge "Concluído" ──────────────────────────────────────────────
        binding.cardCompleted.visibility = if (event.isCompleted) View.VISIBLE else View.GONE

        // ── Botão Favoritar ────────────────────────────────────────────────
        updateFavoriteButton(event.isFavorite)
        binding.btnToggleFavorite.setOnClickListener { viewModel.toggleFavorite() }

        // ── Botão Concluir ─────────────────────────────────────────────────
        updateCompleteButton(event.isCompleted)
        binding.btnToggleCompleted.setOnClickListener { viewModel.toggleCompleted() }
    }

    private fun updateFavoriteButton(isFav: Boolean) {
        if (isFav) {
            binding.btnToggleFavorite.text = getString(R.string.btn_favorite_remove)
            binding.btnToggleFavorite.setIconResource(R.drawable.ic_star_filled)
        } else {
            binding.btnToggleFavorite.text = getString(R.string.btn_favorite_add)
            binding.btnToggleFavorite.setIconResource(R.drawable.ic_star_outline)
        }
    }

    private fun updateCompleteButton(isCompleted: Boolean) {
        binding.btnToggleCompleted.text = getString(
            if (isCompleted) R.string.btn_uncomplete else R.string.btn_complete
        )
    }

    // ── Confirmação de exclusão ────────────────────────────────────────────────

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_msg)
            .setNegativeButton(R.string.btn_cancel, null)
            .setPositiveButton(R.string.btn_confirm) { _, _ ->
                viewModel.delete()
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
