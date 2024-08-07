package com.hcmus.tenderus.ui.screens.discover

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DiscoverScreen(navController: NavController) {
    var location by remember { mutableStateOf("Ho Chi Minh city, VietNam") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Female") }
    var distance by remember { mutableStateOf(40f) }
    var startAge by remember { mutableStateOf(20f) }
    var endAge by remember { mutableStateOf(28f) }
    var showNotifications by remember { mutableStateOf(false) }

    // Example user profile images
    var profiles by remember { mutableStateOf(
        listOf(
            "https://fastly.picsum.photos/id/813/400/400.jpg?hmac=3eUkOPA1X4a9JB_fNq27cSoZ_ii17tUciJnLjDvW7lA",
            "https://fastly.picsum.photos/id/117/400/400.jpg?hmac=lqQqWF--nOABfxYFPF-OUZTuCyYMv3Y0siDTCYlbbdI",
            "https://fastly.picsum.photos/id/947/400/400.jpg?hmac=dPYdI-hfEy6EqwlKDEBuAtx8AVMy0u05pV5jTtGVCKc",
            "https://fastly.picsum.photos/id/652/400/400.jpg?hmac=rU1jgJh7wB4lwyFsI0DfW0_Pk03cA-e2OeFfWYSbg6E",
            "https://fastly.picsum.photos/id/67/400/400.jpg?hmac=wlcqJPOdBr1W3h-XmG1YRKKBfSI8uFQ0EOaVR1nbuIc",
            "https://fastly.picsum.photos/id/165/400/400.jpg?hmac=2pNjhj20nxxGLi_7LTBU5NgrX60JSaoI4Nsq15NZDRQ",
            "https://fastly.picsum.photos/id/737/400/400.jpg?hmac=X3PgjnQsTxQJNaxmk0fjtfJ1NlSaM1dzCNBNqDK_XSY",
            "https://fastly.picsum.photos/id/914/400/400.jpg?hmac=jpTaivRKgUauZAhcOBbCE3guVYVcjWuP_a5k7vIu6xs",
        )
    )}

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
                    text = "Discover",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB62424)
                )
                Spacer(modifier = Modifier.width(105.dp))
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
            Text(
                text = location,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp)
            )

            if (showNotifications) {
                NotificationDialog(onDismiss = { showNotifications = false })
            }

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

            SwipeableProfiles(profiles) { updatedProfiles ->
                profiles = updatedProfiles
            }
        }
    }
}

@Composable
fun NotificationDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .shadow(8.dp, shape = MaterialTheme.shapes.medium)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Example notifications
                NotificationList(
                    notifications = listOf(
                        "New match found!",
                        "Profile liked!",
                        "New message received!"
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBD0D36),
                        contentColor = Color.White
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun NotificationList(notifications: List<String>) {
    Column {
        notifications.forEach { notification ->
            Text(
                text = notification,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun SwipeableProfiles(profiles: List<String>, onProfilesUpdated: (List<String>) -> Unit) {
    var currentProfileIndex by remember { mutableStateOf(0) }
    var showProfileDetails by remember { mutableStateOf(false) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    if (profiles.isNotEmpty()) {
        val profileUrl = profiles[currentProfileIndex]

        // Example user information (replace with actual data as needed)
        val userName = "John Doe"
        val userAge = "25"

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
                            change.consume()
                        },
                        onDragCancel = {
                            offsetX.value = 0f // Reset offset when drag is canceled
                            offsetY.value = 0f // Reset offset when drag is canceled
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value > 300f || offsetX.value < -300f) {
                                    val newProfiles = profiles.toMutableList().apply {
                                        removeAt(currentProfileIndex)
                                    }
                                    onProfilesUpdated(newProfiles)
                                    currentProfileIndex = (currentProfileIndex + 1).coerceAtMost(newProfiles.size - 1)
                                    offsetX.value = 0f
                                    offsetY.value = 0f
                                    showProfileDetails = false // Collapse profile details on swipe
                                } else if (offsetY.value < -300f) {
                                    // Swiped up to show full profile
                                    showProfileDetails = true
                                    offsetY.value = 0f
                                } else {
                                    // Reset offset if swipe is not significant
                                    offsetX.value = 0f
                                    offsetY.value = 0f
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
                Image(
                    painter = rememberAsyncImagePainter(profileUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay profile information
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    contentAlignment = Alignment.BottomStart
                ) {
                    if (!showProfileDetails) {
                        Column(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp)
                        ) {
                            Row {
                                Text(
                                    text = userName,
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp), // Adjust font size
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = userAge,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp) // Adjust font size
                                )
                            }
                        }
                    }
                }

                if (!showProfileDetails) {
                    // Profile button at the bottom right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(64.dp)
                            .clickable { showProfileDetails = true }
                            .background(Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_button),
                            contentDescription = "Show Profile",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (showProfileDetails) {
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
                                .padding(top = 200.dp) // Ensure content doesn't overlap with the image
                        ) {
                            // Profile Image as part of the detailed profile
                            Image(
                                painter = rememberAsyncImagePainter(profileUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Adjust height as needed
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("User Name", style = MaterialTheme.typography.headlineSmall)
                            Text("Age: 25", style = MaterialTheme.typography.bodyLarge)
                            Text("Location: Ho Chi Minh city, VietNam", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "About: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // Add more profile details here

                            Spacer(modifier = Modifier.height(16.dp))

                            // Use profile_button image as collapse button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .size(64.dp)
                                    .clickable { showProfileDetails = false }
                                    .background(Color.Transparent)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.collapse_button), // Use the same image for collapsing
                                    contentDescription = "Collapse Profile",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
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





// eg to run
@Composable
fun MessageScreen(navController: NavController) {

}
@Composable
fun MatchesScreen(navController: NavController) {

}



