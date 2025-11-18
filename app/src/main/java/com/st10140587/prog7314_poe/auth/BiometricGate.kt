package com.st10140587.prog7314_poe.auth

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.st10140587.prog7314_poe.SessionLock

class BiometricGate(
    private val activity: FragmentActivity,
    private val onSuccess: () -> Unit,
    private val onFailOrCancel: (String) -> Unit
) {
    private fun authFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        }
    }

    fun isAvailable(context: Context): Boolean {
        val res = BiometricManager.from(context).canAuthenticate(authFlags())
        return res == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun show() {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    SessionLock.unlock()
                    onSuccess()
                }
                override fun onAuthenticationError(code: Int, err: CharSequence) {
                    onFailOrCancel("Error: $err")
                }
                override fun onAuthenticationFailed() {
                    onFailOrCancel("Failed")
                }
            })

        val infoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Weather")
            .setSubtitle("Use biometrics to continue")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            infoBuilder.setAllowedAuthenticators(authFlags())
        } else {
            @Suppress("DEPRECATION")
            infoBuilder.setDeviceCredentialAllowed(true)
        }

        prompt.authenticate(infoBuilder.build())
    }
}
