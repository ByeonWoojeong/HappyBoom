package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.Comment;

public class CommentListAdapter extends BaseAdapter {
    private static final String TAG = "CommentListAdapter";
    Context context;
    int layout;
    ArrayList<Comment> data;
    ListView listView;
    AQuery aQuery = null;
    CommentListAdapter commentListAdapter = this;

    SharedPreferences get_token;
    String getToken;

    OneBtnDialog oneBtnDialog;
    ReportDialog reportDialog;
    DeleteDialog deleteDialog;

    public CommentListAdapter(Context context, int layout, ArrayList<Comment> data, ListView listView) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.listView = listView;
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
        get_token = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");
        if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            vh = new ViewHolder();
            vh.comment_con = convertView.findViewById(R.id.comment_con);
            vh.nickname = convertView.findViewById(R.id.nickname1);
            vh.date = convertView.findViewById(R.id.date);
            vh.content = convertView.findViewById(R.id.content);
            vh.re_nickname = convertView.findViewById(R.id.nickname2);
            vh.re_date = convertView.findViewById(R.id.re_date);
            vh.re_content = convertView.findViewById(R.id.re_content);
            vh.write_recomment = convertView.findViewById(R.id.write_recomment);
            vh.report_comment = convertView.findViewById(R.id.report_comment);
            vh.edit_comment = convertView.findViewById(R.id.edit_comment);
            vh.delete_comment = convertView.findViewById(R.id.delete_comment);
            vh.re_report = convertView.findViewById(R.id.re_report);
            vh.edit_re = convertView.findViewById(R.id.edit_re);
            vh.delete_re = convertView.findViewById(R.id.delete_re);
            vh.re_comment_con = convertView.findViewById(R.id.re_comment_con);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Comment item = data.get(position);

        vh.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("member", item.getMember());
                context.startActivity(intent);
            }
        });

        vh.re_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("member", item.getMember());
                context.startActivity(intent);
            }
        });

        if("1".equals(item.getIsReply())){
            //대댓글이면?
            vh.re_comment_con.setVisibility(View.VISIBLE);
            vh.comment_con.setVisibility(View.GONE);
            vh.re_nickname.setText(item.getNick());
            vh.re_date.setText(item.getDate());
            vh.re_content.setText(item.getContent());

            if("1".equals(item.getIsMy())){
                //내꺼면?
                vh.edit_re.setVisibility(View.VISIBLE);
                vh.delete_re.setVisibility(View.VISIBLE);
                vh.re_report.setVisibility(View.GONE);
            }else {
                vh.edit_re.setVisibility(View.GONE);
                vh.delete_re.setVisibility(View.GONE);
                vh.re_report.setVisibility(View.VISIBLE);
            }

        }else {
            //그냥 댓글이면?
            vh.re_comment_con.setVisibility(View.GONE);
            vh.comment_con.setVisibility(View.VISIBLE);
            vh.nickname.setText(item.getNick());
            vh.date.setText(item.getDate());
            vh.content.setText(item.getContent());

            if("1".equals(item.getIsMy())){
                //내꺼면?
                vh.edit_comment.setVisibility(View.VISIBLE);
                vh.delete_comment.setVisibility(View.VISIBLE);
                vh.report_comment.setVisibility(View.GONE);
            }else {
                vh.edit_comment.setVisibility(View.GONE);
                vh.delete_comment.setVisibility(View.GONE);
                vh.report_comment.setVisibility(View.VISIBLE);
            }
        }


        vh.write_recomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CommunityActivity)context).commentMode = "write_recomment";
                ((CommunityActivity)context).to_re_nick.setVisibility(View.VISIBLE);
                ((CommunityActivity)context).to_re_nick.setText(item.getNick());
                ((CommunityActivity)context).target = item.getKey();
            }
        });

        vh.report_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialog = new ReportDialog(context, "댓글 신고 사유", item.getKey());
                reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportDialog.setCancelable(false);
                reportDialog.show();
            }
        });

        vh.edit_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("삭제된 댓글입니다.".equals(item.getContent())){
                    oneBtnDialog = new OneBtnDialog(context, "삭제된 댓글입니다 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
                ((CommunityActivity)context).commentMode = "edit_comment";
                ((CommunityActivity)context).edit_comment.setText(item.getContent());
                ((CommunityActivity)context).getKey = item.getKey();

            }
        });

        vh.delete_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog = new DeleteDialog(context, item.getKey());
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.setCancelable(false);
                deleteDialog.show();
            }
        });

        vh.re_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialog = new ReportDialog(context, "댓글 신고 사유", item.getKey());
                reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportDialog.setCancelable(false);
                reportDialog.show();
            }
        });

        vh.edit_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("삭제된 댓글입니다.".equals(item.getContent())){
                    oneBtnDialog = new OneBtnDialog(context, "삭제된 댓글입니다 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                ((CommunityActivity)context).commentMode = "edit_comment";
                ((CommunityActivity)context).edit_comment.setText(item.getContent());
                ((CommunityActivity)context).getKey = item.getKey();
            }
        });

        vh.delete_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog = new DeleteDialog(context, item.getKey());
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.setCancelable(false);
                deleteDialog.show();
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView nickname, date, content, re_nickname, re_date,re_content, write_recomment, report_comment,edit_comment, delete_comment, re_report, edit_re, delete_re;
        RelativeLayout comment_con, re_comment_con;
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

    public class ReportDialog extends Dialog {
        ReportDialog reportDialog = this;
        Context context;

        public ReportDialog(final Context context, final String title, final String idx) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_report);
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
                    reportDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String url = ServerUrl.getBaseUrl() + "/community/rsingo";
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("idx", idx);
                    params.put("content", content.getText().toString());
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " " + jsonObject);
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                    reportDialog.dismiss();
                                    Toast.makeText(context, "해당 댓글을 신고하였습니다.",Toast.LENGTH_SHORT).show();
                                } else if (!jsonObject.getBoolean("return")) {
                                    Toast.makeText(context, "다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.header("epoch-agent", getToken).header("User-Agent", "android"));
                }
            });
        }
    }

    public class DeleteDialog extends Dialog {
        DeleteDialog deleteDialog = this;
        Context context;

        public DeleteDialog(final Context context, final String key) {
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
            title1.setText("댓글을 삭제하시겠습니까?");
            btn1.setText("아니요");
            btn2.setText("네");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //아니요
                    deleteDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //네
                    final String url = ServerUrl.getBaseUrl() + "/community/rdelete";
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("idx", key);
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " " + jsonObject);
                            deleteDialog.dismiss();
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                    Toast.makeText(context, "해당 댓글을 삭제하였습니다.",Toast.LENGTH_SHORT).show();
                                ((CommunityActivity)context).showing(false);
                                } else if (!jsonObject.getBoolean("return")) {
                                    Toast.makeText(context, "다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.header("epoch-agent", getToken).header("User-Agent", "android"));

                }
            });
        }
    }
}
