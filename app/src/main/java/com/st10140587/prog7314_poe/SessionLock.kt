package com.st10140587.prog7314_poe

object SessionLock {
    @Volatile var unlocked: Boolean = false
    fun lock() { unlocked = false }
    fun unlock() { unlocked = true }
}
