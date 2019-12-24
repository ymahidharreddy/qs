package mizpahsoft.apps.bplus.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Model.SearchModel;
import mizpahsoft.apps.bplus.R;

/**** Created by Mizpah_DEV on 29-Nov-16. */

public class DonorsWithGroupAdapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    private ArrayList<SearchModel> arrayList;
    private ProgressDialog progressDialog;
    private SharedPreferences sp;

    public DonorsWithGroupAdapter(Activity activity, ArrayList<SearchModel> arrayList) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if (convertView == null)
        convertView = activity.getLayoutInflater().inflate(R.layout.searchwithblood, null);

        sp = activity.getSharedPreferences("loginprefs", 0);

        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        name.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView location = (TextView) convertView.findViewById(R.id.tv_location);
        location.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView tv_SendReq = (TextView) convertView.findViewById(R.id.dshbf);
        tv_SendReq.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        ImageView image_userpic = (ImageView) convertView.findViewById(R.id.image_userpic);


        TextView bloodGrp = (TextView) convertView.findViewById(R.id.tv_bloodgroup);
        LinearLayout ll_vv = (LinearLayout) convertView.findViewById(R.id.ll_vv);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        SearchModel sm = arrayList.get(position);
        try {

            name.setText(sm.getName());
            location.setText(sm.getAddress());
            bloodGrp.setText(sm.getBloodgroup());
            String profilePicture = sm.getProfilePicture();

            String url = Global.Images_Base_URL + "uploads/profilePics/" + profilePicture;
            Picasso.with(activity).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).resize(150, 150).into(image_userpic);

            final TextView id1 = new TextView(activity);
            id1.setLayoutParams(lparams);
            id1.setTag(sm.getUserId());
            id1.setTextSize(16);
            ll_vv.addView(id1);

            tv_SendReq.setTag(sm.getUserId());
            tv_SendReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendRequest(v.getTag().toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void SendRequest(final String id) {
        progressDialog = ProgressDialog.show(activity,
                "Sending Request", "Please wait!");
        sp = activity.getSharedPreferences("loginprefs", 0);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.SingleReq,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            String message = object.getString("message");
                            if (success == 1) {
                                TastyToast.makeText(activity, "" + message, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                progressDialog.dismiss();
                            } else {
                                TastyToast.makeText(activity, "" + message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            MyApplication.getInstance().trackException(e);
                            Toast.makeText(activity, "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        TastyToast.makeText(activity, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("donarId", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}

