package com.hcmus.tenderus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.AccountUiState
import com.hcmus.tenderus.ui.viewmodels.AccountViewModel

@Composable
fun TestApiScreen(
    accountUiState: AccountUiState
) {
    when (accountUiState) {
        is AccountUiState.Loading -> LoadingScreen()
        is AccountUiState.Error -> ErrorScreen()
        is AccountUiState.Success -> AccountListScreen(accounts = accountUiState.accounts)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Loading")
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Error")
    }
}

@Composable
fun AccountListScreen(accounts: List<Account>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = accounts) {account ->
            Row {
                Text(text = account.username)
                Text(text = account.role)
            }
        }
    }
}