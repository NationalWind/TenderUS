package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.R
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.Status
import com.hcmus.tenderus.ui.screens.admin.composable.ErrorScreen
import com.hcmus.tenderus.ui.screens.admin.composable.LoadingScreen
import com.hcmus.tenderus.ui.screens.admin.composable.SelectField
import com.hcmus.tenderus.ui.theme.PinkPrimary
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.UiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReportDetailScreen(
    reportDetailUiState: UiState<Report>,
    retryAction: () -> Unit,
    backAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (reportDetailUiState) {
        is UiState.Loading -> LoadingScreen()

        is UiState.Error -> ErrorScreen(
            retryAction, modifier = modifier.fillMaxSize()
        )

        is UiState.Success -> ReportDetail(
            report = reportDetailUiState.data,
            backAction = backAction,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun ReportDetail(report: Report, backAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Card(shape = CircleShape) {
                AsyncImage(
                    model = report.reporterAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Report,
                    contentDescription = null,
                    tint = colorResource(id = R.color.pink_primary)
                )
                Text(text = "has reported", color = Color.Gray, fontSize = 12.sp)
            }
            Card(shape = CircleShape) {
                AsyncImage(
                    model = report.reportedAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                )
            }
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = colorResource(id = R.color.pink_primary),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "Detail",
                    color = colorResource(id = R.color.pink_primary),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(text = "Report ID: ${report.id}")
            Text(
                text = "Reporter: ${report.reporter}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Reported: ${report.reported}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Report date: ${
                    LocalDateTime
                        .parse(report.date.dropLast(1))
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                }"
            )
            Text(text = "Status: ${report.status}")
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = null,
                    tint = colorResource(id = R.color.pink_primary),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "Message",
                    color = colorResource(id = R.color.pink_primary),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = report.message)
        }
        ActionMenu(backAction = backAction)
    }
}

@Composable
fun ActionMenu(backAction: () -> Unit, modifier: Modifier = Modifier) {
    val actions = listOf("Nothing", "Restrict", "Ban")
    var selectedAction by remember { mutableStateOf(actions[0]) }

    Column(modifier = modifier) {
        SelectField(options = actions, selected = selectedAction, select = { selectedAction = it })
        if (selectedAction === "Restrict") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = { })
                Text("Restrict discover")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = { })
                Text("Restrict message")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = false, onCheckedChange = { })
            Text("Also delete reported content")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = backAction,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                border = BorderStroke(color = PinkPrimary, width = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Cancel")
            }
            Button(
                onClick = { },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportDetailScreenPreview() {
    val report = Report(
        id = "000002",
        reporter = "aLongLongLongLongLongUsername",
        reported = "anotherLongLongLongLongUsername",
        reporterAvatar = "https://us-tuna-sounds-images.voicemod.net/6d7e6aff-da39-4fb8-be82-5f12ee5fc75c-1698204266163.png",
        reportedAvatar = "https://us-tuna-sounds-images.voicemod.net/78f23c41-369a-4769-9568-7aae749c4e06-1704762972412.jpg",
        date = "2024-12-31T00:00:00.000Z",
        status = Status.PENDING,
        message = "aLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongMessage"
    )

    TenderUSTheme {
        ReportDetail(report = report, backAction = {})
    }
}