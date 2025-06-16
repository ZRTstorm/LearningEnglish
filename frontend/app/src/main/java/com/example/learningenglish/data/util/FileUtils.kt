package com.example.learningenglish.data.util

import android.content.Context
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

suspend fun saveDownloadedFile(
    response: Response<ResponseBody>,
    fileName: String,
    context: Context
): File? {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            val file = File(context.cacheDir, fileName)
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } else null
    } else null
}
