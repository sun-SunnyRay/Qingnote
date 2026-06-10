package com.qingguang.qingnote.ui.page.router

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.qingguang.qingnote.ui.page.PictureDisplayPage
import com.qingguang.qingnote.ui.page.data.DataManagerPage
import com.qingguang.qingnote.ui.page.input.MemoInputPage
import com.qingguang.qingnote.ui.page.input.MemoPreviewPage
import com.qingguang.qingnote.ui.page.main.MainScreen
import com.qingguang.qingnote.ui.page.search.SearchPage
import com.qingguang.qingnote.ui.page.settings.ExplorePage
import com.qingguang.qingnote.ui.page.settings.TaskSettingsPage
import com.qingguang.qingnote.ui.page.settings.NotificationGuardPage
import com.qingguang.qingnote.ui.page.share.SharePage
import com.qingguang.qingnote.ui.page.tag.CommentListPage
import com.qingguang.qingnote.ui.page.tag.DateRangePage
import com.qingguang.qingnote.ui.page.tag.LocationDetailPage
import com.qingguang.qingnote.ui.page.tag.TagDetailPage
import com.qingguang.qingnote.ui.page.tag.TagListPage
import com.qingguang.qingnote.ui.page.tag.YearDetailPage
import com.qingguang.qingnote.utils.SettingsPreferences
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.UnstableSaltApi
import com.moriafly.salt.ui.lightSaltColors
import com.moriafly.salt.ui.darkSaltColors
import com.moriafly.salt.ui.saltColorsByColorScheme
import com.moriafly.salt.ui.saltConfigs

fun NavHostController.debouncedPopBackStack() {
    val currentRoute = this.currentBackStackEntry?.destination?.route
    val previousRoute = this.previousBackStackEntry?.destination?.route

    if (currentRoute != null && previousRoute != null) {
        this.popBackStack()
    } else {
        Log.w("Navigation", "Attempted to pop empty back stack")
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(UnstableSaltApi::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val themeModeState by SettingsPreferences.themeMode.collectAsState(SettingsPreferences.ThemeMode.SYSTEM)
    val dynamicColor by SettingsPreferences.dynamicColor.collectAsState(false)

    val darkTheme = when (themeModeState) {
        SettingsPreferences.ThemeMode.SYSTEM -> isSystemInDarkTheme()
        SettingsPreferences.ThemeMode.DARK -> true
        else -> false
    }

    val colors = when (themeModeState) {
        SettingsPreferences.ThemeMode.LIGHT -> if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            saltColorsByColorScheme(
                dynamicLightColorScheme(context)
            )
        } else lightSaltColors()

        SettingsPreferences.ThemeMode.DARK -> if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) saltColorsByColorScheme(dynamicDarkColorScheme(context)) else darkSaltColors()

        SettingsPreferences.ThemeMode.SYSTEM -> {
            if (isSystemInDarkTheme())
                if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) saltColorsByColorScheme(
                    dynamicDarkColorScheme(context)
                ) else darkSaltColors()
            else
                if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) saltColorsByColorScheme(
                    dynamicLightColorScheme(context)
                ) else lightSaltColors()
        }
    }


    val materialColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    CompositionLocalProvider(LocalRootNavController provides navController) {
        MaterialTheme(colorScheme = materialColorScheme) {
            SaltTheme(
                colors = colors,
                configs = saltConfigs(isDarkTheme = darkTheme),
            ) {
                NavHostContainer(navController = navController)
            }
        }
    }
}

val LocalRootNavController = compositionLocalOf<NavHostController> { error("Not find") }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavHostContainer(
    navController: NavHostController,
) {
    NavHost(
        navController,
        startDestination = Screen.Main,
    ) {
        composable<Screen.TagList> {
            TagListPage(navController = navController)
        }

        composable<Screen.Main> {
            MainScreen(navController = navController)
        }

        composable<Screen.RandomWalk> {
            ExplorePage(navHostController = navController)
        }
        composable<Screen.Search> {
            SearchPage(navController = navController)
        }
        composable<Screen.DataManager> {
            DataManagerPage(navController = navController)
        }
        composable<Screen.TagDetail> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.TagDetail>()
            TagDetailPage(tag = args.tag, navController = navController)
        }

        composable<Screen.YearDetail> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.YearDetail>()
            YearDetailPage(year = args.year, navController = navController)
        }

        composable<Screen.DateRangePage> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.DateRangePage>()
            DateRangePage(startTime = args.startTime, endTime = args.endTime, navController = navController)
        }

        composable<Screen.LocationDetail> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.LocationDetail>()
            LocationDetailPage(location = args.location, navController = navController)
        }

        composable<Screen.PictureDisplay> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.PictureDisplay>()
            PictureDisplayPage(pathList = args.pathList, curIndex = args.curIndex, timestamps = args.timestamps, navController = navController)
        }

        composable<Screen.InputDetail> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.InputDetail>()
            MemoInputPage(args.id)
        }

        composable<Screen.MemoPreview> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.MemoPreview>()
            MemoPreviewPage(args.id)
        }

        composable<Screen.Share> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.Share>()
            SharePage(args.id, navController)
        }

        composable<Screen.TaskSettings> {
            TaskSettingsPage(navController = navController)
        }

        composable<Screen.NotificationGuard> {
            NotificationGuardPage(navController = navController)
        }

        composable<Screen.CommentList> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Screen.CommentList>()
            CommentListPage(parentNoteId = args.parentNoteId, navController = navController)
        }
    }
}
