package com.hapataka.questwalk.feature.onboarding.util

import android.app.Activity
import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.hapataka.questwalk.feature.onboarding.BuildConfig

internal suspend fun getCredential(
    context: Context,
): Result<Credential> {
    return runCatching {
        val credentialManager = CredentialManager.create(context)
        val credentialOption = buildGoogleOptions()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(credentialOption)
            .build()

        credentialManager.getCredential(
            request = request,
            context = context as Activity
        ).credential
    }
}

private fun buildGoogleOptions(): GetGoogleIdOption {
    return GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()
}