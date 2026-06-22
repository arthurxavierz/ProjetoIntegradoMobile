package com.example.financaspessoais1.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val label: String,
    val color: Color,
    val icon: ImageVector
) {
    ALIMENTACAO("Alimentação", Color(0xFFF97316), Icons.Rounded.Fastfood),
    TRANSPORTE("Transporte",   Color(0xFF3B82F6), Icons.Rounded.DirectionsCar),
    SAUDE("Saúde",             Color(0xFFEF4444), Icons.Rounded.LocalHospital),
    LAZER("Lazer",             Color(0xFF8B5CF6), Icons.Rounded.Celebration),
    EDUCACAO("Educação",       Color(0xFF14B8A6), Icons.Rounded.MenuBook),
    MORADIA("Moradia",         Color(0xFFF59E0B), Icons.Rounded.Home),
    OUTROS("Outros",           Color(0xFF6B7280), Icons.Rounded.GridView)
}
