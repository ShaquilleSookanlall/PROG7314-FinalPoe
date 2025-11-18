package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.firebase.auth.FirebaseAuth
import com.st10140587.prog7314_poe.auth.BiometricGate
import timber.log.Timber

class LandingActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    //Comment to push
    // 🔹 Make this Activity use the saved language for all getString()/layouts
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        supportActionBar?.hide()

        // Start parallax MotionLayout animation
        val motion = findViewById<MotionLayout>(R.id.motionRoot)
        motion?.post {
            motion.transitionToEnd()
        }

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            // Already signed in – require biometrics before entering the app
            val gate = BiometricGate(
                activity = this,
                onSuccess = {
                    Timber.d("Biometric success, opening MainActivity")
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                    )
                    finish()
                },
                onFailOrCancel = { reason ->
                    Timber.w("Biometric failed/cancelled: $reason")
                    // You can keep the user on landing screen here
                }
            )

            if (gate.isAvailable(this)) {
                gate.show()
            } else {
                // Fallback if no biometrics enrolled: just continue
                Timber.i("Biometrics not available – proceeding without")
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                )
                finish()
            }
        }
    }
}
