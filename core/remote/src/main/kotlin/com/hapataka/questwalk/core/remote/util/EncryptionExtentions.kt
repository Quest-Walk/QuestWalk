package com.hapataka.questwalk.core.remote.util

import android.util.Base64
import com.hapataka.questwalk.core.model.Location
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

// TODO: 종속성 제거 후 internal로 바꿔야함
fun String.encryptECB(encryptKey: String): String {
    val keySpec = SecretKeySpec(encryptKey.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")

    cipher.init(Cipher.ENCRYPT_MODE, keySpec)

    val cipherText = cipher.doFinal(this.toByteArray())
    val encodeByte = Base64.encode(cipherText, Base64.DEFAULT)

    return String(encodeByte)
}

@JvmName("encryptLocationList")
fun List<Location>.encryptECB(encryptKey: String): String {
    val pairList = this.map { Pair(it.latitude, it.longitude) }
    val json = Json { ignoreUnknownKeys = true }
    val jsonString = json.encodeToJsonElement(pairList).toString()
    val keySpec = SecretKeySpec(encryptKey.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")

    cipher.init(Cipher.ENCRYPT_MODE, keySpec)

    val cipherText = cipher.doFinal(jsonString.toByteArray())
    val encodeByte = Base64.encode(cipherText, Base64.DEFAULT)

    return String(encodeByte)
}

fun Location.encryptECB(encryptKey: String): String {
    val pair = Pair(this.latitude, this.longitude)
    val json = Json { ignoreUnknownKeys = true }
    val jsonString = json.encodeToJsonElement(pair).toString()
    val keySpec = SecretKeySpec(encryptKey.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")

    cipher.init(Cipher.ENCRYPT_MODE, keySpec)

    val cipherText = cipher.doFinal(jsonString.toByteArray())
    val encodeByte = Base64.encode(cipherText, Base64.DEFAULT)

    return String(encodeByte)
}

fun String.decryptECB(encryptKey: String): String {
    val keySpec = SecretKeySpec(encryptKey.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING")

    cipher.init(Cipher.DECRYPT_MODE, keySpec)

    val decodeByte = Base64.decode(this, Base64.DEFAULT)
    val output = cipher.doFinal(decodeByte)

    return String(output)
}
