package com.example.gestaoacademica2.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.EventCategory
import com.example.gestaoacademica2.data.model.Priority
import com.example.gestaoacademica2.databinding.FragmentAddEditEventBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditViewModel by viewModels {
        AddEditViewModel.Factory((requireActivity().application as AcademicApp).repository)
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId", -1) ?: -1

        setupToolbar(eventId)
        setupCategoryDropdown()
        setupPriorityChips()
        setupDatePicker()
        setupSaveButton()

        if (eventId != -1) {
            viewModel.loadEvent(eventId)
            observeViewModel()
        }
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────

    private fun setupToolbar(eventId: Int) {
        binding.toolbar.title = getString(
            if (eventId == -1) R.string.title_new_event else R.string.title_edit_event
        )
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    // ── Dropdown de categoria ─────────────────────────────────────────────────

    private fun setupCategoryDropdown() {
        val labels = EventCategory.entries.map { it.label }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, labels)
        binding.actvCategory.setAdapter(adapter)
        binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
            viewModel.category.value = EventCategory.entries[position]
            binding.tilCategory.error = null
        }
    }

    // ── Chips de prioridade ───────────────────────────────────────────────────

    private fun setupPriorityChips() {
        binding.chipGroupPriority.setOnCheckedStateChangeListener { _, checkedIds ->
            val priority = when (checkedIds.firstOrNull()) {
                R.id.chipPriorityLow    -> Priority.BAIXA
                R.id.chipPriorityHigh   -> Priority.ALTA
                else                    -> Priority.MEDIA
            }
            viewModel.priority.value = priority
        }
    }

    // ── DatePicker ────────────────────────────────────────────────────────────

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        // Sem constraints — permite datas passadas e futuras
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.label_date))
            .setSelection(viewModel.date.value ?: MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.addOnPositiveButtonClickListener { selectionUtc ->
            // MaterialDatePicker retorna UTC midnight; ajustar para não perder 1 dia em UTC-X
            val utc = TimeZone.getTimeZone("UTC")
            val cal = java.util.Calendar.getInstance(utc)
            cal.timeInMillis = selectionUtc
            // Converter para timestamp local (meia-noite local)
            val local = java.util.Calendar.getInstance().apply {
                set(cal.get(java.util.Calendar.YEAR),
                    cal.get(java.util.Calendar.MONTH),
                    cal.get(java.util.Calendar.DAY_OF_MONTH),
                    0, 0, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            viewModel.date.value = local.timeInMillis
            binding.btnSelectDate.text = dateFormatter.format(Date(local.timeInMillis))
            binding.tilDateError.visibility = View.GONE
        }

        picker.show(parentFragmentManager, "date_picker")
    }

    // ── Observar ViewModel para edição ────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.title.observe(viewLifecycleOwner) { binding.etTitle.setText(it) }
        viewModel.subject.observe(viewLifecycleOwner) { binding.etSubject.setText(it) }
        viewModel.description.observe(viewLifecycleOwner) { binding.etDescription.setText(it) }
        viewModel.location.observe(viewLifecycleOwner) { binding.etLocation.setText(it) }

        viewModel.category.observe(viewLifecycleOwner) { cat ->
            if (cat != null) binding.actvCategory.setText(cat.label, false)
        }

        viewModel.priority.observe(viewLifecycleOwner) { priority ->
            val chipId = when (priority) {
                Priority.BAIXA -> R.id.chipPriorityLow
                Priority.ALTA  -> R.id.chipPriorityHigh
                else           -> R.id.chipPriorityMedium
            }
            binding.chipGroupPriority.check(chipId)
        }

        viewModel.date.observe(viewLifecycleOwner) { ts ->
            if (ts != null) {
                binding.btnSelectDate.text = dateFormatter.format(Date(ts))
            }
        }
    }

    // ── Salvar ────────────────────────────────────────────────────────────────

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            // Sincronizar campos de texto com o ViewModel antes de salvar
            viewModel.title.value       = binding.etTitle.text?.toString()?.trim() ?: ""
            viewModel.subject.value     = binding.etSubject.text?.toString()?.trim() ?: ""
            viewModel.description.value = binding.etDescription.text?.toString()?.trim() ?: ""
            viewModel.location.value    = binding.etLocation.text?.toString()?.trim() ?: ""

            when (val error = viewModel.save()) {
                null -> findNavController().navigateUp()  // sucesso
                "title"    -> binding.tilTitle.error    = getString(R.string.error_title_empty)
                "category" -> binding.tilCategory.error = getString(R.string.error_category_empty)
                "date"     -> {
                    binding.tilDateError.text       = getString(R.string.error_date_empty)
                    binding.tilDateError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
