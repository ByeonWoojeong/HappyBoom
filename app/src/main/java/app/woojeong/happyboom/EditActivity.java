package app.woojeong.happyboom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;
import static app.woojeong.happyboom.HappyBoommm.REQUEST_CODE;

public class EditActivity extends AppCompatActivity {
    private static String TAG = "EditActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    ProgressDialog progressDialog;
    SharedPreferences get_token;
    String getToken;

    FrameLayout back_con, ok_con;
    ImageView back;
    ScrollView scrollView;
    EditText content;
    TextView file_name, video_upload, ok;

    String filePath, idx;
    Uri videoUri, fileUri;
    File thumnailFile;
    Bitmap bitmap;
    String thumbnailFileUrl, videoPath, videoName, getContent;

    boolean change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(EditActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        idx = getIntent().getStringExtra("idx");
        getContent = getIntent().getStringExtra("content");
        videoPath = getIntent().getStringExtra("video");

        Log.i(TAG, " idx " + idx);
        Log.i(TAG, " getContent " + getContent);
        Log.i(TAG, " videoPath " + videoPath);

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

        ok_con = findViewById(R.id.ok_con);

        content = findViewById(R.id.content);
        content.setText(getContent);

        ok = findViewById(R.id.ok);
        file_name = findViewById(R.id.file_name);
        video_upload = findViewById(R.id.video_upload);

        video_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "video"), 1);
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
                if ("".equals(content.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "내용을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                if(!change){
                    sendServer();
                } else {
                    upload();
                }


            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, " onResume");
        SharedPreferences sharedPreferences = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean skip = sharedPreferences.getBoolean("skip", false);

        boolean encoding = sharedPreferences.getBoolean("encoding", false);
        String name = sharedPreferences.getString("filename", "");

        if("".equals(name) || name == null) {
            name = "HappyBoom";
        } else if (sharedPreferences.getBoolean("finish", false)){
            sendServer();
        }

//        if (sharedPreferences.getBoolean("finish",false)) {
//            Log.i(TAG, "encoding" + encoding);
//            if (encoding) {
//                editor.putBoolean("encoding", false);
//                editor.remove("filename");
//                Intent intent = new Intent(context, Send1Activity.class);
//                intent.putExtra("path", "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp4");
//                startActivity(intent);
//            } else {
//                editor.putBoolean("encoding", true);
//
//                Intent intent = new Intent(context, BridgeActivity.class);
//                intent.putExtra("path", "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp4");
//                startActivityForResult(intent, REQUEST_CODE);
//            }
//        }else{
//            editor.putBoolean("encoding", false);
//        }
//        editor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, " onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {

                if (data != null) {

                    //video true

                    videoUri = data.getData();
//                    videoPath = getPath(videoUri);
                    videoPath = generatePath(videoUri, context);
                    videoName = getName(videoUri);
                    String uriId = getUriId(videoUri);

                    file_name.setText(videoName);
                    change = true;
                    Log.i(TAG, "실제경로 : " + videoPath + "\n파일명 : " + videoName + "\nuri : " + videoUri.toString() + "\nuri id : " + uriId);
                }


            } else if (requestCode == REQUEST_CODE) {

            }
        }
    }

    // 실제 경로 찾기
    public String generatePath(Uri uri, Context context) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            filePath = generateFromKitkat(uri, context);
        }

        if (filePath != null) {
            return filePath;
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? uri.getPath() : filePath;
    }


    //실제 경로 찾기 2
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    @TargetApi(19)
    private String generateFromKitkat(Uri uri, Context context) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Video.Media.DATA};
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        }
        return filePath;
    }

    // 파일명 찾기
    private String getName(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri 아이디 찾기
    private String getUriId(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String saveBitmapToJpeg(Context context, Bitmap bitmap) {
        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = "thumbnail.png";  // 파일이름은 마음대로!
        File tempFile = new File(storage, fileName);
        try {
            tempFile.createNewFile();  // 파일을 생성해주고
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }

    public static void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath, String filename) {
        File file = new File(strFilePath);

        if (!file.exists())
            file.mkdirs();

        File fileCacheItem = new File(strFilePath + filename);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void getThumbnail() {
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        thumbnailFileUrl = saveBitmapToJpeg(EditActivity.this, bitmap);
    }

    void encoding() {
        Intent intent = new Intent(context, BridgeActivity.class);
        intent.putExtra("path", videoUri.toString());
        intent.putExtra("skip", true);
//        intent.putExtra("community", true);
        startActivityForResult(intent, REQUEST_CODE);
    }

    void sendServer() {
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String url = ServerUrl.getBaseUrl() + "/community/update";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idx", idx);

        if (change) {
            thumnailFile = new File(thumbnailFileUrl);
            params.put("image", thumnailFile);

            File videoFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom", "HappyBoom.mp4");
            params.put("video", videoFile);

            change = false;

            SharedPreferences sharedPreferences = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("finish", false);
            editor.commit();
        }

        params.put("content", content.getText().toString());
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                        Intent intent = new Intent(EditActivity.this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(EditActivity.this, "다시 시도해주세요 !", "확인");
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

    void upload() {
        if(change){
            //원본영상 썸네일 생성
            getThumbnail();

            //선택한 동영상 인코딩
            encoding();
        }
    }

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
}
