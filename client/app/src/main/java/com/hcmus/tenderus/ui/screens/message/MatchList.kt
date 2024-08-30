package com.hcmus.tenderus.ui.screens.message

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hcmus.tenderus.R
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.ui.screens.admin.composable.ErrorScreen
import com.hcmus.tenderus.ui.theme.Typography
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.MatchState
import com.hcmus.tenderus.utils.subtractInMinutes

@Composable
fun AvatarIcon(match: MatchState) {
    Box(
        modifier = Modifier
            .size(56.dp)
    ) {
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
        if (match.isActive) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .background(Color(0xFFE94057), shape = CircleShape)
                    .align(Alignment.BottomEnd)
                    .padding(7.dp)
            )
        }
    }
}

@Composable
fun MatchItem(match: MatchState, onclick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onclick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarIcon(match)
        Column(
            modifier = Modifier
                .padding(15.dp)
                .weight(5f)
        ) {
            Text(
                text = match.displayName,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val msg =
                if (match.messageArr.first().msgType == "Text") match.messageArr.first().content else match.messageArr.first().msgType
            if (match.messageArr.first().receiver == match.username) {
                Text(
                    text = "You: $msg",
                    style = Typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {

                Text(
                    text = msg,
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Text(text = str, maxLines = 1)
    }

}

@Composable
fun NewMatchItem(match: MatchState, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarIcon(match)
        Text(
            text = match.displayName, modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            overflow = TextOverflow.Ellipsis
        )
    }

}



@Composable
fun MatchList(navController: NavController) {
    if (TokenManager.getRole() != "USER") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "You have to log in as user to chat", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
        }
    } else {
        val matchListVM: MatchListVM = viewModel(factory = MatchListVM.Factory)
        val matches = matchListVM.matches
        var searchText by remember { mutableStateOf("") }

        val scope = rememberCoroutineScope()

        when (matchListVM.uiState) {
            MatchListVM.MessageStatus.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            MatchListVM.MessageStatus.FAILED -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorScreen {
                        matchListVM.getMatches()
                    }
                }
            }

            MatchListVM.MessageStatus.SUCCESS -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Text(
                            "Messages",
                            fontWeight = FontWeight.Bold,
                            style = Typography.headlineLarge,
                            color = Color(0xFFBD0D36)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Image(
                                    painterResource(id = R.drawable.search),
                                    null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            value = searchText,
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "New Matches",
                            fontWeight = FontWeight.Bold,
                            style = Typography.titleLarge,
                            color = Color(0xFFBD0D36)
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
                            color = Color(0xFFBD0D36)
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
                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            modifier = Modifier.width(200.dp)
                                        )
                                    }

                                }
                            }
                        }
                    }

                }
            }
        }
    }

}

