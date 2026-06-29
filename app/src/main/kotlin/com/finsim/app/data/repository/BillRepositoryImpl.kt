package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.BillDao
import com.finsim.app.data.local.entity.BillEntity
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.BillCategory
import com.finsim.app.domain.repository.BillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BillRepositoryImpl @Inject constructor(
    private val dao: BillDao
) : BillRepository {

    override suspend fun save(bill: Bill): Long =
        dao.insert(bill.toEntity())

    override suspend fun update(bill: Bill) =
        dao.update(bill.toEntity())

    override fun getByProfileIdAndMonth(profileId: Long, month: Int): Flow<List<Bill>> =
        dao.getByProfileIdAndMonth(profileId, month).map { list -> list.map { it.toDomain() } }

    override suspend fun updatePaidStatus(billId: Long, isPaid: Boolean) =
        dao.updatePaidStatus(billId, isPaid)
}

// --- Mappers ---

private fun Bill.toEntity() = BillEntity(
    id = id,
    profileId = profileId,
    name = name,
    amount = amount,
    month = month,
    isPaid = isPaid,
    category = category.name,
    dueMonth = dueMonth
)

private fun BillEntity.toDomain() = Bill(
    id = id,
    profileId = profileId,
    name = name,
    amount = amount,
    month = month,
    isPaid = isPaid,
    category = BillCategory.valueOf(category),
    dueMonth = dueMonth
)
