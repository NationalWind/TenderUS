package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.hcmus.tenderus.ui.theme.PinkPrimary
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen() {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Report detail",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                )
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
            modifier = Modifier
                .padding(contentPaddings)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                    model = "https://us-tuna-sounds-images.voicemod.net/6d7e6aff-da39-4fb8-be82-5f12ee5fc75c-1698204266163.png",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(64.dp)
                                        .height(64.dp)
                                )
                            }
                            Text(text = "Blabla", color = Color.Gray, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Report,
                                contentDescription = null,
                                tint = colorResource(id = R.color.pink_primary)
                            )
                            Text(text = "has reported", color = Color.Gray, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                    model = "https://us-tuna-sounds-images.voicemod.net/78f23c41-369a-4769-9568-7aae749c4e06-1704762972412.jpg",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(64.dp)
                                        .height(64.dp)
                                )
                            }
                            Text(text = "Huh", color = Color.Gray, fontSize = 12.sp)
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
                                text = "Detail",
                                color = colorResource(id = R.color.pink_primary),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(text = "Report ID: ${"R01234"}")
                        Text(text = "Report type: ${"User behavior"}")
                        Text(text = "Report date: ${"01 - 01 - 2024"}")
                        Text(text = "Status: ${"Pending"}")
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Message,
                                contentDescription = null,
                                tint = colorResource(id = R.color.pink_primary),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = "Message",
                                color = colorResource(id = R.color.pink_primary),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = """
                                Leute, wir sind von einem Waffenstillstand zu einem Ausfall 
                                übergegangen. Die Führung macht einfach, was sie will. Aber da es 
                                unser Job ist, haben wir keine Wahl. Wir müssen unseren Befehlen 
                                folgen und den Feind vernichten. Aber jetzt ist unser Feind die 
                                ganze Welt. Es gibt keine Möglichkeit, einen grausamen Krieg zu 
                                vermeiden, und wir müssen endlos lange kämpfen.""".trimIndent()
                        )
                    }
                }
            }
            ActionMenu(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFFFFFFFE))
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                    border = BorderStroke(color = PinkPrimary, width = 1.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionMenu(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val actions = listOf("Nothing", "Restrict", "Ban")
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
                label = { Text("Choose an action") },
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
        when (selectedAction) {
            "Nothing" -> {}
            "Restrict" -> {
                Text(
                    text = "Choose activities to be restricted",
                    color = colorResource(id = R.color.pink_primary),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = false, onCheckedChange = { })
                    Text("Discover")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = false, onCheckedChange = { })
                    Text("Message")
                }
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Restrict duration") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "Ban" -> {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Ban duration") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportDetailScreenPreview() {
    TenderUSTheme {
        ReportDetailScreen()
    }
}