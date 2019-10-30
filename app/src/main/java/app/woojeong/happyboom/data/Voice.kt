package com.dev.hongsw.happyBoom.data

import android.speech.tts.Voice

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/

data class Voice(val name: String, val voice: Voice?) {
    override fun equals(other: Any?): Boolean {
        return this.name == other
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + voice.hashCode()
        return result
    }
}