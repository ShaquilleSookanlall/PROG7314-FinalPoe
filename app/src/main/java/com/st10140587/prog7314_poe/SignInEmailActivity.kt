package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.st10140587.prog7314_poe.auth.BiometricGate
import timber.log.Timber

class SignInEmailActivity : AppCompatActivity() {
    // 🔹 Make MainActivity use the saved language
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.title = getString(R.string.sign_in_with_email)

        // If already signed in (rare on this screen), prompt biometrics now
        auth.currentUser?.let {
            promptBiometricThenEnter()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass  = findViewById<EditText>(R.id.etPassword)
        val btn     = findViewById<Button>(R.id.btnDoSignIn)

        btn.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass  = etPass.text?.toString()?.trim().orEmpty()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.error = "Invalid email"; return@setOnClickListener }
            if (pass.isBlank()) { etPass.error = "Enter password"; return@setOnClickListener }

            btn.isEnabled = false
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    Timber.d("Email sign-in OK: ${auth.currentUser?.email}")
                    promptBiometricThenEnter()
                }
                .addOnFailureListener { e ->
                    Timber.e(e, "Email sign-in failed")
                    Toast.makeText(this, e.localizedMessage ?: "Sign in failed", Toast.LENGTH_LONG).show()
                }
                .addOnCompleteListener { btn.isEnabled = true }
        }
    }

    private fun promptBiometricThenEnter() {
        val gate = BiometricGate(
            activity = this as FragmentActivity,
            onSuccess = {
                SessionLock.unlock()
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            },
            onFailOrCancel = {
                // stay on this screen if user cancels
            }
        )
        if (gate.isAvailable(this)) gate.show()
        else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
