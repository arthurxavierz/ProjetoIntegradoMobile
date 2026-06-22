package com.example.gestaoacademica2.data.model

import androidx.annotation.ColorRes
import com.example.gestaoacademica2.R

enum class Priority(
    val label: String,
    @ColorRes val colorRes: Int,
    @ColorRes val containerColorRes: Int
) {
    BAIXA("Baixa", R.color.priority_low,    R.color.priority_low_container),
    MEDIA("Média", R.color.priority_medium, R.color.priority_medium_container),
    ALTA ("Alta",  R.color.priority_high,   R.color.priority_high_container)
}
