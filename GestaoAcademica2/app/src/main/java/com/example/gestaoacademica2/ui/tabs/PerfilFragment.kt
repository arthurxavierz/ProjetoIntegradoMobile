package com.example.gestaoacademica2.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gestaoacademica2.AcademicApp
import com.example.gestaoacademica2.databinding.FragmentPerfilBinding
import com.example.gestaoacademica2.ui.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity().application as AcademicApp).repository, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogout.setOnClickListener { viewModel.logout() }
        binding.rowCourse.setOnClickListener {
            editField("Curso", binding.tvCourseValue.text.toString()) { viewModel.setCourse(it) }
        }
        binding.rowSemester.setOnClickListener {
            editField("Semestre", binding.tvSemesterValue.text.toString()) { viewModel.setSemester(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.settings.collect { s ->
                        binding.tvProfileName.text = s.userName.ifBlank { "Usuário" }
                        binding.tvProfileEmail.text = s.userEmail
                        binding.tvProfileSubtitle.text = "${s.course} · ${s.semester}"
                        binding.tvCourseValue.text = s.course
                        binding.tvSemesterValue.text = s.semester
                    }
                }
                launch { viewModel.allEvents.collect { binding.tvTotalEvents.text = it.size.toString() } }
                launch { viewModel.favoriteEvents.collect { binding.tvFavStat.text = it.size.toString() } }
            }
        }
    }

    /** Diálogo simples de edição de um campo de texto do perfil. */
    private fun editField(label: String, current: String, onSave: (String) -> Unit) {
        val input = EditText(requireContext()).apply {
            setText(current)
            setSelection(text.length)
            setSingleLine()
        }
        val pad = (20 * resources.displayMetrics.density).toInt()
        val container = FrameLayout(requireContext()).apply {
            setPadding(pad, pad / 2, pad, 0)
            addView(input)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(label)
            .setView(container)
            .setPositiveButton("Salvar") { _, _ ->
                val value = input.text.toString().trim()
                if (value.isNotEmpty()) onSave(value)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
