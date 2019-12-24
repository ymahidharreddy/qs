package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Adapters.RecentBloodReqst_Adapter;
import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class RecentBloodRequest_Activity extends AppCompatActivity {

    ListView lv_recentbloodreq;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    private ProgressDialog progressDialog;

    JSONArray RecentBloodreqArray;
    RecentBloodReqst_Adapter RecentBloodreqAdapter;

    JSONArray jsonArray, ReultArray;
    JSONObject jsonObject, JO;
    TextView NoRecords;
    InterstitialAd mInterstitialAd;
    Handler handler;
    Runnable runnable;

    ArrayList<NotificationsModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_blood_request);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Recent Blood Requests");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        lv_recentbloodreq = (ListView) findViewById(R.id.lv_recentbloodreq);
        NoRecords = (TextView) findViewById(R.id.tv_norecords);
        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();

        RecentBloodreqAdapter = new RecentBloodReqst_Adapter(RecentBloodRequest_Activity.this, arrayList);

        //Set the above adapter as the adapter of choice for our list
        lv_recentbloodreq.setAdapter(RecentBloodreqAdapter);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        lv_recentbloodreq.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    lv_recentbloodreq.addFooterView(footerView);
                    getRecentList();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog = ProgressDialog.show(RecentBloodRequest_Activity.this, "", "please wait...");
        getRecentList();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                mInterstitialAd = new InterstitialAd(RecentBloodRequest_Activity.this);
                mInterstitialAd.setAdUnitId(getString(R.string.INTERSTITIAL_AD_ID));
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (mInterstitialAd.isLoaded())
                            mInterstitialAd.show();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                    }
                });
                handler.removeCallbacks(runnable);
            }
        };
        handler.postDelayed(runnable, 10000);

        lv_recentbloodreq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent i = new Intent(getApplicationContext(), Individual_Myrequest_Activity.class);
                    i.putExtra("DATA", arrayList.get(position));
                    startActivityForResult(i, Global.ACTIVITY_FOR_RESULT);
                } catch (Exception e) {
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("lv_recentbloodreq", "item click", "get into some exception");
                }

            }
        });
    }

    private void getRecentList() {
        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.RecentRequests_Url + sp.getString("user_id", "") + "&start=" + start, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    lv_recentbloodreq.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            NotificationsModel nm = new NotificationsModel();
                            nm.setB_req_id(object.getString("b_req_id"));
                            nm.setName(object.getString("name"));
                            nm.setAddress(object.getString("address"));
                            nm.setAge(object.getString("age"));
                            nm.setBloodGroup(object.getString("bloodGroup"));
                            nm.setReq_status_id(object.getString("req_status_id"));
                            nm.setDonor(object.getString("donor"));
                            nm.setMessage(object.getString("message"));
                            nm.setGender(object.getString("gender"));
                            nm.setCreatedTime(object.getString("createdTime"));
                            nm.setAcceptedUsers(object.getJSONArray("acceptedUsers").toString());
                            arrayList.add(nm);
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        RecentBloodreqAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getRecentList", "status=1", "no records");
                    } else {
                        NO_POST_DATA = true;
                        if (arrayList.isEmpty()) {
                            NoRecords.setVisibility(View.VISIBLE);
                            lv_recentbloodreq.setVisibility(View.GONE);
                        }


                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getRecentList", "status=0", "no records");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getRecentList", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED = false;
                RecentBloodreqAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                progressDialog.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getRecentList", "volley error listener", "get into some exception");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("RecentBloodRequestActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
