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
import android.os.Handler;
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
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class EditProfileCEOActivity extends AppCompatActivity {
    private static String TAG = "EditProfileCEOActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    TwoBtnDialog twoBtnDialog;

    SharedPreferences get_token;
    String getToken, getKind;

    FrameLayout back_con, spinner_con, ok_con;
    ImageView back, profile_img, spinner_down;
    ScrollView scrollView;
    EditText company_name, worker_cnt, since_year, homepage, introduce;
    SpinnerReselect spinner;
    TextView spinner_text, ok;

    ArrayList<Bitmap> originalBitmap = new ArrayList<Bitmap>();
    ArrayList<Bitmap> resizeBitmap = new ArrayList<Bitmap>();
    ArrayList<String> imagePath = new ArrayList<String>();
    ArrayList<File> file = new ArrayList<File>();
    String filePath;
    Uri fileUri;
    int CAMERA = 700, GALLERY = 800, totalImageCount = 0;

    String getName, getCnt, getYear, getType, getHome, getContent, getPhoto;  //기업
    boolean isChange;

    String[] kindOf = new String[]{"개인사업자", "법인사업자", "금융기관", "공기업", "협회/단체", "기타"};
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_ceo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(EditProfileCEOActivity.this, true);
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

        spinner_con = findViewById(R.id.spinner_con);
        ok_con = findViewById(R.id.ok_con);

        profile_img = findViewById(R.id.profile_img);
        spinner_down = findViewById(R.id.spinner_down);

        company_name = findViewById(R.id.company_name);
        worker_cnt = findViewById(R.id.worker_cnt);
        since_year = findViewById(R.id.since_year);
        homepage = findViewById(R.id.homepage);
        introduce = findViewById(R.id.introduce);

        spinner = findViewById(R.id.spinner);

        spinner_text = findViewById(R.id.spinner_text);
        ok = findViewById(R.id.ok);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                twoBtnDialog = new TwoBtnDialog(EditProfileCEOActivity.this);
                twoBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                twoBtnDialog.show();
            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(EditProfileCEOActivity.this, R.layout.spinner_item, kindOf);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner_text.setText(spinner.getSelectedItem().toString());
                getKind = String.valueOf((spinner.getSelectedItemPosition()) + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
                if("".equals(company_name.getText().toString())){
                    oneBtnDialog = new OneBtnDialog(context, "기업 명을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if("".equals(worker_cnt.getText().toString())){
                    oneBtnDialog = new OneBtnDialog(context, " 사원 수를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if("".equals(since_year.getText().toString())){
                    oneBtnDialog = new OneBtnDialog(context, "설립 연도를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if(since_year.getText().toString().length() > 8 || since_year.getText().toString().length() < 8){
                    if(!getYear.equals(since_year.getText().toString())){
                        oneBtnDialog = new OneBtnDialog(context, "설립 연도를 정확히 입력해주세요 !\n ex) 20190101", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }
                } else if("".equals(spinner_text.getText().toString())){
                    oneBtnDialog = new OneBtnDialog(context, "기업 형태를 선택해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if("".equals(homepage.getText().toString())){
                    oneBtnDialog = new OneBtnDialog(context, "홈페이지 주소를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if("".equals(introduce.getText().toString()) || "null".equals(introduce.getText().toString())){
                    oneBtnDialog = new OneBtnDialog(context, "기업 소개란을 입력해주세요 !", "확인");
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
                            imagePath.add(getImageRealPathFromURI(EditProfileCEOActivity.this.getContentResolver(), imgUri));
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
                        imagePath.add(getImageRealPathFromURI(EditProfileCEOActivity.this.getContentResolver(), imgUri));
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

    void importData(){
        getName = getIntent().getStringExtra("name");
        getCnt = getIntent().getStringExtra("cnt");
        getYear = getIntent().getStringExtra("year");
        getType = getIntent().getStringExtra("type");
        getHome = getIntent().getStringExtra("home");
        getContent = getIntent().getStringExtra("content");
        getPhoto = getIntent().getStringExtra("image");

        Log.i(TAG, " getType " + getType);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if("1".equals(getType)){
                    spinner_text.setText("개인 사업자");
                    getKind = "1";
                } else if ("2".equals(getType)){
                    spinner_text.setText("법인사업자");
                    getKind = "2";
                } else if ("3".equals(getType)){
                    spinner_text.setText("금융기관");
                    getKind = "3";
                } else if ("4".equals(getType)){
                    spinner_text.setText("공기업");
                    getKind = "4";
                } else if ("5".equals(getType)){
                    spinner_text.setText("단체");
                    getKind = "5";
                } else if ("6".equals(getType)){
                    spinner_text.setText("기타");
                    getKind = "6";
                }
            }
        }, 550);

        putImage(profile_img, getPhoto);
        company_name.setText(getName);
        if("null".equals(getName) || null == getName){
            company_name.setText("");
        }
        worker_cnt.setText(getCnt);
        if("null".equals(getCnt) || null == getCnt){
            worker_cnt.setText("");
        }
        since_year.setText(getYear);
        if("null".equals(getYear) || null == getYear){
            since_year.setText("");
        }
        homepage.setText(getHome);
        if("null".equals(getHome) || null == getHome){
            homepage.setText("");
        }
        introduce.setText(getContent);
        if("null".equals(getContent) || null == getContent){
            introduce.setText("");
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
        Glide.with(EditProfileCEOActivity.this)
                .load(getImg)
                .into(imageView);
    }

    void upload() {
        final String url = ServerUrl.getBaseUrl() + "/member/update";
        Map<String, Object> params = new HashMap<String, Object>();

        file.clear();
        if (resizeBitmap.size() == 1) {
            fileUri = getImageUri(EditProfileCEOActivity.this, resizeBitmap.get(0));
            filePath = getImageRealPathFromURI(EditProfileCEOActivity.this.getContentResolver(), fileUri);
            File makeFile = new File(filePath);
            params.put("image", makeFile);
            isChange = true;
        }

        params.put("name", company_name.getText().toString());
        params.put("cnt", worker_cnt.getText().toString());
        params.put("year", since_year.getText().toString());
        params.put("type", getKind);
        params.put("home", homepage.getText().toString());
        params.put("content", introduce.getText().toString());
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(EditProfileCEOActivity.this, "프로필을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(EditProfileCEOActivity.this, "프로필 수정 실패\n다시 시도해주세요.", "확인");
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
