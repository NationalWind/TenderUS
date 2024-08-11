package com.hcmus.tenderus.utils.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageUtil {

    companion object {
        fun uploadToStorage(uri: Uri, context: Context, type: String, callback:(url: String) -> Unit = {}) {
            val storage = Firebase.storage

            // Create a storage reference from our app
            val storageRef = storage.reference

            val uniqueImageName = UUID.randomUUID()

            val spaceRef = if (type == "image"){
                storageRef.child("images/$uniqueImageName.jpg")
            }else{
                storageRef.child("audio/$uniqueImageName.mp3")
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
                    callback(spaceRef.downloadUrl.toString())
                }
            }

        }

    }
}