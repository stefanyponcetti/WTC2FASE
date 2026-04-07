package com.example.wtcapp.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn

class GoogleAuthManager(private val context: Context) {

    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("709322502085-0rotrcvap2gdmdvqp5i0r3tg30v82kij.apps.googleusercontent.com")
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }
}