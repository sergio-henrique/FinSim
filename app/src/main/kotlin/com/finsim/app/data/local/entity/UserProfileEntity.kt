package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para persistência do perfil local do usuário.
 *
 * Nenhum dado pessoal sensível é coletado além do nome (apelido
 * ou nome fictício a critério do usuário). A faixa etária (ageRange)
 * é usada exclusivamente para calibrar conteúdo educativo.
 *
 * Valores monetários em centavos (Long) para evitar erros de ponto flutuante.
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val ageRange: String,       // AgeRange enum serializado como String
    val monthlyIncome: Long,    // centavos
    val currentMonth: Int,
    val createdAt: Long         // epoch millis
)
