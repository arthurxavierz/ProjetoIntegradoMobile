package com.example.gestaoacademica2.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.gestaoacademica2.R

/**
 * Tipos de evento do protótipo. Cada tipo carrega seu rótulo, ícone e o par de
 * cores (acento + fundo claro) usados nos cartões e chips.
 */
enum class EventType(
    val key: String,
    val label: String,
    @ColorRes val colorRes: Int,
    @ColorRes val bgColorRes: Int,
    @DrawableRes val iconRes: Int
) {
    PROVA     ("prova",     "Prova",     R.color.type_prova,     R.color.type_prova_bg,     R.drawable.ic_type_prova),
    TRABALHO  ("trabalho",  "Trabalho",  R.color.type_trabalho,  R.color.type_trabalho_bg,  R.drawable.ic_type_trabalho),
    PALESTRA  ("palestra",  "Palestra",  R.color.type_palestra,  R.color.type_palestra_bg,  R.drawable.ic_type_palestra),
    REUNIAO   ("reuniao",   "Reunião",   R.color.type_reuniao,   R.color.type_reuniao_bg,   R.drawable.ic_type_reuniao),
    ATIVIDADE ("atividade", "Atividade", R.color.type_atividade, R.color.type_atividade_bg, R.drawable.ic_type_atividade);

    companion object {
        fun fromKey(key: String): EventType = entries.firstOrNull { it.key == key } ?: ATIVIDADE
    }
}
