package com.hcmus.tenderus.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hcmus.tenderus.R

@Composable
fun ProfileDetails1Screen(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var selectedUniversity by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val universities = listOf(
        "VNU-HCM University of Technology",
        "VNU-HCM University of Science",
        "VNU-HCM University of Social Sciences and Humanities",
        "VNU-HCM International University",
        "VNU-HCM University of Information Technology",
        "VNU-HCM University of Economics and Law",
        "VNU-HCM An Giang University",
        "VNU-HCM School of Medicine",
        "VNU-HCM School of Political and Administration Sciences",
        "VNU-HCM Institute for Environment and Resources",
        "University of Economics Ho Chi Minh City"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                profileImageUri = uri
            }
        }
    )

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable { focusManager.clearFocus() }, // Dismiss keyboard when clicking outside
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_camera),
                contentDescription = "Add Image",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(8.dp)
        ) {
            Text(text = if (selectedUniversity.isEmpty()) "Select University" else selectedUniversity)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                universities.forEach { university ->
                    DropdownMenuItem(
                        text = { Text(university) },
                        onClick = {
                            selectedUniversity = university
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("ProfileDetails2")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue", color = Color.White)
        }
    }
}
