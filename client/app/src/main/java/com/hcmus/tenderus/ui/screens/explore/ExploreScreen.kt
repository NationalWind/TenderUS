package com.hcmus.tenderus.ui.screens.explore

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.network.ApiClient
import com.hcmus.tenderus.network.ExploreService
import com.hcmus.tenderus.ui.screens.discover.DiscoverScreen
import com.hcmus.tenderus.ui.screens.discover.SwipeableProfiles
import com.hcmus.tenderus.ui.viewmodels.DiscoverUiState
import com.hcmus.tenderus.ui.viewmodels.ExploreVM
import kotlinx.coroutines.launch
import kotlin.math.exp

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun ExploreScreen(navController: NavController, exploreVM: ExploreVM = viewModel(factory = ExploreVM.Factory)) {
    if (TokenManager.getRole() != "USER") {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "You have to log in as user to explore", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WelcomeSection()
                CategorySection(navController)
                WelcomeJointopic()
                TopicSection()
            }
        }

        if (exploreVM.group != null) {

            DiscoverScreen(navController, "Explore", viewModel = exploreVM)

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.circlecancle),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            exploreVM.group = null
                        }
                        .align(Alignment.TopEnd)
                )
            }

        }
    }

}

@Composable
fun WelcomeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),

    ) {
        Text(
            text = "Welcome to Explore",
            color = Color(0xFFBD0D36),
            fontSize = 27.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "My Vibe ...",
            color = Color(0xFF979797),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CategorySection(navController: NavController, exploreVM: ExploreVM = viewModel(factory = ExploreVM.Factory)) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CategoryItem(
                imageRes = R.drawable.looking_for_love,
                text = "Looking for\nLove",
                onClick = {
                    navController.navigate("discover?customTitle=Looking for Love")
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryItem(
                imageRes = R.drawable.free_tonight,
                text = "Free \ntonight?",
                onClick = { navController.navigate("discover?customTitle=Free tonight?") }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CategoryItem(
                imageRes = R.drawable.coffe_date,
                text = "Coffee\nDate",
                onClick = {
                    exploreVM.getJoinStatus("Coffee Date", scope) {
                        navController.navigate("coffee_date")
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryItem(
                imageRes = R.drawable.let_friend,
                text = "Let's be\nfriend",
                onClick = { /* Hành động hoặc điều hướng khác */ }
            )
        }
    }
}

@Composable
fun CategoryItem(imageRes: Int, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .width(160.dp) // Increase the width of the item
            .aspectRatio(0.75f) // Adjust the aspect ratio for larger images
            .clickable(onClick = onClick)  // Thêm hành động click vào đây

    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 50f
                    )
                )
        )

        if (text.isNotEmpty()) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun WelcomeJointopic() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),

        ) {
        Text(
            text = "For you",
            color = Color(0xFFBD0D36),
            fontSize = 27.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Suggested based on your profile",
            color = Color(0xFF979797),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TopicSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center the images horizontally
        ) {
            CategoryItem(
                imageRes = R.drawable.drink,
                text = "Like to go \ndrinking",
                onClick = {}  // Thêm onClick rỗng nếu không cần xử lý sự kiện
            )
            Spacer(modifier = Modifier.width(16.dp)) // Increase space between items
            CategoryItem(
                imageRes = R.drawable.movie,
                text = "Movie \nLovers",
                onClick = {}  // Thêm onClick rỗng nếu không cần xử lý sự kiện
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Increase space between rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center the images horizontally
        ) {
            CategoryItem(
                imageRes = R.drawable.creative,
                text = "Creative \nLovers",
                onClick = {}  // Thêm onClick rỗng nếu không cần xử lý sự kiện
            )
            Spacer(modifier = Modifier.width(16.dp)) // Increase space between items
            CategoryItem(
                imageRes = R.drawable.sport,
                text = "Love \nSports",
                onClick = {}  // Thêm onClick rỗng nếu không cần xử lý sự kiện
            )
        }
    }
}
