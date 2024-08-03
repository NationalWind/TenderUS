package com.hcmus.tenderus.ui.screens.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@Composable
fun DiscoverScreen(navController: NavController) {
    var location by remember { mutableStateOf("Ho Chi Minh city, VietNam") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Female") }
    var distance by remember { mutableStateOf(40f) }
    var startAge by remember { mutableStateOf(20f) }
    var endAge by remember { mutableStateOf(28f) }

    TenderUSTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1_2),
                    contentDescription = "Logo 1",
                    modifier = Modifier.size(80.dp)// Adjust size as needed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.tim),
                    contentDescription = "Logo 2",
                    modifier = Modifier.size(40.dp) // Adjust size as needed
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                IconButton(onClick = {
                    expanded = !expanded
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_filter),
                        contentDescription = "Filter",
                        tint = Color(0xFFB71C1C)
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
        Text(text = "Distance: ${distance.toInt()} km")
        Slider(
            value = distance,
            onValueChange = onDistanceChanged,
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
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
        Text(text = "Age Range: ${startAge.toInt()} - ${endAge.toInt()}")
        Spacer(modifier = Modifier.height(8.dp))

        // Start age slider
        Text(text = "Select Start Age")
        Slider(
            value = startAge,
            onValueChange = {
                if (it < endAge) onStartAgeChanged(it)
            },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.fillMaxWidth()
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

@Composable
fun ProfileScreen(navController: NavController) {

}
