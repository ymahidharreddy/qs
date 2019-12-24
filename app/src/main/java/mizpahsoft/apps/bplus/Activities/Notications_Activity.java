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

import mizpahsoft.apps.bplus.Adapters.NotificationsAdapter;
import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class Notications_Activity extends AppCompatActivity {
    ListView lv_notifications;
    ProgressDialog progressDialog;
    NotificationsAdapter notificationsAdapter;
    SharedPreferences sp;
    JSONArray jsonArray;
    JSONObject jsonObject, JO;
    TextView NoRecords;
    SharedPreferences.Editor edt;
    ArrayList<NotificationsModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notications_);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Notifications");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        lv_notifications = (ListView) findViewById(R.id.lv_notifications);
        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();

        notificationsAdapter = new NotificationsAdapter(Notications_Activity.this, arrayList);

        //Set the above adapter as the adapter of choice for our list
        lv_notifications.setAdapter(notificationsAdapter);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        lv_notifications.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    lv_notifications.addFooterView(footerView);
                    GetNotifications();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog = ProgressDialog.show(Notications_Activity.this, "", "please wait...");
        GetNotifications();


        lv_notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent i = new Intent(getApplicationContext(), IndividualBloodRequest_Activity.class);
                    i.putExtra("DATA", arrayList.get(position));
                    startActivityForResult(i, Global.ACTIVITY_FOR_RESULT);
                } catch (Exception e) {
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("lv_notifications", "item click", "get into some exception");
                }

            }
        });
    }

    private void GetNotifications() {

        Log.e("URL", Global.Notifications_URL + sp.getString("user_id", "") + "&start=" + start);
        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.Notifications_URL + sp.getString("user_id", "") + "&start=" + start, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    lv_notifications.removeFooterView(footerView);
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
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        notificationsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("GetNotifications", "status=1", "got all notifications");
                    } else {
                        NO_POST_DATA = true;
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("GetNotifications", "status=0", "no records");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("GetNotifications", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                progressDialog.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("GetNotifications", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("NotificationsActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Global.ACTIVITY_FOR_RESULT) {
            start = 0;
            // progressDialog = ProgressDialog.show(Notications_Activity.this, "", "Please wait..");
            // GetNotifications();
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("NOTIFICATION")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
