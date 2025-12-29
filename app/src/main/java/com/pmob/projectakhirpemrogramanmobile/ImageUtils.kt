package com.pmob.projectakhirpemrogramanmobile.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        return Uri.fromFile(file)
    }
}