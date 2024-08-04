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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun DiscoverScreen(navController: NavController) {
    var location by remember { mutableStateOf("Ho Chi Minh city, VietNam") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Female") }
    var distance by remember { mutableStateOf(40f) }
    var startAge by remember { mutableStateOf(20f) }
    var endAge by remember { mutableStateOf(28f) }

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
                    color = Color(0xFFB71C1C)
                )
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
fun SwipeableProfiles(profiles: List<String>, onProfilesUpdated: (List<String>) -> Unit) {
    var currentProfileIndex by remember { mutableStateOf(0) }
    val profileCount = profiles.size

    if (profileCount > 0) {
        val offsetX = remember { mutableStateOf(0f) }
        val coroutineScope = rememberCoroutineScope()
        val profileUrl = profiles[currentProfileIndex]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offsetX.value += dragAmount.x
                    }
                }
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationZ = offsetX.value / 10f
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (offsetX.value > 100f) {
                                // Right swipe - Like
                                coroutineScope.launch {
                                    onProfilesUpdated(profiles.filterIndexed { index, _ -> index != currentProfileIndex })
                                    currentProfileIndex = (currentProfileIndex + 1).coerceAtMost(profileCount - 1)
                                }
                            } else if (offsetX.value < -100f) {
                                // Left swipe - Nope
                                coroutineScope.launch {
                                    onProfilesUpdated(profiles.filterIndexed { index, _ -> index != currentProfileIndex })
                                    currentProfileIndex = (currentProfileIndex + 1).coerceAtMost(profileCount - 1)
                                }
                            }
                            offsetX.value = 0f
                        }
                    )
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
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
fun MatchesScreen(navController: NavController) {

}

@Composable
fun ExploreScreen(navController: NavController) {

}

@Composable
fun ChatScreen(navController: NavController) {

}

