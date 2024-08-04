package com.hcmus.tenderus.ui.screens.profilesetup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHeader(imageRes: Int, name: String, age: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$name, $age", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ProfileButtons(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileButton(
                text = "Edit Profile",
                icon = Icons.Default.Edit,
                isPrimary = false,
                onClick = { /* Handle Edit Profile */ }
            )
        }
        ProfileButton(
            text = "Report App Issue",
            icon = Icons.Default.AddCircle,
            isPrimary = false,
            onClick = { showDialog = true }
        )
        Spacer(modifier = Modifier.height(220.dp))
        ProfileButton(
            text = "Log Out",
            icon = Icons.Default.ExitToApp,
            isPrimary = true,
            onClick = { /* Handle Log Out */ }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(
            text = "About Us",
            icon = Icons.Default.Info,
            isPrimary = false,
            onClick = { /* Handle About Us */ }
        )
    }

    if (showDialog) {
        ReportIssueDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun ProfileButton(text: String, icon: ImageVector, isPrimary: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color(0xFFB71C1C) else Color(0xFFE7D9D9),
            contentColor = if (isPrimary) Color.White else Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.padding(end = 8.dp))
        Text(text)
    }
}

@Composable
fun ReportIssueDialog(onDismiss: () -> Unit) {
    var selectedIssue by remember { mutableStateOf("Bug/Glitch") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Report App Issue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Please select the issue you're experiencing:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                RadioButtonGroup(
                    options = listOf("Bug/Glitch", "Performance Issue", "Other"),
                    selectedOption = selectedIssue,
                    onOptionSelected = { selectedIssue = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe the issue you're experiencing ...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Handle submit feedback
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 10.dp)
                    .clickable { onOptionSelected(option) }
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) },
                    colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFB71C1C),
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option)
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    imageRes = R.drawable.profile_placeholder, // Replace with your image resource
                    name = "Rachel",
                    age = 20
                )
                ProfileButtons(navController)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}
