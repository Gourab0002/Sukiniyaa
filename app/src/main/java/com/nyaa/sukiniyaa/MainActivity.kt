package com.nyaa.sukiniyaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nyaa.sukiniyaa.data.model.Torrent
import com.nyaa.sukiniyaa.ui.screens.BookmarksScreen
import com.nyaa.sukiniyaa.ui.screens.SearchScreen
import com.nyaa.sukiniyaa.ui.screens.SettingsScreen
import com.nyaa.sukiniyaa.ui.screens.TorrentDetailScreen
import com.nyaa.sukiniyaa.ui.theme.SukiniyaaTheme
import com.nyaa.sukiniyaa.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Request high refresh rate for 120Hz displays
        window.attributes = window.attributes.apply {
            preferredRefreshRate = 120f
        }
        setContent {
            val themePrefs = remember { ThemePreferences(this) }
            var themeIndex by remember { mutableIntStateOf(themePrefs.themeIndex) }

            SukiniyaaTheme(themeIndex = themeIndex) {
                SukiniyaaApp(
                    currentThemeIndex = themeIndex,
                    onThemeSelected = { index ->
                        themeIndex = index
                        themePrefs.themeIndex = index
                    }
                )
            }
        }
    }
}

private data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem("search", "Search", Icons.Default.Search),
    BottomNavItem("bookmarks", "Bookmarks", Icons.Default.Bookmark),
    BottomNavItem("settings", "Settings", Icons.Default.Settings)
)

@Composable
fun SukiniyaaApp(
    currentThemeIndex: Int,
    onThemeSelected: (Int) -> Unit
) {
    val navController = rememberNavController()
    var selectedTorrent by rememberSaveable { mutableStateOf<Torrent?>(null) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != "detail"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("search") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "search") {
            composable("search") {
                SearchScreen(
                    onTorrentClick = { torrent ->
                        selectedTorrent = torrent
                        navController.navigate("detail")
                    },
                    bottomPadding = innerPadding.calculateBottomPadding()
                )
            }
            composable("bookmarks") {
                BookmarksScreen(
                    onTorrentClick = { torrent ->
                        selectedTorrent = torrent
                        navController.navigate("detail")
                    },
                    bottomPadding = innerPadding.calculateBottomPadding()
                )
            }
            composable("settings") {
                SettingsScreen(
                    currentThemeIndex = currentThemeIndex,
                    onThemeSelected = onThemeSelected
                )
            }
            composable("detail") {
                selectedTorrent?.let { torrent ->
                    TorrentDetailScreen(
                        torrent = torrent,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
