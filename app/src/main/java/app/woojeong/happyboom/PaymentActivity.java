package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class PaymentActivity extends AppCompatActivity {
    private static String TAG = "PaymentActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;

    SharedPreferences get_token;
    String getToken;

    FrameLayout back_con, ok_con;
    ImageView back;
    ScrollView scrollView;
    TextView explain, ok;
    CheckBox check_a, check_b, check_c;

    private final String LICENSE_KEY = "";
    BillingProcessor billingProcessor = null;
    private String ITEM_1MONTH = "1month";
    private String ITEM_3MONTH = "3month";
    private String ITEM_6MONTH = "6month";
    String why_bill, getDetail, getItem, type, skuId, skuTitle;
    long skuPriceLong;
    boolean bill_overlap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(PaymentActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

        explain = findViewById(R.id.explain);
        ok = findViewById(R.id.ok);

        check_a = findViewById(R.id.check_a);
        check_b = findViewById(R.id.check_b);
        check_c = findViewById(R.id.check_c);

        check_a.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_b.setChecked(false);
                    check_c.setChecked(false);
                    getItem = "1month";
                }
            }
        });

        check_b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_a.setChecked(false);
                    check_c.setChecked(false);
                    getItem = "3month";
                }
            }
        });

        check_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_a.setChecked(false);
                    check_b.setChecked(false);
                    getItem = "6month";
                }
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
                if (!check_a.isChecked() && !check_b.isChecked() && !check_c.isChecked()) {
                    oneBtnDialog = new OneBtnDialog(context, "결제 상품을 선택해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
                if (bill_overlap) {
                    return;
                }
//                Toast.makeText(PaymentActivity.this, "In App Pay", Toast.LENGTH_SHORT).show();


                if("1month".equals(getItem)){
                    purchaseProduct(ITEM_1MONTH);
                    billingProcessor.getPurchaseTransactionDetails(ITEM_1MONTH);
                } else if ("3month".equals(getItem)){
                    purchaseProduct(ITEM_3MONTH);
                    billingProcessor.getPurchaseTransactionDetails(ITEM_3MONTH);
                } else if ("6month".equals(getItem)){
                    purchaseProduct(ITEM_6MONTH);
                    billingProcessor.getPurchaseTransactionDetails(ITEM_6MONTH);
                }
                bill_overlap = true;

                oneBtnDialog = new OneBtnDialog(PaymentActivity.this, "서비스 준비 중 입니다 !", "확인", true);
                oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                oneBtnDialog.setCancelable(false);
                oneBtnDialog.show();
            }
        });

        if (!BillingProcessor.isIabServiceAvailable(this)) {
            oneBtnDialog = new OneBtnDialog(PaymentActivity.this, "인앱 결제 서비스가\n지원되지 않습니다 !", "확인", true);
            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            oneBtnDialog.setCancelable(false);
            oneBtnDialog.show();
        }

        billingProcessor = new BillingProcessor(PaymentActivity.this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {

            /* BillingProcessor가 초기화되고, 구매 준비가 되면 호출된다. 이 부분에서 구매할 아이템들을 리스트로 구성해서 보여주는 코드를 구현하면 된다. */
            @Override
            public void onBillingInitialized() {
                Log.i(TAG, "onBillingInitialized():: 구매 준비 완료");
            }

            /* 특정 제품 ID를 가진 아이템의 구매 성공시 호출된다. */
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

                // 구매한 아이템 정보
                SkuDetails skuDetails = billingProcessor.getPurchaseListingDetails(productId);
                skuTitle = skuDetails.title;  //해당 아이템의 이름
                String skuPriceText = skuDetails.priceText; //아이템 가격의 현지 화폐 단위. ex 0.99$
                skuPriceLong = skuDetails.priceLong; //아이템 가격을 long으로 리턴. ex 0.99
                skuId = skuDetails.productId;    //아이템 ID를 가지고 옴. 어떤 아이템을 구매했는지 판별 가능
                String skuDescription = skuDetails.description;

                Log.i(TAG, "onProductPurchased():: 특정 제품 ID 구매 성공 시 호출");
                Log.i(TAG, "onProductPurchased():: TransactionDetails:: " + details);
                Log.i(TAG, "onProductPurchased():: SkuDetails:: " + skuDetails);
                Log.i(TAG, "onProductPurchased():: skuId:: " + skuId);
                Log.i(TAG, "onProductPurchased():: SkuPriceText:: " + skuPriceText);
                Log.i(TAG, "onProductPurchased():: SkuPriceLong:: " + skuPriceLong);
                Log.i(TAG, "onProductPurchased():: SkuTitle:: " + skuTitle);
                Log.i(TAG, "onProductPurchased():: SkuDescription:: " + skuDescription);

                // 구매 처리
                setResult(Activity.RESULT_OK);
            }

            /* 구매 이력이 있는지 확인하는 메소드 */
            @Override
            public void onPurchaseHistoryRestored() {
                Log.i(TAG, "onPurchaseHistoryRestored():: - 구매 이력이 있는지 확인");
            }

            /* 구매시 어떤 오류가 발생했을 때 호출된다. */
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {

                Log.i(TAG, "onBillingError():: 에러 코드: " + errorCode);

                if (errorCode == Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {   // errorCode == 1
                    Toast.makeText(PaymentActivity.this, "구매 과정에서 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                } else if (errorCode == Constants.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                    Toast.makeText(PaymentActivity.this, "네트워크 연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
                } else if (errorCode == Constants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE) {
                    Toast.makeText(PaymentActivity.this, "요청한 제품을 구매할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "구매 중 오류가 발생했습니다.\nerrorCode : " + errorCode, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult()");

        switch (resultCode) {
            case RESULT_CANCELED:
                bill_overlap = false;
                break;
            case 444:
                finish();
                break;
        }

        if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            bill_overlap = false;
            Log.i(TAG, "onActivityResult()" + " : RESULT_OK");

            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            try {

                SharedPreferences get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
                final String getToken = get_token.getString("Token", "");
                String url = ServerUrl.getBaseUrl() + "";
                JSONObject purchaseDataJson = new JSONObject(purchaseData);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("skuId", skuId);
                params.put("SkuPriceLong", skuPriceLong);
                params.put("SkuTitle", skuTitle);
                params.put("receipt", purchaseDataJson);
                Log.i(TAG, "Params " + params);
                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                        if (jsonObject != null) {
                            try {
                                if (jsonObject.getBoolean("return")) {

                                    Log.i(TAG, "onActivityResult() :: " + " return TRUE");

                                    // 구매에 성공하였습니다. 다이얼로그 띄우기 (성공시 - finish())
                                    oneBtnDialog = new OneBtnDialog(PaymentActivity.this, skuTitle + "\n구매에 성공하였습니다.", "확인", true);
                                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    oneBtnDialog.setCancelable(false);
                                    oneBtnDialog.show();

                                } else if (!jsonObject.getBoolean("return")) {
                                    Log.i(TAG, "onActivityResult() :: " + " return FALSE");
                                    Toast.makeText(PaymentActivity.this, "구매에 실패하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.header("GHsoft-Agent", "" + getToken).header("User-Agent", "android"));

            } catch (JSONException e) {
                Toast.makeText(this, "서버 통신 실패\n다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release(); //billingProcessor 해제
        }
        super.onDestroy();
    }

    //구매하기 함수
    public void purchaseProduct(final String productId) {
        if (billingProcessor.isPurchased(productId)) {
            // 구매하였으면 소비하여 없앤 후 다시 구매하게 하는 로직. 만약 1번 구매 후 계속 이어지게 할 것이면 아래 함수는 주석처리.
            billingProcessor.consumePurchase(productId);    //(주석 처리하면 재구매할 수 없음.)
            Log.i(TAG, "purchaseProduct():: consumPurchase() - 다시 구매하게 하는 로직");
        }
        billingProcessor.purchase(this, productId);
        Log.i(TAG, "purchaseProduct():: purchase() - 구매");
    }

    public class OneBtnDialog extends Dialog {
        OneBtnDialog oneBtnDialog = this;
        Context context;

        public OneBtnDialog(final Context context, final String text, final String btnText, final boolean finish) {
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
                    if (finish) {
                        finish();
                    }
                }
            });
        }

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
}
