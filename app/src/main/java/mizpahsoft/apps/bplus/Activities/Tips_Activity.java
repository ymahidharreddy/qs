package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Adapters.TipsAdapter;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class Tips_Activity extends AppCompatActivity {

    ListView lv_tips;
    ProgressDialog progressDialog;
    TipsAdapter tipsAdapter;
    ArrayList<String> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Tips");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv_tips = (ListView) findViewById(R.id.lv_notification);
        tipsAdapter = new TipsAdapter(Tips_Activity.this, arrayList);//jArray is your json array

        //Set the above adapter as the adapter of choice for our list
        lv_tips.setAdapter(tipsAdapter);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        lv_tips.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());

                    lv_tips.addFooterView(footerView);
                    getTips();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog = ProgressDialog.show(Tips_Activity.this, "", "please wait...");
        getTips();
    }

    private void getTips() {
        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.Tips_Url + start, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    lv_tips.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            arrayList.add(object.getString("tip"));
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        tipsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getTips", "status=1", "Tips loaded");

                    } else {
                        NO_POST_DATA = true;
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getTips", "status=0", jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getTips", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getTips", "volley error listener", "get into some exception");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("TipsActivity");
    }
}
