package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para as contas/despesas mensais virtuais.
 *
 * Uma [BillEntity] representa uma despesa que pode ou não ter sido
 * paga em determinado mês simulado. O campo [dueMonth] é o mês
 * simulado de vencimento; [month] é o mês ao qual a conta pertence
 * (para suportar geração de contas por mês na passagem de mês).
 *
 * Valores monetários em centavos (Long).
 */
@Entity(
    tableName = "bills",
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
        Index(value = ["profileId", "month"])
    ]
)
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val name: String,
    val amount: Long,       // centavos
    val month: Int,         // mês simulado ao qual a conta pertence
    val isPaid: Boolean,
    val category: String,   // BillCategory enum serializado como String
    val dueMonth: Int       // mês simulado de vencimento
)
