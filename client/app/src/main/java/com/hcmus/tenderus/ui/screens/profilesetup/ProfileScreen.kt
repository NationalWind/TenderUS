package com.hcmus.tenderus.ui.screens.profilesetup

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.hcmus.tenderus.R
import com.hcmus.tenderus.utils.firebase.GenAuth
import kotlinx.coroutines.launch
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.ui.viewmodels.ProfileUiState
import com.hcmus.tenderus.ui.viewmodels.ProfileVM
import com.hcmus.tenderus.utils.firebase.StorageUtil
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHeader(imageUri: Uri?, name: String, age: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp)
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        ) {
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = "Default Profile Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
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



fun calculateAgeFromDob(dob: String): Int {
    // Define the date format
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Parse the date of birth string into a LocalDate
    val birthDate = try {
        LocalDate.parse(dob, formatter)
    } catch (e: Exception) {
        // Handle invalid date format
        return -1
    }

    // Get today's date
    val today = LocalDate.now()

    // Calculate the age
    return Period.between(birthDate, today).years
}


@Composable
fun ProfileScreen(navController: NavController, profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)) {
    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    var profile by remember { mutableStateOf<Profile?>(null) }

    LaunchedEffect(Unit) {
        profileVM.getCurrentUserProfile(TokenManager.getToken() ?: "")
    }

    LaunchedEffect(profileUiState) {
        if (profileUiState is ProfileUiState.Success) {
            profile = (profileUiState as ProfileUiState.Success).profile
        }
    }

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
                profile?.let {
                    ProfileHeader(
                        imageUri = it.avatarIcon.takeIf { uri -> uri.isNotEmpty() }?.let { Uri.parse(it) },
                        name = it.displayName,
                        age = calculateAgeFromDob(it.birthDate)
                    )
                }
                ProfileButtons(navController)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf(TextFieldValue("123456789")) }
    var email by remember { mutableStateOf(TextFieldValue("rachel@example.com")) }
    val genders = listOf("Female", "Male", "Other")
    var expanded by remember { mutableStateOf(false) }
    var isConfirmingPhone by remember { mutableStateOf(false) }
    var isConfirmingEmail by remember { mutableStateOf(false) }
    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var birthdate by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf("") }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var successMessage by remember { mutableStateOf("") }
    var newImageSelected by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                profileImageUri = uri
                newImageSelected = true
            }
        }
    )

    // Fetch profile data
    LaunchedEffect(Unit) {
        profileVM.getCurrentUserProfile(TokenManager.getToken() ?: "")
        Log.d("Profile", "Profile fetched")
    }

    // Update the UI state whenever the profile is fetched or changed
    LaunchedEffect(profileUiState) {
        if (profileUiState is ProfileUiState.Success) {
            profile = (profileUiState as ProfileUiState.Success).profile
        }
    }

    LaunchedEffect(profileUiState) {
        if (profileUiState is ProfileUiState.Success) {
            profile = (profileUiState as ProfileUiState.Success).profile
            Log.d("ProfileScreen", "Fetched avatarIcon: ${profile?.avatarIcon}")
        }
    }


    // Handle profile data and errors
    LaunchedEffect(profileUiState) {
        when (profileUiState) {
            is ProfileUiState.Loading -> {
                // Display loading indicator
            }
            is ProfileUiState.Error -> {
                // Display error message
            }
            is ProfileUiState.Success -> {
                // Load profile data into the UI
                val profileData = (profileUiState as ProfileUiState.Success).profile
                profile = profileData
                name = TextFieldValue(profileData.displayName)
                birthdate = TextFieldValue(profileData.birthDate)
                gender = profileData.identity
                profileImageUri = profileData.avatarIcon.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
                newImageSelected = false
            }
            is ProfileUiState.PreferencesSuccess -> {
                // Handle PreferencesSuccess state
            }
        }
    }
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
            Box(
                modifier = Modifier
                    .size(115.dp)
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
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Default Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // Camera icon to indicate image change
            Icon(
                painter = painterResource(id = R.drawable.icon_camera), // Replace with your camera icon resource
                contentDescription = "Change Avatar",
                modifier = Modifier
                    .offset(x = 40.dp,y = (-22).dp)
                    .size(36.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                tint = Color(0xFFB71C1C)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
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
                        containerColor = Color.White,
                        disabledTextColor = Color.Black, // Ensure this matches other fields
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        disabledBorderColor = Color.Gray // Border color when disabled
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
                value = birthdate,
                onValueChange = { birthdate = it },
                label = { Text("Birthdate") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Check if the profileImageUri is valid
                    if (newImageSelected) {
                        try {
                            // Log the URI for debugging
                            Log.d("EditProfileScreen", "Uploading image with URI: ${profileImageUri.toString()}")

                            // Proceed with uploading the image
                            StorageUtil.uploadToStorage(
                                auth = FirebaseAuth.getInstance(),
                                uri = profileImageUri!!,
                                context = context,
                                type = "Image"
                            ) { downloadUrl ->
                                profile?.let { profileData ->
                                    val updatedProfile = profileData.copy(
                                        displayName = name.text,
                                        birthDate = birthdate.text,
                                        identity = gender,
                                        avatarIcon = downloadUrl // Update with the new URL
                                    )
                                    profileVM.updateUserProfile(TokenManager.getToken() ?: "", updatedProfile)
                                    successMessage = "Profile updated successfully!"
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("EditProfileScreen", "Error uploading image", e)
                            // Handle or show an error message if needed
                        }
                    } else {
                        // No image to upload, just update the profile info
                        profile?.let { profileData ->
                            val updatedProfile = profileData.copy(
                                displayName = name.text,
                                birthDate = birthdate.text,
                                identity = gender
                            )
                            profileVM.updateUserProfile(TokenManager.getToken() ?: "", updatedProfile)
                            successMessage = "Profile updated successfully!"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("Save")
            }

            // Display success message
            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = Color.Blue,
                    modifier = Modifier.padding(top = 12.dp)
                )

            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}
