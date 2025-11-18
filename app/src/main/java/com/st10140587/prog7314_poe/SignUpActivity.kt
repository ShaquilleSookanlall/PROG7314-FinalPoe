package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.st10140587.prog7314_poe.auth.BiometricGate
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {
    // 🔹 Make MainActivity use the saved language
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.title = getString(R.string.create_account)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btn = findViewById<Button>(R.id.btnDoSignUp)
        val tvAlreadyAccount = findViewById<TextView>(R.id.tvAlreadyAccount)

        btn.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPass.text?.toString()?.trim().orEmpty()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Invalid email"; return@setOnClickListener
            }
            if (pass.length < 6) {
                etPass.error = "Min 6 characters"; return@setOnClickListener
            }

            btn.isEnabled = false
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    val update = UserProfileChangeRequest.Builder()
                        .setDisplayName(name.ifBlank { null }).build()
                    auth.currentUser?.updateProfile(update)

                    Timber.d("Account created & user signed in: ${auth.currentUser?.email}")
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                    promptBiometricThenEnter()
                }
                .addOnFailureListener { e ->
                    Timber.e(e, "Sign up failed")
                    Toast.makeText(this, e.localizedMessage ?: "Sign up failed", Toast.LENGTH_LONG).show()
                }
                .addOnCompleteListener { btn.isEnabled = true }
        }

        tvAlreadyAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
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
                // If user cancels on sign-up flow, keep them here (or navigate back to SignInActivity if you prefer)
            }
        )
        if (gate.isAvailable(this)) gate.show()
        else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
