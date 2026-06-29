package com.finsim.app

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Teste unitário base para verificar que o pipeline de testes funciona.
 *
 * Testes de regras de negócio reais serão adicionados em pacotes
 * correspondentes às camadas domain/ e simulation/.
 *
 * Nota sobre valores monetários: todas as classes do projeto que lidam
 * com dinheiro devem usar Long (representando centavos) ou BigDecimal.
 * O tipo Double nunca deve ser usado para valores financeiros.
 */
class ExampleUnitTest {

    @Test
    fun pipelineDeTestesEstaFuncionando() {
        assertEquals(2, 1 + 1)
    }
}
