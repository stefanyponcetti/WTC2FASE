package com.example.wtcapp.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn

class GoogleAuthManager(private val context: Context) {

    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("910531162396-es1qcnjl3cq45g5kkd3eqcdr2e9av35o.apps.googleusercontent.com")
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}