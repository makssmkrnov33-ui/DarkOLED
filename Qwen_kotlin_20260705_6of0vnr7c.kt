suspend fun uploadMedia(uri: Uri, chatId: String): String {
    val storage = Firebase.storage
    val ref = storage.reference.child("chats/$chatId/${System.currentTimeMillis()}_${uri.lastPathSegment}")
    ref.putFile(uri).await()
    return ref.downloadUrl.await().toString()
}