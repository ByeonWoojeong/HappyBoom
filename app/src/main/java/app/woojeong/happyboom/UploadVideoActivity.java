package app.woojeong.happyboom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;
import static app.woojeong.happyboom.HappyBoommm.REQUEST_CODE;

public class UploadVideoActivity extends AppCompatActivity {
    private static String TAG = "UploadVideoActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    ProgressDialog progressDialog;

    SharedPreferences get_token;
    String getToken;

    FrameLayout back_con, spinner_con, ok_con;
    ImageView back, spinner_down;
    ScrollView scrollView;

    SpinnerReselect spinner;
    ArrayList<String> itemTitle;
    ArrayList<JSONObject> itemJson;
    String getItem;

    TextView spinner_text, file_name, video_upload, thumbnail_name, thumbnail_upload, ok;
    EditText video_title;

    int GALLERY = 800, totalImageCount = 0;
    ArrayList<Bitmap> originalBitmap = new ArrayList<Bitmap>();
    ArrayList<Bitmap> resizeBitmap = new ArrayList<Bitmap>();
    ArrayList<String> imagePath = new ArrayList<String>();
    ArrayList<File> file = new ArrayList<File>();
    ArrayList<String> getImageList = new ArrayList<String>();
    String filePath, videoPath, videoName, getAdv;
    Uri fileUri, videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(UploadVideoActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

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
        scrollView = findViewById(R.id.scrollView);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 100);


        ok_con = findViewById(R.id.ok_con);

        spinner_con = findViewById(R.id.spinner_con);
        spinner_down = findViewById(R.id.spinner_down);
        spinner = findViewById(R.id.spinner);
        spinner_text = findViewById(R.id.spinner_text);

        file_name = findViewById(R.id.file_name);
        video_upload = findViewById(R.id.video_upload);
        thumbnail_name = findViewById(R.id.thumbnail_name);
        thumbnail_upload = findViewById(R.id.thumbnail_upload);
        ok = findViewById(R.id.ok);

        video_title = findViewById(R.id.video_title);

        itemJson = new ArrayList<JSONObject>();
        itemTitle = new ArrayList<String>();

        spinnerItem();

        video_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "video"), 1);
            }
        });

        thumbnail_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY);
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
                if ("".equals(spinner_text.getText().toString()) || "0".equals(getItem)) {
                    oneBtnDialog = new OneBtnDialog(context, "등록 위치를 선택해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("".equals(video_title.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "영상 제목을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("영상 파일을 첨부해주세요".equals(file_name.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "영상 파일을 첨부해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("썸네일 이미지를 첨부해주세요".equals(thumbnail_name.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "썸네일 이미지를 첨부해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                encoding();
//                onResume 에서 확인후 sendServer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getBoolean("finish",false)) {
            editor.putBoolean("finish", false);
            editor.commit();
            sendServer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult " + requestCode);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    videoUri = data.getData();
                    videoPath = generatePath(videoUri, context);
                    videoName = getName(videoUri);
                    file_name.setText(videoName);
                }
                break;
            case 800:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    Uri imgUri = Uri.parse("");
                    try {
                        imgUri = data.getData();
                        imagePath.clear();
                        imagePath.add(getImageRealPathFromURI(UploadVideoActivity.this.getContentResolver(), imgUri));
                        ExifInterface exif = new ExifInterface(imagePath.get(0));
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        originalBitmap.clear();
                        resizeBitmap.clear();
                        originalBitmap.add(rotate(BitmapFactory.decodeFile(imagePath.get(totalImageCount)), exifDegree));
                        resizeBitmap.add(rotate(resizeBitmap(imagePath.get(totalImageCount), 1080, 1920), exifDegree));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String imageName = getName(imgUri);
                    thumbnail_name.setText(imageName);
                }
                break;
            case 900:
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    void spinnerItem(){
        itemJson.clear();
        itemTitle.clear();

        final String url = ServerUrl.getBaseUrl() + "/premium/option";
        Map<String, Object> params = new HashMap<String, Object>();
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        final JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        JSONObject jsonObjectList = new JSONObject("{title:\"선택해주세요.\",key:0}");
                        itemJson.add(jsonObjectList);
                        itemTitle.add(jsonObjectList.getString("title"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObjectList = jsonArray.getJSONObject(i);
                            itemJson.add(jsonObjectList);
                            itemTitle.add(jsonObjectList.getString("title"));
                            Log.i(TAG, " title " + itemJson.get(i).getString("title"));
                        }
                    } else if (!jsonObject.getBoolean("return")) {
                        //type : permission - 기업회원이 아닐 때
                        oneBtnDialog = new OneBtnDialog(UploadVideoActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, itemTitle);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        JSONObject selectItem = itemJson.get(spinner.getSelectedItemPosition());
                        try{
                            spinner_text.setText(selectItem.getString("title"));
                            getItem = selectItem.getString("key");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }



                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    Uri getFileUri() {
        File dir = new File(getFilesDir(), "Picture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, System.currentTimeMillis() + ".png");
        imagePath.add(file.getAbsolutePath() + "");
        return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
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

    String getImageRealPathFromURI(ContentResolver contentResolver, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int path = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String tmp = cursor.getString(path);
            cursor.close();
            return tmp;
        }
    }

    int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {

            }
        }
        return bitmap;
    }

    Bitmap resizeBitmap(String file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    void encoding() {
        Log.i(TAG, " encoding()");
        Intent intent = new Intent(context, BridgeActivity.class);
        intent.putExtra("path", videoUri.toString());
        intent.putExtra("skip", true);
        startActivityForResult(intent, REQUEST_CODE);
    }

    void getAdv(){
        final String url = ServerUrl.getBaseUrl() + "/main/random";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        getAdv = ServerUrl.getBaseUrl() + "/uploads/videos/" + jsonData.getString("video");
                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void sendServer() {
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String url = ServerUrl.getBaseUrl() + "/premium/insert";
        Map<String, Object> params = new HashMap<String, Object>();
        if (resizeBitmap.size() == 1) {
            fileUri = getImageUri(UploadVideoActivity.this, resizeBitmap.get(0));
            filePath = getImageRealPathFromURI(UploadVideoActivity.this.getContentResolver(), fileUri);
            File makeFile = new File(filePath);
            params.put("image", makeFile);
        }

        File videoFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom", "HappyBoom.mp4");
        params.put("content", videoFile);

        params.put("key", getItem);
        params.put("title", video_title.getText().toString());
        Log.i(TAG, " params " + params);

        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Intent intent = new Intent(UploadVideoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(UploadVideoActivity.this, "다시 시도해주세요 !", "확인");
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

//    public class TwoBtnDialog extends Dialog {
//        TwoBtnDialog twoBtnDialog = this;
//        Context context;
//
//        public TwoBtnDialog(final Context context) {
//            super(context);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            setContentView(R.layout.dialog_two_btn);
//            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            this.context = context;
//            TextView title1 = (TextView) findViewById(R.id.title1);
//            TextView title2 = (TextView) findViewById(R.id.title2);
//            TextView btn1 = (TextView) findViewById(R.id.btn1);
//            TextView btn2 = (TextView) findViewById(R.id.btn2);
//            title2.setVisibility(View.GONE);
//            title1.setText("이미지 첨부방식을\n선택해 주세요 !");
//            btn1.setText("카메라");
//            btn2.setText("갤러리");
//            btn1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    twoBtnDialog.dismiss();
//                    if (Build.VERSION.SDK_INT > 21) {
//                        Intent intent = new Intent(Intent.ACTION_PICK);
//                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
//                        startActivityForResult(intent, CAMERA);
//                    } else {
//                        Intent intent = new Intent(Intent.ACTION_PICK);
//                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(intent, CAMERA);
//                    }
//                }
//            });
//            btn2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    twoBtnDialog.dismiss();
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, GALLERY);
//                }
//            });
//        }
//    }


}
