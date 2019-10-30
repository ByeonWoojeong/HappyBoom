package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main3Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Main3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main3Fragment extends Fragment {
    private static final String TAG = "Main3Fragment";
    AQuery aQuery = null;
    Context context;
    View view;

    String token, mode, getPayNum0, getPayNum1, getPayNum3, getPayNum6;
    SharedPreferences prefToken, HappyBoom;

    OneBtnDialog oneBtnDialog;

    private OnFragmentInteractionListener mListener;

    ImageView profile_img;
    TextView nickname, join_date, birth, dream, introduce_normal, edit, video,
            company_name, worker_cnt, since_year, company_form, homepage, introduce_ceo, exp_date1, video_cnt1, exp_date3, video_cnt3, exp_date6, video_cnt6;
    FrameLayout edit_con, video_con;
    LinearLayout ceo_con, normal_con, item0_con, item1_con, item3_con, item6_con;
    ScrollView scrollView;

    String getNick, getBirth, getDream, getImage, getIntro; //일반
    String getName, getCnt, getYear, getType, getHome, getContent, getPhoto;  //기업

    public Main3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Main3Fragment newInstance(String param1, String param2) {
        Main3Fragment fragment = new Main3Fragment();
        Bundle args = new Bundle();
        args.putString("some_int", param1);
        args.putString("some_string", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main3, container, false);
        context = getContext();
        aQuery = new AQuery(context);

        prefToken = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        token = prefToken.getString("Token", "");

        scrollView = view.findViewById(R.id.scrollView);

        profile_img = view.findViewById(R.id.profile_img);
        ceo_con = view.findViewById(R.id.ceo_con);
        normal_con = view.findViewById(R.id.normal_con);
        edit = view.findViewById(R.id.edit);
        edit_con = view.findViewById(R.id.edit_con);
        video = view.findViewById(R.id.video);
        video_con = view.findViewById(R.id.video_con);

        //일반 회원
        nickname = view.findViewById(R.id.nickname);
        join_date = view.findViewById(R.id.join_date);
        birth = view.findViewById(R.id.birth);
        dream = view.findViewById(R.id.dream);
        introduce_normal = view.findViewById(R.id.introduce_normal);

        //기업 회원
        company_name = view.findViewById(R.id.company_name);
        worker_cnt = view.findViewById(R.id.worker_cnt);
        since_year = view.findViewById(R.id.since_year);
        company_form = view.findViewById(R.id.company_form);
        homepage = view.findViewById(R.id.homepage);
        introduce_ceo = view.findViewById(R.id.introduce_ceo);
        exp_date1 = view.findViewById(R.id.exp_date1);
        video_cnt1 = view.findViewById(R.id.video_cnt1);
        exp_date3 = view.findViewById(R.id.exp_date3);
        video_cnt3 = view.findViewById(R.id.video_cnt3);
        exp_date6 = view.findViewById(R.id.exp_date6);
        video_cnt6 = view.findViewById(R.id.video_cnt6);
        item0_con = view.findViewById(R.id.item0_con);
        item1_con = view.findViewById(R.id.item1_con);
        item3_con = view.findViewById(R.id.item3_con);
        item6_con = view.findViewById(R.id.item6_con);

        item0_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductManagementActivity.class);
                intent.putExtra("idx", getPayNum1);
                startActivity(intent);
            }
        });
        item1_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductManagementActivity.class);
                intent.putExtra("idx", getPayNum0);
                startActivity(intent);
            }
        });
        item3_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductManagementActivity.class);
                intent.putExtra("idx", getPayNum3);
                startActivity(intent);
            }
        });
        item6_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductManagementActivity.class);
                intent.putExtra("idx", getPayNum6);
                startActivity(intent);
            }
        });

        edit_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.callOnClick();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Log.i(TAG, " Mode " + mode);
                if ("normal".equals(mode)) {
                    intent = new Intent(context, EditProfileNormalActivity.class);
                    intent.putExtra("image", getImage);
                    intent.putExtra("nick", getNick);
                    intent.putExtra("birth", getBirth);
                    intent.putExtra("dream", getDream);
                    intent.putExtra("intro", getIntro);
                    startActivity(intent);
                } else {
                    intent = new Intent(context, EditProfileCEOActivity.class);
                    intent.putExtra("image", getPhoto);
                    intent.putExtra("name", getName);
                    intent.putExtra("cnt", getCnt);
                    intent.putExtra("year", getYear);
                    intent.putExtra("type", getType);
                    intent.putExtra("home", getHome);
                    intent.putExtra("content", getContent);
                    startActivity(intent);
                }

            }
        });

        video_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video.callOnClick();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UploadVideoActivity.class);
                startActivity(intent);
            }
        });


        ((MainActivity)getActivity()).tab_con.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, " onResume");
        reload();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    void reload() {
        SharedPreferences HappyBoom = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        mode = HappyBoom.getString("Mode", "");

        if ("normal".equals(mode)) {
            normal_con.setVisibility(View.VISIBLE);
            ceo_con.setVisibility(View.GONE);
            video_con.setVisibility(View.GONE);
            showingNormal();
        } else {
            normal_con.setVisibility(View.GONE);
            ceo_con.setVisibility(View.VISIBLE);
            video_con.setVisibility(View.VISIBLE);
            video_cnt1.setText("0");
            video_cnt3.setText("0");
            video_cnt6.setText("0");
            showingCEO();
        }
    }

    void showingNormal() {
        final String url = ServerUrl.getBaseUrl() + "/member/profile";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                        JSONObject jsonData = jsonObject.getJSONObject("data");

                        getNick = jsonData.getString("nick");
                        getBirth = jsonData.getString("birth");
                        getDream = jsonData.getString("dream");
                        getImage = ServerUrl.getBaseUrl() + "/uploads/images/origin/" + jsonData.getString("image");
                        getIntro = jsonData.getString("intro");

                        nickname.setText(getNick);
                        if("null".equals(getNick)){
                            nickname.setText("");
                        }
                        birth.setText(getBirth);
                        if("null".equals(getBirth)){
                            birth.setText("");
                        }
                        join_date.setText(jsonData.getString("date"));
                        dream.setText(getDream);
                        if("null".equals(getDream)){
                            dream.setText("");
                        }
                        putImage(profile_img, getImage);
                        introduce_normal.setText(getIntro);
                        if("null".equals(getIntro)){
                            introduce_normal.setText("");
                        }

                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", token).header("User-Agent", "android"));
    }

    void showingCEO() {
        final String url = ServerUrl.getBaseUrl() + "/member/profile";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                        JSONObject jsonData = jsonObject.getJSONObject("data");

                        getName = jsonData.getString("name");
                        getCnt = jsonData.getString("cnt");
                        getYear = jsonData.getString("year");
                        getType = jsonData.getString("type");
                        getHome = jsonData.getString("home");
                        getContent = jsonData.getString("content");
                        getPhoto = ServerUrl.getBaseUrl() + "/uploads/images/origin/" + jsonData.getString("image");

                        company_name.setText(getName);
                        if("null".equals(getName)){
                            company_name.setText("");
                        }

                        worker_cnt.setText(getCnt);
                        if("null".equals(getCnt)){
                            worker_cnt.setText("");
                        }

                        since_year.setText(getYear);
                        if("null".equals(getYear)){
                            since_year.setText("");
                        }

                        if ("1".equals(getType)) {
                            company_form.setText("개인 사업자");
                        } else if ("2".equals(getType)) {
                            company_form.setText("법인사업자");
                        } else if ("3".equals(getType)) {
                            company_form.setText("금융기관");
                        } else if ("4".equals(getType)) {
                            company_form.setText("공기업");
                        } else if ("5".equals(getType)) {
                            company_form.setText("단체");
                        } else if ("6".equals(getType)) {
                            company_form.setText("기타");
                        }
                        homepage.setText(getHome);
                        if("null".equals(getHome)){
                            homepage.setText("");
                        }

                        putImage(profile_img, getPhoto);
                        introduce_ceo.setText(getContent);
                        if("null".equals(getContent)){
                            introduce_ceo.setText("");
                        }

                        JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject getJsonObject = jsonArray.getJSONObject(i);
                            if ("구인공고".equals(getJsonObject.getString("type"))) {
                                item0_con.setBackgroundColor(Color.WHITE);
                                item0_con.setClickable(true);
                                getPayNum0 = getJsonObject.getString("idx");
                            }else if ("1개월".equals(getJsonObject.getString("type"))) {
                                item1_con.setBackgroundColor(Color.WHITE);
                                item1_con.setClickable(true);
                                exp_date1.setText(getJsonObject.getString("date"));
                                video_cnt1.setText(getJsonObject.getString("cnt"));
                                getPayNum1 = getJsonObject.getString("idx");
                            } else if ("3개월".equals(getJsonObject.getString("type"))) {
                                item3_con.setBackgroundColor(Color.WHITE);
                                item3_con.setClickable(true);
                                exp_date3.setText(getJsonObject.getString("date"));
                                video_cnt3.setText(getJsonObject.getString("cnt"));
                                getPayNum3 = getJsonObject.getString("idx");
                            } else if ("6개월".equals(getJsonObject.getString("type"))) {
                                item6_con.setBackgroundColor(Color.WHITE);
                                item6_con.setClickable(true);
                                exp_date6.setText(getJsonObject.getString("date"));
                                video_cnt6.setText(getJsonObject.getString("cnt"));
                                getPayNum6 = getJsonObject.getString("idx");
                            }
                        }

                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", token).header("User-Agent", "android"));
    }

    void putImage(ImageView imageView, String getImg) {
        Glide.with(context)
                .load(getImg)
                .into(imageView);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
