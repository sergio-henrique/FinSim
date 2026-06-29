package com.finsim.app.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Paleta de cores FinSim
// Escolhida para transmitir clareza e confiança ao público jovem (10-18 anos).
// ---------------------------------------------------------------------------
internal val Blue60 = Color(0xFF1A73E8)   // Azul educativo — ação primária
internal val Green50 = Color(0xFF34A853)  // Verde positivo — ganho / saldo positivo
internal val Orange40 = Color(0xFFFBBC04) // Laranja alerta  — atenção / risco
internal val BackgroundLight = Color(0xFFF8F9FA) // Fundo claro — leitura confortável
internal val SurfaceWhite = Color(0xFFFFFFFF)
internal val OnPrimary = Color(0xFFFFFFFF)
internal val OnBackground = Color(0xFF1C1B1F)
internal val OnSurface = Color(0xFF1C1B1F)

/**
 * Esquema de cores claro do FinSim.
 *
 * O dark mode não faz parte do MVP — o foco é legibilidade para jovens
 * em ambientes escolares e domésticos com luz ambiente.
 */
private val FinSimColorScheme = lightColorScheme(
    primary = Blue60,
    onPrimary = OnPrimary,
    secondary = Green50,
    onSecondary = OnPrimary,
    tertiary = Orange40,
    onTertiary = OnBackground,
    background = BackgroundLight,
    onBackground = OnBackground,
    surface = SurfaceWhite,
    onSurface = OnSurface,
)

/**
 * Tipografia padrão do FinSim.
 *
 * Usa as fontes padrão do Material3 para o MVP.
 * Fontes customizadas podem ser adicionadas em iterações futuras
 * sem impacto na estrutura do tema.
 */
private val FinSimTypography = Typography()

/**
 * Tema principal do FinSim.
 *
 * Envolve toda a árvore de composables com [MaterialTheme] configurado
 * com as cores e tipografia educativas do projeto.
 *
 * Uso:
 * ```kotlin
 * FinSimTheme {
 *     // Conteúdo da tela
 * }
 * ```
 */
@Composable
fun FinSimTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FinSimColorScheme,
        typography = FinSimTypography,
        content = content,
    )
}
