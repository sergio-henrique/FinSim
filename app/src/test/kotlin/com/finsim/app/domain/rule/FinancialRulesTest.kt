package com.finsim.app.domain.rule

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.BillCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitarios para [FinancialRules].
 *
 * Sem dependencias Android, Room, Hilt ou MockK.
 * Valores monetarios em centavos (Long): R$ 1,00 = 100L.
 */
class FinancialRulesTest {

    // -------------------------------------------------------------------------
    // Helpers de fixture
    // -------------------------------------------------------------------------

    private fun account(balance: Long, reserveBalance: Long = 0L) = Account(
        id = 1L,
        profileId = 1L,
        balance = balance,
        emergencyReserveBalance = reserveBalance
    )

    private fun bill(amount: Long, isPaid: Boolean = false) = Bill(
        id = 1L,
        profileId = 1L,
        name = "Conta de luz",
        amount = amount,
        month = 1,
        isPaid = isPaid,
        category = BillCategory.HOUSING,
        dueMonth = 1
    )

    // -------------------------------------------------------------------------
    // RN-002 - canPayBill
    // -------------------------------------------------------------------------

    @Test
    fun canPayBill_saldoExatamenteIgual_retornaTrue() {
        assertTrue(FinancialRules.canPayBill(account(5_000L), bill(5_000L)))
    }

    @Test
    fun canPayBill_saldoMaiorQueValor_retornaTrue() {
        assertTrue(FinancialRules.canPayBill(account(10_000L), bill(3_500L)))
    }

    @Test
    fun canPayBill_saldoInsuficiente_retornaFalse() {
        assertFalse(FinancialRules.canPayBill(account(2_000L), bill(5_000L)))
    }

    @Test
    fun canPayBill_saldoZeroContaPositiva_retornaFalse() {
        assertFalse(FinancialRules.canPayBill(account(0L), bill(100L)))
    }

    // -------------------------------------------------------------------------
    // RN-003 - isBillAlreadyPaid
    // -------------------------------------------------------------------------

    @Test
    fun isBillAlreadyPaid_contaPaga_retornaTrue() {
        assertTrue(FinancialRules.isBillAlreadyPaid(bill(1_000L, isPaid = true)))
    }

    @Test
    fun isBillAlreadyPaid_contaNaoPaga_retornaFalse() {
        assertFalse(FinancialRules.isBillAlreadyPaid(bill(1_000L, isPaid = false)))
    }

    // -------------------------------------------------------------------------
    // RN-005 - canTransferToReserve
    // -------------------------------------------------------------------------

    @Test
    fun canTransferToReserve_saldoSuficienteValorPositivo_retornaTrue() {
        assertTrue(FinancialRules.canTransferToReserve(account(20_000L), 10_000L))
    }

    @Test
    fun canTransferToReserve_valorExatamenteIgualAoSaldo_retornaTrue() {
        assertTrue(FinancialRules.canTransferToReserve(account(5_000L), 5_000L))
    }

    @Test
    fun canTransferToReserve_valorZero_retornaFalse() {
        assertFalse(FinancialRules.canTransferToReserve(account(10_000L), 0L))
    }

    @Test
    fun canTransferToReserve_valorNegativo_retornaFalse() {
        assertFalse(FinancialRules.canTransferToReserve(account(10_000L), -1L))
    }

    @Test
    fun canTransferToReserve_valorMaiorQueSaldo_retornaFalse() {
        assertFalse(FinancialRules.canTransferToReserve(account(3_000L), 5_000L))
    }

    @Test
    fun canTransferToReserve_saldoZero_retornaFalse() {
        assertFalse(FinancialRules.canTransferToReserve(account(0L), 1_000L))
    }

    // -------------------------------------------------------------------------
    // RN-007 - canInvest
    // -------------------------------------------------------------------------

    @Test
    fun canInvest_saldoSuficienteValorPositivo_retornaTrue() {
        assertTrue(FinancialRules.canInvest(account(50_000L), 25_000L))
    }

    @Test
    fun canInvest_valorExatamenteIgualAoSaldo_retornaTrue() {
        assertTrue(FinancialRules.canInvest(account(10_000L), 10_000L))
    }

    @Test
    fun canInvest_valorZero_retornaFalse() {
        assertFalse(FinancialRules.canInvest(account(10_000L), 0L))
    }

    @Test
    fun canInvest_valorMaiorQueSaldo_retornaFalse() {
        assertFalse(FinancialRules.canInvest(account(5_000L), 10_000L))
    }

    @Test
    fun canInvest_saldoZero_retornaFalse() {
        assertFalse(FinancialRules.canInvest(account(0L), 1_000L))
    }

    // -------------------------------------------------------------------------
    // RN-001 - isBalanceConsistent
    // -------------------------------------------------------------------------

    @Test
    fun isBalanceConsistent_saldoZero_retornaTrue() {
        assertTrue(FinancialRules.isBalanceConsistent(0L))
    }

    @Test
    fun isBalanceConsistent_saldoPositivo_retornaTrue() {
        assertTrue(FinancialRules.isBalanceConsistent(100_000L))
    }

    @Test
    fun isBalanceConsistent_saldoNegativo_retornaFalse() {
        assertFalse(FinancialRules.isBalanceConsistent(-1L))
    }

    // -------------------------------------------------------------------------
    // calculateHealthScore
    // -------------------------------------------------------------------------

    @Test
    fun calculateHealthScore_todosCriteriosAtendidos_retorna100() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 10_000L,
            billsTotal = 10_000L,
            hasReserve = true,
            hasInvestment = true,
            isBalancePositive = true
        )
        assertEquals(100, score)
    }

    @Test
    fun calculateHealthScore_nenhumCriterioSemContas_retorna0() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 0L,
            billsTotal = 0L,
            hasReserve = false,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertEquals(0, score)
    }

    @Test
    fun calculateHealthScore_contasNaoPagasSemOutrosCriterios_retorna0() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 0L,
            billsTotal = 10_000L,
            hasReserve = false,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertEquals(0, score)
    }

    @Test
    fun calculateHealthScore_apenasContasPagasIntegralmente_retorna40() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 5_000L,
            billsTotal = 5_000L,
            hasReserve = false,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertEquals(40, score)
    }

    @Test
    fun calculateHealthScore_apenasReservaExistenteSemContas_retorna30() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 0L,
            billsTotal = 0L,
            hasReserve = true,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertEquals(30, score)
    }

    @Test
    fun calculateHealthScore_apenasInvestimentoAtivoSemContas_retorna20() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 0L,
            billsTotal = 0L,
            hasReserve = false,
            hasInvestment = true,
            isBalancePositive = false
        )
        assertEquals(20, score)
    }

    @Test
    fun calculateHealthScore_apenasSaldoPositivoSemContas_retorna10() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 0L,
            billsTotal = 0L,
            hasReserve = false,
            hasInvestment = false,
            isBalancePositive = true
        )
        assertEquals(10, score)
    }

    @Test
    fun calculateHealthScore_metadeDasContasPagas_pontuacaoProporcional() {
        // 50% de 40 pontos = 20 pontos
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 5_000L,
            billsTotal = 10_000L,
            hasReserve = false,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertEquals(20, score)
    }

    @Test
    fun calculateHealthScore_metadeContasMaisReserva_retorna50() {
        // 20 (proporcional) + 30 (reserva) = 50
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 5_000L,
            billsTotal = 10_000L,
            hasReserve = true,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertEquals(50, score)
    }

    @Test
    fun calculateHealthScore_naoUltrapassaLimite100() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 10_000L,
            billsTotal = 10_000L,
            hasReserve = true,
            hasInvestment = true,
            isBalancePositive = true
        )
        assertTrue(score <= 100)
    }

    @Test
    fun calculateHealthScore_naoEhNegativo() {
        val score = FinancialRules.calculateHealthScore(
            billsPaid = 0L,
            billsTotal = 0L,
            hasReserve = false,
            hasInvestment = false,
            isBalancePositive = false
        )
        assertTrue(score >= 0)
    }
}
