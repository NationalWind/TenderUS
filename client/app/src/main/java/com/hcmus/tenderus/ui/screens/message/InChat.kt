package com.hcmus.tenderus.ui.screens.message

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hcmus.tenderus.R
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.ui.screens.BottomNavItem
import com.hcmus.tenderus.ui.theme.Typography
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.MatchState

@Composable
fun InChatTopBar(match: MatchState, onclick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onclick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
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
        Spacer(modifier = Modifier.weight(0.25f))
        Column(
            modifier = Modifier.weight(5f)
        ) {
            Text(
                text = match.displayName,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = if (match.isActive) "Online" else "Offline")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InChatScreen(navController: NavController, matchListVM: MatchListVM) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageTexting by remember { mutableStateOf("") }
    val matches = matchListVM.matches
    val usernameInChat = matchListVM.curReceiver

    val idx = matches.indexOfFirst { it.username == usernameInChat }
    if (!matches[idx].isRead && matches[idx].messageArr.isNotEmpty()) {
        matchListVM.haveReadMessage(matches[idx].messageArr.first().conversationID)
        matches[idx].isRead = true
    }
    Scaffold(
        containerColor = Color.White,
        contentColor = Color.Black,
        topBar = {
            Row {
                IconButton(
                    onClick = {
                        navController.navigate(BottomNavItem.Chat.route) {
                            popUpTo(BottomNavItem.Chat.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_ic),
                        contentDescription = "Back",
                        tint = Color(0xFFB71C1C),
                        modifier = Modifier.size(50.dp)
                    )
                }
                InChatTopBar(matches[idx])
            }
        },

        ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Log.d("d", it.toString())
            if (matches[idx].messageArr.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        "No messages yet",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            } else {
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(state = listState, reverseLayout = true) {
                        items(matches[idx].messageArr.size) { msgIdx ->
                            if (matches[idx].messageArr[msgIdx].msgType == "Text") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = if (matches[idx].messageArr[msgIdx].sender != usernameInChat) Arrangement.End else Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 250.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (matches[idx].messageArr[msgIdx].sender != usernameInChat) Color(
                                                    0xFFFDF1F3
                                                ) else Color.LightGray
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = matches[idx].messageArr[msgIdx].content,
                                            style = Typography.bodyMedium
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(0.2f))
                AnimatedVisibility(messageTexting == "") {
                    Image(
                        painterResource(id = R.drawable.imageclecir),
                        modifier = Modifier
                            .weight(1f)
                            .size(50.dp)
                            .clickable {

                                matchListVM.sendMessage(
                                    MessageSendingRequest(
                                        usernameInChat,
                                        "Text",
                                        messageTexting
                                    )
                                )
                                messageTexting = ""

                            },
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.weight(0.2f))
                OutlinedTextField(
                    shape = RoundedCornerShape(12.dp),
//            leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    value = messageTexting,
                    onValueChange = { messageTexting = it },
                    modifier = Modifier.weight(5f),
                    label = { Text("Your message") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = TextStyle(color = Color.Black)
                )
                Spacer(modifier = Modifier.weight(0.2f))
                AnimatedVisibility(messageTexting == "") {
                    Image(
                        painterResource(id = R.drawable.micircle),
                        modifier = Modifier
                            .weight(1f)
                            .size(55.dp)
                            .clickable {

                                matchListVM.sendMessage(
                                    MessageSendingRequest(
                                        usernameInChat,
                                        "Text",
                                        messageTexting
                                    )
                                )
                                messageTexting = ""

                            },
                        contentDescription = null,
                    )
                }
                AnimatedVisibility(messageTexting != "") {
                    Image(
                        painterResource(id = R.drawable.send),
                        modifier = Modifier
                            .weight(1f)
                            .size(55.dp)
                            .clickable {

                                matchListVM.sendMessage(
                                    MessageSendingRequest(
                                        usernameInChat,
                                        "Text",
                                        messageTexting
                                    )
                                )
                                messageTexting = ""

                            },
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color(0xFFE94057))
                    )
                }
                Spacer(modifier = Modifier.weight(0.2f))
            }

        }


    }
}