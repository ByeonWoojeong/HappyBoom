package app.woojeong.happyboom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import com.arthenica.mobileffmpeg.Config
import com.dev.hongsw.happyBoom.data.Font
import com.dev.hongsw.happyBoom.util.download
import com.dev.hongsw.happyBoom.util.toWrite
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 6
 * @license Copyright 2019. H&S All rights reserved.
 **/

class HappyBoommm public constructor(private val mContext: Activity,
                                      private val mInput: Uri,
                                      private val mOutput: File,
                                      private val mFontList: HashSet<Font>,
                                      private val mWatermark: Pair<URL, File>?,
                                      private val mEncodingVideo: Uri,
                                      private val listener: (HappyBoommm, Int) -> Unit) {



    private fun callback(result: Int) {
        if (result == SUCCESS) initSuccess = true
        listener(this ,result)
    }

    companion object {
        private const val TAG = "HappyBoommm"
        const val SUCCESS = 0
        const val PERMISSION_DENIED = 1
        const val INPUT_URI_OPEN_FAILED = 2
        const val INPUT_URI_WRITE_FAILED = 3
        const val FONT_INIT_SUCCESS = 4
        const val FONT_INIT_FAILED = 5
        const val REQUEST_CODE = 1414
    }

    private var initSuccess = false
    private lateinit var mInputFile: File
    private val mDefaultFont: HashSet<Font> =
        hashSetOf(Font(R.raw.hs_bombaram, "hs_bombaram.ttf", "HSBombaram 2.0"),
            Font(R.raw.dotum, "dotum.ttf", "KoPubWorldDotum"))
    private var mImageFile: File? = null

    init {
        run {
            Log.d(TAG, "초기화 시작")

            if ((ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
                or (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)) {
                callback(PERMISSION_DENIED)
                Log.d(TAG, "퍼미션 없음")
                return@run
            }

            Log.d(TAG, "퍼미션 있음")

            try {
                mContext.contentResolver.openInputStream(mInput)?.let { inputStream ->
                    inputStream.toWrite(mContext, "video.mp4")?.let { file ->
                        mInputFile = file
                    }
                }
            } catch (e: FileNotFoundException) {
                callback(INPUT_URI_OPEN_FAILED)
                Log.d(TAG, "비디오 파일이 존재하지 않거나 찾을 수 없음", e)
                return@run
            } catch (e: Exception) {
                callback(INPUT_URI_WRITE_FAILED)
                Log.d(TAG, "비디오 파일을 복사하던 중 에러", e)
                return@run
            }

            if (registerAppFont(mContext, mDefaultFont)) {
                Log.d(TAG, "기본 폰트 등록 성공")
            } else Log.d(TAG, "기본 폰트 등록 실패")

            if (mFontList.size > 0) {
                if (registerAppFont(mContext, mFontList)) {
                    Log.d(TAG, "추가 폰트 등록 성공")
                    callback(FONT_INIT_SUCCESS)
                } else {
                    Log.d(TAG, "추가 폰트 등록 실패")
                    callback(FONT_INIT_FAILED)
                }
            }

            mWatermark?.let { it -> download(it.first, it.second)?.let { mImageFile = it } }

            Log.d(TAG,"초기화 성공")
            callback(SUCCESS)
        }
    }

    data class Builder(private val context: BridgeActivity, private val inputVideoPath: Uri, private val encodingVideo: Uri,
                       private val listener: (HappyBoommm, Status: Int) -> Unit) {
        private var outputVideoPath: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "output.mp4")
        private var fontList: HashSet<Font> = hashSetOf()
        private var watermark: Pair<URL,File>? = null

        fun setOuputVideoPath(path: File) = apply { this.outputVideoPath = path }
        fun addFontList(fontList: Iterable<Font>) = apply { this.fontList.addAll(fontList)}
        fun setWatermark(watermark: Pair<URL,File>) = apply { this.watermark = watermark }
        fun build() = HappyBoommm(context, inputVideoPath, outputVideoPath, fontList, watermark, encodingVideo, listener)
    }

    fun activate() {
        val intent = Intent(mContext, EditVideoActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("input", mInputFile)
        bundle.putSerializable("output", mOutput)
        bundle.putSerializable("image", mImageFile)
        bundle.putParcelable("encodingVideo", mEncodingVideo)
        intent.putExtra("ffmpegData", bundle)

        mContext.startActivityForResult(intent, REQUEST_CODE)
    }

    private fun registerAppFont(context: Activity, fontList: Iterable<Font>): Boolean {
        val fileDir = context.filesDir
        val fontNameMapping = HashMap<String, String>()

        for ((fontId, fileName, fontName) in fontList) {
            if (rawResourceToFile(context, fontId, File(fileDir, fileName))) fontNameMapping[fontName] = fontName
        }
        if (fontNameMapping.size > 0) {
            Config.setFontDirectory(context, fileDir.absolutePath, fontNameMapping)
            return true
        }
        return false
    }

    private fun rawResourceToFile(context: Activity, resourceId: Int, file: File): Boolean {
        val inputStream = context.resources.openRawResource(resourceId)
        if (file.exists()) {
            file.delete()
        }
        val outputStream = FileOutputStream(file)
        var result = true

        try {
            val buffer = ByteArray(1024)
            var readSize: Int = inputStream.read(buffer)
            while (readSize > 0) {
                outputStream.write(buffer, 0, readSize)
                readSize = inputStream.read(buffer)
            }
        } catch (e: IOException) {
            result = false
            Log.d(TAG, "폰트 저장 실패", e)
        } finally {
            inputStream.close()
            outputStream.flush()
            outputStream.close()
        }

        Log.d(TAG, "폰트 저장 성공 : ${file.name}")

        return result
    }
}