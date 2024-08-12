package com.hcmus.tenderus.ui.screens.profilesetup

import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.tenderus.R
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.DiscoverUiState
import com.hcmus.tenderus.ui.viewmodels.ProfileUiState
import com.hcmus.tenderus.ui.viewmodels.ProfileVM
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

val token = TokenManager.getToken() ?: ""

@Composable
fun ProfileDetails1Screen(
    navController: NavHostController,
    profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)
) {
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                profileImageUri = uri
            }
        }
    )

    val focusManager = LocalFocusManager.current
    val isFormComplete = fullName.isNotEmpty() && dateOfBirth.isNotEmpty() && profileImageUri != null

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
                fontSize = 48.sp,
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
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(200.dp))


            // Continue Button
            Button(
                onClick = {
                    val profile = Profile(
                        displayName = fullName,
                        avatarIcon = profileImageUri.toString(),
                        pictures = listOf(),
                        description = "",
                        longitude = 0f,
                        latitude = 0f,
                        identity = "",
                        birthDate = dateOfBirth,
                        interests = listOf(),
                        groups = listOf(),
                        isActive = true
                    )

                    profileVM.createUserProfile(token, profile)
                    navController.navigate("profilesetup2")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormComplete) Color(0xFFB71C1C) else Color.Gray
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormComplete
            ) {
                Text("Continue", color = Color.White)
            }
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}


@Composable
fun ProfileDetails2Screen(
    navController: NavHostController,
    profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)
) {
    var selectedGender by remember { mutableStateOf("") }
    var isButtonClicked by remember { mutableStateOf(false) }

    var profile by remember { mutableStateOf<Profile?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    val genders = listOf("Male", "Female", "Other")
    val icons = listOf(R.drawable.male, R.drawable.female, R.drawable.othergender)

    LaunchedEffect(Unit) {
        profileVM.getCurrentUserProfile(token)
    }

    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    val updateProfileState by remember { derivedStateOf { profileVM.updateProfileState } }

    // Handle profile data and errors
    when (profileUiState) {
        is ProfileUiState.Success -> {
            profile = (profileUiState as ProfileUiState.Success).profile
            Log.d("ProfileDetails2Screen", "Profile fetched: $profile")
        }
        is ProfileUiState.Error -> {
            error = true
        }
        is ProfileUiState.Loading -> {
            loading = true
        }
        else -> Unit
    }

    // Observe update status
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is ProfileUiState.Success -> {
                // Navigate to the next screen on successful profile update
                navController.navigate("profilesetup3")
            }
            is ProfileUiState.Error -> {
                // Handle error scenario
                error = true
            }
            else -> Unit
        }
    }

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
                fontSize = 48.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFB71C1C),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "I am a",
                fontSize = 35.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
                            .padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.height(100.dp))
            genders.forEachIndexed { index, gender ->
                OutlinedButton(
                    onClick = { selectedGender = gender
                        isButtonClicked = true},
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
                                .size(35.dp)
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
                    if (profile != null) {
                        val updatedProfile = profile!!.copy(identity = selectedGender)
                        profileVM.updateUserProfile(token, updatedProfile)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isButtonClicked) Color(0xFFB71C1C) else Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White)
            }
            Spacer(modifier = Modifier.height(160.dp))
        }
    }
}

@Composable
fun ProfileDetails3Screen(
    navController: NavHostController,
    profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)
) {
    val interests = listOf(
        "Photography", "Karaoke", "Cooking", "Run", "Art",
        "Extreme", "Drink", "Shopping", "Yoga", "Tennis",
        "Swimming", "Traveling", "Music", "Video games"
    )

    val selectedInterests = remember { mutableStateListOf<String>() }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        profileVM.getCurrentUserProfile(token)
    }

    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    val updateProfileState by remember { derivedStateOf { profileVM.updateProfileState } }

    // Handle profile data and errors
    when (profileUiState) {
        is ProfileUiState.Success -> {
            profile = (profileUiState as ProfileUiState.Success).profile
            selectedInterests.clear()
            selectedInterests.addAll(profile?.interests ?: emptyList())
        }
        is ProfileUiState.Error -> {
            error = true
        }
        is ProfileUiState.Loading -> {
            loading = true
        }
        else -> Unit
    }

    // Observe update status
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is ProfileUiState.Success -> {
                // Navigate to the next screen on successful profile update
                navController.navigate("filter")
            }
            is ProfileUiState.Error -> {
                // Handle error scenario
                error = true
            }
            else -> Unit
        }
    }

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
                    .padding(top = 16.dp)
            ) {
                Text("Skip", color = Color.Gray, fontSize = 18.sp)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp),
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
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a few of your interests and let everyone know what you're passionate about.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 16.sp
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
                        profile?.let {
                            val updatedProfile = it.copy(interests = selectedInterests.toList())
                            profileVM.updateUserProfile(token, updatedProfile)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue", color = Color.White, fontSize = 18.sp) // Increase font size
                }
            }

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (error) {
                Text("An error occurred. Please try again.", color = Color.Red, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ProfileDetails4Screen(
    navController: NavHostController,
    profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)
) {
    var imageUris by remember { mutableStateOf(listOf<Uri?>()) }
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val imageUriLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUris = imageUris.toMutableList().apply { add(uri) }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraImageUri?.let { uri ->
                    imageUris = imageUris.toMutableList().apply { add(uri) }
                }
            }
        }
    )

    var profile by remember { mutableStateOf<Profile?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    val updateProfileState by remember { derivedStateOf { profileVM.updateProfileState } }

    // Handle profile data and errors
    when (profileUiState) {
        is ProfileUiState.Success -> {
            profile = (profileUiState as ProfileUiState.Success).profile
            imageUris = profile?.pictures?.map { Uri.parse(it) } ?: emptyList()
        }
        is ProfileUiState.Error -> {
            error = true
        }
        is ProfileUiState.Loading -> {
            loading = true
        }
        else -> Unit
    }

    // Observe update status
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is ProfileUiState.Success -> {
                navController.navigate("houserules")
            }
            is ProfileUiState.Error -> {
                error = true
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
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
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0 until 3) {
                        PhotoBox(imageUri = imageUris.getOrNull(i), onClick = {
                            if (imageUris.getOrNull(i) != null) {
                                imageUris = imageUris.toMutableList().apply { removeAt(i) }
                            } else {
                                showDialog = true
                            }
                        })
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 3 until 6) {
                        PhotoBox(imageUri = imageUris.getOrNull(i), onClick = {
                            if (imageUris.getOrNull(i) != null) {
                                imageUris = imageUris.toMutableList().apply { removeAt(i) }
                            } else {
                                showDialog = true
                            }
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    profile?.let {
                        val updatedProfile = it.copy(pictures = imageUris.map { uri -> uri.toString() })
                        profileVM.updateUserProfile(token, updatedProfile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                enabled = imageUris.size >= 2,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White)
            }
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (error) {
            Text("An error occurred. Please try again.", color = Color.Red, modifier = Modifier.align(Alignment.Center))
        }
    }

    if (showDialog) {
        ChooseImageSourceDialog(
            onDismiss = { showDialog = false },
            onGalleryClick = {
                showDialog = false
                imageUriLauncher.launch("image/*")
            },
            onCameraClick = {
                showDialog = false
                val photoFile = File(context.cacheDir, "camera_image.jpg")
                val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                cameraImageUri = photoUri
                cameraLauncher.launch(photoUri)
            }
        )
    }
}

@Composable
fun ChooseImageSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Choose an option", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onGalleryClick) {
                    Text("Choose from Gallery")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCameraClick) {
                    Text("Take a Picture")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
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
                    .size(24.dp)
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
                painter = painterResource(id = R.drawable.addimg),
                contentDescription = "Add Image",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
@Composable
fun HouseRulesScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFB71C1C),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


            Image(
                painter = painterResource(id = R.drawable.logo1_2),
                contentDescription = "logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Giảm khoảng cách giữa Image và Text

            Text(
                text = "Please follow these House Rules",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp), // Giảm khoảng cách padding
            )

            Text(
                text = "Be yourself.\n" +
                        "Make sure your photos, age, and bio are\ntrue to who you are.\n\n" +
                        "Stay safe.\n" +
                        "Don't be too quick to give out personal\ninformation.\n\n" +
                        "Play it cool.\n" +
                        "Respect others and treat them as you\nwould like to be treated.\n\n" +
                        "Be proactive.\n" +
                        "Always report bad behavior.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    navController.navigate("main")
                },
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C)
                )
            ) {
                Text(
                    text = "I AGREE",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

