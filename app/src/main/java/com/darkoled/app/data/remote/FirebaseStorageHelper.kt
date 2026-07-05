package com.darkoled.app.data.remote

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

suspend fun uploadMedia(uri: Uri, chatId: String, fileName: String = "file"): String? {
    return try {
        val ref = Firebase.storage.reference.child("chats/$chatId/$fileName")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    } catch (e: Exception) {
        null
    }
}
