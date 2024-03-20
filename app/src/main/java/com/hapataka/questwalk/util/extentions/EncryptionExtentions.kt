package com.hapataka.questwalk.util.extentions

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

const val SECRET_KEY = "Q2CR35WC121QCB4T"

fun String.encryptECB(): String {
    val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")

    cipher.init(Cipher.ENCRYPT_MODE, keySpec)

    val cipherText = cipher.doFinal(this.toByteArray())
    val encodeByte = Base64.encode(cipherText, Base64.DEFAULT)

    return String(encodeByte)
}

fun String.decryptECB(): String {
    val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")

    cipher.init(Cipher.DECRYPT_MODE, keySpec)

    val decodeByte = Base64.decode(this, Base64.DEFAULT)
    val output = cipher.doFinal(decodeByte)

    return String(output)
}
