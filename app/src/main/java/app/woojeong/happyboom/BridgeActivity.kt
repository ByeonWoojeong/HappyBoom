package app.woojeong.happyboom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.NeedsPermission
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import org.json.JSONException
import org.json.JSONObject
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.OnShowRationale
import java.io.File
import java.net.URL
import java.util.HashMap


/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 5. 29
 * @license Copyright 2019. H&S All rights reserved.
 **/

@RuntimePermissions
class BridgeActivity : AppCompatActivity() {


    var advVideo = ""
    var waterMark = ""
    lateinit var get_token: SharedPreferences
    var getToken: String? = null
    var aQuery: AQuery? = null

    var filename = ""
    var filepath = ""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("BridgeActivity", "  path::  " + intent.getStringExtra("path"))

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE)
        getToken = get_token.getString("Token", "")
        aQuery = AQuery(this)


        val preferences = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        waterMark = resources.getDrawable(R.drawable.watermark).toString()
        filename = "HappyBoom"
        advVideo = "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-137.mp4"

        val title = intent.getStringExtra("title")
//        val premium = intent.getBooleanExtra("premium", false)

        val serverAdv = intent.getStringExtra("adv")
        val serverWater = intent.getStringExtra("watermark")


        if (!"".equals(title) && title != null) {
            filename = title
        }
        if (!"".equals(serverAdv) && serverAdv != null) {
            advVideo = serverAdv
        }
        if (!"".equals(serverWater) && serverWater != null) {
            waterMark = serverWater
        }

        Log.i("Bridge", " 비디오 " + advVideo)
        Log.i("Bridge", " 워터마크 " + waterMark)

        if(intent.getBooleanExtra("community", false)){
            editor.putBoolean("community", true)
        }

        editor.putString("filename", filename)
        editor.putBoolean("finish",false)
//        editor.putBoolean("premium", premium)
        if (intent.getBooleanExtra("skip", false)) {
            editor.putBoolean("skip", true)
        }
        
//        if (intent.getBooleanExtra("community", false)) {
//            editor.putBoolean("community", true)
//        }
        editor.commit()

        filepath = intent.getStringExtra("path")
        Log.i("BridgeActivity", filepath)


        getWaterMark()


//        HappyBoommm.Builder(this, Uri.parse(filepath), Uri.parse(advVideo)) { happyBoommm, Status ->
//            when (Status) {
//                HappyBoommm.SUCCESS -> {
//                    happyBoommm.activate()
//                }
//                HappyBoommm.FONT_INIT_FAILED -> {
//                }
//                HappyBoommm.INPUT_URI_WRITE_FAILED -> {
//                }
//                HappyBoommm.INPUT_URI_OPEN_FAILED -> {
//                }
//                HappyBoommm.PERMISSION_DENIED -> {
//                }
//            }
//        }
//                .setWatermark(Pair(URL(waterMark), File(filesDir, "mm.png")))
//                .setOuputVideoPath(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + ".mp4"))
//                .build()
//        finish()

    }

    internal fun getWaterMark() {
        val url = ServerUrl.getBaseUrl() + "/main/watermark"
        val params = HashMap<String, Any>()
        Log.i("BridgeActivity", " params $params")
        aQuery!!.ajax(url, params, String::class.java, object : AjaxCallback<String>() {
            override fun callback(url: String?, jsonString: String?, status: AjaxStatus?) {
                Log.i("BridgeActivity", " jsonString " + jsonString!!)
                try {
                    val jsonObject = JSONObject(jsonString)

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        val jsonData = jsonObject.getJSONObject("data")
                        waterMark = ServerUrl.getBaseUrl() + "/uploads/images/origin/" + jsonData.getString("image")
                        getAdv()
                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"))
    }

    fun getAdv() {
        val url = ServerUrl.getBaseUrl() + "/main/random"
        val params = HashMap<String, Any>()
        Log.i("BridgeActivity", " params $params")
        aQuery!!.ajax(url, params, String::class.java, object : AjaxCallback<String>() {
            override fun callback(url: String?, jsonString: String?, status: AjaxStatus?) {
                Log.i("BridgeActivity", " jsonString " + jsonString!!)
                try {
                    val jsonObject = JSONObject(jsonString)

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        val jsonData = jsonObject.getJSONObject("data")
                        advVideo = ServerUrl.getBaseUrl() + "/uploads/videos/" + jsonData.getString("video")
                        HappyBoommm.Builder(this@BridgeActivity, Uri.parse(filepath), Uri.parse(advVideo)) { happyBoommm, Status ->
                            when (Status) {
                                HappyBoommm.SUCCESS -> {
                                    happyBoommm.activate()
                                }
                                HappyBoommm.FONT_INIT_FAILED -> {
                                }
                                HappyBoommm.INPUT_URI_WRITE_FAILED -> {
                                }
                                HappyBoommm.INPUT_URI_OPEN_FAILED -> {
                                }
                                HappyBoommm.PERMISSION_DENIED -> {
                                }
                            }
                        }
                                .setWatermark(Pair(URL(waterMark), File(filesDir, "mm.png")))
                                .setOuputVideoPath( File(Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom", filename + ".mp4"))
                                .build()
                        finish()
                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"))
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("video/*")
        startActivityForResult(Intent.createChooser(intent, "video"), 1)
    }

//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 1) {
//                if (data != null) {
//                    if (data.data != null) {
//                        HappyBoommm.Builder(this, data.data!!, Uri.parse("https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-137.mp4")) { happyBoommm, Status ->
//                            when(Status) {
//                                HappyBoommm.SUCCESS -> { happyBoommm.activate() }
//                                HappyBoommm.FONT_INIT_FAILED -> {}
//                                HappyBoommm.INPUT_URI_WRITE_FAILED -> {}
//                                HappyBoommm.INPUT_URI_OPEN_FAILED -> {}
//                                HappyBoommm.PERMISSION_DENIED -> {}
//                            }
//                        }
//                            .setWatermark(Pair(URL("https://lh3.googleusercontent.com/9MeehakhUNFgmdb0f9EQE3ChJGUaCOPrcJfM4qpwmpy940iXo5hnEa6FWu1pAzjA4c2KIqAG5gzm4vVYpuz74qieAOV4mPo=s688"), File(filesDir, "mm.png")))
//                            .setOuputVideoPath(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "output.mp4"))
//                            .build()
//                    }
//                }
//            } else if (requestCode == HappyBoommm.REQUEST_CODE) {
//
//            }
//        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        onRequestPermissionsResult(requestCode, grantResults)
//    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    fun selectOnShow(request: PermissionRequest) {
        showRationaleDialog("asdasd", request)
    }

    private fun showRationaleDialog(msg: String, request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setPositiveButton("OK") { _, _ -> request.proceed() }
                .setNegativeButton("NO") { _, _ -> request.cancel() }
                .setCancelable(false)
                .setMessage(msg)
                .show()
    }
}