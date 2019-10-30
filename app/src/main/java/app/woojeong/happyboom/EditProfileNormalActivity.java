package app.woojeong.happyboom;

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
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import androidx.core.content.FileProvider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class EditProfileNormalActivity extends AppCompatActivity {
    private static String TAG = "EditProfileNormalActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    TwoBtnDialog twoBtnDialog;

    SharedPreferences get_token;
    String getToken;

    String getNick, getBirth, getDream, getImage, getIntro; //일반

    FrameLayout back_con, ok_con;
    ImageView back, profile_img, dup_check;
    ScrollView scrollView;
    EditText nickname, birth, profile_intro, my_intro;
    TextView ok;
    boolean isNick, isChange;

    ArrayList<Bitmap> originalBitmap = new ArrayList<Bitmap>();
    ArrayList<Bitmap> resizeBitmap = new ArrayList<Bitmap>();
    ArrayList<String> imagePath = new ArrayList<String>();
    ArrayList<File> file = new ArrayList<File>();
    String filePath;
    Uri fileUri;
    int CAMERA = 700, GALLERY = 800, totalImageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_normal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(EditProfileNormalActivity.this, true);
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

        profile_img = findViewById(R.id.profile_img);
        dup_check = findViewById(R.id.dup_check);

        nickname = findViewById(R.id.nickname);
        birth = findViewById(R.id.birth);
        profile_intro = findViewById(R.id.profile_intro);
        my_intro = findViewById(R.id.my_intro);

        ok = findViewById(R.id.ok);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                twoBtnDialog = new TwoBtnDialog(EditProfileNormalActivity.this);
                twoBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                twoBtnDialog.show();
            }
        });

        dup_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(nickname.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "닉네임을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                nickDuplication();
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
                if ("".equals(nickname.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "닉네임을 확인해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("".equals(birth.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "생년월일을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("".equals(profile_intro.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "프로필 소개를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("".equals(my_intro.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "자기 소개를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                upload();
            }
        });

        importData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult " + requestCode);
        switch (requestCode) {
            case 1:
                break;
            case 700:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    try {
                        if (Build.VERSION.SDK_INT < 21) {
                            Uri imgUri = data.getData();
                            imagePath.clear();
                            imagePath.add(getImageRealPathFromURI(EditProfileNormalActivity.this.getContentResolver(), imgUri));
                        }
                        ExifInterface exif = new ExifInterface(imagePath.get(totalImageCount));
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        originalBitmap.clear();
                        resizeBitmap.clear();
                        originalBitmap.add(rotate(BitmapFactory.decodeFile(imagePath.get(totalImageCount)), exifDegree));
                        resizeBitmap.add(rotate(resizeBitmap(imagePath.get(totalImageCount), 1080, 1920), exifDegree));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    putImage(profile_img, imagePath.get(totalImageCount));
                }
                break;
            case 800:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    try {
                        Uri imgUri = data.getData();
                        imagePath.clear();
                        imagePath.add(getImageRealPathFromURI(EditProfileNormalActivity.this.getContentResolver(), imgUri));
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
                    putImage(profile_img, imagePath.get(totalImageCount));
                }
                break;
            case 900:
                break;
            case RESULT_CANCELED:
                break;
        }
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

    void putImage(ImageView imageView, String getImg){
        Glide.with(EditProfileNormalActivity.this)
                .load(getImg)
                .into(imageView);
    }

//    닉네임 중복 체크
    void nickDuplication() {
        final String url = ServerUrl.getBaseUrl() + "/join/id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nick", nickname.getText().toString());
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(EditProfileNormalActivity.this, "사용 가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        isNick = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(EditProfileNormalActivity.this, "중복된 닉네임 입니다.", "확인");
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

    void importData(){

        getNick = getIntent().getStringExtra("nick");
        getBirth = getIntent().getStringExtra("birth").replaceAll("년","").replaceAll("월", "").replaceAll("일", "");
        getDream = getIntent().getStringExtra("dream");
        getIntro = getIntent().getStringExtra("intro");
        getImage = getIntent().getStringExtra("image");

        putImage(profile_img, getImage);
        nickname.setText(getNick);
        if("null".equals(getNick)){
            nickname.setText("");
        }

        birth.setText(getBirth.replaceAll(" ",""));
        if("null".equals(getBirth)){
            birth.setText("");
        }

        profile_intro.setText(getDream);
        if("null".equals(getDream)){
            profile_intro.setText("");
        }

        my_intro.setText(getIntro);
        if("null".equals(getIntro)){
            my_intro.setText("");
        }

    }

    void upload() {
        final String url = ServerUrl.getBaseUrl() + "/member/update";
        Map<String, Object> params = new HashMap<String, Object>();

        file.clear();
        if (resizeBitmap.size() == 1) {
            fileUri = getImageUri(EditProfileNormalActivity.this, resizeBitmap.get(0));
            filePath = getImageRealPathFromURI(EditProfileNormalActivity.this.getContentResolver(), fileUri);
            File makeFile = new File(filePath);
            params.put("image", makeFile);
            isChange = true;
        }

        params.put("nick", nickname.getText().toString());
        if(!"null".equals(birth.getText().toString()) && !"".equals(birth.getText().toString())){
            params.put("birth", birth.getText().toString());
        }
        params.put("dream", profile_intro.getText().toString());
        params.put("intro", my_intro.getText().toString());
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(EditProfileNormalActivity.this, "프로필을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(EditProfileNormalActivity.this, "프로필 수정 실패\n다시 시도해주세요.", "확인");
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

    public class TwoBtnDialog extends Dialog {
        TwoBtnDialog twoBtnDialog = this;
        Context context;

        public TwoBtnDialog(final Context context) {
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
            title1.setText("이미지 첨부방식을\n선택해 주세요 !");
            btn1.setText("카메라");
            btn2.setText("갤러리");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twoBtnDialog.dismiss();
                    if (Build.VERSION.SDK_INT > 21) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
                        startActivityForResult(intent, CAMERA);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA);
                    }
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twoBtnDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY);
                }
            });
        }
    }
}
