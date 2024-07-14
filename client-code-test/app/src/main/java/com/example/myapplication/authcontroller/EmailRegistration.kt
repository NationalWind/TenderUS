package com.example.myapplication.authcontroller

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.myapplication.User
import com.google.firebase.auth.actionCodeSettings

class EmailRegistration() {
    private var TAG = "EmailRegistration"
    val auth = Firebase.auth


    fun EmailSend(user: User, text: MutableState<String>) {
        val actionCodeSettings = actionCodeSettings {
            // URL you want to redirect back to. The domain (www.example.com) for this
            // URL must be whitelisted in the Firebase Console.
            url = "https://play.google.com/"
            // This must be true
            handleCodeInApp = true
            setAndroidPackageName(
                "com.example.myapplication",
                true, // installIfNotAvailable
                "12", // minimumVersion
            )
        }
        Firebase.auth.sendSignInLinkToEmail(user.email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    text.value = "OK!"
                }
            }
    }

    fun ConfirmAndSync(deepLink: Uri, user:User, text: MutableState<String>){
        val emailLink = deepLink.toString()
        Log.d(TAG, emailLink)
        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(user.email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully signed in with email link!")
                        val result = task.result
                        // You can access the new user via result.getUser()
                        // Additional user info profile *not* available via:
                        // result.getAdditionalUserInfo().getProfile() == null
                        // You can check if the user is new or existing:
                        // result.getAdditionalUserInfo().isNewUser()


                        text.value = "Done!"
                    } else {
                        Log.e(TAG, "Error signing in with email link", task.exception)
                    }
                }
        }
    }

}