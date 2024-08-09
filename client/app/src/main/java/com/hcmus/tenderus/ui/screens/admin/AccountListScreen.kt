package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Scaffold
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
import com.hcmus.tenderus.ui.screens.admin.composable.TopBar
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@Composable
fun AccountListScreen(accounts: List<Account>) {
    Scaffold(
        topBar = { TopBar() },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(accounts) { account ->
                    AccountCard(account = account)
                }
            }
        }
    )
}

@Composable
fun AccountCard(account: Account, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
        ) {
            AsyncImage(
                model = "https://i.pinimg.com/736x/74/f4/f5/74f4f548392fbdafbe8a5d9764c83eaf.jpg",
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
                        text = account.email,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = account.phone,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color.LightGray
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "View")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountListScreenPreview() {
    val accounts = listOf(
        Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
        Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
        Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
        Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
        Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
        Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
        Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
        Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
    )
    TenderUSTheme {
        AccountListScreen(accounts = accounts)
    }
}