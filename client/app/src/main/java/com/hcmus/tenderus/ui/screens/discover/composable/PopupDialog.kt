package com.hcmus.tenderus.ui.screens.discover.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hcmus.tenderus.ui.theme.PinkPrimary
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@Composable
fun PopupDialog(
    options: List<String>,
    selectAction: (String) -> Unit,
    closeAction: () -> Unit,
    title: @Composable () -> Unit
) {
    Dialog(onDismissRequest = closeAction) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.padding(vertical = 12.dp)) {
                    title()
                }
                options.forEach { option ->
                    HorizontalDivider(thickness = 0.5.dp)
                    Text(
                        text = option,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectAction(option)
                                closeAction()
                            }
                            .padding(vertical = 12.dp)
                    )
                }
                HorizontalDivider(thickness = 0.5.dp)
                Text(
                    text = "Cancel",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { closeAction() }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PopupDialogPreview() {
    TenderUSTheme {
        PopupDialog(
            options = listOf("Apple", "Orange", "Lemon", "Kiwi", "Grape"),
            selectAction = { },
            closeAction = { },
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "FRUIT MENU", color = PinkPrimary, fontWeight = FontWeight.Bold)
                Text(text = "Pick one", color = Color.Gray)
            }
        }
    }
}