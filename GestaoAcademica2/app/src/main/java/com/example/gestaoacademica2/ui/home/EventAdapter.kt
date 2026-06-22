package com.example.gestaoacademica2.ui.home

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val onItemClick: (AcademicEvent) -> Unit,
    private val onFavoriteToggle: (AcademicEvent) -> Unit
) : ListAdapter<AcademicEvent, EventAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: AcademicEvent) {
            val ctx = binding.root.context

            // Textos
            binding.tvTitle.text = event.title
            binding.tvSubject.text = event.subject.ifBlank { "Sem disciplina" }
            binding.tvDate.text = formatDate(event.date)

            // Barra lateral colorida por categoria
            val catColor = ContextCompat.getColor(ctx, event.category.colorRes)
            binding.viewCategoryStripe.setBackgroundColor(catColor)

            // Chip de categoria
            binding.chipCategory.text = event.category.label
            binding.chipCategory.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, event.category.containerColorRes))
            binding.chipCategory.setTextColor(catColor)

            // Ponto de prioridade
            binding.viewPriorityDot.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, event.priority.colorRes))
            binding.tvPriority.text = event.priority.label

            // Favorito
            if (event.isFavorite) {
                binding.btnFavorite.setImageResource(R.drawable.ic_star_filled)
                binding.btnFavorite.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.amber))
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_star_outline)
                binding.btnFavorite.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.colorOnSurfaceVariant))
            }

            // Concluído
            binding.tvCompleted.visibility = if (event.isCompleted) View.VISIBLE else View.GONE
            binding.tvTitle.paintFlags = if (event.isCompleted)
                binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                binding.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            // Cliques
            binding.root.setOnClickListener { onItemClick(event) }
            binding.btnFavorite.setOnClickListener { onFavoriteToggle(event) }
        }
    }

    private fun formatDate(timestamp: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(timestamp))

    companion object DiffCallback : DiffUtil.ItemCallback<AcademicEvent>() {
        override fun areItemsTheSame(old: AcademicEvent, new: AcademicEvent) = old.id == new.id
        override fun areContentsTheSame(old: AcademicEvent, new: AcademicEvent) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}
