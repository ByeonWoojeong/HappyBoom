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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;
import static app.woojeong.happyboom.HappyBoommm.REQUEST_CODE;

public class WriteActivity extends AppCompatActivity {
    private static String TAG = "WriteActivity";
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
    TextView file_name, video_upload, thumbnail_name, thumbnail_upload, ok;

    String filePath;
    Uri videoUri, fileUri;
    File thumnailFile;
    Bitmap bitmap;
    String thumbnailFileUrl, videoPath, videoName;

    int GALLERY = 800, totalImageCount = 0;
    ArrayList<Bitmap> originalBitmap = new ArrayList<Bitmap>();
    ArrayList<Bitmap> resizeBitmap = new ArrayList<Bitmap>();
    ArrayList<String> imagePath = new ArrayList<String>();
    ArrayList<File> file = new ArrayList<File>();
    ArrayList<String> getImageList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(WriteActivity.this, true);
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

        content = findViewById(R.id.content);

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

        thumbnail_name = findViewById(R.id.thumbnail_name);
        thumbnail_upload = findViewById(R.id.thumbnail_upload);
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
                if ("".equals(content.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "내용을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
                if ("영상 파일을 첨부해주세요".equals(file_name.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "영상 파일을 첨부해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
                //등록을 누르면 원본 썸네일 생성하고, 인코딩 후 동영상 파일 서버에 전송
                upload();
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
        //
//        if(skip){
//            editor.remove("skip");
//            editor.commit();
//            sendServer();
//        }

        if (sharedPreferences.getBoolean("finish",false)) {
            editor.putBoolean("finish", false);
            editor.commit();
            sendServer();
        }
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

                    Log.i(TAG, "실제경로 : " + videoPath + "\n파일명 : " + videoName + "\nuri : " + videoUri.toString() + "\nuri id : " + uriId);
                }


            } else if (requestCode == REQUEST_CODE) {

            }
        }

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
                        imagePath.add(getImageRealPathFromURI(WriteActivity.this.getContentResolver(), imgUri));
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

    // 실제 경로 찾기
    public String generatePath(Uri uri,Context context) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if(isKitKat){
            filePath = generateFromKitkat(uri,context);
        }

        if(filePath != null){
            return filePath;
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);

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
    private String generateFromKitkat(Uri uri,Context context){
        String filePath = null;
        if(DocumentsContract.isDocumentUri(context, uri)){
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Video.Media.DATA };
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);



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

    public static String saveBitmapToJpeg(Context context,Bitmap bitmap){
        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = "thumbnail.png";  // 파일이름은 마음대로!
        File tempFile = new File(storage,fileName);
        try{
            tempFile.createNewFile();  // 파일을 생성해주고
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
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

    void getThumbnail(){
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        if(bitmap!=null){
            thumbnailFileUrl = saveBitmapToJpeg(WriteActivity.this, bitmap);
        } else {
            Log.i(TAG, " ELSE ");
        }
    }

    void encoding(){
        Log.i(TAG, " encoding()");
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(context, BridgeActivity.class);
        intent.putExtra("path", videoUri.toString());
        intent.putExtra("skip", true);
        intent.putExtra("community", true);
        startActivityForResult(intent, REQUEST_CODE);
        progressDialog.dismiss();
    }

    void sendServer(){
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String url = ServerUrl.getBaseUrl() + "/community/insert";
        Map<String, Object> params = new HashMap<String, Object>();

//        if (resizeBitmap.size() == 1) {
//            fileUri = getImageUri(WriteActivity.this, resizeBitmap.get(0));
//            filePath = getImageRealPathFromURI(WriteActivity.this.getContentResolver(), fileUri);
//            File makeFile = new File(filePath);
//            params.put("image", makeFile);
//        }

        if(!"".equals(thumbnailFileUrl)){
            File thumnail = new File(thumbnailFileUrl);
            params.put("image", thumnail);
        }

        File videoFile = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom", "HappyBoom.mp4");
        params.put("video", videoFile);

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

                        SharedPreferences HappyBoom = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = HappyBoom.edit();
                        editor.putBoolean("reload", true);
                        editor.commit();

                        Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {

                        oneBtnDialog = new OneBtnDialog(WriteActivity.this, "다시 시도해주세요 !", "확인");
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
        //원본영상 썸네일 생성
        getThumbnail();

        //선택한 동영상 인코딩
       encoding();
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
