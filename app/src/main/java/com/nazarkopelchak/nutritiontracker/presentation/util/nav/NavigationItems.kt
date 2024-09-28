package com.nazarkopelchak.nutritiontracker.presentation.util.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings

val HOME_NAVIGATION_ITEM = NavigationItem(
    title = "Home",
    route = Routes.HOME_SCREEN,
    selectedIcon = Icons.Filled.Home,
    unselectedIcon = Icons.Outlined.Home
)

val HISTORY_NAVIGATION_ITEM = NavigationItem(
    title = "History",
    route = Routes.NUTRITION_HISTORY_SCREEN,
    selectedIcon = Icons.AutoMirrored.Filled.List,
    unselectedIcon = Icons.AutoMirrored.Outlined.List
)

val SETTING_NAVIGATION_ITEM = NavigationItem(
    title = "Settings",
    route = Routes.SETTING_SCREEN,
    selectedIcon = Icons.Filled.Settings,
    unselectedIcon = Icons.Outlined.Settings
)

val NAVIGATION_ITEMS = listOf(
    HOME_NAVIGATION_ITEM,
    HISTORY_NAVIGATION_ITEM
)