package com.hcmus.tenderus.ui.screens.message

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.hcmus.tenderus.R
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.ui.screens.discover.FullProfile
import com.hcmus.tenderus.ui.screens.discover.composable.ReportButton
import com.hcmus.tenderus.ui.theme.Typography
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.MatchState
import com.hcmus.tenderus.utils.AudioRecorder
import com.hcmus.tenderus.utils.convertIsoToHanoiTime
import com.hcmus.tenderus.utils.convertIsoToHumanReadableDate
import com.hcmus.tenderus.utils.convertIsoToHumanReadableDateTime
import com.hcmus.tenderus.utils.firebase.StorageUtil.Companion.uploadToStorage
import com.hcmus.tenderus.utils.getCurrentDateTimeIso
import com.hcmus.tenderus.utils.subtractInMinutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds

@Composable
fun ImageMessage(
    url: String,
    onClick: () -> Unit,
) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxWidth = 250.dp, maxHeight = 250.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onClick()
                }
        )
}

@Composable
fun VoiceMessage(audioUrl: String, isSender: Boolean) {
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

                },
                colorFilter = if (isSender) null else ColorFilter.tint(Color(0xFF333333))
            )

        }
        AnimatedVisibility(isPlaying) {
            Image(painter = painterResource(id = R.drawable.pausecircle), contentDescription = null, modifier = Modifier
                .size(55.dp)
                .weight(1f)
                .clickable {
                    mediaPlayer.pause()
                    isPlaying = false
                },
                colorFilter = if (isSender) null else ColorFilter.tint(Color(0xFF333333))
            )
        }



        val sliderColors = if (isSender) SliderDefaults.colors(thumbColor = Color(0xFFE94057), activeTrackColor = Color(0xFFE94057), inactiveTrackColor = Color(0xFFEFB8C8)) else SliderDefaults.colors(thumbColor = Color(0xFF333333), activeTrackColor = Color(0xFF333333), inactiveTrackColor = Color(0xFF747474))
        Spacer(modifier = Modifier.weight(0.2f))
        Slider(
            value = currentValue,
            onValueChange = {
                mediaPlayer.seekTo((it * mediaPlayer.duration).toInt())
                currentValue = it
            },
            modifier = Modifier.weight(5f),
            colors = sliderColors
        )
    }
}

@Composable
fun InChatTopBar(match: MatchState, onclick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onclick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarIcon(match)

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

@Composable
fun InChatScreen(navController: NavController, context: Context, matchListVM: MatchListVM, fusedLocationProviderClient: FusedLocationProviderClient) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageTexting by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val matches = matchListVM.matches
    val usernameInChat = matchListVM.curReceiver
    var heldImage by remember { mutableStateOf("") }
    var showProfileDetails by remember { mutableStateOf(false) }

    val audioRecorder = remember { AudioRecorder(context) }
    val file = remember { File(context.cacheDir.toString(), "audio.mp3") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isRecording = true
            audioRecorder.start(file)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    matchListVM.uiState = MatchListVM.MessageStatus.LOADING
                    uploadToStorage(Firebase.auth, uri, context, "Image") {
                        matchListVM.sendMessage(MessageSendingRequest(usernameInChat, "Image", it))
                    }
                }
            }
        }
    )

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
                InChatTopBar(matches[idx]) {
                    matchListVM.getMatchProfile()
                    showProfileDetails = true
                }
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
                            .size(50.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
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
                            .size(55.dp)
                            .clickable {
                                launcher.launch(Manifest.permission.RECORD_AUDIO)
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
                                matchListVM.uiState = MatchListVM.MessageStatus.LOADING
                                uploadToStorage(Firebase.auth, file.toUri(), context, "Audio") {
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
//            Log.d("d", it.toString())
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
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {
                                val isToday =
                                    convertIsoToHumanReadableDate(matches[idx].messageArr[msgIdx].createdAt) == convertIsoToHumanReadableDate(
                                        getCurrentDateTimeIso()
                                    )
                                val text =
                                    if (isToday) convertIsoToHanoiTime(matches[idx].messageArr[msgIdx].createdAt) else convertIsoToHumanReadableDateTime(
                                        matches[idx].messageArr[msgIdx].createdAt
                                    )
                                if (msgIdx == matches[idx].messageArr.size - 1) {
                                    Text(text)
                                } else {
                                    if (convertIsoToHumanReadableDate(matches[idx].messageArr[msgIdx].createdAt) != convertIsoToHumanReadableDate(
                                            matches[idx].messageArr[msgIdx + 1].createdAt
                                        )
                                    ) {
                                        Text(text)
                                    } else {
                                        if (isToday && subtractInMinutes(
                                                matches[idx].messageArr[msgIdx + 1].createdAt,
                                                matches[idx].messageArr[msgIdx].createdAt
                                            ) >= 15
                                        ) {
                                            Text(text)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = if (matches[idx].messageArr[msgIdx].sender != usernameInChat) Arrangement.End else Arrangement.Start
                                ) {
                                    var showReport by remember { mutableStateOf(false) }

                                    if (matches[idx].messageArr[msgIdx].msgType == "Image") {
                                        ImageMessage(
                                            url = matches[idx].messageArr[msgIdx].content,
                                        ) {
                                            heldImage = matches[idx].messageArr[msgIdx].content
                                        }

                                    } else {
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
//                                                .clickable { showReport = !showReport }
                                                .pointerInput(Unit) {
                                                    detectTapGestures(onLongPress = {showReport = !showReport})
                                                }
                                        ) {
                                            if (matches[idx].messageArr[msgIdx].msgType == "Text") {
                                                Text(
                                                    text = matches[idx].messageArr[msgIdx].content,
                                                    style = Typography.bodyMedium,
                                                )
                                            } else if (matches[idx].messageArr[msgIdx].msgType == "Audio") {
                                                VoiceMessage(
                                                    audioUrl = matches[idx].messageArr[msgIdx].content,
                                                    isSender = matches[idx].messageArr[msgIdx].sender != usernameInChat
                                                )
                                            }
                                        }
                                    }

                                    if (showReport && matches[idx].messageArr[msgIdx].sender == usernameInChat) {
                                        ReportButton(reported = matches[idx].messageArr[msgIdx].sender, message = matches[idx].messageArr[msgIdx].content)
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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        if (messageTexting == "" && isRecording) {
                            Row {
                                Spacer(modifier = Modifier.weight(29f))
                                Image(
                                    painterResource(id = R.drawable.circlecancle),
                                    modifier = Modifier
                                        .size(55.dp)
                                        .clickable {
                                            audioRecorder.stop()
                                            isRecording = false
                                        },
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                }
            }
        }
    }

    AnimatedVisibility(
        visible = showProfileDetails,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        matchListVM.curProfile?.let {
            FullProfile(it, fusedLocationProviderClient) {
                showProfileDetails = false
            }
        }
    }

    if (heldImage != "") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    heldImage = ""
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            AsyncImage(
                model = heldImage,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 700.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }

    if (matchListVM.uiState == MatchListVM.MessageStatus.LOADING) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    var showError by remember { mutableStateOf(false) }

    if (matchListVM.uiState == MatchListVM.MessageStatus.FAILED) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "An error occurred. Try again", Toast.LENGTH_SHORT).show()
        }
    }
}


