package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Adapters.DonorsWithGroupAdapter;
import mizpahsoft.apps.bplus.Model.SearchModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class SearchDonaorByBloodGrp_Activity extends AppCompatActivity {
    ArrayAdapter bloodarrayAdapter;
    ListView lv_blood_group, lv_donors_list;
    ProgressDialog pd;
    ArrayList<String> blood_groups_array = new ArrayList<>();
    ArrayList<String> blood_groups_id = new ArrayList<>();
    DonorsWithGroupAdapter donorsAdapter;
    String userid;
    SharedPreferences sp;
    ArrayList<SearchModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;
    String BLOODGROUPID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donaor_by_blood_grp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("loginprefs", 0);
        lv_blood_group = (ListView) findViewById(R.id.lv_blood_group);
        lv_donors_list = (ListView) findViewById(R.id.lv_donors_list);

        lv_blood_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userid = sp.getString("user_id", "N/A");
                dialogshow();
                BLOODGROUPID = blood_groups_id.get(i);
                arrayList.clear();
                GetDonorsByGroup(userid, blood_groups_id.get(i));
            }
        });

        donorsAdapter = new DonorsWithGroupAdapter(SearchDonaorByBloodGrp_Activity.this, arrayList);//jArray is your json array
        //Set the above adapter as the adapter of choice for our list
        lv_donors_list.setAdapter(donorsAdapter);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        lv_donors_list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                userid = sp.getString("user_id", "N/A");
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    lv_donors_list.addFooterView(footerView);
                    GetDonorsByGroup(userid, BLOODGROUPID);
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        GetBloodGroups();
    }

    public void dialogshow() {

        pd = ProgressDialog.show(SearchDonaorByBloodGrp_Activity.this, "", "Please wait...", true);

    }

    public void dialogcancel() {
        pd.dismiss();
    }

    private void GetBloodGroups() {
        dialogshow();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.profile_spinnerdata,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                JSONArray ar_results = object.getJSONArray("result");
                                for (int i = 0; i < ar_results.length(); i++) {
                                    JSONObject ob_results = ar_results.getJSONObject(i);
                                    blood_groups_array.add(ob_results.getString("bloodgroup"));
                                    blood_groups_id.add(ob_results.getString("id"));
                                }

                                bloodarrayAdapter = new ArrayAdapter(SearchDonaorByBloodGrp_Activity.this, R.layout.blood_group_list, blood_groups_array);
                                lv_blood_group.setAdapter(bloodarrayAdapter);
                                dialogcancel();
                                MyApplication.getInstance().trackEvent("GetBloodGroups", "status=1", "got all blodd groups");

                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(SearchDonaorByBloodGrp_Activity.this, message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                dialogcancel();
                                MyApplication.getInstance().trackEvent("GetBloodGroups", "status=0", message);
                            }
                        } catch (Exception e) {
                            //TastyToast.makeText(SearchDonaorByBloodGrp_Activity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            dialogcancel();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("GetBloodGroups", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TastyToast.makeText(SearchDonaorByBloodGrp_Activity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        dialogcancel();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("GetBloodGroups", "volley error listener", "get into some exception");
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void GetDonorsByGroup(String userid, String bloodgrpid) {
        String URL = "";
        if (Global.getSP(this, Global.USER_TYPE).equals("1")) {
            URL = Global.DONORS_WITH_BOOLD_GROUP + "?bloodGroup=" + bloodgrpid + "&userId=" + userid + "&start=" + start;
        } else if (Global.getSP(this, Global.USER_TYPE).equals("2")) {
            URL = Global.DONORS_WITH_BOOLD_GROUP + "?bloodGroup=" + bloodgrpid + "&bloodBankId=" + userid + "&start=" + start;
        }
        StringRequest stringrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    lv_donors_list.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        lv_blood_group.setVisibility(View.GONE);
                        lv_donors_list.setVisibility(View.VISIBLE);
                        JSONArray array = jsonObject.getJSONArray("donors");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            SearchModel sm = new SearchModel();
                            sm.setUserId(object.getString("userId"));
                            sm.setName(object.getString("name"));
                            sm.setAddress(object.getString("address"));
                            sm.setProfilePicture(object.getString("profilePicture"));
                            sm.setBloodgroup(object.getString("bloodgroup"));
                            arrayList.add(sm);
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        donorsAdapter.notifyDataSetChanged();
                    } else {
                        NO_POST_DATA = true;
                        if (arrayList.isEmpty())
                            TastyToast.makeText(SearchDonaorByBloodGrp_Activity.this, "No donors available for this blood group try again later!", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                        dialogcancel();
                        MyApplication.getInstance().trackEvent("GetDonorsByGroup", "status=0", "No donors available for this blood group");
                    }
                } catch (JSONException e) {
                    dialogcancel();
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("GetDonorsByGroup", "mainjsonparsingexception", "get into some exception");
                    //Toast.makeText(getApplicationContext(),""+e, Toast.LENGTH_SHORT).show();
                }
                dialogcancel();
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                dialogcancel();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("GetDonorsByGroup", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // key="completed";
                start = 0;
                if (lv_blood_group.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    lv_blood_group.setVisibility(View.VISIBLE);
                    lv_donors_list.setVisibility(View.GONE);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
// super.onBackPressed();
        start = 0;
        if (lv_blood_group.getVisibility() == View.VISIBLE) {
            finish();
        } else {
            lv_blood_group.setVisibility(View.VISIBLE);
            lv_donors_list.setVisibility(View.GONE);
        }
        MyApplication.getInstance().trackEvent("onBackPressed", "navigation", "respective action will be taken");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("SearchDonorByBloodGroup");
    }
}
