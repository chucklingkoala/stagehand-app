package com.chucklingkoala.stagehand.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chucklingkoala.stagehand.presentation.categories.CategoriesScreen
import com.chucklingkoala.stagehand.presentation.dashboard.DashboardScreen
import com.chucklingkoala.stagehand.presentation.urldetail.UrlDetailScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object UrlDetail : Screen("url/{urlId}") {
        fun createRoute(urlId: Int) = "url/$urlId"
    }
    object Categories : Screen("categories")
}

@Composable
fun StagehandNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToUrl = { urlId ->
                    navController.navigate(Screen.UrlDetail.createRoute(urlId))
                },
                onNavigateToCategories = {
                    navController.navigate(Screen.Categories.route)
                }
            )
        }

        composable(
            route = Screen.UrlDetail.route,
            arguments = listOf(
                navArgument("urlId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val urlId = backStackEntry.arguments?.getInt("urlId") ?: return@composable
            UrlDetailScreen(
                urlId = urlId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
