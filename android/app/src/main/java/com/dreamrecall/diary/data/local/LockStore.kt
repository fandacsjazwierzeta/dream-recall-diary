package com.dreamrecall.diary.data.local

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

class LockStore(context: Context) {

    private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun hasPin(): Boolean = prefs.getString(KEY_HASH, null) != null

    fun definePin(pin: String) {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        prefs.edit()
            .putString(KEY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(KEY_HASH, hash(pin, salt))
            .apply()
    }

    fun clearPin() {
        prefs.edit().remove(KEY_SALT).remove(KEY_HASH).apply()
    }

    fun matches(pin: String): Boolean {
        val storedSalt = prefs.getString(KEY_SALT, null) ?: return false
        val storedHash = prefs.getString(KEY_HASH, null) ?: return false
        val salt = runCatching { Base64.decode(storedSalt, Base64.NO_WRAP) }.getOrNull() ?: return false
        return hash(pin, salt) == storedHash
    }

    private fun hash(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return Base64.encodeToString(digest.digest(pin.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }

    private companion object {
        const val FILE = "dream_recall_lock"
        const val KEY_SALT = "pin_salt"
        const val KEY_HASH = "pin_hash"
    }
}
