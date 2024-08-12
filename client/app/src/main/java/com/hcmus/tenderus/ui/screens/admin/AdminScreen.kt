package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.ui.screens.admin.composable.BottomNav
import com.hcmus.tenderus.ui.screens.admin.composable.BottomNavigationBar
import com.hcmus.tenderus.ui.screens.admin.composable.TopBar
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.ReportListViewModel

@Composable
fun AdminScreen() {
    val navController = rememberNavController()

    val reportListViewModel: ReportListViewModel = viewModel(factory = ReportListViewModel.Factory)

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNav.Analytics.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(BottomNav.Analytics.route) {}
            composable(BottomNav.Reports.route) {
                ReportListScreen(
                    reportListUiState = reportListViewModel.reportListUiState,
                    retryAction = reportListViewModel::getReportList
                )
            }
            composable(BottomNav.Users.route) {}
            composable(BottomNav.Settings.route) {}
        }
    }
}

@Preview
@Composable
fun AdminScreenPreview() {
    TenderUSTheme {
        AdminScreen()
    }
}