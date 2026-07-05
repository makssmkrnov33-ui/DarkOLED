package com.darkoled.app.engine

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {
    val CAMERA = Manifest.permission.CAMERA
    val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
    val READ_CONTACTS = Manifest.permission.READ_CONTACTS
    val READ_STORAGE = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    fun isGranted(ctx: Context, perm: String): Boolean =
        ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED

    fun allGranted(ctx: Context, perms: List<String>): Boolean =
        perms.all { isGranted(ctx, it) }
}
