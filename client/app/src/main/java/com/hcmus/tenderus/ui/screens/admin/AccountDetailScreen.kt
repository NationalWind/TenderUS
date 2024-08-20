package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.R
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.AccountAction
import com.hcmus.tenderus.model.Role
import com.hcmus.tenderus.ui.screens.admin.composable.ErrorScreen
import com.hcmus.tenderus.ui.screens.admin.composable.LoadingScreen
import com.hcmus.tenderus.ui.screens.admin.composable.SelectField
import com.hcmus.tenderus.ui.theme.PinkPrimary
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.UiState

@Composable
fun AccountDetailScreen(
    accountDetailUiState: UiState<Account>,
    retryAction: () -> Unit,
    backAction: () -> Unit,
    saveAction: (accountAction: AccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (accountDetailUiState) {
        is UiState.Loading -> LoadingScreen()

        is UiState.Error -> ErrorScreen(
            retryAction, modifier = modifier.fillMaxSize()
        )

        is UiState.Success -> AccountDetail(
            account = accountDetailUiState.data,
            backAction = backAction,
            saveAction = saveAction,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun AccountDetail(
    account: Account,
    backAction: () -> Unit,
    saveAction: (accountAction: AccountAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(
                        0xFFFFFFFE
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                AsyncImage(
                    model = account.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(96.dp)
                        .height(96.dp)
                        .clip(CircleShape)
                )
            }
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = colorResource(id = R.color.pink_primary),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "User Detail",
                    color = colorResource(id = R.color.pink_primary),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(text = "Username: ${account.username}")
            Text(text = "Role: ${account.role.name}")
            Text(text = "Email: ${account.email ?: "None"}")
            Text(text = "Phone: ${account.phone ?: "None"}")
        }
        AccountAnalytics()
        AccountActionMenu(
            accountPenalty = account.penalty,
            backAction = backAction,
            saveAction = saveAction,
        )
    }
}

@Composable
fun AccountAnalytics(modifier: Modifier = Modifier) {
    val durations = listOf("Daily", "Weekly", "Monthly", "Yearly")
    var selectedDuration by remember { mutableStateOf(durations[0]) }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.BarChart,
                contentDescription = null,
                tint = colorResource(id = R.color.pink_primary),
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = "Statistics",
                color = colorResource(id = R.color.pink_primary),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        SelectField(
            options = durations,
            selected = selectedDuration,
            select = { selectedDuration = it }
        )
        Text(text = "Average session duration")
        Text(text = "Insert chart $selectedDuration")
        Text(text = "Messages sent")
        Text(text = "Insert chart $selectedDuration")
        Text(text = "Matches made")
        Text(text = "Insert chart $selectedDuration")
        Text(text = "Profile views")
        Text(text = "Insert chart $selectedDuration")
    }
}

@Composable
fun AccountActionMenu(
    accountPenalty: List<String>,
    backAction: () -> Unit,
    saveAction: (accountAction: AccountAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.ReportProblem,
                contentDescription = null,
                tint = colorResource(id = R.color.pink_primary),
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = "Penalty Manage",
                color = colorResource(id = R.color.pink_primary),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        for (penalty in accountPenalty) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = penalty)
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete penalty",
                        tint = PinkPrimary
                    )
                }
            }
        }
//        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = backAction,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                border = BorderStroke(color = PinkPrimary, width = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Cancel")
            }
            Button(
                onClick = {},
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountDetailScreenPreview() {
    val account = Account(
        id = "0001",
        username = "orange",
        role = Role.USER,
        avatar = "https://i.pinimg.com/564x/98/a0/9e/98a09ed13b0640a10d9fba126208864a.jpg",
        email = "orange@gmew.com",
        phone = "0123456789"
    )
    TenderUSTheme {
        AccountDetail(account = account, backAction = {}, saveAction = {})
    }
}