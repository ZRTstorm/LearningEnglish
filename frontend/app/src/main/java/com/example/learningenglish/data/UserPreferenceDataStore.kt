package com.example.learningenglish.data.util

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

suspend fun getIdTokenOnce(): String? {
    return Firebase.auth.currentUser?.getIdToken(true)?.await()?.token
}
