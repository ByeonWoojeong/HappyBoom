package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class TermsListActivity extends AppCompatActivity {
    private static String TAG = "TermsListActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;

    SharedPreferences get_token;
    String getToken;

    FrameLayout back_con, service_con, privacy1_con, privacy2_con, payment_con, refund_con;
    ImageView back;
    TextView service, privacy1, privacy2, payment, refund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(TermsListActivity.this, true);
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

        service_con = findViewById(R.id.service_con);
        privacy1_con = findViewById(R.id.privacy1_con);
        privacy2_con = findViewById(R.id.privacy2_con);
        payment_con = findViewById(R.id.payment_con);
        refund_con = findViewById(R.id.refund_con);

        service = findViewById(R.id.service);
        privacy1 = findViewById(R.id.privacy1);
        privacy2 = findViewById(R.id.privacy2);
        payment = findViewById(R.id.payment);
        refund = findViewById(R.id.refund);

        service_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.callOnClick();
            }
        });
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermsListActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 1);
                startActivity(intent);
            }
        });

        privacy1_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacy1.callOnClick();
            }
        });
        privacy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermsListActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 2);
                startActivity(intent);
            }
        });

        privacy2_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacy2.callOnClick();
            }
        });
        privacy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermsListActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 3);
                startActivity(intent);
            }
        });

        payment_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment.callOnClick();
            }
        });
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermsListActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 4);
                startActivity(intent);
            }
        });

        refund_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refund.callOnClick();
            }
        });
        refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermsListActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 5);
                startActivity(intent);
            }
        });
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
}
