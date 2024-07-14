package com.example.myapplication.authview
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myapplication.User
import com.example.myapplication.authcontroller.EmailRegistration


class EmailRegistration {
    private var TAG = "EmailRegistration"
    @Composable
    fun EmailSendView() {
        var text = remember { mutableStateOf("Hello") }
        var cUser by remember{
            mutableStateOf(
                User("tenderutest", "tenderutest", "ng.nguynv@gmail.com", "", "")
            )}
        Column {
            Button(onClick = {
                EmailRegistration().EmailSend(cUser, text)
            }) {
                Text(text.value)
            }
        }
    }

    @Composable
    fun ConfirmAndSync(deepLink: Uri) {
        var text = remember { mutableStateOf("Confirming and Syncing...") }
        var cUser by remember{
            mutableStateOf(
                User("tenderutest", "tenderutest", "ng.nguynv@gmail.com", "", "")
            )}
        Log.d(TAG, cUser.toString())
        EmailRegistration().ConfirmAndSync(deepLink, cUser, text)
        Column {
            Text(text.value)
        }
    }
}