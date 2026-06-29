package com.finsim.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.finsim.app.presentation.common.FinSimTheme
import com.finsim.app.presentation.navigation.FinSimNavGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * Ponto de entrada do aplicativo FinSim.
 *
 * Responsabilidades desta Activity:
 * - Inicializar o Hilt ([AndroidEntryPoint]).
 * - Configurar o edge-to-edge display.
 * - Hospedar o [FinSimTheme] e o [FinSimNavGraph].
 *
 * Nenhuma lógica de negócio deve existir aqui.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinSimTheme {
                val navController = rememberNavController()
                FinSimNavGraph(navController = navController)
            }
        }
    }
}
