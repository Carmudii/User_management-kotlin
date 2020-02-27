package com.user.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class FileUtil {

    companion object {
        fun getRealPathFromURI(contentURI: Uri, context: Context): String? {
            var result: String?
            val cursor = context.contentResolver
                .query(contentURI, null, null, null, null)
            if (cursor == null) {
                result = contentURI.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex("_data")
                result = cursor.getString(idx)
                cursor.close()
            }
            return result
        }
    }
}