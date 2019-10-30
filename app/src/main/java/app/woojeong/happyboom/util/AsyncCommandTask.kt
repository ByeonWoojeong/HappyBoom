package com.dev.hongsw.happyBoom.util

import android.os.AsyncTask
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.util.RunCallback

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 5
 * @license Copyright 2019. H&S All rights reserved.
 **/

class AsyncCommandTask(private val runCallback: RunCallback?) : AsyncTask<String, Int, Int>() {

    override fun doInBackground(vararg arguments: String): Int? {
        return FFmpeg.execute(arguments[0], "<!@#>")
    }

    override fun onPostExecute(rc: Int?) {
        runCallback?.apply(rc!!)
    }

}
