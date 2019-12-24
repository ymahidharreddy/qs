package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class Individual_PendingEvent_Activity extends AppCompatActivity {
    ImageView iv_image;

    EditText et_eventadd_name, et_eventadd_date, et_eventadd_contactnos,
            et_eventadd_condby, et_eventadd_orgname, et_eventadd_description;
    ProgressDialog progressDialog1;
    SharedPreferences sp;
    String address = "", userid = "", type = "", Mesg = "";
    Button Approve, Reject;
    TextView tv_event_name,tv_eventname,tv_event_date,tv_eventdate,tv_contact_number,tv_contactnos,tv_address,ac_address,
            tv_eventaddress,tv_event_head,tv_eventhead,tv_organisation,tv_eventorgname,tv_description,tv_eventdesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual__pending_event);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("EvntId");


        sp = getSharedPreferences("loginprefs", 0);
        userid = sp.getString("user_id", "N/A");

        iv_image = (ImageView) findViewById(R.id.iv_image);

        tv_event_name= (TextView) findViewById(R.id.tv_event_name);
        tv_event_name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_eventname= (TextView) findViewById(R.id.tv_eventname);
        tv_eventname.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_event_date= (TextView) findViewById(R.id.tv_event_date);
        tv_event_date.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_eventdate= (TextView) findViewById(R.id.tv_eventdate);
        tv_eventdate.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_contact_number= (TextView) findViewById(R.id.tv_contact_number);
        tv_contact_number.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_contactnos= (TextView) findViewById(R.id.tv_contactnos);
        tv_contactnos.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_address= (TextView) findViewById(R.id.tv_address);
        tv_address.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_eventaddress= (TextView) findViewById(R.id.tv_eventaddress);
        tv_eventaddress.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_event_head= (TextView) findViewById(R.id.tv_event_head);
        tv_event_head.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_eventhead= (TextView) findViewById(R.id.tv_eventhead);
        tv_eventhead.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_organisation= (TextView) findViewById(R.id.tv_organisation);
        tv_organisation.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_eventorgname= (TextView) findViewById(R.id.tv_eventorgname);
        tv_eventorgname.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_description= (TextView) findViewById(R.id.tv_description);
        tv_description.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_eventdesc= (TextView) findViewById(R.id.tv_eventdesc);
        tv_eventdesc.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_name = (EditText) findViewById(R.id.et_eventadd_name);
        et_eventadd_name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_date = (EditText) findViewById(R.id.et_eventadd_date);
        et_eventadd_date.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_contactnos = (EditText) findViewById(R.id.et_eventadd_contactnos);
        et_eventadd_contactnos.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_condby = (EditText) findViewById(R.id.et_eventadd_condby);
        et_eventadd_condby.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_orgname = (EditText) findViewById(R.id.et_eventadd_orgname);
        et_eventadd_orgname.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_description = (EditText) findViewById(R.id.et_eventadd_description);
        et_eventadd_description.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        ac_address = (TextView) findViewById(R.id.ac_address);
        ac_address.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Approve = (Button) findViewById(R.id.btn_Approve);
        Approve.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Reject = (Button) findViewById(R.id.btn_Reject);
        Reject.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_eventadd_name.setEnabled(false);
        et_eventadd_date.setEnabled(false);
        et_eventadd_contactnos.setEnabled(false);
        et_eventadd_condby.setEnabled(false);
        et_eventadd_orgname.setEnabled(false);
        et_eventadd_description.setEnabled(false);

        GetEvents1(id);

        Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Approve";
                Mesg = "Event approved successfully";
                SendEventId(id, type);
                MyApplication.getInstance().trackEvent("Approve","onClick()","specified action done");
            }
        });
        Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Reject";
                Mesg = "Event rejected successfully";
                SendEventId(id, type);
                MyApplication.getInstance().trackEvent("Reject","onClick()","specified action done");
            }
        });
    }

    private void GetEvents1(String id) {
        progressDialog1 = ProgressDialog.show(Individual_PendingEvent_Activity.this, "", "please wait..");
        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.Events2_URl + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");

                    if (status == 1) {
                        try {
                            JSONArray res = jsonObject.getJSONArray("result");
                            JSONObject obj_res = res.getJSONObject(0);
                            if (obj_res.getString("event").equals("") || obj_res.getString("event").equals("null") || obj_res.getString("event").equals(null)) {

                            } else {
                                et_eventadd_name.setText(obj_res.getString("event"));
                            }
                            if (obj_res.getString("eventDate").equals("") || obj_res.getString("eventDate").equals("null") || obj_res.getString("eventDate").equals(null)) {

                            } else {
                                et_eventadd_date.setText(obj_res.getString("eventDate"));
                            }
                            if (obj_res.getString("phoneNumber").equals("") || obj_res.getString("phoneNumber").equals("null") || obj_res.getString("phoneNumber").equals(null)) {

                            } else {
                                et_eventadd_contactnos.setText(obj_res.getString("phoneNumber"));
                            }
                            if (obj_res.getString("eventAddress").equals("") || obj_res.getString("eventAddress").equals("null") || obj_res.getString("eventAddress").equals(null)) {

                            } else {
                                ac_address.setText(obj_res.getString("eventAddress"));
                            }
                            if (obj_res.getString("eventConductedBy").equals("") || obj_res.getString("eventConductedBy").equals("null") || obj_res.getString("eventConductedBy").equals(null)) {

                            } else {
                                et_eventadd_condby.setText(obj_res.getString("eventConductedBy"));

                            }
                            if (obj_res.getString("organizationName").equals("") || obj_res.getString("organizationName").equals("null") || obj_res.getString("organizationName").equals(null)) {

                            } else {
                                et_eventadd_orgname.setText(obj_res.getString("organizationName"));

                            }
                            if (obj_res.getString("description").equals("") || obj_res.getString("description").equals("null") || obj_res.getString("description").equals(null)) {

                            } else {
                                et_eventadd_description.setText(obj_res.getString("description"));
                            }
                            String img = obj_res.getString("eventImage");
                            if (!(img.equals("")) && !(img.equals(null))) {
                                String url = Global.Events_image + img;
                                Picasso.with(Individual_PendingEvent_Activity.this).load(url).error(R.mipmap.eventimg).into(iv_image);
                            }
                            progressDialog1.dismiss();
                            MyApplication.getInstance().trackEvent("getEvents1","status=1","all events retrieved successfully");

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("getEvents1","status=1","get into some exception");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        progressDialog1.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents1","status=0",jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getEvents1","mainjsonparsingexception","get into some exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getEvents1","volley error listener","get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }


    private void SendEventId(final String Eventid, final String Type) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Admin_events_aprooved,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int status = jsonObject.getInt("status");
                            if (status == 1) {
                                Toast.makeText(Individual_PendingEvent_Activity.this, Mesg, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Individual_PendingEvent_Activity.this, AdminPage.class));
                                MyApplication.getInstance().trackEvent("SendEventId","status=1","present to admin page navigation");
                            } else {
                                Toast.makeText(Individual_PendingEvent_Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("SendEventId","status=0",jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SendEventId","mainjsonparsingexception","get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("SendEventId","volley error listener","get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventId", Eventid);
                params.put("type", Type);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Individual_PendingEvent_Activity.this);
        requestQueue.add(stringRequest);


    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("IndividualPendingEventActivity");
    }
}
