package com.st10140587.prog7314_poe

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.st10140587.prog7314_poe.notify.NotificationHelper
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Logging
        Timber.plant(Timber.DebugTree())
        NotificationHelper.createChannel(this)

        // Observe app-level foreground/background to control the biometric lock
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppForegroundObserver)
    }
}

/**
 * Locks the session ONLY when the whole app goes to background.
 * This prevents re-prompting biometrics when simply navigating between screens.
 */
private object AppForegroundObserver : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        // App no longer visible → lock until next foreground
        Timber.d("App moved to background → locking session")
        SessionLock.lock()
    }
    // We do not auto-unlock onStart/onResume; MainActivity will prompt as needed.
}
