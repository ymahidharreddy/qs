package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import mizpahsoft.apps.bplus.Adapters.Admin_to_Aproove_events_Adapter;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class AdminPage extends AppCompatActivity {

    ProgressDialog progressDialog;
    JSONArray eventslist;
    Admin_to_Aproove_events_Adapter eventAdapter;
    ListView events_list;
    LinearLayout noevents;
    int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        events_list = (ListView) findViewById(R.id.ll_events);
        noevents = (LinearLayout) findViewById(R.id.noevents);
        GetEvents();
    }

    private void GetEvents() {
        progressDialog = ProgressDialog.show(AdminPage.this, "", "please wait..");

        String URL = "";
        if (Global.getSP(this, Global.USER_TYPE).equals("2"))
            URL = Global.Admin_events_to_aproove + "&start=" + start + "&userType=2";
        else
            URL = Global.Admin_events_to_aproove + "&start=" + start + "&userType=1";

        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.Admin_events_to_aproove, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");

                    if (status == 1) {
                        try {
                            eventslist = jsonObject.getJSONArray("result");
                            eventAdapter = new Admin_to_Aproove_events_Adapter(AdminPage.this, eventslist);

                            if (eventslist.length() != 0) {
                                events_list.setAdapter(eventAdapter);
                                events_list.setVisibility(View.VISIBLE);
                                noevents.setVisibility(View.GONE);
                            } else {
                                noevents.setVisibility(View.VISIBLE);
                                events_list.setVisibility(View.GONE);
                            }

                            progressDialog.dismiss();


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("geteventmethod", "status=1", "get into some exception");
                        }
                    } else {
                        noevents.setVisibility(View.VISIBLE);
                        events_list.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("geteventmethod", "status=0", "failed to get events");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("geteventmethod", "mainjsonparsingexception", "get into some exception");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("geteventmethod", "volleyerrorlistener", "get into some exception");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("AdminPage");
    }
}
