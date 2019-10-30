package app.woojeong.happyboom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;
import static app.woojeong.happyboom.HappyBoommm.REQUEST_CODE;

public class VideoActivity extends AppCompatActivity {
    private static String TAG = "VideoActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    ProgressDialog progressDialog;
    TitleDialog titleDialog;
    DeleteDialog deleteDialog;

    SharedPreferences get_token;
    String getToken, getKey, videoPath, getTitle, videoName;

    FrameLayout back_con, delete_con, ok_con;
    ImageView back;
    TextView title, delete, ok;
    WebView video_view;

    boolean encoding;

    File path, outputFile;
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(VideoActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        getKey = getIntent().getStringExtra("idx");

        back_con = findViewById(R.id.back_con);
        back = findViewById(R.id.back);
        back_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.callOnClick();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = findViewById(R.id.title);

        delete_con = findViewById(R.id.delete_con);
        ok = findViewById(R.id.ok);

        delete = findViewById(R.id.delete);
        ok_con = findViewById(R.id.ok_con);

        video_view = findViewById(R.id.video_view);
        video_view.setWebChromeClient(new FullscreenableChromeClient(VideoActivity.this));

        delete_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.callOnClick();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog = new DeleteDialog(VideoActivity.this);
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.setCancelable(false);
                deleteDialog.show();
            }
        });

        ok_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok.callOnClick();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //동영상 다운을 받아서 저장한 후에


                titleDialog = new TitleDialog(VideoActivity.this, "영상 제목 입력");
                titleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                titleDialog.setCancelable(false);
                titleDialog.show();
            }
        });

        showing();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, " onResume");
        SharedPreferences preferences = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(null != outputFile){
            if(preferences.getBoolean("finish",false)){
                editor.putBoolean("finish", false);
                Intent intent = new Intent(context, Send1Activity.class);
                intent.putExtra("path", "file://" + outputFile.getAbsolutePath());
                startActivity(intent);
                editor.commit();
            }
        }
    }

    void showing() {
        final String url = ServerUrl.getBaseUrl() + "/premium/detail";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key", getKey);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                        JSONObject jsonData = jsonObject.getJSONObject("data");

                        videoPath = "video=" + jsonData.getString("content");
                        videoName = jsonData.getString("content");
                        videoPath += "&image=" + jsonData.getString("image");
                        video_view.postUrl(ServerUrl.getBaseUrl() + "/video", videoPath.getBytes());
//                        video_view.setVideoURI(Uri.parse(videoPath));
//                        video_view.seekTo(1);
                        title.setText(jsonData.getString("title"));

                        if ("1".equals(jsonData.getString("ismy"))) {
                            delete_con.setVisibility(View.VISIBLE);
                        } else {
                            delete_con.setVisibility(View.GONE);
                        }
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(VideoActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void delete() {
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String url = ServerUrl.getBaseUrl() + "/premium/delete";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key", getKey);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(VideoActivity.this, "영상을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {

                        oneBtnDialog = new OneBtnDialog(VideoActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void counting() {
        final String url = ServerUrl.getBaseUrl() + "/premium/encode";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key", getKey);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(VideoActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    public class FullscreenableChromeClient extends WebChromeClient {
        private Activity mActivity = null;

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;

        private FrameLayout mFullscreenContainer;

        private final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        public FullscreenableChromeClient(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                mOriginalOrientation = mActivity.getRequestedOrientation();
                FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(mActivity);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);
            }

            super.onShowCustomView(view, callback);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
            this.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            setFullscreen(false);
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            mActivity.setRequestedOrientation(mOriginalOrientation);
        }

        private void setFullscreen(boolean enabled) {
            Window win = mActivity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (enabled) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                winParams.flags |= bits;
            } else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                winParams.flags &= ~bits;
                if (mCustomView != null) {
                    mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            win.setAttributes(winParams);
        }

        private class FullscreenHolder extends FrameLayout {
            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
            }

            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, String, Long> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }


        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressDialog = new ProgressDialog(context);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        //파일 다운로드를 진행합니다.
        @Override
        protected Long doInBackground(String... string_url) { //3
            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;

            try {
                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();


                //파일 크기를 가져옴
                FileSize = connection.getContentLength();

                //URL 주소로부터 파일다운로드하기 위한 input stream
                input = new BufferedInputStream(url.openStream(), 8192);

                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputFile = new File(path, getTitle + ".mp4"); //파일명까지 포함함 경로의 File 객체 생성

                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFile);


                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //사용자가 BACK 버튼 누르면 취소가능
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float) downloadedSize / FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int) per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                    }

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                mWakeLock.release();

            }
            return FileSize;
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressDialog.dismiss();

            //비디오 인코딩 카운트
            counting();

            if (size > 0) {
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다.", Toast.LENGTH_LONG).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, BridgeActivity.class);
//                    intent.putExtra("title", content.getText().toString());
                        intent.putExtra("path","file://" + outputFile.getAbsolutePath());
//                intent.putExtra("skip", false);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }, 500);


//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                mediaScanIntent.setData(Uri.fromFile(outputFile));
//                sendBroadcast(mediaScanIntent);
//                playVideo(outputFile.getPath());
            } else
                Toast.makeText(getApplicationContext(), "다운로드 에러 발생", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!readAccepted || !writeAccepted) {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(VideoActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

//    private void playVideo(String path) {
//        Uri videoUri = Uri.fromFile(new File(path));
//        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
//        videoIntent.setDataAndType(videoUri, "video/*");
//        if (videoIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(Intent.createChooser(videoIntent, null));
//        }
//    }

    public class OneBtnDialog extends Dialog {
        OneBtnDialog oneBtnDialog = this;
        Context context;

        public OneBtnDialog(final Context context, final String text, final String btnText) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_one_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            title2.setVisibility(View.GONE);
            title1.setText(text);
            btn1.setText(btnText);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oneBtnDialog.dismiss();
                }
            });
        }
    }

    public class ProgressDialog extends Dialog {

        public ProgressDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_progress);
        }
    }


    public class DeleteDialog extends Dialog {
        DeleteDialog deleteDialog = this;
        Context context;

        public DeleteDialog(final Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_two_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            title2.setVisibility(View.GONE);
            title1.setText("영상을 삭제하시겠습니까?");
            btn1.setText("아니요");
            btn2.setText("네");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                    deleteDialog.dismiss();
                }
            });
        }
    }

    public class TitleDialog extends Dialog {
        TitleDialog titleDialog = this;
        Context context;

        public TitleDialog(final Context context, final String title) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_input);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = findViewById(R.id.title1);
            EditText content = findViewById(R.id.content);
            final TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            title1.setText(title);
            btn1.setText("취소");
            btn2.setText("확인");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleDialog.dismiss();

                    getTitle = content.getText().toString();

                    //영상 다운받기
                    String fileURL = ServerUrl.getBaseUrl() + "/uploads/videos/" + videoName;
//                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    outputFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom", getTitle + ".mp4");
                    if (outputFile.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
                        builder.setTitle("파일 다운로드");
                        builder.setMessage("이미 존재하는 파일 이름입니다.\n이 파일로 변경하시겠습니까?");
                        builder.setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        Toast.makeText(getApplicationContext(), "기존 파일을 플레이합니다.", Toast.LENGTH_LONG).show();
//                                        playVideo(outputFile.getPath());
                                    }
                                });
                        builder.setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        outputFile.delete(); //파일 삭제

                                        final DownloadFilesTask downloadTask = new DownloadFilesTask(VideoActivity.this);
                                        downloadTask.execute(fileURL);
                                    }
                                });
                        builder.show();
                    } else {
                        final DownloadFilesTask downloadTask = new DownloadFilesTask(VideoActivity.this);
                        downloadTask.execute(fileURL);
                    }

                    Log.i(TAG, " PATH " + outputFile.getAbsolutePath());
                }
            });
        }
    }
}
