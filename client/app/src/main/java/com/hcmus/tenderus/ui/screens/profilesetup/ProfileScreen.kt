package com.hcmus.tenderus.ui.screens.profilesetup

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.hcmus.tenderus.R
import com.hcmus.tenderus.utils.firebase.GenAuth
import kotlinx.coroutines.launch
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.ProfileUiState
import com.hcmus.tenderus.ui.viewmodels.ProfileVM
import com.hcmus.tenderus.utils.firebase.StorageUtil
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Collections

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
                .size(160.dp)
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
fun ProfileButtons(navController: NavController, onSignedOut: () -> Unit) {
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
        Spacer(modifier = Modifier.weight(1f))
        ProfileButton(
            text = "Log Out",
            icon = Icons.Default.ExitToApp,
            isPrimary = true,
            onClick = { /* Handle Log Out */

                scope.launch {
                    try {
                        GenAuth.signOut()
                        onSignedOut()
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
            onClick = { navController.navigate("about_us") }
        )
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
fun ProfileScreen(navController: NavController, profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory), onSignedOut: () -> Unit) {
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
        contentWindowInsets = WindowInsets(
            top =  15.dp,
            bottom = 0.dp
        ),
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                profile?.let {
                    ProfileHeader(
                        imageUri = it.avatarIcon!!.takeIf { uri -> uri.isNotEmpty() }?.let { Uri.parse(it) },
                        name = it.displayName!!,
                        age = calculateAgeFromDob(it.birthDate!!)
                    )
                }
                ProfileButtons(navController, onSignedOut)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)) {
    val context = LocalContext.current
    val genders = listOf("Female", "Male", "Other")
    var expanded by remember { mutableStateOf(false) }
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
                name = TextFieldValue(profileData.displayName!!)
                birthdate = TextFieldValue(profileData.birthDate!!)
                gender = profileData.identity!!
                profileImageUri = profileData.avatarIcon!!.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
                newImageSelected = false
            }
            is ProfileUiState.PreferencesSuccess -> {
                // Handle PreferencesSuccess state
            }
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(
            top =  0.dp,
            bottom = 0.dp
        ),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top =  0.dp,
                    bottom = 0.dp
                ),
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
                    .size(150.dp)
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

            Spacer(modifier = Modifier.weight(16f))


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

            Spacer(modifier = Modifier.weight(16f))

            OutlinedTextField(
                value = birthdate,
                onValueChange = { birthdate = it },
                label = { Text("Birthdate") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("interest") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF3E5F5), // Light background color
                    contentColor = Color.Black // Text and icon color
                ),
                shape = RoundedCornerShape(50) // Rounded corners for the button
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Interests",
                    tint = Color.Black // Icon color
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                Text(
                    text = "Edit Interests",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(16f))

            Button(
                onClick = { navController.navigate("addphoto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF3E5F5), // Light background color
                    contentColor = Color.Black // Text and icon color
                ),
                shape = RoundedCornerShape(50) // Rounded corners for the button
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Add Photos",
                    tint = Color.Black // Icon color
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                Text(
                    text = "Add Photos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(modifier = Modifier.weight(50f))

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
                                    profileVM.upsertUserProfile(TokenManager.getToken() ?: "", updatedProfile)
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
                            profileVM.upsertUserProfile(TokenManager.getToken() ?: "", updatedProfile)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Interest(
    navController: NavController,
    profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)
) {
    val interests = listOf(
        "Photography", "Karaoke", "Cooking", "Run", "Art",
        "Extreme", "Drink", "Shopping", "Yoga", "Tennis",
        "Swimming", "Traveling", "Music", "Video games"
    )

    var successMessage by remember { mutableStateOf("") }
    val selectedInterests = remember { mutableStateListOf<String>() }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileVM.getCurrentUserProfile(TokenManager.getToken() ?: "")
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
                successMessage = "Profile updated successfully!"
            }
            is ProfileUiState.Error -> {
                error = true
            }
            else -> Unit
        }
    }

    TenderUSTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(
                top =  0.dp,
                bottom = 0.dp
            ),
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets(
                        top =  0.dp,
                        bottom = 0.dp
                    ),
                    title = {
                        Text("Interests", color = Color(0xFFB71C1C))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFB71C1C))
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced space between items
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Your Interests",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Select a few of your interests and let everyone know what you're passionate about.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Reduced height for less spacing
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
                    Spacer(modifier = Modifier.height(8.dp)) // Reduced height for less spacing
                    Button(
                        onClick = {
                            profile?.let {
                                val updatedProfile = it.copy(interests = selectedInterests.toList())
                                profileVM.upsertUserProfile(TokenManager.getToken() ?: "", updatedProfile)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xE6B71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save", color = Color.White, fontSize = 18.sp)
                    }
                }
                // Display success message
                if (successMessage.isNotEmpty()) {
                    Text(
                        text = successMessage,
                        color = Color.Blue,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
                if (error) {
                    Text("An error occurred. Please try again.", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Photos(
    navController: NavController,
    profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)
) {
    var imageUris by remember { mutableStateOf(listOf<Uri?>()) }
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var successMessage by remember { mutableStateOf("") }

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
    var uploadError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileVM.getCurrentUserProfile(TokenManager.getToken() ?: "")
        Log.d("Photos", "Profile fetched")
    }

    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    val updateProfileState by remember { derivedStateOf { profileVM.updateProfileState } }

    // Handle profile data and errors
    when (profileUiState) {
        is ProfileUiState.Success -> {
            profile = (profileUiState as ProfileUiState.Success).profile
            profile?.pictures?.let { urls ->
                val uris = urls.map { url -> Uri.parse(url) }
                imageUris = uris
            }
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
                successMessage = "Profile updated successfully!"
                loading = false
            }
            is ProfileUiState.Error -> {
                uploadError = true
                loading = false
            }
            else -> Unit
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(
            top =  0.dp,
            bottom = 0.dp
        ),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(
                    top =  0.dp,
                    bottom = 0.dp
                ),
                title = {
                    Text("Add Photos", color = Color(0xFFB71C1C))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFB71C1C))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
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
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Add Photos",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add at least 2 photos to continue",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                val gridModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = gridModifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val imageUrls = mutableListOf<String>()
                        val newImageUris = imageUris.filter { it?.scheme == "content" }
                        val existingImageUrls = imageUris.filter { it?.scheme != "content" }.map { it.toString() }

                        val totalImages = newImageUris.size + existingImageUrls.size
                        var uploadCount = 0

                        profile?.let { userProfile ->
                            newImageUris.forEach { uri ->
                                uri?.let { imageUri ->
                                    StorageUtil.uploadToStorage(
                                        auth = FirebaseAuth.getInstance(),
                                        uri = imageUri,
                                        context = context,
                                        type = "Image"
                                    ) { downloadUrl ->
                                        imageUrls.add(downloadUrl)
                                        uploadCount++

                                        if (uploadCount == newImageUris.size) {
                                            imageUrls.addAll(existingImageUrls)
                                            val updatedProfile = userProfile.copy(pictures = imageUrls)
                                            profileVM.upsertUserProfile(TokenManager.getToken() ?: "", updatedProfile)
                                            Log.d("Add Photos", "Profile updated with new images")
                                        }
                                    }
                                }
                            }

                            if (newImageUris.isEmpty()) {
                                imageUrls.addAll(existingImageUrls)
                                val updatedProfile = userProfile.copy(pictures = imageUrls)
                                profileVM.upsertUserProfile(TokenManager.getToken() ?: "", updatedProfile)
                                Log.d("Add Photos", "Profile updated with existing images only")
                            }
                        } ?: run {
                            Log.e("Add Photos", "Profile is null, cannot update.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    enabled = imageUris.size >= 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Save", color = Color.White)
                }

            }

            if (error) {
                Text(
                    text = "An error occurred. Please try again.",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (uploadError) {
                Text(
                    text = "An error occurred while uploading images.",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = Color.Blue,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
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
}

//@Composable
//@Preview(showBackground = true)
//fun ProfileScreenPreview() {
//    ProfileScreen(rememberNavController())
//}