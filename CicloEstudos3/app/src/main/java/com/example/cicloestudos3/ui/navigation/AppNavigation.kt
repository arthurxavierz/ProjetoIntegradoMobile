package com.example.cicloestudos3.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cicloestudos3.ui.home.HomeScreen
import com.example.cicloestudos3.ui.revisions.RevisionsScreen
import com.example.cicloestudos3.ui.subjects.SubjectDetailScreen
import com.example.cicloestudos3.ui.subjects.SubjectsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home      : Screen("home",      "Início",      Icons.Rounded.Home)
    object Subjects  : Screen("subjects",  "Disciplinas", Icons.Rounded.MenuBook)
    object Revisions : Screen("revisions", "Revisões",    Icons.Rounded.CalendarMonth)
}

private val bottomNavItems = listOf(Screen.Home, Screen.Subjects, Screen.Revisions)

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route)      { HomeScreen(navController) }
        composable(Screen.Subjects.route)  { SubjectsScreen(navController) }
        composable(Screen.Revisions.route) { RevisionsScreen(navController) }
        composable(
            route = "subject/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { back ->
            SubjectDetailScreen(
                subjectId   = back.arguments?.getInt("subjectId") ?: -1,
                navController = navController
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on sub-screens
    val showBar = bottomNavItems.any { it.route == currentDestination?.route }
    if (!showBar) return

    NavigationBar {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon     = { Icon(screen.icon, contentDescription = screen.label) },
                label    = { Text(screen.label) },
                selected = selected,
                onClick  = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        }
    }
}
