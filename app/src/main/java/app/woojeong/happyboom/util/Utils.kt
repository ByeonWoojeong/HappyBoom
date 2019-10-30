package com.dev.hongsw.happyBoom.util

import android.app.Activity
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.TimeUnit
import android.graphics.Bitmap
import android.util.Log
import app.woojeong.happyboom.model.ListViewModel
import kotlin.math.log


/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 3
 * @license Copyright 2019. H&S All rights reserved.
 **/

internal fun Long.toSeconds(): String {
    return TimeUnit.MILLISECONDS.toSeconds(this).toString()
}

//[H:MM:SS.ss]
internal fun String.toTimeHintString(): String {
    return String.format("00:%02d:%02d.00", this.toInt() / 60, this.toInt() % 60)
}

internal fun String.hahahohomillis(): Long {
    return (this.substring(3,5).toLong() * 60000) + (this.substring(6,8).toLong() * 1000)
}

internal fun InputStream.toWrite(context: Context, fileName: String): File?{
    val file = File(context.cacheDir, fileName)
    if (file.exists()) file.delete()
    try {
        val outputStream = FileOutputStream(file)
        this.copyTo(outputStream)
        this.close()
        outputStream.flush()
        outputStream.close()
        return file
    } catch (e: Exception) {
        throw e
    }
}

fun getEncodingCommand(width: Int, height: Int, input: File, image: File?, subtitle: File?, output: File, userInputImage: ArrayList<File?>, skip : Boolean): ArrayList<String>  {
    val imageWidth = ((width * 0.1f).toInt() * 1.5).toInt()
        Log.i("encoding", image!!.absolutePath);

    val transparent =  if(skip)0; else 0.6;

        val array = arrayListOf("-hide_banner", "-y", "-i", input.absolutePath, "-i", image.absolutePath)
        var filter = "[1]scale=$imageWidth:-1, format=rgba, colorchannelmixer=aa=${transparent}[wm];[0:v][wm]overlay=0:0,subtitles=${subtitle!!.absolutePath}[out];"
        if (userInputImage[0] != null && userInputImage[1] != null) {
            userInputImage.forEach { it -> it?.let { array.addAll(arrayOf("-loop", "1", "-t", "3", "-i", it.absolutePath)) } }
            array.addAll(arrayOf("-f", "lavfi", "-t", "3", "-i", "anullsrc", "-c:v", "libx264", "-crf", "28", "-filter_complex"))
            filter += "[2]scale=w=$width:h=$height:force_original_aspect_ratio=decrease,pad=$width:$height:(ow-iw)/2:(oh-ih)/2,setsar=1:1[out1];" +
                    "[3]scale=w=$width:h=$height:force_original_aspect_ratio=decrease,pad=$width:$height:(ow-iw)/2:(oh-ih)/2,setsar=1:1[out2];[out1][4:a][out][0:a][out2][4:a]concat=n=3:v=1:a=1"
            array.add(filter)
        } else if (userInputImage[0] == null && userInputImage[1] != null) {
            userInputImage.forEach { it -> it?.let { array.addAll(arrayOf("-loop", "1", "-t", "3", "-i", it.absolutePath)) } }
            array.addAll(arrayOf("-f", "lavfi", "-t", "3", "-i", "anullsrc", "-c:v", "libx264", "-crf", "28", "-filter_complex"))
            filter += "[2]scale=w=$width:h=$height:force_original_aspect_ratio=decrease,pad=$width:$height:(ow-iw)/2:(oh-ih)/2,setsar=1:1[out1];" +
                    "[out][0:a][out1][3:a]concat=n=2:v=1:a=1"
            array.add(filter)
        } else if (userInputImage[0] != null && userInputImage[1] == null) {
            userInputImage.forEach { it -> it?.let { array.addAll(arrayOf("-loop", "1", "-t", "3", "-i", it.absolutePath)) } }
            array.addAll(arrayOf("-f", "lavfi", "-t", "3", "-i", "anullsrc", "-c:v", "libx264", "-crf", "28", "-filter_complex"))
            filter += "[2]scale=w=$width:h=$height:force_original_aspect_ratio=decrease,pad=$width:$height:(ow-iw)/2:(oh-ih)/2,setsar=1:1[out1];" +
                    "[out1][3:a][out][0:a]concat=n=2:v=1:a=1"
            array.add(filter)
        } else {
            array.addAll(arrayOf("-c:v", "libx264", "-crf", "28", "-filter_complex"))
            array.add("[1]scale=$imageWidth:-1, format=rgba, colorchannelmixer=aa=${transparent}[wm];[0][wm]overlay=0:0,subtitles=${subtitle.absolutePath}")
        }
        array.addAll(arrayOf("-preset", "veryfast", output.absolutePath))
    return array

}

fun getSoundMixCommand(context: Context, inputVideo: File, ttsAudio: ArrayList<ListViewModel>?, musicAudio: String?, recordAudio: String?, output: File, videoVolume: Float, ttsVolume: Float, playTime: Int): ArrayList<String> {
    val command = ArrayList<String>()
    command.add("-hide_banner")
    command.add("-y")
    command.add("-i")
    command.add(inputVideo.absolutePath)
    ttsAudio?.forEachIndexed{ _, listViewModel ->
        if (listViewModel.subtitle.available) {
            command.add("-i")
            command.add(File(context.filesDir, listViewModel.subtitle.Text.sha1() + ".mp3").absolutePath)
        }
    }
    musicAudio?.let {
        command.add("-i")
        command.add(it)
    }
    recordAudio?.let {
        command.add("-i")
        command.add(it)
    }
    command.add("-filter_complex")
    var mixcommand = ""
    mixcommand += "[0]adelay=0|0,volume=$videoVolume[a0];"
    var inputCount = 1
    ttsAudio?.forEachIndexed{ _, listViewModel ->
        if (listViewModel.subtitle.available) {
            mixcommand += "[$inputCount]adelay=${listViewModel.subtitle.Start.hahahohomillis()}|${listViewModel.subtitle.Start.hahahohomillis()},volume=${listViewModel.ttsVolume * ttsVolume}[a${inputCount++}];"
        }
    }
    musicAudio?.let {
        mixcommand += "[1]adelay=0|0,volume=$ttsVolume[a1];"
    }
    recordAudio?.let {
        mixcommand += "[1]adelay=0|0,volume=$ttsVolume[a1];"
    }
    mixcommand += "[a0]"
    inputCount = 1
    ttsAudio?.forEachIndexed { _, listViewModel ->
        if (listViewModel.subtitle.available) {
            mixcommand += "[a${inputCount++}]"

        }
    }
    musicAudio?.let {
        mixcommand += "[a${inputCount++}]"
    }
    recordAudio?.let {
        mixcommand += "[a${inputCount++}]"
    }
    mixcommand += "amix=$inputCount"
    command.add(mixcommand)
    command.add("-c:v")
    command.add("copy")
    command.add("-t")
    command.add(playTime.toString())
    command.add(output.absolutePath)

    return command
}

fun download(link: URL, path: File): File? {
    var file: File? = null
    Thread {
        try {
            if (path.exists()) path.delete()
            link.openStream().use { input ->
                FileOutputStream(path).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        file = path
    }.run {isDaemon = true; start(); join()}
    return file
}


fun scaleDown(image: Bitmap, videoWidth: Int): Bitmap? {

    val width = (videoWidth * 0.15).toInt()
    val height = image.height * (width.toFloat() / image.width.toFloat())

    return if (width > 0 && height > 0) {
        Bitmap.createScaledBitmap(
            image, (videoWidth * 0.15).toInt(),
            height.toInt(), true
        )
    } else {
        null
    }
}

fun isAudioFile(path: String): Boolean {
    val array = arrayOf("mp3", "ogg", "wav", "wma", "aac", "flac", "mpc", "tta", "m4a")

    return array.indexOf(path.split(".").last().toLowerCase()) != -1
}