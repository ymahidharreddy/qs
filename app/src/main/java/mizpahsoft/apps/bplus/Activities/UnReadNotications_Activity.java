package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.Adapters.RecentBloodReqst_Adapter;
import mizpahsoft.apps.bplus.Adapters.UnreadNotificationsArrayAdapter;
import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class UnReadNotications_Activity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor edt;
    private ProgressDialog pd;
    UnreadNotificationsArrayAdapter UnreadNotificationsArrayAdapter;
    ListView lv_unread;
    String noti_id;
    String userId;
    TextView NoRecords;
    ArrayList<NotificationsModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_read_notications);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("UnRead Notifications");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();
        userId = sp.getString("user_id", "");
        lv_unread = (ListView) findViewById(R.id.lv_unread);

        lv_unread.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    noti_id = arrayList.get(i).getNotiId();
                    SendNotificationStatus(noti_id, i);
                    Intent i1 = new Intent(getApplicationContext(), IndividualBloodRequest_Activity.class);
                    i1.putExtra("DATA", arrayList.get(i));
                    startActivity(i1);
                } catch (Exception e) {
                    Toast.makeText(UnReadNotications_Activity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {

            arrayList = getIntent().getParcelableArrayListExtra("DATA");
            start = getIntent().getIntExtra("start", 10);

            if (arrayList.size() == 0) {
                NoRecords = (TextView) findViewById(R.id.tv_norecords);
                lv_unread.setVisibility(View.GONE);
                NoRecords.setVisibility(View.VISIBLE);
                //TastyToast.makeText(UnReadNotications_Activity.this, "No unread notifications", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            } else {
                UnreadNotificationsArrayAdapter = new UnreadNotificationsArrayAdapter(UnReadNotications_Activity.this, arrayList);//jArray is your json array
                //Set the above adapter as the adapter of choice for our list
                lv_unread.setAdapter(UnreadNotificationsArrayAdapter);
            }

        } catch (Exception e) {
            TastyToast.makeText(UnReadNotications_Activity.this, "" + e, TastyToast.LENGTH_LONG, TastyToast.WARNING);
        }

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        lv_unread.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    lv_unread.addFooterView(footerView);
                    GetNotificationsCount();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

    }

    public void dialogshow() {
        pd = ProgressDialog.show(UnReadNotications_Activity.this, "", "Please wait...", true);

        //pd.show(AddFriends.this, "Alert", "Please wait...", true);

    }

    public void dialogcancel() {
        pd.dismiss();

    }

    private void SendNotificationStatus(final String noti_id, final int pos) {

        StringRequest stringrequest = new StringRequest(Request.Method.POST, Global.NotificationsCount_Read_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        arrayList.remove(pos);
                        UnreadNotificationsArrayAdapter.notifyDataSetChanged();
                        try {
                            MyApplication.getInstance().trackEvent("SendNotificationStatus", "status=1", "Notification sent");
                        } catch (Exception e) {
                            Toast.makeText(UnReadNotications_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SendNotificationStatus", "status=1", "get into some exception");
                        }
                    } else {
                        Toast.makeText(UnReadNotications_Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        MyApplication.getInstance().trackEvent("SendNotificationStatus", "status=0", jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("SendNotificationStatus", "mainjsonparsingexception", "get into some exception");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UnReadNotications_Activity.this, "" + error, Toast.LENGTH_SHORT).show();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("SendNotificationStatus", "volley error listener", "get into some exception");
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userId);
                params.put("notiId", noti_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    // to get notifications count
    private void GetNotificationsCount() {

        edt.putString("UnreadNotifications", "NoData").commit();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.NotificationsCount_URL + "?userId=" + sp.getString("user_id", "") + "&start=" + start,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (footerView != null)
                            lv_unread.removeFooterView(footerView);

                        try {
                            JSONArray BannerOJ = new JSONArray(response);
                            JSONObject jb = BannerOJ.getJSONObject(0);
                            JSONObject jb12 = jb.getJSONObject("userDetails");
                            String roleid = jb12.getString("roleId");
                            edt.putString("roleid", roleid).commit();
                            JSONObject jb1 = jb.getJSONObject("userDetails");
                            edt.putString("is_phoneNumber_visible", jb1.getString("is_phoneNumber_visible")).commit();
                            edt.putString("noti_enable", jb1.getString("noti_enable")).commit();
                            if (jb.getInt("status") == 1) {
                                JSONArray UnreadNotifications = jb.getJSONArray("result");
                                edt.putString("UnreadNotifications", UnreadNotifications.toString()).commit();
                                JSONArray array = jb.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    NotificationsModel nm = new NotificationsModel();
                                    nm.setB_req_id(object.getString("b_req_id"));
                                    nm.setNotiId(object.getString("notiId"));
                                    nm.setNotification(object.getString("notification"));
                                    nm.setName(object.getString("name"));
                                    nm.setAddress(object.getString("address"));
                                    nm.setAge(object.getString("age"));
                                    nm.setProfilePicture(object.getString("profilePicture"));
                                    nm.setNotiCreatedTime(object.getString("notiCreatedTime"));
                                    nm.setBloodGroup(object.getString("bloodGroup"));
                                    nm.setReq_status_id(object.getString("req_status_id"));
                                    nm.setDonor(object.getString("donor"));
                                    nm.setMessage(object.getString("message"));
                                    nm.setGender(object.getString("gender"));
                                    nm.setPhoneNumber(object.getString("phoneNumber"));
                                    nm.setAcceptedUsers("" + object.getJSONArray("acceptedUsers").length());
                                    arrayList.add(nm);
                                }
                                start = start + Integer.parseInt(jb.getString("offset"));
                                UnreadNotificationsArrayAdapter.notifyDataSetChanged();
                            } else {
                                NO_POST_DATA = true;
                            }
                            MyApplication.getInstance().trackEvent("GetNotificationsCount", "status=1", "got all new notifications");
                        } catch (Exception e) {
                            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("GetNotificationsCount", "mainjsonparsingexception", "get into some exception");
                        }
                        IS_API_CALLED = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (footerView != null)
                            lv_unread.removeFooterView(footerView);

                        IS_API_CALLED = false;
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("GetNotificationsCount", "volley error listener", "get into some exception");
                        //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("UnreadNotificationsActivity");
    }
}
