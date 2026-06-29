package com.finsim.app.domain.model

/**
 * Perfil local do usuário no simulador.
 *
 * Não armazena dados sensíveis. O nome é um apelido livre,
 * não vinculado a identidade real. A faixa etária calibra
 * apenas o conteúdo educativo exibido.
 */
data class UserProfile(
    val id: Long = 0,
    val name: String,
    val ageRange: AgeRange,
    val monthlyIncome: Long,    // centavos
    val currentMonth: Int,
    val createdAt: Long         // epoch millis
)

enum class AgeRange { CHILD, TEEN, YOUNG }
