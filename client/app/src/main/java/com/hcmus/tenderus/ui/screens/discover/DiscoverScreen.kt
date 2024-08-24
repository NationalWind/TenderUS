package com.hcmus.tenderus.ui.screens.discover

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import coil.compose.rememberAsyncImagePainter
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.network.LikeRequest
import com.hcmus.tenderus.network.PassRequest
import com.hcmus.tenderus.ui.screens.BottomNavItem
import com.hcmus.tenderus.ui.viewmodels.DiscoverUiState
import com.hcmus.tenderus.ui.viewmodels.DiscoverVM
import com.hcmus.tenderus.ui.viewmodels.ProfileUiState
import com.hcmus.tenderus.ui.viewmodels.ProfileVM
import com.hcmus.tenderus.ui.viewmodels.SwipeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import kotlin.math.roundToInt

fun calculateAgeFromDob(dob: String): Int {
    // Extract the year from the last 4 characters of the date of birth string
    val yearStr = dob.take(4)

    // Convert extracted year to integer
    val yearOfBirth = yearStr.toIntOrNull() ?: return -1 // Return -1 for invalid year format

    // Create LocalDate with the extracted year and default month and day
    val today = LocalDate.now()
    val birthday = LocalDate.of(yearOfBirth, today.month, today.dayOfMonth)

    // Calculate age
    return Period.between(birthday, today).years
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun DiscoverScreen(navController: NavController,customTitle: String?,
                   profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory),
                   viewModel: DiscoverVM = viewModel(factory = DiscoverVM.Factory)) {
    var location by remember { mutableStateOf("Ho Chi Minh city, VietNam") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Female") }
    var distance by remember { mutableStateOf(40f) }
    var startAge by remember { mutableStateOf(20f) }
    var endAge by remember { mutableStateOf(28f) }
    var showNotifications by remember { mutableStateOf(false) }

    // Observe the state from the ViewModel
    val discoverUiState by remember { derivedStateOf { viewModel.discoverUiState } }
    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var profiles by remember { mutableStateOf<List<Profile>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getProfiles(TokenManager.getToken() ?: "", "10")
        profileVM.getCurrentUserProfile(TokenManager.getToken() ?: "")
    }

    LaunchedEffect(profileUiState) {
        if (profileUiState is ProfileUiState.Success) {
            profile = (profileUiState as ProfileUiState.Success).profile
        }
    }

    TenderUSTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(top = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = customTitle ?: "Discover",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB62424),
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.width(105.dp))
                if (customTitle == "Discover") {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { showNotifications = !showNotifications }
                            .background(Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_noti), // Replace with your notification icon
                            contentDescription = "Notifications",
                            tint = Color(0xFFB71C1C),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clickable { expanded = !expanded }
                            .background(Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_filter),
                            contentDescription = "Filter",
                            tint = Color(0xFFB71C1C),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            Text(
                text = location,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Gender Selection
                    Text(
                        text = "Interested in",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )

                    GenderSelection(selectedGender) {
                        selectedGender = it
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location Selection
                    Text(
                        text = "Location",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    LocationSelection(location) {
                        location = it
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Distance Slider
                    DistanceSlider(distance) {
                        distance = it
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Age Range Slider
                    AgeRangeSlider(
                        startAge = startAge,
                        endAge = endAge,
                        onStartAgeChanged = { startAge = it },
                        onEndAgeChanged = { endAge = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (discoverUiState) {
                is DiscoverUiState.Loading -> {
                    // Show a loading spinner or progress indicator
                    CircularProgressIndicator()
                }
                is DiscoverUiState.Success -> {
                    profiles = (discoverUiState as DiscoverUiState.Success).profiles
                    profile?.let { SwipeableProfiles(navController, it, profiles!!, viewModel) }
                }
                is DiscoverUiState.Error -> {
                    // Show an error message
                    Text("Failed to load profiles", color = Color.Red)
                }
            }
        }
    }
}


//@Composable
//fun NotificationDialog(onDismiss: () -> Unit) {
//    Dialog(onDismissRequest = onDismiss) {
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(Color.White, shape = MaterialTheme.shapes.medium)
//                .shadow(8.dp, shape = MaterialTheme.shapes.medium)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Notifications",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//                // Example notifications
//                NotificationList(
//                    notifications = listOf(
//                        "New match found!",
//                        "Profile liked!",
//                        "New message received!"
//                    )
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(
//                    onClick = onDismiss,
//                    modifier = Modifier.align(Alignment.End),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFFBD0D36),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text("Close")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun NotificationList(notifications: List<String>) {
//    Column {
//        notifications.forEach { notification ->
//            Text(
//                text = notification,
//                fontSize = 14.sp,
//                color = Color.Black,
//                modifier = Modifier.padding(vertical = 4.dp)
//            )
//        }
//    }
//}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun SwipeableProfiles(navController: NavController,
                      currentProfile: Profile,
                      profiles: List<Profile>,
                      viewModel: DiscoverVM = viewModel(factory = DiscoverVM.Factory)) {
    var currentProfileIndex by remember { mutableStateOf(0) }
    var showProfileDetails by remember { mutableStateOf(false) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    // State for button animation
    var isLikeButtonActive by remember { mutableStateOf(false) }
    var isDislikeButtonActive by remember { mutableStateOf(false) }
    val profile by remember {
        derivedStateOf { profiles.getOrNull(currentProfileIndex) ?: Profile() }
    }

    val swipeUiState by remember { derivedStateOf { viewModel.swipeUiState } }

    // Reset button states when the profile changes
    LaunchedEffect(currentProfileIndex) {
        isLikeButtonActive = false
        isDislikeButtonActive = false
    }

    if (profiles.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            offsetX.value = 0f // Reset offset at the start of the drag
                            offsetY.value = 0f // Reset offset at the start of the drag
                        },
                        onDrag = { change, dragAmount ->
                            offsetX.value += dragAmount.x // Update offset based on drag amount
                            offsetY.value += dragAmount.y // Update offset based on drag amount

                            isLikeButtonActive = offsetX.value > 100f
                            isDislikeButtonActive = offsetX.value < -100f

                            change.consume()
                        },
                        onDragCancel = {
                            offsetX.value = 0f // Reset offset when drag is canceled
                            offsetY.value = 0f // Reset offset when drag is canceled
                            isLikeButtonActive = false // Deactivate buttons when drag is canceled
                            isDislikeButtonActive = false
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value > 300f) {
                                    // Like
                                    currentProfileIndex =
                                        (currentProfileIndex + 1).coerceAtMost(profiles.size - 1)
                                    offsetX.value = 0f
                                    offsetY.value = 0f
                                    showProfileDetails = false // Collapse profile details on swipe
                                    isLikeButtonActive =
                                        false // Deactivate buttons when drag is canceled
                                    isDislikeButtonActive = false
                                    viewModel.likeProfile(
                                        TokenManager.getToken() ?: "",
                                        LikeRequest(currentProfile.username!!, profile.username!!)
                                    )

                                } else if (offsetX.value < -300f) {
                                    // Dislike
                                    currentProfileIndex =
                                        (currentProfileIndex + 1).coerceAtMost(profiles.size - 1)
                                    offsetX.value = 0f
                                    offsetY.value = 0f
                                    showProfileDetails = false // Collapse profile details on swipe
                                    isLikeButtonActive =
                                        false // Deactivate buttons when drag is canceled
                                    isDislikeButtonActive = false
                                    viewModel.passProfile(
                                        TokenManager.getToken() ?: "",
                                        PassRequest(currentProfile.username!!, profile.username!!)
                                    )
                                } else {
                                    // Reset offset if swipe is not significant
                                    offsetX.value = 0f
                                    offsetY.value = 0f
                                    isLikeButtonActive = false
                                    isDislikeButtonActive = false
                                }
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                    .graphicsLayer {
                        rotationZ = offsetX.value / 20f
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(350.dp, 550.dp) // Fixed size for rectangular image
                        .align(Alignment.Center)
                        .offset(y = (-45).dp)
                        .clip(RoundedCornerShape(12.dp)) // Rounded corners
                        .background(Color.Black.copy(alpha = 0.5f)) // Optional background
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.avatarIcon!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Overlay profile information
                if (!showProfileDetails) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 100.dp)
                            .padding(start = 20.dp)
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Column {
                            Row {
                                Text(
                                    text = profile.displayName!!,
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${calculateAgeFromDob(profile.birthDate!!)}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp)
                                )
                            }
                        }
                    }
                }

                // Profile button at the bottom right
                if (!showProfileDetails) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(52.dp)
                            .offset(x = (-10).dp, y = (-100).dp)
                            .clickable { showProfileDetails = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_button),
                            contentDescription = "Show Profile",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                AnimatedVisibility (
                    visible = showProfileDetails,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    // Full Profile Information Section
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())

                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // Profile Image as part of the detailed profile
                            Image(
                                painter = rememberAsyncImagePainter(profile.avatarIcon!!),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Adjust height as needed
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display additional images
                            profile.pictures!!.drop(1).forEach { imageUrl ->
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp) // Adjust height as needed
                                        .padding(vertical = 8.dp) // Add spacing between images
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(profile.displayName!!, style = MaterialTheme.typography.headlineSmall)
                            Text("Age: ${calculateAgeFromDob(profile.birthDate!!)}", style = MaterialTheme.typography.bodyLarge)
                            Text("Location: Ho Chi Minh city, VietNam", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                profile.description!!,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Use collapse button image for collapsing profile details
                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .size(52.dp)
                                    .offset(x = (-10).dp, y = (-100).dp)
                                    .clickable { showProfileDetails = false }
                                    .background(Color.Transparent)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.collapse_button),
                                    contentDescription = "Collapse Profile",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Action buttons at the bottom center (always visible)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomCenter) // Align buttons at the bottom center of the screen
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Dislike Button
                    Box(
                        modifier = Modifier
                            .size(115.dp) // Increased size
                            .graphicsLayer(
                                scaleX = if (isDislikeButtonActive) 1.3f else 1f,
                                scaleY = if (isDislikeButtonActive) 1.3f else 1f,
                                alpha = if (isDislikeButtonActive) 0.5f else 1f
                            )
                            .offset(x = (-12).dp, y = (10).dp)
                            .background(Color.Transparent)
                            .clickable {
                                // Dislike
                                if (profiles.isNotEmpty()) {

                                    offsetX.value = 0f
                                    offsetY.value = 0f
                                    showProfileDetails = false // Collapse profile details on swipe
                                    viewModel.passProfile(
                                        TokenManager.getToken() ?: "",
                                        PassRequest(currentProfile.username!!, profile.username!!)
                                    )
                                    currentProfileIndex =
                                        (currentProfileIndex + 1).coerceAtMost(profiles.size - 1)
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isDislikeButtonActive) R.drawable.big_dislike else R.drawable.dislike
                            ),
                            contentDescription = "Dislike",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Like Button
                    Box(
                        modifier = Modifier
                            .size(95.dp) // Increased size
                            .graphicsLayer(
                                scaleX = if (isLikeButtonActive) 1.5f else 1f,
                                scaleY = if (isLikeButtonActive) 1.5f else 1f,
                                alpha = if (isLikeButtonActive) 0.5f else 1f
                            )
                            .offset(x = (10).dp, y = (10).dp)
                            .background(Color.Transparent)
                            .clickable {
                                // Like
                                if (profiles.isNotEmpty()) {

                                    offsetX.value = 0f
                                    offsetY.value = 0f
                                    showProfileDetails = false // Collapse profile details on swipe
                                    viewModel.likeProfile(
                                        TokenManager.getToken() ?: "",
                                        LikeRequest(currentProfile.username!!, profile.username!!)
                                    )
                                    currentProfileIndex =
                                        (currentProfileIndex + 1).coerceAtMost(profiles.size - 1)
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isLikeButtonActive) R.drawable.big_like else R.drawable.like
                            ),
                            contentDescription = "Like",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        when (swipeUiState) {
            is SwipeUiState.Loading -> {
                // Show a loading spinner or progress indicator
//                CircularProgressIndicator()
            }
            is SwipeUiState.LikeSuccess -> {
                if ((swipeUiState as SwipeUiState.LikeSuccess).match) {
                    (swipeUiState as SwipeUiState.LikeSuccess).match = false
                    navController.navigate("itsamatch")

                }
            }
            is SwipeUiState.PassSuccess -> {
                Log.d("Pass", "Passed")
            }
            is SwipeUiState.Error -> {
                // Show an error message
                Text("Failed to swipe", color = Color.Red)
            }
        }

    }
}

@Composable
fun MatchOverlay(onAnimationEnd: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000) // Adjust delay for fade-out duration
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MATCH! MATCH! MATCH!",
            fontSize = 40.sp, // Adjust font size as needed
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ItsAMatchScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFBD0D36) // Background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "It's a match",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                    navController.navigate(BottomNavItem.Chat.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    } },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Chat Now",
                    color = Color(0xFFBD0D36)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text(
                    text = "Not now",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}



@Composable
fun GenderSelection(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("Female", "Male", "Both").forEach { gender ->
            Button(
                onClick = { onGenderSelected(gender) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGender == gender) Color(0xFFB71C1C) else Color.LightGray,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(gender)
            }
            if (gender != "Both") Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun LocationSelection(location: String, onLocationChanged: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(location) }

    val locations = listOf(
        "Ho Chi Minh city, VietNam",
        "Ha Noi, VietNam",
        "Da Nang, VietNam",
        "Can Tho, VietNam",
        "Hai Phong, VietNam",
        "Hue, VietNam",
        "Nha Trang, VietNam",
        "Vung Tau, VietNam",
        "Quang Ninh, VietNam",
        "Binh Duong, VietNam",
        "Dong Nai, VietNam",
        "Thanh Hoa, VietNam",
        "Nghe An, VietNam",
        "Khanh Hoa, VietNam",
        "Binh Thuan, VietNam"
        // Add more provinces as needed
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { expanded = true }
        .background(Color.White)
    ) {
        Text(
            text = if (selectedLocation.isEmpty()) "Select Location" else selectedLocation,
            modifier = Modifier.padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            locations.forEach { loc ->
                DropdownMenuItem(
                    onClick = {
                        selectedLocation = loc
                        onLocationChanged(loc)
                        expanded = false
                    },
                    text = { Text(loc) }
                )
            }
        }
    }
}

@Composable
fun DistanceSlider(distance: Float, onDistanceChanged: (Float) -> Unit) {
    Column {
        Text(
            text = "Distance: ${distance.toInt()} km",
            fontWeight = FontWeight.Bold
        )
        Slider(
            value = distance,
            onValueChange = onDistanceChanged,
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFB71C1C), // Customize thumb color
                activeTrackColor = Color(0xFFD32F2F), // Customize active track color
                inactiveTrackColor = Color(0xFFBDBDBD) // Customize inactive track color
            ),
        )
    }
}

@Composable
fun AgeRangeSlider(
    startAge: Float,
    endAge: Float,
    onStartAgeChanged: (Float) -> Unit,
    onEndAgeChanged: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 18f..60f
) {
    Column {
        // Display the selected age range
        Text(
            text = "Age Range: ${startAge.toInt()} - ${endAge.toInt()}",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Start age slider
        Text(text = "Select Start Age")
        Slider(
            value = startAge,
            onValueChange = {
                if (it < endAge) onStartAgeChanged(it)
            },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFB71C1C), // Customize thumb color
                activeTrackColor = Color(0xFFD32F2F), // Customize active track color
                inactiveTrackColor = Color(0xFFBDBDBD) // Customize inactive track color
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // End age slider
        Text(text = "Select End Age")
        Slider(
            value = endAge,
            onValueChange = {
                if (it > startAge) onEndAgeChanged(it)
            },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFB71C1C), // Customize thumb color
                activeTrackColor = Color(0xFFD32F2F), // Customize active track color
                inactiveTrackColor = Color(0xFFBDBDBD) // Customize inactive track color
            ),
        )
    }
}



@Composable
fun MatchesScreen(navController: NavController) {

}

