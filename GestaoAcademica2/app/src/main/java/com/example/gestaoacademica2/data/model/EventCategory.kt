package com.example.gestaoacademica2.data.model

import androidx.annotation.ColorRes
import com.example.gestaoacademica2.R

enum class EventCategory(
    val label: String,
    @ColorRes val colorRes: Int,
    @ColorRes val containerColorRes: Int
) {
    PROVA    ("Prova",    R.color.cat_prova,    R.color.cat_prova_container),
    TRABALHO ("Trabalho", R.color.cat_trabalho, R.color.cat_trabalho_container),
    PALESTRA ("Palestra", R.color.cat_palestra, R.color.cat_palestra_container),
    AULA     ("Aula",     R.color.cat_aula,     R.color.cat_aula_container),
    OUTRO    ("Outro",    R.color.cat_outro,    R.color.cat_outro_container)
}
