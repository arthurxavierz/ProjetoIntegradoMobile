package com.example.gestaoacademica2.ui.addedit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.data.model.EventType
import com.example.gestaoacademica2.databinding.BottomSheetEventBinding
import com.example.gestaoacademica2.ui.MainViewModel
import com.example.gestaoacademica2.util.DateUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Drawer de cadastro/edição de evento, espelhando o bottom-sheet do protótipo.
 * [newInstance] recebe o id do evento a editar, ou null para criar um novo.
 */
class AddEditBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity().application as AcademicApp).repository, requireContext())
    }

    private var editing: AcademicEvent? = null

    // Estado do formulário
    private var selectedType: EventType = EventType.PROVA
    private var dateIso: String = DateUtils.today()
    private var timeStr: String = "10:00"

    override fun getTheme(): Int = R.style.ThemeOverlay_GestaoAcademica2_BottomSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Edge-to-edge (API 36): faz o drawer branco ir até a borda inferior, sem o
        // vão da barra de gestos; o conteúdo recebe padding do inset de navegação.
        dialog?.window?.let { w ->
            WindowCompat.setDecorFitsSystemWindows(w, false)
            // Barra de gestos branca p/ continuar o drawer sem vão cinza.
            w.navigationBarColor = Color.WHITE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                w.isNavigationBarContrastEnforced = false
            }
            WindowCompat.getInsetsController(w, w.decorView).isAppearanceLightNavigationBars = true
        }
        // Material adiciona padding-bottom (inset) no container do sheet, deixando o
        // branco parar acima da barra de gestos. Zeramos isso p/ o branco ir até a borda.
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { c ->
            c.fitsSystemWindows = false
            ViewCompat.setOnApplyWindowInsetsListener(c) { v, insets ->
                v.updatePadding(bottom = 0)
                insets
            }
        }
        // ...e jogamos o inset de navegação para o conteúdo interno (botões acima dos gestos).
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.updatePadding(bottom = bottom)
            insets
        }
        dialog?.window?.decorView?.let { ViewCompat.requestApplyInsets(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val editId = arguments?.getLong(ARG_ID, -1L) ?: -1L
        editing = if (editId >= 0) viewModel.allEvents.value.firstOrNull { it.id == editId } else null

        setupTypeDropdown()
        setupPickers()
        bindInitialState()
        setupButtons()
    }

    private fun bindInitialState() {
        val e = editing
        if (e != null) {
            binding.tvDrawerTitle.setText(R.string.drawer_edit)
            binding.etName.setText(e.name)
            binding.etLocation.setText(e.location)
            selectedType = e.type
            dateIso = e.date
            timeStr = e.time
            // botões: Excluir + Salvar Alterações (1 : 2)
            binding.btnDelete.visibility = View.VISIBLE
            (binding.btnSave.layoutParams as LinearLayout.LayoutParams).weight = 2f
            binding.btnSave.setText(R.string.btn_save_changes)
            binding.btnSave.textSize = 14f
        } else {
            binding.tvDrawerTitle.setText(R.string.drawer_new)
            binding.btnDelete.visibility = View.GONE
            (binding.btnSave.layoutParams as LinearLayout.LayoutParams).weight = 1f
            binding.btnSave.setText(R.string.btn_add_event)
            binding.btnSave.textSize = 15f
        }
        binding.spType.setText(selectedType.label, false)
        binding.etDate.setText(DateUtils.displayDate(dateIso))
        binding.etTime.setText(timeStr)
    }

    private fun setupTypeDropdown() {
        val labels = EventType.entries.map { it.label }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
        binding.spType.setAdapter(adapter)
        binding.spType.setOnClickListener { binding.spType.showDropDown() }
        binding.spType.setOnItemClickListener { _, _, position, _ ->
            selectedType = EventType.entries[position]
        }
    }

    private fun setupPickers() {
        binding.etDate.setOnClickListener {
            val (y, m, d) = DateUtils.parts(dateIso)
            DatePickerDialog(requireContext(), { _, year, month, day ->
                dateIso = DateUtils.toIso(year, month, day)
                binding.etDate.setText(DateUtils.displayDate(dateIso))
            }, y, m, d).show()
        }
        binding.etTime.setOnClickListener {
            val parts = timeStr.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 10
            val min = parts.getOrNull(1)?.toIntOrNull() ?: 0
            TimePickerDialog(requireContext(), { _, hour, minute ->
                timeStr = "%02d:%02d".format(hour, minute)
                binding.etTime.setText(timeStr)
            }, h, min, true).show()
        }
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnDelete.setOnClickListener {
            editing?.let { viewModel.deleteEvent(it.id) }
            dismiss()
        }
        binding.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tvError.setText(R.string.err_name_required)
            binding.tvError.visibility = View.VISIBLE
            return
        }
        val location = binding.etLocation.text.toString().trim()
        val e = editing
        if (e == null) {
            viewModel.addEvent(name, selectedType, dateIso, timeStr, location)
        } else {
            viewModel.updateEvent(
                e.copy(name = name, type = selectedType, date = dateIso, time = timeStr, location = location)
            )
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ID = "event_id"

        fun newInstance(eventId: Long?): AddEditBottomSheet =
            AddEditBottomSheet().apply {
                arguments = Bundle().apply { putLong(ARG_ID, eventId ?: -1L) }
            }
    }
}
