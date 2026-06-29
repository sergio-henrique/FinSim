package com.finsim.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Teste instrumentado base para verificar que o ambiente Compose UI Test
 * está configurado corretamente.
 *
 * Testes de UI reais serão adicionados conforme as telas forem implementadas.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun activityInicializaSemErro() {
        // Verifica apenas que a Activity sobe sem lançar exceção.
        // Testes de componentes visuais virão com as telas do MVP 1.
    }
}
