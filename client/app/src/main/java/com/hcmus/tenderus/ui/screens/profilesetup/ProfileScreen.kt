package com.hcmus.tenderus.ui.screens.profilesetup

import android.util.Log
import com.hcmus.tenderus.ui.screens.report.ReportIssueDialog
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.utils.firebase.GenAuth
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
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
                onClick = {
                    navController.navigate("editprofile")
                }
            )
        }
        ProfileButton(
            text = "Report App Issue",
            icon = Icons.Default.AddCircle,
            isPrimary = false,
            onClick = { showDialog = true }
        )
        Spacer(modifier = Modifier.height(180.dp))
        ProfileButton(
            text = "Log Out",
            icon = Icons.Default.ExitToApp,
            isPrimary = true,
            onClick = { /* Handle Log Out */

                scope.launch {
                    try {
                        GenAuth.signOut()
                        navController.navigate("signin")
                    } catch (e: Exception) {
                        // GUI error message here
                    }
                }

            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue("Rachel")) }
    var age by remember { mutableStateOf(TextFieldValue("20")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("123456789")) }
    var email by remember { mutableStateOf(TextFieldValue("rachel@example.com")) }
    var gender by remember { mutableStateOf("Female") }
    var birthday by remember { mutableStateOf(TextFieldValue("01/01/2000")) }

    val genders = listOf("Female", "Male", "Other")
    var expanded by remember { mutableStateOf(false) }
    var isConfirmingPhone by remember { mutableStateOf(false) }
    var isConfirmingEmail by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Profile", color = Color(0xFFB71C1C))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFB71C1C))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White)
                .clickable(
                    onClick = {
                        keyboardController?.hide() // Hide the keyboard
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_placeholder), // Replace with your image resource
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (isConfirmingPhone) {
                        Icon(Icons.Default.Check, contentDescription = "Confirmed", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send Verification", tint = Color(0xFFB71C1C))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (isConfirmingEmail) {
                        Icon(Icons.Default.Check, contentDescription = "Confirmed", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send Verification", tint = Color(0xFFB71C1C))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { /* no-op */ },
                    label = { Text("Gender") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White
                    )
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genders.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = {
                                gender = selection
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                label = { Text("Birthday") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Handle save profile logic here
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("Save")
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}
