package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.st10140587.prog7314_poe.auth.BiometricGate
import timber.log.Timber

class SignInActivity : AppCompatActivity() {
    // 🔹 Make MainActivity use the saved language
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var btnGoogle: Button
    private lateinit var btnDoSignIn: Button
    private lateinit var tvSignUp: TextView

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
        try {
            val acct = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            startLoading()
            auth.signInWithCredential(credential)
                .addOnSuccessListener { proceedToAppAfterSignIn("google") }
                .addOnFailureListener { e -> fail("Firebase sign-in with Google credential failed", e) }
                .addOnCompleteListener { stopLoading() }
        } catch (e: Exception) {
            val status = (e as? ApiException)?.statusCode
            val msg = when (status) {
                CommonStatusCodes.CANCELED -> "Google sign-in cancelled"
                CommonStatusCodes.NETWORK_ERROR -> "Network error"
                CommonStatusCodes.API_NOT_CONNECTED -> "Google Play services issue"
                CommonStatusCodes.DEVELOPER_ERROR ->
                    "DEVELOPER_ERROR: check google-services.json, package name, and SHA-1/SHA-256 in Firebase"
                else -> "Google sign-in error (code $status)"
            }
            fail(msg, e)
            stopLoading()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already signed in (cold start), show biometrics immediately.
        auth.currentUser?.let {
            promptBiometricThenEnter()
            return
        }

        setContentView(R.layout.activity_sign_in)

        btnGoogle   = findViewById(R.id.btnGoogle)
        btnDoSignIn = findViewById(R.id.btnDoSignIn)
        tvSignUp    = findViewById(R.id.tvSignUp)

        btnGoogle.setOnClickListener { startGoogle() }
        btnDoSignIn.setOnClickListener {
            val email = findViewById<android.widget.EditText>(R.id.etEmail).text?.toString()?.trim()
            val password = findViewById<android.widget.EditText>(R.id.etPassword).text?.toString()?.trim()

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                startLoading()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { proceedToAppAfterSignIn("email") }
                    .addOnFailureListener { e -> fail("Email sign-in failed", e) }
                    .addOnCompleteListener { stopLoading() }
            }
        }
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun promptBiometricThenEnter() {
        val gate = BiometricGate(
            activity = this as FragmentActivity,
            onSuccess = {
                SessionLock.unlock()
                Timber.d("Biometric success → MainActivity")
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            },
            onFailOrCancel = { reason ->
                Timber.w("Biometric failed/cancelled: $reason")
                // Stay on this screen if user cancels
            }
        )
        if (gate.isAvailable(this)) gate.show()
        else {
            // No biometrics enrolled → allow entry
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun proceedToAppAfterSignIn(source: String) {
        Timber.d("Auth OK via $source: ${auth.currentUser?.email}")
        promptBiometricThenEnter()
    }

    private fun startGoogle() {
        startLoading()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, gso)
        googleClient.signOut().addOnCompleteListener {
            googleLauncher.launch(googleClient.signInIntent)
        }
    }

    private fun startLoading() {
        findViewById<View>(R.id.progress)?.visibility = View.VISIBLE
        if (this::btnGoogle.isInitialized) btnGoogle.isEnabled = false
        if (this::btnDoSignIn.isInitialized) btnDoSignIn.isEnabled = false
        if (this::tvSignUp.isInitialized) tvSignUp.isEnabled = false
    }
    private fun stopLoading() {
        findViewById<View>(R.id.progress)?.visibility = View.GONE
        if (this::btnGoogle.isInitialized) btnGoogle.isEnabled = true
        if (this::btnDoSignIn.isInitialized) btnDoSignIn.isEnabled = true
        if (this::tvSignUp.isInitialized) tvSignUp.isEnabled = true
    }

    private fun fail(msg: String, e: Exception) {
        Timber.e(e, msg)
        Toast.makeText(this, "$msg: ${e.localizedMessage ?: "unknown"}", Toast.LENGTH_LONG).show()
    }
}
