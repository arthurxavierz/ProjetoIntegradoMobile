package com.example.financaspessoais1.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.financaspessoais1.ui.theme.*

/**
 * Categorias espelhando o objeto CATS do protótipo: id, nome, cor, fundo,
 * teto (limite) e ícone. A ordem é a mesma usada nas listas do protótipo.
 */
enum class ExpenseCategory(
    val id: String,
    val label: String,
    val color: Color,
    val bgColor: Color,
    val limit: Double,
    val icon: ImageVector
) {
    ALIMENTACAO("alimentacao", "Alimentação", CatAlimentacaoColor, CatAlimentacaoBg, 600.0, Icons.Rounded.Restaurant),
    TRANSPORTE ("transporte",  "Transporte",  CatTransporteColor,  CatTransporteBg,  200.0, Icons.Rounded.DirectionsBus),
    LAZER      ("lazer",       "Lazer",       CatLazerColor,       CatLazerBg,       300.0, Icons.Rounded.MusicNote),
    SAUDE      ("saude",       "Saúde",       CatSaudeColor,       CatSaudeBg,       200.0, Icons.Rounded.Favorite),
    EDUCACAO   ("educacao",    "Educação",    CatEducacaoColor,    CatEducacaoBg,    400.0, Icons.Rounded.School),
    OUTROS     ("outros",      "Outros",      CatOutrosColor,      CatOutrosBg,      300.0, Icons.Rounded.MoreHoriz);

    companion object {
        fun fromId(id: String): ExpenseCategory = entries.firstOrNull { it.id == id } ?: OUTROS
    }
}
