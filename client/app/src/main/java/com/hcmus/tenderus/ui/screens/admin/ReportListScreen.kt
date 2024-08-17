package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.Status
import com.hcmus.tenderus.ui.screens.admin.composable.ErrorScreen
import com.hcmus.tenderus.ui.screens.admin.composable.LoadingScreen
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.UiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReportListScreen(
    reportListUiState: UiState<List<Report>>,
    retryAction: () -> Unit,
    detailNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (reportListUiState) {
        is UiState.Loading -> LoadingScreen()

        is UiState.Error -> ErrorScreen(
            retryAction, modifier = modifier.fillMaxSize()
        )

        is UiState.Success -> ReportList(
            reportList = reportListUiState.data,
            detailNavigate = detailNavigate,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        )
    }
}

@Composable
fun ReportList(
    reportList: List<Report>,
    detailNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(reportList) { report ->
            ReportCard(report = report, detailNavigate = { detailNavigate(report.id) })
        }
    }
}

@Composable
fun ReportCard(report: Report, detailNavigate: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            onClick = detailNavigate,
            color = Color(0xFFFEFEFE)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(12.dp)
            ) {
                AsyncImage(
                    model = report.reportedAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(start = 16.dp, end = 8.dp, top = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = report.reported,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                        Text(
                            text = LocalDateTime
                                .parse(report.date.dropLast(1))
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                    Text(
                        text = report.message,
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "View")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    val reports = listOf(
        Report(
            id = "000001",
            reporter = "user001",
            reported = "user002",
            reportedAvatar = "https://i.kym-cdn.com/photos/images/original/002/735/538/c9a",
            date = "2024-01-01T00:00:00.000Z",
            status = Status.PENDING,
            message = "しかのこのこのここしたんたん"
        ), Report(
            id = "000002",
            reporter = "aLongLongLongLongLongUsername",
            reported = "anotherLongLongLongLongUsername",
            reportedAvatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTS3A70DEIYDx1nSsojnhwYSidMrb7YBAtsfeUyLxlQTBcjXapyMLhhv5xE06BHdVH2Tpk",
            date = "2024-12-31T00:00:00.000Z",
            status = Status.PENDING,
            message = "aLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongMessage"
        )
    )
    TenderUSTheme {
        ReportList(reportList = reports, detailNavigate = {})
    }
}