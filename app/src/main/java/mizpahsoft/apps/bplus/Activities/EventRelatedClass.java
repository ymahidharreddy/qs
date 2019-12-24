package mizpahsoft.apps.bplus.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.Adapters.AdapterIndividualEvent;
import mizpahsoft.apps.bplus.Adapters.EventsAdapter;
import mizpahsoft.apps.bplus.Model.EventsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

/*** Created by Mizpah-DEV on 22-Nov-16. */

public class EventRelatedClass extends Activity {
    private static final String TAG = "EVENTRELATEDCLASS";
    /*ImageView iv_back;
        TextView tv_tabname;*/
    LinearLayout scr_tab3;
    RelativeLayout scr_tab1, scr_tab2;
    LinearLayout iv_one, iv_two, iv_three;
    View view1, view2, view3;

    //declarations related to all events tab
    ListView events_list;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;
    JSONArray eventslist;
    EventsAdapter eventAdapter;
    //end of all events relatives

    //declarations related to individual events class
    ListView events_list1;
    ProgressDialog progressDialog1;
    JSONArray eventslist1;
    AdapterIndividualEvent eventAdapter1;
    String userid;
    SharedPreferences sp;
    //end of individual events relatives

    //declarations related to add event class
    LinearLayout ll_eventadd_tab1_continue, ll_eventadd_tab2_continue,
            ll_bt_iv_layout;
    EditText et_eventadd_name, et_eventadd_date, et_eventadd_contactnos,
            et_eventadd_condby, et_eventadd_orgname, et_eventadd_description;
    LinearLayout fr_tocompress;
    ImageView iv_ev_image;
    ScrollView ll_eventadd_tab1, ll_eventadd_tab2;
    Button bt_post, bt_iv_back;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD7GFvtyyLyDAPdAMyc0lf7axug6QZt6OQ";
    static String REQUEST_TYPE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_CAM = 2;
    int eventname = 0, eventdate = 0, contactnumbers = 0, eventaddress = 0, eventcond = 0, eventorg = 0, eventdesc = 0;
    String address = "", eventName = "", eventDate = "", phoneNumber = "",
            eventConductedBy = "", organizationName = "", eventImage = "", description = "";

    Display display;
    int width, height, req_height;
    int statusbarheight;
    DatePickerDialog mDatePicker;
    //end of add event relatives
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    ArrayList<EventsModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;
    ArrayList<EventsModel> arrayList1 = new ArrayList<>();
    int start1 = 0;
    boolean NO_POST_DATA1 = false;
    boolean IS_API_CALLED1 = false;
    TextView tv_events_title, tv_my_events_title, tv_add_events_title, tv_event_name, tv_event_date,
            tv_contact_number, tv_location, ac_address, tv_eventadd_tab1_continue, tv_conducting_by,
            tv_organising, tv_description, tv_back, tv_eventadd_tab2_continue, tv_no_events, tv_no_myevents;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_tabs);

        sp = getSharedPreferences("loginprefs", 0);
        userid = sp.getString("user_id", "N/A");

        tv_events_title = (TextView) findViewById(R.id.tv_events_title);
        tv_events_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_my_events_title = (TextView) findViewById(R.id.tv_my_events_title);
        tv_my_events_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_add_events_title = (TextView) findViewById(R.id.tv_add_events_title);
        tv_add_events_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_event_name = (TextView) findViewById(R.id.tv_event_name);
        tv_event_name.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_event_date = (TextView) findViewById(R.id.tv_event_date);
        tv_event_date.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_contact_number = (TextView) findViewById(R.id.tv_contact_number);
        tv_contact_number.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_location.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_conducting_by = (TextView) findViewById(R.id.tv_conducting_by);
        tv_conducting_by.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_organising = (TextView) findViewById(R.id.tv_organising);
        tv_organising.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_description.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        //iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_one = (LinearLayout) findViewById(R.id.iv_one);
        iv_two = (LinearLayout) findViewById(R.id.iv_two);
        iv_three = (LinearLayout) findViewById(R.id.iv_three);

        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);
        view3 = (View) findViewById(R.id.view3);

        //tv_tabname=(TextView)findViewById(R.id.tv_tabname);

        scr_tab1 = (RelativeLayout) findViewById(R.id.scr_tab1);
        scr_tab2 = (RelativeLayout) findViewById(R.id.scr_tab2);
        scr_tab3 = (LinearLayout) findViewById(R.id.scr_tab3);
        //the following related to all events
        events_list = (ListView) findViewById(R.id.events_list);

        eventAdapter = new EventsAdapter(EventRelatedClass.this, arrayList);//jArray is your json array
        //Set the above adapter as the adapter of choice for our list
        events_list.setAdapter(eventAdapter);
        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        events_list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    events_list.addFooterView(footerView);
                    GetEvents();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog = ProgressDialog.show(EventRelatedClass.this, "", "please wait...");
        GetEvents();

        events_list1 = (ListView) findViewById(R.id.events_list1);

        eventAdapter1 = new AdapterIndividualEvent(EventRelatedClass.this, arrayList1, userid);//jArray is your json array

        //Set the above adapter as the adapter of choice for our list
        events_list1.setAdapter(eventAdapter1);
        events_list1.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA1 && !IS_API_CALLED1) {
                    IS_API_CALLED1 = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList1.size());
                    events_list1.addFooterView(footerView);
                    GetEvents1();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog1 = ProgressDialog.show(EventRelatedClass.this, "", "please wait...");
        GetEvents1();

        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        statusbarheight = getStatusBarHeight();
        req_height = height - (statusbarheight + 150);
        ll_eventadd_tab1 = (ScrollView) findViewById(R.id.ll_eventadd_tab1);
        ll_eventadd_tab1.setMinimumHeight(req_height);
        ll_eventadd_tab1_continue = (LinearLayout) findViewById(R.id.ll_eventadd_tab1_continue);
        ll_bt_iv_layout = (LinearLayout) findViewById(R.id.ll_bt_iv_layout);

        ll_eventadd_tab2 = (ScrollView) findViewById(R.id.ll_eventadd_tab2);
        ll_eventadd_tab2.setMinimumHeight(req_height);
        ll_eventadd_tab2_continue = (LinearLayout) findViewById(R.id.ll_eventadd_tab2_continue);

        fr_tocompress = (LinearLayout) findViewById(R.id.fr_tocompress);
        fr_tocompress.setMinimumHeight(req_height - 300);
        bt_post = (Button) findViewById(R.id.bt_post);
        bt_post.setTypeface(Global.setFont(this, Global.REGULARFONT));
        bt_iv_back = (Button) findViewById(R.id.bt_iv_back);
        bt_iv_back.setTypeface(Global.setFont(this, Global.REGULARFONT));

        iv_ev_image = (ImageView) findViewById(R.id.iv_ev_image);
        // iv_after_change=(ImageView)findViewById(R.id.iv_after_change);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_eventadd_tab1_continue = (TextView) findViewById(R.id.tv_eventadd_tab1_continue);
        tv_eventadd_tab1_continue.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_eventadd_tab2_continue = (TextView) findViewById(R.id.tv_eventadd_tab2_continue);
        tv_eventadd_tab2_continue.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_no_events = (TextView) findViewById(R.id.tv_no_events);
        tv_no_events.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_no_myevents = (TextView) findViewById(R.id.tv_no_myevents);
        tv_no_myevents.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_eventadd_name = (EditText) findViewById(R.id.et_eventadd_name);
        et_eventadd_name.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        // et_eventadd_name.addTextChangedListener(name);
        et_eventadd_date = (EditText) findViewById(R.id.et_eventadd_date);
        et_eventadd_date.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        // et_eventadd_date.addTextChangedListener(date);
        et_eventadd_contactnos = (EditText) findViewById(R.id.et_eventadd_contactnos);
        et_eventadd_contactnos.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        // et_eventadd_contactnos.addTextChangedListener(contactnos);
        et_eventadd_condby = (EditText) findViewById(R.id.et_eventadd_condby);
        et_eventadd_condby.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        // et_eventadd_condby.addTextChangedListener(conduct);
        et_eventadd_orgname = (EditText) findViewById(R.id.et_eventadd_orgname);
        et_eventadd_orgname.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        // et_eventadd_orgname.addTextChangedListener(orga);
        et_eventadd_description = (EditText) findViewById(R.id.et_eventadd_description);
        et_eventadd_description.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        // et_eventadd_description.addTextChangedListener(desc);

        ac_address = (TextView) findViewById(R.id.ac_address);
        ac_address.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        ac_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog2 = ProgressDialog.show(EventRelatedClass.this, "", "Please wait..");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(EventRelatedClass.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent called");
                } catch (GooglePlayServicesRepairableException e) {
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent failed exception");
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent failed exception");
                    // TODO: Handle the error.
                }
            }
        });
        ll_eventadd_tab1_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventName = et_eventadd_name.getText().toString().trim();
                phoneNumber = et_eventadd_contactnos.getText().toString().trim();
                if (eventName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter Event Name", Toast.LENGTH_SHORT).show();
                } else if (eventName.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Please enter Event Name with at least 3 characters.", Toast.LENGTH_SHORT).show();
                } else if (eventDate.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select Event Date", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please specify at least one contact number", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Please specify valid contact number", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.matches("[0123456]+")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else if (address.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select address from drop down", Toast.LENGTH_SHORT).show();
                } else {
                    ll_eventadd_tab1.setVisibility(View.GONE);
                    ll_eventadd_tab2.setVisibility(View.VISIBLE);
                    MyApplication.getInstance().trackEvent("ll_eventadd_tab1_continue", "onClick", "clicked. So, specified action taken");
                }

            }
        });
        ll_eventadd_tab2_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = et_eventadd_description.getText().toString().trim();
                eventConductedBy = et_eventadd_condby.getText().toString().trim();
                organizationName = et_eventadd_orgname.getText().toString().trim();
                if (eventConductedBy.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please specify Event Conducted By", Toast.LENGTH_SHORT).show();
                } else if (description.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please describe your event in few words", Toast.LENGTH_SHORT).show();
                } else {
                    ll_eventadd_tab2.setVisibility(View.GONE);
                    fr_tocompress.setVisibility(View.VISIBLE);
                }
                hideKeyboard();
                MyApplication.getInstance().trackEvent("ll_eventadd_tab2_continue", "onClick", "clicked and specified action done");
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_eventadd_tab2.setVisibility(View.GONE);
                fr_tocompress.setVisibility(View.GONE);
                ll_eventadd_tab1.setVisibility(View.VISIBLE);
                MyApplication.getInstance().trackEvent("tv_back", "onClick", "clicked and specified action done");
            }
        });
        iv_ev_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
                MyApplication.getInstance().trackEvent("fr_tocompress", "onClick", "clicked and specified action done");
            }
        });

        et_eventadd_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                final Calendar mcurrentDate = Calendar.getInstance();

                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                final Calendar mcurrentDate2 = Calendar.getInstance();

                int mYear1 = mcurrentDate.get(Calendar.YEAR) + 1;
                int mMonth1 = mcurrentDate.get(Calendar.MONTH);
                int mDay1 = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mcurrentDate2.set(mYear1, mMonth1, mDay1);

                mDatePicker = new DatePickerDialog(EventRelatedClass.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        eventDate = selectedyear + "-" + (selectedmonth + 1) + "-" + selectedday;
                        et_eventadd_date.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);
                        eventdate = 1;
                        MyApplication.getInstance().trackEvent("et_eventadd_date", "onClick", "date selected manually by user");
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
                mDatePicker.getDatePicker().setMaxDate(mcurrentDate2.getTimeInMillis());
                mDatePicker.show();
            }
        });
        bt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForTolerance();
                MyApplication.getInstance().trackEvent("bt_post", "onClick", "clicked and specified action done");
            }
        });
        bt_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fr_tocompress.setVisibility(View.GONE);
                ll_eventadd_tab2.setVisibility(View.VISIBLE);
                MyApplication.getInstance().trackEvent("bt_iv_back", "onClick", "clicked and specified action done");
            }
        });
        //end of add event relatives
        iv_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.setBackgroundColor(getResources().getColor(R.color.white));
                view2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                view3.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                scr_tab1.setVisibility(View.VISIBLE);
                scr_tab2.setVisibility(View.GONE);
                scr_tab3.setVisibility(View.GONE);
                MyApplication.getInstance().trackEvent("iv_one", "onClick", "clicked and specified action done");
                // tv_tabname.setText("Universal Events");
            }
        });
        iv_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view2.setBackgroundColor(getResources().getColor(R.color.white));
                view1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                view3.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                scr_tab1.setVisibility(View.GONE);
                scr_tab2.setVisibility(View.VISIBLE);
                scr_tab3.setVisibility(View.GONE);
                MyApplication.getInstance().trackEvent("iv_two", "onClick", "clicked and specified action done");
                //tv_tabname.setText("Individual Event(s)");
            }
        });
        iv_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view3.setBackgroundColor(getResources().getColor(R.color.white));
                view2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                view1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                scr_tab1.setVisibility(View.GONE);
                scr_tab2.setVisibility(View.GONE);
                scr_tab3.setVisibility(View.VISIBLE);
                MyApplication.getInstance().trackEvent("iv_three", "onClick", "clicked and specified action done");
                //  tv_tabname.setText("Add an Event");
            }
        });
    }


    //code to hide keyboard
    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method belongs to all events class
    private void GetEvents() {
        String URL = "";
        if (Global.getSP(this, Global.USER_TYPE).equals("2"))
            URL = Global.Events_URl + start + "&userType=2";
        else
            URL = Global.Events_URl + start + "&userType=1";
        StringRequest stringrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    events_list.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            EventsModel em = new EventsModel();
                            em.setEventId(object.getString("eventId"));
                            em.setEvent(object.getString("event"));
                            em.setEventDate(object.getString("eventDate"));
                            em.setEventAddress(object.getString("eventAddress"));
                            em.setEventImage(object.getString("eventImage"));
                            arrayList.add(em);
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        eventAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents", "status=1", "got all events");
                    } else {
                        NO_POST_DATA = true;
                        if (arrayList.isEmpty()) {
                            tv_no_events.setVisibility(View.VISIBLE);
                            events_list.setVisibility(View.GONE);
                        }
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents", "status=0", "get into some exception");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getEvents", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                progressDialog.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getEvents", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    //end of all event relatives
    //method belongs to individual events class
    private void GetEvents1() {
        String URL = "";
        if (Global.getSP(this, Global.USER_TYPE).equals("2"))
            URL = Global.Events1_URl + userid + "&start=" + start1 + "&userType=2";
        else
            URL = Global.Events1_URl + userid + "&start=" + start1 + "&userType=1";

        StringRequest stringrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    events_list1.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {

                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            EventsModel em = new EventsModel();
                            em.setEventId(object.getString("eventId"));
                            em.setEvent(object.getString("event"));
                            em.setEventDate(object.getString("eventDate"));
                            em.setEventAddress(object.getString("eventAddress"));
                            em.setEventImage(object.getString("eventImage"));
                            em.setIsActive(object.getString("isActive"));
                            arrayList1.add(em);
                        }
                        start1 = start1 + Integer.parseInt(jsonObject.getString("offset"));
                        eventAdapter1.notifyDataSetChanged();
                        progressDialog1.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents1", "status=1", "got all events");
                        tv_no_myevents.setVisibility(View.GONE);
                        events_list1.setVisibility(View.VISIBLE);
                    } else {
                        NO_POST_DATA1 = true;
                        if (arrayList1.isEmpty()) {
                            tv_no_myevents.setVisibility(View.VISIBLE);
                            events_list1.setVisibility(View.GONE);
                        }
                        progressDialog1.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents1", "status=0", "get into some exception");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog1.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getEvents1", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED1 = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED1 = false;
                progressDialog1.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getEvents1", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }
    //end of individual event relatives

    //fun belongs to add event funda
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void checkForTolerance() {
        postIt();
        bt_post.setEnabled(false);
        bt_post.setText("Posting...");

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete1(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public static ArrayList autocomplete1(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
           /* Log.e(LOG_TAG, "Error processing Places API URL", e);*/
            Log.i("", "@@@@@@@@ " + e);
            return resultList;
        } catch (IOException e) {
            Log.i("", "@@@@@@@@111111111 " + e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
               /* System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");*/
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
               /* resultList.add(predsJsonArray.getJSONObject(i).getString("poastal_code"));*/
            }
        } catch (JSONException e) {

            Log.i("", "@@@@@@@@222222222222 " + e);
        }

        return resultList;
    }

    private final TextWatcher name = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                eventname = 0;
            } else {
                eventname = 1;
            }
            callForCheck();
        }
    };
    private final TextWatcher date = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                eventdate = 0;
            } else {
                eventdate = 1;
            }
            callForCheck();
        }
    };
    private final TextWatcher contactnos = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                contactnumbers = 0;
            } else {
                contactnumbers = 1;
            }
            callForCheck();
        }
    };
    private final TextWatcher conduct = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                eventcond = 0;
            } else {
                eventcond = 1;
            }
            callForCheck();
        }
    };
    private final TextWatcher orga = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                eventorg = 0;
            } else {
                eventorg = 1;
            }
            callForCheck();
        }
    };
    private final TextWatcher desc = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                eventdesc = 0;
            } else {
                eventdesc = 1;
            }
            callForCheck();
        }
    };
    private final TextWatcher addr = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                eventaddress = 0;
            } else {
                eventaddress = 1;
            }
            callForCheck();
        }
    };

    public void callForCheck() {
        if (eventname == 1 && eventdate == 1 && eventaddress == 1 && contactnumbers == 1 && eventcond == 0 && eventdesc == 0) {
            ll_eventadd_tab1_continue.setClickable(true);
            tv_eventadd_tab1_continue.setTextColor(Color.parseColor("#ecd4af37"));
        } else if (eventname == 1 && eventdate == 1 && eventaddress == 1 && contactnumbers == 1 && eventcond == 1 && eventdesc == 1) {
            ll_eventadd_tab1_continue.setClickable(true);
            tv_eventadd_tab1_continue.setTextColor(Color.parseColor("#ecd4af37"));
            ll_eventadd_tab2_continue.setClickable(true);
            tv_eventadd_tab2_continue.setTextColor(Color.parseColor("#ecd4af37"));
        } else if (eventcond == 1 && eventdesc == 1) {
            ll_eventadd_tab2_continue.setClickable(true);
            tv_eventadd_tab2_continue.setTextColor(Color.parseColor("#ecd4af37"));
        } else if (eventname == 0 || eventdate == 0 || eventaddress == 0 || contactnumbers == 0 || eventcond == 0 || eventorg == 0) {
            ll_eventadd_tab1_continue.setClickable(false);
            tv_eventadd_tab1_continue.setTextColor(Color.parseColor("#acacac"));
            ll_eventadd_tab2_continue.setClickable(false);
            tv_eventadd_tab2_continue.setTextColor(Color.parseColor("#acacac"));
        } else if (eventname == 1 && eventdate == 1 && eventaddress == 1 && contactnumbers == 1 && eventcond == 1 && eventdesc == 1) {
            fr_tocompress.setVisibility(View.VISIBLE);
        }
    }

    private void showFileChooser() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EventRelatedClass.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    REQUEST_TYPE = "CAMERA";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Do something for lollipop and above versions
                        if (checkPermission()) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, 1);
                            }
                        } else {
                            requestPermission();
                        }
                    } else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                } else if (items[item].equals("Choose from Gallery")) {

                    REQUEST_TYPE = "GALLERY";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Do something for lollipop and above versions
                        if (checkPermission()) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        } else {
                            requestPermission();
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }


                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

       /* Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (REQUEST_TYPE.equals("CAMERA")) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                            MyApplication.getInstance().trackEvent("onReqPermission", "camera permission", "accepted & called respective intent");
                        }
                    }
                } else {
                    TastyToast.makeText(getApplicationContext(), "you denied permission? you can't add your profile pic.", TastyToast.LENGTH_LONG,
                            TastyToast.ERROR);
                    MyApplication.getInstance().trackEvent("onReqPermission", "camera/gallery permission", "denied by user");
                }
                break;
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (REQUEST_TYPE.equals("GALLERY")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                        MyApplication.getInstance().trackEvent("onReqPermission", "gallery permission", "accepted & called respective intent");
                    }
                } else {
                    TastyToast.makeText(getApplicationContext(), "you denied permission? you can't add your profile pic.", TastyToast.LENGTH_LONG,
                            TastyToast.ERROR);
                    MyApplication.getInstance().trackEvent("onReqPermission", "camera/gallery permission", "denied by user");
                }
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestPermission() {
        if (REQUEST_TYPE.equals("GALLERY")) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Toast.makeText(this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        EventRelatedClass.this);
                // set title
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setCancelable(true);
                // set dialog message
                alertDialogBuilder
                        .setMessage("READ_EXTERNAL_STORAGE permission allows us to access location data. Please allow in App Settings for additional functionality.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.cancel();
                                    MyApplication.getInstance().trackEvent("requestPermission()", "accepted by user", "gallery permission given");
                                    //so some work
                                } catch (Exception e) {
                                    //Exception
                                }
                            }
                        });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            } else {
                MyApplication.getInstance().trackEvent("requestPermission()", "denied by user(gallery)", "requesting again(We can't assure that this action works)");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Toast.makeText(this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        EventRelatedClass.this);
                // set title
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setCancelable(true);
                // set dialog message
                alertDialogBuilder
                        .setMessage("CAMERA permission allows us to take picture. Please allow in App Settings for additional functionality.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.cancel();
                                    MyApplication.getInstance().trackEvent("requestPermission()", "accepted by user", "camera permission given");
                                    //so some work
                                } catch (Exception e) {
                                    //Exception
                                }
                            }
                        });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            } else {
                MyApplication.getInstance().trackEvent("requestPermission()", "denied by user(camera)", "requesting again(We can't assure that this action works)");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAM);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int THUMBNAIL_HEIGHT = 400;
       /* if(resultCode != RESULT_CANCELED){
            if (requestCode == 2&& data!=null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                iv_propic.setImageBitmap(photo);
            }
        }*/
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                address = "" + place.getAddress();
                ac_address.setText(address);
                progressDialog2.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "got address");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
                address = "";
                progressDialog2.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "error in getting address");

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                address = "";
                progressDialog2.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "cancelled(getting address)");
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Bundle extras = data.getExtras();
                Bitmap bitmap1 = (Bitmap) extras.get("data");
                eventImage = encodeTobase64(bitmap1);
                iv_ev_image.setScaleType(ImageView.ScaleType.FIT_XY);
                iv_ev_image.setImageBitmap(bitmap1);
                ll_bt_iv_layout.setVisibility(View.VISIBLE);
                //iv_after_change.setVisibility(View.VISIBLE);
                bt_post.setVisibility(View.VISIBLE);
                MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=1", "got bitmap");
            } else if (requestCode == 2) {
                try {
                    Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    Matrix matrix = new Matrix();
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    if (manufacturer.equalsIgnoreCase("Samsung") && model.equalsIgnoreCase("SM-J200G")) {
                        matrix.postRotate(-90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap2, 220, 220, true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        iv_ev_image.setScaleType(ImageView.ScaleType.FIT_XY);
                        iv_ev_image.setImageBitmap(rotatedBitmap);
                        eventImage = encodeTobase64(rotatedBitmap);
                        ll_bt_iv_layout.setVisibility(View.VISIBLE);
                        // iv_after_change.setVisibility(View.VISIBLE);
                        bt_post.setVisibility(View.VISIBLE);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got resized bitmap for samsung");
                    } else {
                        iv_ev_image.setScaleType(ImageView.ScaleType.FIT_XY);
                        iv_ev_image.setImageBitmap(bitmap2);
                        eventImage = encodeTobase64(bitmap2);
                        ll_bt_iv_layout.setVisibility(View.VISIBLE);
                        // iv_after_change.setVisibility(View.VISIBLE);
                        bt_post.setVisibility(View.VISIBLE);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got bitmap");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "get into some exception");
                }
            }
        }
        if (requestCode == Global.ACTIVITY_FOR_RESULT) {
            start1 = 0;
            arrayList1.clear();
            GetEvents1();
        }
    }

   /* public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }*/

    public boolean checkPermission() {
        if (REQUEST_TYPE.equals("GALLERY")) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                MyApplication.getInstance().trackEvent("checkPermission", "for gallery", "yes");
                return true;

            } else {
                MyApplication.getInstance().trackEvent("checkPermission", "for gallery", "no");
                return false;

            }
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (result == PackageManager.PERMISSION_GRANTED) {
                MyApplication.getInstance().trackEvent("checkPermission", "for camera", "yes");
                return true;

            } else {
                MyApplication.getInstance().trackEvent("checkPermission", "for camera", "no");
                return false;

            }

        }
    }

    public void postIt() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.addevent_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                // iv_after_change.setVisibility(View.GONE);
                                bt_post.setEnabled(false);
                                bt_post.setTextColor(Color.parseColor("#ecd4af37"));
                                bt_post.setText("Waiting for Approval");
                                // startActivity(getIntent());
                                Toast.makeText(EventRelatedClass.this, "Posted successfully, Waiting for Admin Approval", Toast.LENGTH_LONG).show();
                                //finish();
                                view2.setBackgroundColor(getResources().getColor(R.color.white));
                                view1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                view3.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                scr_tab1.setVisibility(View.GONE);
                                scr_tab2.setVisibility(View.VISIBLE);
                                scr_tab3.setVisibility(View.GONE);
                                ll_eventadd_tab1.setVisibility(View.VISIBLE);
                                ll_eventadd_tab2.setVisibility(View.GONE);
                                fr_tocompress.setVisibility(View.GONE);
                                clearDataForTabThree();
                                start1 = 0;
                                arrayList1.clear();
                                progressDialog1.show();
                                GetEvents1();
                                MyApplication.getInstance().trackEvent("postIt", "status=1", "posted successfully");
                            } else {
                                bt_post.setEnabled(true);
                                bt_post.setTextColor(getResources().getColor(R.color.colorAccent));
                                bt_post.setText("Sorry! Posting failed");
                                String message = object.getString("message");
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("postIt", "status=0", "get into some exception");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            bt_post.setEnabled(true);
                            bt_post.setTextColor(getResources().getColor(R.color.colorAccent));
                            bt_post.setText("Sorry! Posting failed");
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("postIt", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        bt_post.setEnabled(true);
                        bt_post.setTextColor(getResources().getColor(R.color.colorAccent));
                        bt_post.setText("Sorry! Posting failed");
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("postIt", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", "N/A"));
                params.put("eventName", eventName);
                params.put("eventDate", eventDate);
                params.put("eventConductedBy", eventConductedBy);
                params.put("organizationName", organizationName);
                params.put("address", address);
                params.put("eventImage", eventImage);
                params.put("phoneNumber", phoneNumber);
                params.put("description", description);
                if (Global.getSP(getApplicationContext(), Global.USER_TYPE).equals("2"))
                    params.put("userType", "2");
                else
                    params.put("userType", "1");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    //end of add event relatives fun.
    //onBackPressed

    @Override
    public void onBackPressed() {
        if (scr_tab1.isShown()) {
            finish();
            MyApplication.getInstance().trackEvent("onBackPressed()", "override", "from tab1,so finished activity.");
            super.onBackPressed();
        } else if (scr_tab2.isShown()) {
            iv_one.performClick();
            MyApplication.getInstance().trackEvent("onBackPressed()", "override", "from tab2");
        } else if (scr_tab3.isShown()) {
            iv_two.performClick();
            MyApplication.getInstance().trackEvent("onBackPressed()", "override", "from tab3");
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tempbyte = getBytesFromBitmap(resize(image, 500, 500));
        // immagex.compress(Bitmap.CompressFormat.PNG, 70, baos);
        // byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(tempbyte, Base64.DEFAULT);


        return imageEncoded;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            progressDialog2.dismiss();
        } catch (Exception e) {

        }

    }

    public void clearDataForTabThree() {
        et_eventadd_name.setText("");
        et_eventadd_date.setText("");
        et_eventadd_contactnos.setText("");
        ac_address.setText("");
        et_eventadd_condby.setText("");
        et_eventadd_orgname.setText("");
        et_eventadd_description.setText("");
        bt_post.setText("Post");
        iv_ev_image.setScaleType(ImageView.ScaleType.CENTER);
        iv_ev_image.setImageResource(R.mipmap.eventimg);
        bt_post.setTextColor(getResources().getColor(R.color.colorAccent));
        bt_post.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("eventrelatedclass");
    }
}
