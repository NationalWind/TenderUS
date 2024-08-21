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
import com.hcmus.tenderus.model.AccountAction
import com.hcmus.tenderus.model.ReportAction
import com.hcmus.tenderus.ui.screens.admin.composable.BottomNav
import com.hcmus.tenderus.ui.screens.admin.composable.BottomNavigationBar
import com.hcmus.tenderus.ui.screens.admin.composable.TopBar
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileScreen
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.AdminViewModel

@Composable
fun AdminScreen(onSignedOut: () -> Unit = {}) {
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
                StatisticsScreen()
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
                        backAction = { navController.popBackStack() },
                        saveAction = { reportAction: ReportAction ->
                            adminViewModel.postReportAction(id, reportAction)
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable(BottomNav.Accounts.route) {
                AccountListScreen(
                    accountListUiState = adminViewModel.accountListUiState,
                    retryAction = adminViewModel::getAccountList,
                    detailNavigate = { id: String ->
                        navController.navigate("${BottomNav.Accounts.route}/${id}")
                    }
                )
            }
            composable("${BottomNav.Accounts.route}/{id}") { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id")
                if (id != null) {
                    LaunchedEffect(id) {
                        adminViewModel.getAccountDetail(id)
                    }
                    AccountDetailScreen(
                        accountDetailUiState = adminViewModel.accountDetailUiState,
                        retryAction = { adminViewModel.getAccountDetail(id) },
                        backAction = { navController.popBackStack() },
                        saveAction = { accountAction: AccountAction ->
                            adminViewModel.postAccountAction(id, accountAction)
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable(BottomNav.Settings.route) {
                ProfileScreen(navController, onSignedOut = onSignedOut)
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