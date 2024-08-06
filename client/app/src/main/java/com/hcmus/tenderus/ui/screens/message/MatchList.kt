package com.hcmus.tenderus.ui.screens.message

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.Typography
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.MatchState
import com.hcmus.tenderus.utils.subtractInMinutes


@Composable
fun MatchItem(match: MatchState, onclick: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onclick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(match.avatarIcon).build(),
            placeholder = painterResource(R.drawable.profile_placeholder),
            error = painterResource(R.drawable.profile_placeholder),
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(56.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop
            )
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(text = match.displayName, style = Typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (match.messageArr.first().receiver == match.username) {
                Text(text =  "You: " + match.messageArr.first().content, style = Typography.bodyMedium.copy(color = Color.Gray), maxLines = 1, overflow = TextOverflow.Ellipsis)
            } else {

                Text(
                    text = match.messageArr.first().content,
                    style = Typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (match.isRead) null else FontWeight.Bold
                )

            }

        }

        val diff = subtractInMinutes(match.messageArr.first().createdAt)
        var str = ""
        if (diff < 60) {
            str = diff.toString() + " mins"
        } else if (diff / 60 < 24) {
            str = (diff / 60).toString() + " hrs"
        } else {
            str = (diff / 60 / 24).toString() + " days"
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))
        Text(text = str, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

}

@Composable
fun NewMatchItem(match: MatchState, onClick: () -> Unit) {
    Column (
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(match.avatarIcon).build(),
            placeholder = painterResource(R.drawable.profile_placeholder),
            error = painterResource(R.drawable.profile_placeholder),
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(56.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(text = match.displayName, modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
            overflow = TextOverflow.Ellipsis)
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchList(navController: NavController, matchListVM: MatchListVM) {
    val matches = matchListVM.matches
    var searchText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Text(
                "Messages",
                fontWeight = FontWeight.Bold,
                style = Typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Image(painterResource(id = R.drawable.search), null, modifier = Modifier.size(20.dp)) },
                value=searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black)

            )

        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier= Modifier.fillMaxWidth()) {
            Text(
                "New Matches",
                fontWeight = FontWeight.Bold,
                style = Typography.titleLarge,
            )
            LazyRow {
                items(matches.size) { matchIdx ->
                    if (matches[matchIdx].displayName.startsWith(searchText) && matches[matchIdx].messageArr.isEmpty()) {
                        NewMatchItem(matches[matchIdx], onClick = {
                            matchListVM.curReceiver = matches[matchIdx].username
                            navController.navigate("inchat")
                        })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Column(modifier = Modifier.fillMaxWidth()) {

            Text(
                "Messages",
                fontWeight = FontWeight.Bold,
                style = Typography.titleLarge,
            )

            LazyColumn {
                items(matches.size) { matchIdx ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (matches[matchIdx].displayName.startsWith(searchText) && !matches[matchIdx].messageArr.isEmpty()) {
                            MatchItem(matches[matchIdx]) {
                                matchListVM.curReceiver = matches[matchIdx].username
                                navController.navigate("inchat")

                            }
                            HorizontalDivider(thickness = 1.dp, modifier = Modifier.width(200.dp))
                        }

                    }
                }
            }
        }

    }
}

