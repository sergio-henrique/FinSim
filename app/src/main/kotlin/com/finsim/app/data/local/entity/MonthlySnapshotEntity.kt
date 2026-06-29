package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para o snapshot financeiro mensal do usuário.
 *
 * Um snapshot é criado imutavelmente ao final de cada passagem de mês.
 * Ele consolida o estado patrimonial para exibição de estatísticas
 * educativas sem precisar recalcular a partir do histórico de transações.
 *
 * [financialHealthScore] é um valor inteiro de 0 a 100 calculado pelas
 * regras de negócio — não é armazenado como Double para evitar imprecisão.
 *
 * Todos os campos monetários em centavos (Long).
 */
@Entity(
    tableName = "monthly_snapshots",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["profileId", "month"], unique = true)
    ]
)
data class MonthlySnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val month: Int,
    val accountBalance: Long,           // centavos
    val reserveBalance: Long,           // centavos
    val fixedIncomeBalance: Long,       // centavos
    val totalWealth: Long,              // centavos
    val billsPaidAmount: Long,          // centavos
    val billsPendingAmount: Long,       // centavos
    val financialHealthScore: Int       // 0..100
)
