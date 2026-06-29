package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para investimentos simulados de renda fixa.
 *
 * No MVP 1 apenas TESOURO_SELIC_SIMULADO é suportado.
 * O campo [maturityMonth] é nulo para produtos com liquidez diária
 * (sem vencimento definido no simulador).
 *
 * [monthlyRateBps] armazena a taxa mensal em pontos-base (bps) para
 * evitar ponto flutuante — ex.: 100 bps = 1,00% ao mês.
 * [investedAmount] e [currentAmount] em centavos (Long).
 */
@Entity(
    tableName = "fixed_income_investments",
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
data class FixedIncomeInvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val productType: String,        // FixedIncomeProductType enum como String
    val investedAmount: Long,       // valor aplicado em centavos
    val currentAmount: Long,        // valor atual com rendimento em centavos
    val monthlyRateBps: Int,        // taxa mensal em pontos-base (evita Double)
    val startMonth: Int,            // mês simulado de início
    val maturityMonth: Int?,        // nulo = sem vencimento fixo
    val liquidityType: String,      // LiquidityType enum como String
    val createdAt: Long             // epoch millis
)
