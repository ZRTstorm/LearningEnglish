package com.example.learningenglish.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.learningenglish.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth = Firebase.auth
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    suspend fun getIdToken(): String? {
        return auth.currentUser?.getIdToken(true)?.await()?.token
    }


    fun signInWithGoogle(activity: Activity, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        launcher.launch(googleSignInClient.signInIntent)
    }

    suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return auth.signInWithCredential(credential).await().user
    }

    suspend fun signUpWithEmail(email: String, password: String, nickname: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
        )?.await()
        return result.user
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).await().user
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun logout() = auth.signOut()

    suspend fun signOutGoogle() {
        googleSignInClient.signOut().await()
    }

}

