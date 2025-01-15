package com.hapataka.questwalk.core.common.extention

import java.math.BigInteger
import java.security.MessageDigest

fun String.generateUid(): String {
    // 문자를 SHA-256을 사용해 해싱
    val digest = MessageDigest.getInstance("SHA-256")
    val hashByte = digest.digest(this.toByteArray())
    // 최종적으로 사용될 문자 62개
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    // 해싱된 바이트를 숫자로 변환
    val bigInt = BigInteger(1, hashByte)
    val base62 = StringBuilder()
    var num = bigInt
    val base = allowedChars.length.toLong()

    // 숫자가 0보다 작거나 uid의 길이가 24보다 작을때 까지 반복
    while (num > BigInteger.ZERO && base62.length < 24) {
        // 사용 가능한 문자 인덱스 추출을 위해 mod를 사용해 인덱스 추출
        val index = num.mod(BigInteger.valueOf(base)).toInt()

        base62.append(allowedChars[index])
        num = num.divide(BigInteger.valueOf(base))
    }

    return base62.toString().padStart(24, allowedChars[0])
}