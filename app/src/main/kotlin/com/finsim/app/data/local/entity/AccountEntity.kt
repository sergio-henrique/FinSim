package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para a conta corrente virtual do usuário.
 *
 * Cada perfil possui exatamente uma conta. A foreign key garante
 * que uma conta nunca exista sem um perfil associado.
 *
 * Valores monetários em centavos (Long).
 */
@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"])]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val balance: Long,                  // saldo corrente em centavos
    val emergencyReserveBalance: Long,  // reserva de emergência em centavos
    val updatedAt: Long                 // epoch millis
)
