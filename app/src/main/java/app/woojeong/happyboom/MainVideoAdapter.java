package app.woojeong.happyboom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import app.woojeong.happyboom.DTO.MainVideo;

public class MainVideoAdapter extends BaseAdapter {
    private static final String TAG = "MainVideoAdapter";
    Context context;
    int layout, selectPosition = -1;
    ArrayList<MainVideo> data;
    ListView listView;
    AQuery aQuery = null;
    MainVideoAdapter mainVideoAdapter = this;

    Function fun;

    SharedPreferences get_token;
    String getToken = "";

    public MainVideoAdapter(Context context, int layout, ArrayList<MainVideo> data, ListView listView) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.listView = listView;
        this.fun = fun;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        aQuery = new AQuery(context);

        if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            vh = new ViewHolder();
            vh.thumbnail = convertView.findViewById(R.id.thumbnail);
            vh.profile_img = convertView.findViewById(R.id.profile_img);
            vh.nickname = convertView.findViewById(R.id.nickname);
            vh.date = convertView.findViewById(R.id.date);
            vh.content = convertView.findViewById(R.id.content);
            vh.like_count = convertView.findViewById(R.id.like_count);
            vh.share_count = convertView.findViewById(R.id.share_count);
            vh.comment_count = convertView.findViewById(R.id.comment_count);
            vh.like = convertView.findViewById(R.id.like);
            vh.like_con = convertView.findViewById(R.id.like_con);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final MainVideo item = data.get(position);
        final ViewHolder finalVh = vh;

        get_token = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        putImage(vh.thumbnail, ServerUrl.getBaseUrl() + "/uploads/images/origin/" + item.getThumbnail());
        putImage(vh.profile_img, ServerUrl.getBaseUrl() + "/uploads/images/origin/" + item.getProfieImage());
        vh.nickname.setText(item.getNick());
        vh.date.setText(item.getDate());
        vh.content.setText(item.getContent());
        vh.like_count.setText(item.getLikeCnt());
        vh.share_count.setText(item.getShareCnt());
        vh.comment_count.setText(item.getReplyCnt());

        if ("0".equals(item.getIsLike())) {
            vh.like.setChecked(false);
        } else {
            vh.like.setChecked(true);
        }

        vh.like_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalVh.like.callOnClick();
            }
        });

//        mainPagerAdapter = new MainPagerAdapter(fragmentManager);

        vh.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = ServerUrl.getBaseUrl() + "/community/like";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("key", item.getIdx());
                Log.i(TAG, " params " + params);
                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                        Log.i(TAG, " jsonObject " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("return")) {    //return이 true 면?

                                if ("0".equals(jsonObject.getString("data"))) {
                                    // -1
                                    item.setLikeCnt(Integer.parseInt(item.getLikeCnt()) - 1 + "");
                                    finalVh.like_count.setText(item.getLikeCnt());

                                } else {
                                    // +1
                                    item.setLikeCnt(Integer.parseInt(item.getLikeCnt()) + 1 + "");
                                    finalVh.like_count.setText(item.getLikeCnt());
                                }

                            } else if (!jsonObject.getBoolean("return")) {
                                Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.header("epoch-agent", getToken).header("User-Agent", "android"));
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                Intent intent = new Intent(context, CommunityActivity.class);
                intent.putExtra("idx", item.getIdx());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView thumbnail, profile_img;
        TextView nickname, date, content, like_count, share_count, comment_count;
        CheckBox like;
        FrameLayout like_con;
    }

    void putImage(ImageView imageView, String getImg) {
        Glide.with(context)
                .load(getImg)
                .into(imageView);
    }

    int selectItem() {

        return selectPosition;

    }

}
