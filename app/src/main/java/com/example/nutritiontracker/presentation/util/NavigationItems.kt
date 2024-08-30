package com.example.nutritiontracker.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings

object NavigationItems {
    val navItems = listOf(
        NavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            title = "History",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        ),
        NavigationItem(
            title = "Setting",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
}