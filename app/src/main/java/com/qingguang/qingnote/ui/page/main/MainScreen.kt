package com.qingguang.qingnote.ui.page.main

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.ui.page.home.AllNotesPage
import com.qingguang.qingnote.ui.page.home.CalenderPage
import com.qingguang.qingnote.ui.page.settings.SettingsPage
import com.qingguang.qingnote.utils.isWideScreen
import com.moriafly.salt.ui.SaltTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val destinations = NavigationBarPath.entries
    var currentDestination by rememberSaveable { mutableStateOf(destinations[0].route) }
    val pagerState = rememberPagerState(initialPage = 0) { destinations.size }
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    var hideNavBar by rememberSaveable { mutableStateOf(false) }
    val isWideScreen = remember(configuration.orientation) { isWideScreen(context) }

    if (isWideScreen) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .displayCutoutPadding()
        ) {
            if (!hideNavBar) {
                AdaptiveNavigationBar(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = { index ->
                        currentDestination = destinations[index].route
                        scope.launch { pagerState.scrollToPage(index) }
                    },
                    isWideScreen = true
                )
            }
            MainPager(
                pagerState = pagerState,
                navController = navController,
                onHideNavBar = { hideNavBar = it },
                modifier = Modifier.fillMaxHeight().weight(1f)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SaltTheme.colors.subBackground)
        ) {
            MainPager(
                pagerState = pagerState,
                navController = navController,
                onHideNavBar = { hideNavBar = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .run {
                        if (!hideNavBar) {
                            this.consumeWindowInsets(NavigationBarDefaults.windowInsets)
                        } else this
                    }
            )
            if (!hideNavBar) {
                AdaptiveNavigationBar(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = { index ->
                        currentDestination = destinations[index].route
                        scope.launch { pagerState.scrollToPage(index) }
                    },
                    isWideScreen = false
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainPager(
    pagerState: PagerState,
    navController: NavHostController,
    onHideNavBar: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> AllNotesPage(navController = navController, hideBottomNavBar = onHideNavBar)
            1 -> CalenderPage(navController = navController)
            2 -> SettingsPage(navController = navController)
        }
    }
}

@Composable
private fun AdaptiveNavigationBar(
    destinations: List<NavigationBarPath>,
    currentDestination: String,
    onNavigateToDestination: (Int) -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    if (isWideScreen) {
        NavigationRail(modifier, containerColor = SaltTheme.colors.subBackground) {
            destinations.forEachIndexed { index, destination ->
                NavigationRailItem(
                    selected = destination.route == currentDestination,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onNavigateToDestination(index)
                    },
                    icon = destination.icon,
                )
            }
        }
    } else {
        val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        // 判断是否为虚拟导航栏（通常高度 > 0）
        val isSystemBarVisible = navigationBarHeight > 0.dp
        
        NavigationBar(
            modifier = modifier.height(if (isSystemBarVisible) 80.dp else 64.dp),
            containerColor = SaltTheme.colors.subBackground,
            windowInsets = WindowInsets.navigationBars
        ) {
            destinations.forEachIndexed { index, destination ->
                NavigationBarItem(
                    selected = destination.route == currentDestination,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onNavigateToDestination(index)
                    },
                    icon = {
                        // 如果是手势导航（全面屏），增加一些间距，
                        // 虚拟导航栏则依靠 NavigationBar 默认的居中逻辑
                        val iconModifier = if (!isSystemBarVisible) Modifier.padding(top = 8.dp) else Modifier
                        Box(modifier = iconModifier) {
                            destination.icon()
                        }
                    },
                    label = null,
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaltTheme.colors.highlight,
                        unselectedIconColor = SaltTheme.colors.text.copy(alpha = 0.6f),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

enum class NavigationBarPath(
    val route: String,
    val icon: @Composable () -> Unit,
) {
    AllNote(
        route = "Home",
        icon = { Icon(Icons.Default.Home, contentDescription = null) }
    ),
    Calendar(
        route = "Calendar",
        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
    ),
    Settings(
        route = "Settings",
        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
    )
}
