package com.chucklingkoala.stagehand.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chucklingkoala.stagehand.data.local.TokenManager
import com.chucklingkoala.stagehand.presentation.categories.CategoriesScreen
import com.chucklingkoala.stagehand.presentation.dashboard.DashboardScreen
import com.chucklingkoala.stagehand.presentation.login.LoginScreen
import com.chucklingkoala.stagehand.presentation.urldetail.UrlDetailScreen
import org.koin.compose.koinInject

sealed class Screen(val route: String) {
    object Login : Screen("login")
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
    val tokenManager: TokenManager = koinInject()
    val startDestination = if (tokenManager.isLoggedIn()) Screen.Dashboard.route else Screen.Login.route

    LaunchedEffect(Unit) {
        tokenManager.authFailureEvent.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

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
