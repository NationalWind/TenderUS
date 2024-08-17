package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.hcmus.tenderus.ui.viewmodels.AdminViewModel

@Composable
fun AdminScreen() {
    val navController = rememberNavController()

    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory)

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNav.Analytics.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(BottomNav.Analytics.route) {
                Text(text = BottomNav.Analytics.route)
            }
            composable(BottomNav.Reports.route) {
                ReportListScreen(
                    reportListUiState = adminViewModel.reportListUiState,
                    retryAction = adminViewModel::getReportList,
                    detailNavigate = { id: String ->
                        navController.navigate("${BottomNav.Reports.route}/${id}")
                    }
                )
            }
            composable("${BottomNav.Reports.route}/{id}") { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                if (id != null) {
                    LaunchedEffect(id) {
                        adminViewModel.getReportDetail(id)
                    }
                    ReportDetailScreen(
                        reportDetailUiState = adminViewModel.reportDetailUiState,
                        retryAction = { adminViewModel.getReportDetail(id) },
                        backAction = { navController.popBackStack() }
                    )
                }
            }
            composable(BottomNav.Users.route) {
                Text(text = BottomNav.Users.route)
            }
            composable(BottomNav.Settings.route) {
                Text(text = BottomNav.Settings.route)
            }
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