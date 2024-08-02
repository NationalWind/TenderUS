package com.hcmus.tenderus.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme

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

    TenderUSTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(16.dp)
                .clickable { focusManager.clearFocus() }, // Dismiss keyboard when clicking outside
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.tim),
                contentDescription = "Heart Icon",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Profile Details",
                fontSize = 40.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFB71C1C),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_camera),
                        contentDescription = "Add Image",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
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
            Spacer(modifier = Modifier.height(20.dp))

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
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.navigate("profilesetup2")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White)
            }
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

@Composable
fun ProfileDetails2Screen(navController: NavHostController) {
    var selectedGender by remember { mutableStateOf("") }

    val genders = listOf("Male", "Female", "Other")
    val icons = listOf(R.drawable.male, R.drawable.female, R.drawable.othergender)

    TenderUSTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.tim),
                contentDescription = "Heart Icon",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Profile Details",
                fontSize = 40.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFB71C1C),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(130.dp))

            Text(
                text = "Select Gender",
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            genders.forEachIndexed { index, gender ->
                OutlinedButton(
                    onClick = { selectedGender = gender },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedGender == gender) Color(0xFFB71C1C) else Color.White,
                        contentColor = if (selectedGender == gender) Color.White else Color.Black
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = icons[index]),
                            contentDescription = gender,
                            tint = if (selectedGender == gender) Color.White else Color.Black,
                            modifier = Modifier
                                .size(35.dp) // Set the size for the icon
                                .padding(end = 8.dp)
                        )
                        Text(text = gender)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    navController.navigate("profilesetup3")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White)
            }
            Spacer(modifier = Modifier.height(160.dp))
        }
    }
}

@Composable
fun ProfileDetails3Screen(navController: NavHostController) {
    val interests = listOf(
        "Photography", "Karaoke", "Cooking", "Run", "Art",
        "Extreme", "Drink", "Shopping", "Yoga", "Tennis",
        "Swimming", "Traveling", "Music", "Video games"
    )

    val selectedInterests = remember { mutableStateListOf<String>() }

    TenderUSTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Skip Button
            TextButton(
                onClick = { navController.navigate("filter") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp) // Adjust padding to move the button down
            ) {
                Text("Skip", color = Color.Gray, fontSize = 18.sp) // Increase font size
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp), // Adjust padding to prevent overlap with the skip button
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tim),
                    contentDescription = "Heart Icon",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Interests",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFB71C1C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp // Increase font size
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a few of your interests and let everyone know what you're passionate about.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 16.sp // Increase font size for better readability
                )
                Spacer(modifier = Modifier.height(16.dp))
                interests.chunked(2).forEach { rowInterests ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowInterests.forEach { interest ->
                            OutlinedButton(
                                onClick = {
                                    if (selectedInterests.contains(interest)) {
                                        selectedInterests.remove(interest)
                                    } else {
                                        selectedInterests.add(interest)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedInterests.contains(interest)) Color(0xFFB71C1C) else Color.White,
                                    contentColor = if (selectedInterests.contains(interest)) Color.White else Color.Black
                                )
                            ) {
                                Text(interest)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("filter")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue", color = Color.White, fontSize = 18.sp) // Increase font size
                }
            }
        }
    }
}

@Composable
fun ProfileDetails4Screen(navController: NavHostController) {
    var imageUris by remember { mutableStateOf(listOf<Uri?>()) }
    val imageUriLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUris = imageUris.toMutableList().apply { add(uri) }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(id = R.drawable.tim),
            contentDescription = "Heart Icon",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Add Photos",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB71C1C)
        )
        Text(
            text = "Add at least 2 photos to continue",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val gridModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            modifier = gridModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp), // Center the row by adding horizontal padding
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until 3) {
                    PhotoBox(imageUri = imageUris.getOrNull(i), onClick = {
                        if (imageUris.getOrNull(i) != null) {
                            imageUris = imageUris.toMutableList().apply { removeAt(i) }
                        } else {
                            imageUriLauncher.launch("image/*")
                        }
                    })
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp), // Center the row by adding horizontal padding
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 3 until 6) {
                    PhotoBox(imageUri = imageUris.getOrNull(i), onClick = {
                        if (imageUris.getOrNull(i) != null) {
                            imageUris = imageUris.toMutableList().apply { removeAt(i) }
                        } else {
                            imageUriLauncher.launch("image/*")
                        }
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = { navController.navigate("nextScreen") }, // Replace with your navigation target
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            enabled = imageUris.size >= 0,  // for test gui ( must >=2)
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue", color = Color.White)
        }
    }
}

@Composable
fun PhotoBox(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 150.dp)
            .background(Color.LightGray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(50.dp)
                    .background(Color.Red, shape = CircleShape)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Image",
                    tint = Color.White
                )
            }
        } else {
            Icon(
                painter = painterResource(id = R.drawable.addimg), // Replace with your add icon resource ID
                contentDescription = "Add Image",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
