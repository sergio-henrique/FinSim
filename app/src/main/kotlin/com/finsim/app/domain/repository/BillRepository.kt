package com.finsim.app.domain.repository

import com.finsim.app.domain.model.Bill
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso às contas/despesas mensais virtuais.
 */
interface BillRepository {
    suspend fun save(bill: Bill): Long
    suspend fun update(bill: Bill)
    fun getByProfileIdAndMonth(profileId: Long, month: Int): Flow<List<Bill>>
    suspend fun updatePaidStatus(billId: Long, isPaid: Boolean)
}
