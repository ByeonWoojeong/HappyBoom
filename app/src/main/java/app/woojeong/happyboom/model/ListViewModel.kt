package app.woojeong.happyboom.model

import androidx.annotation.StringRes
import app.woojeong.happyboom.R
import com.dev.hongsw.happyBoom.subtitle.Subtitle
import com.dev.hongsw.happyBoom.util.toSeconds
import com.dev.hongsw.happyBoom.util.toTimeHintString

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/

class ListViewModel(val subtitle: Subtitle) {


    var startTime: String = ""
    set(value) {
        if (value.isNotBlank()){
            subtitle.Start = value.toTimeHintString()
        }
        field = try {
            value.toInt().toString()
        } catch (e: Exception) {
            value
        }
    }
    var endTime: String = ""
        set(value) {
            if (value.isNotBlank()){
                subtitle.End = value.toTimeHintString()
            }
            field = try {
                value.toInt().toString()
            } catch (e: Exception) {
                value
            }
        }
    var tts: String = ""
    var ttsVolume: Float = 1f
    var videoPlayTime: Int = 0
    var timeHint: String = ""

    fun setTimeHint(time: Long) {
        timeHint = "End (Max: ${time.toSeconds()} sec)"
        videoPlayTime = (time / 1000).toInt()
    }

    @StringRes
    fun isVirginComplete(): Int {
        if (startTime.isBlank() || endTime.isBlank() || startTime.toInt() > videoPlayTime || endTime.toInt() > videoPlayTime) return R.string.check_time
        if (startTime.toInt() > endTime.toInt()) return R.string.end_faster_than_start
        if (subtitle.Text.isBlank()) return R.string.enter_contents
        if (tts.isBlank()) return R.string.select_voice_type
        return R.string.no_problem
    }

}