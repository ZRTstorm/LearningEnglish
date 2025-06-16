package com.example.learningenglish.data.util

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

fun recognizeTextFromBitmap(
    bitmap: Bitmap,
    onResult: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText -> onResult(visionText.text) }
        .addOnFailureListener { e -> onError(e) }
}

suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val result = recognizer.process(image).await()
    return result.text
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val stream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(stream)
    } catch (e: Exception) {
        null
    }
}


