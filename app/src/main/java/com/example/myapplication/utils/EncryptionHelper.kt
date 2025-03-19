package com.example.myapplication.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {
    private const val SECRET_KEY = "1234567890123456"

    private fun getSecretKeySpec(): SecretKeySpec {
        return SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec())
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec())
        val decodedBytes = Base64.decode(data, Base64.DEFAULT)
        return String(cipher.doFinal(decodedBytes))
    }

}