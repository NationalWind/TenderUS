package com.hcmus.tenderus.ui.screens.message

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.hcmus.tenderus.R
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.ui.screens.BottomNavItem
import com.hcmus.tenderus.ui.theme.Typography
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.MatchState
import com.hcmus.tenderus.utils.AudioRecorder
import com.hcmus.tenderus.utils.firebase.StorageUtil.Companion.uploadToStorage
import kotlinx.coroutines.delay
import java.io.File
import kotlin.time.Duration.Companion.seconds


@Composable
fun VoiceMessage(audioUrl: String) {
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember {MediaPlayer()}
    var currentValue by remember { mutableFloatStateOf(0f) }
    var isPrepared by remember { mutableStateOf(false) }





    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
    if (isPlaying) {
        LaunchedEffect(Unit) {
            while (true) {
                currentValue = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
                delay(1.seconds / 30)
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(!isPlaying) {
            Image(painter = painterResource(id = R.drawable.playcircle), contentDescription = null, modifier = Modifier
                .size(55.dp)
                .weight(1f)
                .clickable {

                    mediaPlayer.setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )

                    try {
                        if (isPrepared) {
                            mediaPlayer.start()
                        } else {
                            mediaPlayer.setDataSource(audioUrl)
                            mediaPlayer.prepare()
                            mediaPlayer.setOnCompletionListener {
                                isPlaying = false
                            }
                            mediaPlayer.start()
                            isPrepared = true
                        }


                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                    isPlaying = true

                })
        }
        AnimatedVisibility(isPlaying) {
            Image(painter = painterResource(id = R.drawable.pausecircle), contentDescription = null, modifier = Modifier
                .size(55.dp)
                .weight(1f)
                .clickable {
                    mediaPlayer.pause()
                    isPlaying = false
                })
        }

        Spacer(modifier = Modifier.weight(0.2f))
        Slider(
            value = currentValue,
            onValueChange = {
                mediaPlayer.seekTo((it * mediaPlayer.duration).toInt())
                currentValue = it
            },
            modifier = Modifier.weight(5f),
            colors = SliderDefaults.colors(thumbColor = Color(0xFFE94057), activeTrackColor = Color(0xFFE94057), inactiveTrackColor = Color.LightGray)
        )
    }
}

@Composable
fun InChatTopBar(match: MatchState, onclick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onclick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
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
fun InChatScreen(navController: NavController, matchListVM: MatchListVM, auth: FirebaseAuth, context: Context) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageTexting by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val matches = matchListVM.matches
    val usernameInChat = matchListVM.curReceiver
    val audioRecorder = remember { AudioRecorder(context) }
    val file = remember {File(context.cacheDir.toString(), "audio.mp3")}

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
                        navController.popBackStack()
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
        bottomBar = {
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
                    textStyle = TextStyle(color = Color.Black),
                    enabled = !isRecording
                )
                Spacer(modifier = Modifier.weight(0.2f))
                AnimatedVisibility(messageTexting == "" && !isRecording) {
                    Image(
                        painterResource(id = R.drawable.micircle),
                        modifier = Modifier
                            .weight(1f)
                            .size(55.dp)
                            .clickable {
                                isRecording = true
                                audioRecorder.start(file)
                            },
                        contentDescription = null,
                    )
                }
                AnimatedVisibility(messageTexting == "" && isRecording) {
                    Image(
                        painterResource(id = R.drawable.circlestop),
                        modifier = Modifier
                            .weight(1f)
                            .size(55.dp)
                            .clickable {
                                audioRecorder.stop()
                                uploadToStorage(auth, file.toUri(), context, "Audio") {
                                    matchListVM.sendMessage(
                                        MessageSendingRequest(
                                            usernameInChat,
                                            "Audio",
                                            it
                                        )
                                    )
                                    isRecording = false
                                }


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
                                        if (matches[idx].messageArr[msgIdx].msgType == "Text") {
                                            Text(
                                                text = matches[idx].messageArr[msgIdx].content,
                                                style = Typography.bodyMedium
                                            )
                                        } else if (matches[idx].messageArr[msgIdx].msgType == "Audio") {
                                            VoiceMessage(audioUrl = matches[idx].messageArr[msgIdx].content)
                                        }
                                    }
                                }


                        }
                        item {
                            LaunchedEffect(Unit) {
                                matchListVM.loadMessage()
                            }

                        }
                    }
                }
            }


        }


    }


}