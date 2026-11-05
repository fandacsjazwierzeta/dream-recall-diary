package com.dreamrecall.diary.data.local

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class BackupCodec {

    fun encode(payload: String, passphrase: String): String? = runCatching {
        val salt = ByteArray(SALT_BYTES)
        val iv = ByteArray(IV_BYTES)
        val random = SecureRandom()
        random.nextBytes(salt)
        random.nextBytes(iv)
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, deriveKey(passphrase, salt), GCMParameterSpec(TAG_BITS, iv))
        val sealed = cipher.doFinal(payload.toByteArray(Charsets.UTF_8))
        val blob = ByteArray(salt.size + iv.size + sealed.size)
        System.arraycopy(salt, 0, blob, 0, salt.size)
        System.arraycopy(iv, 0, blob, salt.size, iv.size)
        System.arraycopy(sealed, 0, blob, salt.size + iv.size, sealed.size)
        Base64.encodeToString(blob, Base64.NO_WRAP)
    }.getOrNull()

    fun decode(code: String, passphrase: String): String? = runCatching {
        val blob = Base64.decode(code.trim(), Base64.NO_WRAP)
        require(blob.size > SALT_BYTES + IV_BYTES)
        val salt = blob.copyOfRange(0, SALT_BYTES)
        val iv = blob.copyOfRange(SALT_BYTES, SALT_BYTES + IV_BYTES)
        val sealed = blob.copyOfRange(SALT_BYTES + IV_BYTES, blob.size)
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.DECRYPT_MODE, deriveKey(passphrase, salt), GCMParameterSpec(TAG_BITS, iv))
        String(cipher.doFinal(sealed), Charsets.UTF_8)
    }.getOrNull()

    private fun deriveKey(passphrase: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KDF)
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_BITS)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    private companion object {
        const val TRANSFORM = "AES/GCM/NoPadding"
        const val KDF = "PBKDF2WithHmacSHA256"
        const val SALT_BYTES = 16
        const val IV_BYTES = 12
        const val TAG_BITS = 128
        const val KEY_BITS = 256
        const val ITERATIONS = 12000
    }
}
