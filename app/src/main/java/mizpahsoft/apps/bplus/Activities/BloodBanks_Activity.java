package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import mizpahsoft.apps.bplus.Adapters.BloodBanksAdapter;
import mizpahsoft.apps.bplus.Model.BloodBanksModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class BloodBanks_Activity extends AppCompatActivity {

    ListView lv_bloodbanks;
    ProgressDialog progressDialog;
    BloodBanksAdapter BloodBanksAdapter;
    InterstitialAd mInterstitialAd;
    Handler handler;
    Runnable runnable;
    ArrayList<BloodBanksModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_banks_);
        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Blood banks");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv_bloodbanks = (ListView) findViewById(R.id.lv_bloodbanks);

        BloodBanksAdapter = new BloodBanksAdapter(BloodBanks_Activity.this, arrayList);

        //Set the above adapter as the adapter of choice for our list
        lv_bloodbanks.setAdapter(BloodBanksAdapter);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        lv_bloodbanks.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());

                    lv_bloodbanks.addFooterView(footerView);
                    GetBloodBanks();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog = ProgressDialog.show(BloodBanks_Activity.this, "", "please wait..");
        GetBloodBanks();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                mInterstitialAd = new InterstitialAd(BloodBanks_Activity.this);
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
    }

    private void GetBloodBanks() {
        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.BloodBanks_Url + start, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    lv_bloodbanks.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            BloodBanksModel bb = new BloodBanksModel();
                            bb.setB_bankName(object.getString("b_bankName"));
                            bb.setAddress(object.getString("address"));
                            bb.setB_bank_phoneNo(object.getString("b_bank_phoneNo"));
                            bb.setB_bank_emailId(object.getString("b_bank_emailId"));
                            bb.setB_bank_websitUrl(object.getString("b_bank_websitUrl"));
                            arrayList.add(bb);
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        BloodBanksAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                    } else {
                        NO_POST_DATA = true;
                        MyApplication.getInstance().trackEvent("getbloodbanks", "status=0", jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getbloodbanks", "mainjsonparsingerror", "get into some exception");
                }
                progressDialog.dismiss();
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getbloodbanks", "volleyerror", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("BloodBanksActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
