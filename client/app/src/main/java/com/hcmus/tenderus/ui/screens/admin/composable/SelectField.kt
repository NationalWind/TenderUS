package com.hcmus.tenderus.ui.screens.admin.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hcmus.tenderus.ui.theme.TenderUSTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(
    options: List<String>,
    selected: String,
    select: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = { select(it) },
                readOnly = true,
                label = { Text("Select an option") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFFFFFFFE))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        select(option)
                        expanded = false
                    })
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectFieldPreview() {
    TenderUSTheme {
        SelectField(options = listOf("A", "B", "C"), selected = "A", select = {})
    }
}