package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.Status
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReportListScreen(reports: List<Report>) {
    Scaffold(
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
                model = "https://us-tuna-sounds-images.voicemod.net/6d7e6aff-da39-4fb8-be82-5f12ee5fc75c-1698204266163.png",
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
                    text = """
                                Leute, wir sind von einem Waffenstillstand zu einem Ausfall 
                                übergegangen. Die Führung macht einfach, was sie will. Aber da es 
                                unser Job ist, haben wir keine Wahl. Wir müssen unseren Befehlen 
                                folgen und den Feind vernichten. Aber jetzt ist unser Feind die 
                                ganze Welt. Es gibt keine Möglichkeit, einen grausamen Krieg zu 
                                vermeiden, und wir müssen endlos lange kämpfen.""".trimIndent(),
                    // Will change after having api
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
fun ReportListScreenPreview() {
    val reports = listOf(
        Report("RP0000", "Hehe", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0001", "Hehe", LocalDateTime.now(), Status.PENDING),
        Report("RP0002", "Hehe", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0003", "Hehe", LocalDateTime.now(), Status.PENDING),
        Report("RP0004", "Hehe", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0005", "Hehe", LocalDateTime.now(), Status.PENDING),
        Report("RP0006", "Hehe", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0007", "Hehe", LocalDateTime.now(), Status.PENDING),
        Report("RP0008", "Hehe", LocalDateTime.now(), Status.REVIEWED),
        Report("RP0009", "Hehe", LocalDateTime.now(), Status.PENDING),
    )
    TenderUSTheme {
        ReportListScreen(reports = reports)
    }
}