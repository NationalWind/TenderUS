package com.hcmus.tenderus.ui.screens.message

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.theme.Typography
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.MatchState
import com.hcmus.tenderus.utils.subtractInMinutes
import kotlinx.coroutines.launch




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
            Text(text = if (match.messageArr.first().receiver == match.username) "You: " + match.messageArr.first().content else match.messageArr.first().content, style = Typography.bodyMedium.copy(color = Color.Gray), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Log.d("d", match.displayName)
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
fun MatchList(navController: NavController? = null, matchListVM: MatchListVM) {
    val matches = matchListVM.matches
    var searchText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val showBottomSheetState= remember { mutableStateOf(false) }
    var usernameInChat by remember { mutableStateOf("") }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier
            .padding(15.dp)) {
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
                )


            )

        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)) {

            Text(
                "New Matches",
                fontWeight = FontWeight.Bold,
                style = Typography.titleLarge,
            )
            LazyRow {
                items(matches.size) { matchIdx ->
                    if (matches[matchIdx].displayName.startsWith(searchText) && matches[matchIdx].messageArr.isEmpty()) {
                        NewMatchItem(matches[matchIdx], onClick = {
                            showBottomSheetState.value = true
                            usernameInChat = matches[matchIdx].username
                        })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)) {

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
                                showBottomSheetState.value = true
                                usernameInChat = matches[matchIdx].username
                            }
                            HorizontalDivider(thickness = 1.dp, modifier = Modifier.width(200.dp))
                        }

                    }
                }
            }
        }
        if (showBottomSheetState.value) {
            BottomSheet(showBottomSheetState, sheetState, matches, usernameInChat, matchListVM)

        }

    }
}

@Composable
fun InChatTopBar(match: MatchState, onclick: () -> Unit = {}) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onclick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Spacer(modifier = Modifier.weight(0.5f))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(match.avatarIcon).build(),
            placeholder = painterResource(R.drawable.profile_placeholder),
            error = painterResource(R.drawable.profile_placeholder),
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(56.dp)
                .weight(1f),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.weight(0.25f))
        Column(
            modifier = Modifier.weight(5f)
        ) {
            Text(text = match.displayName, style = Typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = if (match.isActive) "Online" else "Offline")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(showBottomSheetState: MutableState<Boolean>, sheetState: SheetState, matches: SnapshotStateList<MatchState>, usernameInChat: String, matchListVM: MatchListVM) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageTexting by remember { mutableStateOf("") }

    ModalBottomSheet(
        containerColor = Color.White,
        contentColor = Color.Black,
        onDismissRequest = {
            showBottomSheetState.value = false
        },
        sheetState = sheetState
    ) {
        // Sheet content

        val idx = matches.indexOfFirst { it.username == usernameInChat }
        Scaffold(
            containerColor = Color.White,
            contentColor = Color.Black,
            topBar = {
                InChatTopBar(matches[idx])
            },

            ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)) {
                Log.d("d", it.toString())
                if (matches[idx].messageArr.isEmpty()) {
                    Box(modifier=Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No messages yet", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }

                } else {
                    Box(modifier=Modifier.weight(1f)){
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        )
                    )
                    Spacer(modifier = Modifier.weight(0.2f))
                    AnimatedVisibility(messageTexting == "") {
                        Image(
                            painterResource(id = R.drawable.micircle),
                            modifier= Modifier
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
                            modifier= Modifier
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
}