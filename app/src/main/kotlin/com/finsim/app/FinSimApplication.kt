package com.finsim.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application do FinSim.
 *
 * Anotada com [HiltAndroidApp] para inicializar o grafo de dependências
 * do Hilt no startup do processo. Deve ser declarada no AndroidManifest.xml.
 */
@HiltAndroidApp
class FinSimApplication : Application()
