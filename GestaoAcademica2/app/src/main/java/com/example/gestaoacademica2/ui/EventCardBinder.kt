package com.example.gestaoacademica2.ui

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.example.gestaoacademica2.R
import com.example.gestaoacademica2.data.model.AcademicEvent
import com.example.gestaoacademica2.databinding.ItemEventAgendaBinding
import com.example.gestaoacademica2.databinding.ItemEventCardBinding
import com.example.gestaoacademica2.util.DateUtils

/**
 * Pinta os cartões de evento (variantes "card" e "agenda") replicando as cores,
 * ícones e estrelas do protótipo.
 */
object EventCardBinder {

    private fun starColor(fav: Boolean) = if (fav) R.color.star_amber else R.color.chevron_slate

    /** Variante usada em Hoje / Próximos / Favoritos. */
    fun bindCard(
        b: ItemEventCardBinding,
        event: AcademicEvent,
        meta: String,
        forceAmberStar: Boolean = false,
        onFav: (AcademicEvent) -> Unit,
        onClick: ((AcademicEvent) -> Unit)? = null
    ) {
        val ctx = b.root.context
        val color = ContextCompat.getColor(ctx, event.type.colorRes)
        val bg = ContextCompat.getColor(ctx, event.type.bgColorRes)

        b.tvTitle.text = event.name
        b.tvType.text = event.type.label
        b.tvType.setTextColor(color)
        b.tvType.background.setTintCompat(bg)
        b.tvMeta.text = meta

        b.ivIcon.setImageResource(event.type.iconRes)
        b.ivIcon.imageTintList = ColorStateList.valueOf(color)
        b.iconBg.background.setTintCompat(bg)
        b.viewStripe.background.setTintCompat(color)

        val starRes = if (forceAmberStar) R.color.star_amber else starColor(event.isFavorite)
        b.btnFav.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, starRes))
        b.btnFav.setOnClickListener { onFav(event) }
        onClick?.let { cb -> b.eventCard.setOnClickListener { cb(event) } }
    }

    /** Variante da Agenda (com linha de local e botão de editar). */
    fun bindAgenda(
        b: ItemEventAgendaBinding,
        event: AcademicEvent,
        onFav: (AcademicEvent) -> Unit,
        onEdit: (AcademicEvent) -> Unit
    ) {
        val ctx = b.root.context
        val color = ContextCompat.getColor(ctx, event.type.colorRes)
        val bg = ContextCompat.getColor(ctx, event.type.bgColorRes)

        b.tvTitle.text = event.name
        b.tvType.text = event.type.label
        b.tvType.setTextColor(color)
        b.tvType.background.setTintCompat(bg)
        b.tvMeta.text = "${DateUtils.shortLabel(event.date)} · ${event.time}"
        b.tvLocation.text = event.location

        b.ivIcon.setImageResource(event.type.iconRes)
        b.ivIcon.imageTintList = ColorStateList.valueOf(color)
        b.iconBg.background.setTintCompat(bg)
        b.viewStripe.background.setTintCompat(color)

        b.btnFav.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(ctx, starColor(event.isFavorite)))
        b.btnFav.setOnClickListener { onFav(event) }
        // A edição abre tocando no card inteiro.
        b.eventCard.setOnClickListener { onEdit(event) }
    }

    private fun android.graphics.drawable.Drawable.setTintCompat(color: Int) {
        mutate()
        setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}
