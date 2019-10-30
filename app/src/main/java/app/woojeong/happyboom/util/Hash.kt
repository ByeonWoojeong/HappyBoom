package com.dev.hongsw.happyBoom.util

import java.security.MessageDigest

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 7
 * @license Copyright 2019. H&S All rights reserved.
 **/

//internal fun String?.sha512() = hashString("SHA-512", this)

//internal fun String?.sha256() = hashString("SHA-256", this)

internal fun String?.sha1() = hashString("SHA-1", this)

private fun hashString(type: String, input: String?): String {
    if (input == null) return "empty"
    val hex = "0123456789ABCDEF"
    val bytes = MessageDigest
        .getInstance(type)
        .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(hex[i shr 4 and 0x0f])
        result.append(hex[i and 0x0f])
    }

    return result.toString()
}