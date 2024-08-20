//package com.hcmus.tenderus.ui.screens.admin.composable
//
//import androidx.compose.material3.DatePicker
//import androidx.compose.material3.DatePickerDialog
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//
//OutlinedTextField(
//value = Date(datePickerState.selectedDateMillis ?: 0).toString(),
//readOnly = true,
//onValueChange = {},
//label = { Text("Restrict duration") },
//modifier = Modifier
//.fillMaxWidth()
//.clickable { showDatePicker = true }
//)
//if (showDatePicker) {
//    DatePickerDialog(
//        onDismissRequest = { showDatePicker = false },
//        confirmButton = {
//            TextButton(onClick = {
//                showDatePicker = false
//
//            }) {
//                Text(text = "Confirm")
//            }
//        }
//    ) {
//        DatePicker(state = datePickerState)
//    }
//}