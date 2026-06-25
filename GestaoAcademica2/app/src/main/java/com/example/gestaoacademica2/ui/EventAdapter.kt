package com.example.gestaoacademica2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.databinding.ItemEventAgendaBinding
import com.example.gestaoacademica2.databinding.ItemEventCardBinding

/**
 * Adapter de eventos para RecyclerView (exigência do enunciado: listagem dinâmica
 * com RecyclerView). Suporta duas variantes de cartão: [Variant.CARD] (Hoje,
 * Próximos, Favoritos) e [Variant.AGENDA] (com linha de local + botão de editar).
 */
class EventAdapter(
    private val variant: Variant,
    private val onFav: (AcademicEvent) -> Unit,
    private val onPrimary: (AcademicEvent) -> Unit,
    private val forceAmberStar: Boolean = false,
    private val meta: (AcademicEvent) -> String = { "" }
) : ListAdapter<AcademicEvent, RecyclerView.ViewHolder>(Diff) {

    enum class Variant { CARD, AGENDA }

    inner class CardHolder(val b: ItemEventCardBinding) : RecyclerView.ViewHolder(b.root)
    inner class AgendaHolder(val b: ItemEventAgendaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val gap = (8 * parent.resources.displayMetrics.density).toInt()
        return when (variant) {
            Variant.CARD -> CardHolder(ItemEventCardBinding.inflate(inflater, parent, false)).also {
                (it.b.root.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = gap
            }
            Variant.AGENDA -> AgendaHolder(ItemEventAgendaBinding.inflate(inflater, parent, false)).also {
                (it.b.root.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = gap
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        when (holder) {
            is CardHolder -> EventCardBinder.bindCard(
                holder.b, event, meta(event), forceAmberStar,
                onFav = onFav, onClick = onPrimary
            )
            is AgendaHolder -> EventCardBinder.bindAgenda(
                holder.b, event, onFav = onFav, onEdit = onPrimary
            )
        }
    }

    companion object Diff : DiffUtil.ItemCallback<AcademicEvent>() {
        override fun areItemsTheSame(old: AcademicEvent, new: AcademicEvent) = old.id == new.id
        override fun areContentsTheSame(old: AcademicEvent, new: AcademicEvent) = old == new
    }
}
