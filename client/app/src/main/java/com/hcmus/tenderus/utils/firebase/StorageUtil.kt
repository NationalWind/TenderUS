package com.hcmus.tenderus.utils.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageUtil {

    companion object {
        fun uploadToStorage(auth: FirebaseAuth, uri: Uri, context: Context, type: String, callback:(url: String) -> Unit = {}) {
            val storage = Firebase.storage

            // Create a storage reference from our app
            val storageRef = storage.reference

            val uniqueImageName = UUID.randomUUID()

            val spaceRef = if (type == "Image"){
                storageRef.child("users/${auth.currentUser!!.uid}/$uniqueImageName.jpg")
            }else{
                storageRef.child("users/${auth.currentUser!!.uid}/$uniqueImageName.mp3")
            }

            val byteArray: ByteArray? = context.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }

            byteArray?.let{

                var uploadTask = spaceRef.putBytes(byteArray)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    spaceRef.downloadUrl.addOnSuccessListener {
                        callback(it.toString())
                    }
                }
            }

        }

    }
}