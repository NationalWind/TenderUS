package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.Role
import com.hcmus.tenderus.ui.screens.admin.composable.ErrorScreen
import com.hcmus.tenderus.ui.screens.admin.composable.LoadingScreen
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.UiState


@Composable
fun AccountListScreen(
    accountListUiState: UiState<List<Account>>,
    retryAction: () -> Unit,
    detailNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (accountListUiState) {
        is UiState.Loading -> LoadingScreen()

        is UiState.Error -> ErrorScreen(
            retryAction, modifier = modifier.fillMaxSize()
        )

        is UiState.Success -> AccountList(
            accountList = accountListUiState.data,
            detailNavigate = detailNavigate,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        )
    }
}

@Composable
fun AccountList(
    accountList: List<Account>,
    detailNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(accountList) { account ->
            AccountCard(account = account, detailNavigate = { detailNavigate(account.id) })
        }
    }
}

@Composable
fun AccountCard(account: Account, detailNavigate: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            onClick = detailNavigate,
            color = Color(0xFFFEFEFE)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(12.dp)
            ) {
                AsyncImage(
                    model = account.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                        .clip(CircleShape)
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(start = 16.dp, end = 8.dp, top = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = account.username,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = account.role.name,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                    Column {
                        Text(
                            text = account.email ?: "No Email",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = account.phone ?: "No Phone",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.LightGray
                        )
                    }
                }
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "View")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountListScreenPreview() {
    val accounts = listOf(
        Account(
            id = "0001",
            username = "orange",
            role = Role.USER,
            avatar = "https://i.pinimg.com/564x/98/a0/9e/98a09ed13b0640a10d9fba126208864a.jpg",
            email = "orange@gmew.com",
            phone = "0123456789"
        ),
        Account(
            id = "0002",
            username = "orange",
            role = Role.ADMIN,
            avatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8Z7R1ET6oVPfDjhahecLf-H73yFkieXQ3GEtJKxiUI1LxQhSCGB30Tk8S01U45ne4rlM&usqp=CAU",
            email = "orange@gmew.com",
            phone = "0123456789"
        ),
    )
    TenderUSTheme {
        AccountList(accountList = accounts, detailNavigate = {})
    }
}