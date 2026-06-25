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
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.databinding.FragmentFavoritosBinding
import com.example.gestaoacademica2.ui.EventAdapter
import com.example.gestaoacademica2.ui.MainViewModel
import com.example.gestaoacademica2.ui.addedit.AddEditBottomSheet
import com.example.gestaoacademica2.util.DateUtils
import kotlinx.coroutines.launch

class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory((requireActivity().application as AcademicApp).repository, requireContext())
    }

    private val adapter by lazy {
        EventAdapter(
            variant = EventAdapter.Variant.CARD,
            onFav = { viewModel.toggleFavorite(it) },
            onPrimary = { openEdit(it) },
            forceAmberStar = true,
            meta = { ev -> "${DateUtils.shortLabel(ev.date)} · ${ev.time}" }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvFav.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteEvents.collect { list ->
                    binding.tvFavCount.text = "${list.size} eventos salvos"
                    binding.cardEmptyFav.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    adapter.submitList(list)
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
