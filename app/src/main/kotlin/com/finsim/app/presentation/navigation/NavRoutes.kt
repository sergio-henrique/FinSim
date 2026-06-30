package com.finsim.app.presentation.navigation

/**
 * Rotas de navegação do FinSim MVP 1.
 *
 * Cada rota que recebe um argumento expõe [createRoute] para
 * construir a URL tipada sem interpolação manual de strings na UI.
 */
sealed class NavRoutes(val route: String) {

    data object Onboarding : NavRoutes("onboarding")

    data object Dashboard : NavRoutes("dashboard/{profileId}") {
        fun createRoute(profileId: Long) = "dashboard/$profileId"
    }

    data object Bills : NavRoutes("bills/{profileId}") {
        fun createRoute(profileId: Long) = "bills/$profileId"
    }

    data object Reserve : NavRoutes("reserve/{profileId}") {
        fun createRoute(profileId: Long) = "reserve/$profileId"
    }

    data object FixedIncome : NavRoutes("fixedincome/{profileId}") {
        fun createRoute(profileId: Long) = "fixedincome/$profileId"
    }

    data object MonthlySummary : NavRoutes("summary/{profileId}") {
        fun createRoute(profileId: Long) = "summary/$profileId"
    }

    data object Progress : NavRoutes("progress/{profileId}") {
        fun createRoute(profileId: Long) = "progress/$profileId"
    }

    data object StockMarket : NavRoutes("stockmarket/{profileId}") {
        fun createRoute(profileId: Long) = "stockmarket/$profileId"
    }

    data object TransactionHistory : NavRoutes("history/{profileId}") {
        fun createRoute(profileId: Long) = "history/$profileId"
    }
}
