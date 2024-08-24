package com.hcmus.tenderus.ui.screens.profilesetup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Preference
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.ProfileUiState
import com.hcmus.tenderus.ui.viewmodels.ProfileVM


@Composable
fun SearchPreferencesScreen(navController: NavHostController,
                            profileVM: ProfileVM = viewModel(factory = ProfileVM.Factory)) {
    var selectedGender by remember { mutableStateOf("Female") }
    var location by remember { mutableStateOf("Ho Chi Minh city, VietNam") }
    var distance by remember { mutableStateOf(40f) }
    var startAge by remember { mutableStateOf(20f) }
    var endAge by remember { mutableStateOf(28f) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    val profileUiState by remember { derivedStateOf { profileVM.profileUiState } }

    when (profileUiState) {
        is ProfileUiState.Loading -> {
            loading = true
        }
        is ProfileUiState.Error -> {
            error = true
            loading = false
        }
        is ProfileUiState.PreferencesSuccess -> {
            // Profile created successfully, navigate to the next screen
            LaunchedEffect(Unit) {
                navController.navigate("selGoal")
            }
        }
        else -> {
            loading = false
        }
    }

    TenderUSTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
        ) {

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Filter Your Search Preferences",
                fontSize = 35.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB71C1C),
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp) // Adjust the top and bottom padding values as needed
            )

            Spacer(modifier = Modifier.height(8.dp))



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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val preference = Preference(startAge.toInt(), endAge.toInt(), distance, selectedGender)
                    profileVM.upsertUserPreferences(TokenManager.getToken() ?: "", preference)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White)
            }
            // Clear Button
            Button(
                onClick = {
                    selectedGender = "Female"
                    location = ""
                    distance = 10f
                    startAge = 20f
                    endAge = 28f
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear", color = Color.White)
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
        .background(Color.LightGray)
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
fun SelectYourGoalsScreen(navController: NavHostController) {
    val goals = listOf(
        "Finding a romantic partner",
        "Finding a study buddy",
        "Making new friends",
        "Other Goals"
    )
    val selectedGoals = remember { mutableStateListOf<String>() }

    TenderUSTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.tim),
                contentDescription = "Heart Icon",
                modifier = Modifier.size(100.dp)
            )

            // Title
            Text(
                text = "Select Your Goals",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB71C1C),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(50.dp))
            // Goals List
            goals.forEach { goal ->
                val isSelected = selectedGoals.contains(goal)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (isSelected) {
                                selectedGoals.remove(goal)
                            } else {
                                selectedGoals.add(goal)
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFB71C1C) else Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(
                        text = goal,
                        color = if (isSelected) Color.White else Color.Black,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Continue Button
            Button(
                onClick = { navController.navigate("add_photos") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White)
            }
        }
    }
}
