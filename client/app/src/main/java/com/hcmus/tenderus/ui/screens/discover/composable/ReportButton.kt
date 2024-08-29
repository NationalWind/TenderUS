package com.hcmus.tenderus.ui.screens.discover.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.ReportData
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.viewmodels.DiscoverVM

@Composable
fun ReportButton(reported: String?, modifier: Modifier = Modifier, message: String = "") {
    var isOpen by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val discoverViewModel: DiscoverVM = viewModel(factory = DiscoverVM.Factory)

    IconButton(
        onClick = { isOpen = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Report,
            contentDescription = "Report user",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    if (isOpen) {
        PopupDialog(
            options = listOf(
                "Fake profile",
                "Spam",
                "Inappropriate message",
                "Inappropriate photos",
                "Underage member"
            ),
            selectAction = { selectedOption = it },
            closeAction = { isOpen = false },
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Report",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "We will not notify the user", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }

    LaunchedEffect(selectedOption) {
        if (selectedOption != null && reported != null) {
            val reportData = ReportData(
                reporter = TokenManager.getToken() ?: "",
                reported = reported,
                message = "$selectedOption $message"
            )
            discoverViewModel.postReport(reportData)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportButtonPreview() {
    TokenManager.init(LocalContext.current)
    TenderUSTheme {
        ReportButton(reported = "Orange")
    }
}