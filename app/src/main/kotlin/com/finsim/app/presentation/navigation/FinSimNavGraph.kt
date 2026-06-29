package com.finsim.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.finsim.app.presentation.bills.BillsScreen
import com.finsim.app.presentation.fixedincome.FixedIncomeScreen
import com.finsim.app.presentation.home.DashboardScreen
import com.finsim.app.presentation.onboarding.OnboardingScreen
import com.finsim.app.presentation.reserve.ReserveScreen
import com.finsim.app.presentation.summary.SummaryScreen

/**
 * Grafo de navegação principal do FinSim MVP 1.
 *
 * O fluxo padrão é:
 *   Onboarding → Dashboard → (Bills | Reserve | FixedIncome | Summary)
 *
 * Onboarding é removido da pilha ao criar o perfil para que o botão
 * Voltar não leve o usuário de volta ao cadastro.
 */
@Composable
fun FinSimNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Onboarding.route,
    ) {
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onProfileCreated = { profileId ->
                    navController.navigate(NavRoutes.Dashboard.createRoute(profileId)) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = NavRoutes.Dashboard.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            DashboardScreen(
                profileId = profileId,
                onNavigateToBills = { navController.navigate(NavRoutes.Bills.createRoute(profileId)) },
                onNavigateToReserve = { navController.navigate(NavRoutes.Reserve.createRoute(profileId)) },
                onNavigateToFixedIncome = { navController.navigate(NavRoutes.FixedIncome.createRoute(profileId)) },
                onNavigateToSummary = { navController.navigate(NavRoutes.MonthlySummary.createRoute(profileId)) },
            )
        }

        composable(
            route = NavRoutes.Bills.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            BillsScreen(
                profileId = profileId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = NavRoutes.Reserve.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            ReserveScreen(
                profileId = profileId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = NavRoutes.FixedIncome.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            FixedIncomeScreen(
                profileId = profileId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = NavRoutes.MonthlySummary.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            SummaryScreen(
                profileId = profileId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
