package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen() {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = {
                    Text(
                        text = "Account detail",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }, navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .shadow(0.5.dp)
                )
            }
        },
    ) { contentPaddings ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(contentPaddings)
                .fillMaxSize()
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
                        model = "https://i.pinimg.com/736x/74/f4/f5/74f4f548392fbdafbe8a5d9764c83eaf.jpg",
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
                Text(text = "Username: orange")
                Text(text = "Role: admin")
                Text(text = "Email: orange@gmew.com")
                Text(text = "Phone: 12341234")
                Text(text = "Created date: 01-01-2024")
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                StatisticDuration()
                Text(text = "Average session duration")
                Text(text = "Insert chart")
                Text(text = "Messages sent")
                Text(text = "Insert chart")
                Text(text = "Matches made")
                Text(text = "Insert chart")
                Text(text = "Profile views")
                Text(text = "Insert chart")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticDuration(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val actions = listOf("Daily", "Weekly", "Monthly", "Yearly")
    var selectedAction by remember { mutableStateOf(actions[0]) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedAction,
                onValueChange = { selectedAction = it },
                readOnly = true,
                label = { Text("Choose a duration") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFFFFFFFE))
            ) {
                actions.forEach { action ->
                    DropdownMenuItem(text = { Text(action) }, onClick = {
                        selectedAction = action
                        expanded = false
                    })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountDetailScreenPreview() {
    TenderUSTheme {
        AccountDetailScreen()
    }
}