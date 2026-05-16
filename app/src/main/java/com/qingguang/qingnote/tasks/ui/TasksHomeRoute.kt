package com.qingguang.qingnote.tasks.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TasksHomeRoute(
    showFloatingActionButton: Boolean = false,
    viewModel: TaskListViewModel = hiltViewModel(),
) {
    TaskListScreen(
        viewModel = viewModel,
        showFloatingActionButton = showFloatingActionButton,
    )
}
