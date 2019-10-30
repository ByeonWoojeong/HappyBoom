package com.dev.hongsw.happyBoom.subtitle

import android.content.Context
import app.woojeong.happyboom.subtitle.DefaultStyle
import java.io.BufferedWriter
import java.io.File

/**
 *@author Hong Seung Woo <qksn1541@gmail.com>
 *@since 19. 6. 2
 *@license Copyright 2019. H&S All rights reserved.
 **/

class AssWriter(context: Context) {

    private val defaultOne = "[Script Info]\n" +
            ";\n" +
            "Title : H&S\n" +
            "Original Script : Hong Seung Woo\n" +
            "ScriptType: v4.00+\n" +
//            "Collisions: Normal\n" +
            "\n" +
            "WrapStyle: 0\n" +
            "ScaledBorderAndShadow: yes\n" +
            "YCbCr Matrix: None\n"

    private val defaultTwo = "[Aegisub Project Garbage]\n" +
            "[V4+ Styles]\n" +
            "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n"

    private val defaultTh = "[Events]\n" +
            "Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n"


    private val mFile : File = File(context.cacheDir,"H.ass")
    private val mBufferedWriter: BufferedWriter

    init {
        mBufferedWriter = mFile.bufferedWriter()
        mBufferedWriter.write(defaultOne)
    }

    fun addPlayRec(x: Int, y: Int) {
        mBufferedWriter.write("Collisions: Normal\nPlayResX: $x\nPlayResY: $y\n")
        mBufferedWriter.write(defaultTwo)
    }

    fun addStyle(defaultStyle: DefaultStyle) {
        mBufferedWriter.write(defaultStyle.toString())
        mBufferedWriter.write(defaultTh)
    }

    fun addSubtitles(subtitles: List<Subtitle>) {
        subtitles.filter { it.available }.forEach { mBufferedWriter.write(it.toString()) }
    }

    fun endWrite() {
        mBufferedWriter.close()
    }
}