package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.R
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.Status
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AdminReportScreen(reports: List<Report>) {
    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_topbar),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxHeight(0.75f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.PieChart,
                            contentDescription = "Home",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.BarChart,
                            contentDescription = "Home",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.People,
                            contentDescription = "Home",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Home",
                            tint = Color.Gray
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .shadow(0.5.dp)
                )
            }
        },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(reports) { report ->
                    ReportCard(report = report)
                }
            }
        }
    )
}

@Composable
fun ReportCard(report: Report, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
        ) {
            AsyncImage(
                model = "https://i.ytimg.com/vi/oZpYEEcvu5I/hqdefault.jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
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
                        text = report.username,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = report.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
                Text(
                    text = "Lorem ipsum bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla",
                    textAlign = TextAlign.Justify,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "View")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminReportScreenPreview() {
    val reports = listOf(
        Report("RP0000", "Jiji", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0001", "Jiji", LocalDateTime.now(), Status.PENDING),
        Report("RP0002", "Jiji", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0003", "Jiji", LocalDateTime.now(), Status.PENDING),
        Report("RP0004", "Jiji", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0005", "Jiji", LocalDateTime.now(), Status.PENDING),
        Report("RP0006", "Jiji", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0007", "Jiji", LocalDateTime.now(), Status.PENDING),
        Report("RP0008", "Jiji", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0009", "Jiji", LocalDateTime.now(), Status.PENDING),
    )
    TenderUSTheme {
        AdminReportScreen(reports = reports)
    }
}