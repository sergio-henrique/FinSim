package com.finsim.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.finsim.app.presentation.bills.BillsScreen
import com.finsim.app.presentation.challenges.ChallengesScreen
import com.finsim.app.presentation.fixedincome.FixedIncomeScreen
import com.finsim.app.presentation.history.TransactionHistoryScreen
import com.finsim.app.presentation.home.DashboardScreen
import com.finsim.app.presentation.onboarding.OnboardingScreen
import com.finsim.app.presentation.profiles.ProfileSelectorScreen
import com.finsim.app.presentation.progress.ProgressScreen
import com.finsim.app.presentation.ranking.RankingScreen
import com.finsim.app.presentation.reserve.ReserveScreen
import com.finsim.app.presentation.stockmarket.StockMarketScreen
import com.finsim.app.presentation.summary.SummaryScreen

/**
 * Grafo de navegação principal do FinSim.
 *
 * Fluxo: ProfileSelector → (Onboarding | Dashboard)
 * Dashboard → (Bills | Reserve | FixedIncome | StockMarket | Summary | Progress | History | Ranking)
 */
@Composable
fun FinSimNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.ProfileSelector.route,
    ) {
        // --- Seleção de perfil (tela inicial) ---
        composable(NavRoutes.ProfileSelector.route) {
            ProfileSelectorScreen(
                onProfileSelected = { profileId ->
                    navController.navigate(NavRoutes.Dashboard.createRoute(profileId)) {
                        popUpTo(NavRoutes.ProfileSelector.route) { inclusive = false }
                    }
                },
                onCreateNew = {
                    navController.navigate(NavRoutes.Onboarding.route)
                },
            )
        }

        // --- Onboarding (criação de novo perfil) ---
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onProfileCreated = { profileId ->
                    navController.navigate(NavRoutes.Dashboard.createRoute(profileId)) {
                        popUpTo(NavRoutes.ProfileSelector.route) { inclusive = false }
                    }
                },
            )
        }

        // --- Dashboard ---
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
                onNavigateToProgress = { navController.navigate(NavRoutes.Progress.createRoute(profileId)) },
                onNavigateToStockMarket = { navController.navigate(NavRoutes.StockMarket.createRoute(profileId)) },
                onNavigateToHistory = { navController.navigate(NavRoutes.TransactionHistory.createRoute(profileId)) },
                onNavigateToRanking = { navController.navigate(NavRoutes.Ranking.route) },
                onNavigateToChallenges = { navController.navigate(NavRoutes.Challenges.createRoute(profileId)) },
                onNavigateToProfileSelector = {
                    navController.navigate(NavRoutes.ProfileSelector.route) {
                        popUpTo(NavRoutes.ProfileSelector.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = NavRoutes.Bills.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            BillsScreen(profileId = profileId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.Reserve.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            ReserveScreen(profileId = profileId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.FixedIncome.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            FixedIncomeScreen(profileId = profileId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.MonthlySummary.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            SummaryScreen(profileId = profileId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.Progress.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            ProgressScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.StockMarket.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            StockMarketScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.TransactionHistory.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: return@composable
            TransactionHistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.Ranking.route) {
            RankingScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = NavRoutes.Challenges.route,
            arguments = listOf(navArgument("profileId") { type = NavType.LongType }),
        ) {
            ChallengesScreen(onBack = { navController.popBackStack() })
        }
    }
}
