package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para o ledger de transações da conta virtual.
 *
 * Cada transação é imutável após inserção — nunca atualizada, apenas
 * consultada. Isso preserva o histórico educativo para que o usuário
 * possa ver o efeito de cada decisão ao longo dos meses.
 *
 * O campo [month] representa o mês simulado (1, 2, 3…), não uma data real.
 * Valores monetários em centavos (Long).
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["accountId", "month"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val type: String,           // TransactionType enum serializado como String
    val amount: Long,           // centavos, sempre positivo; o tipo define direção
    val description: String,
    val month: Int,             // mês simulado
    val createdAt: Long         // epoch millis
)
