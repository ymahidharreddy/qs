package mizpahsoft.apps.bplus.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

/**
 * Created by Mizpah-DEV on 23-Nov-16.
 */

public class DetailIndividualOne extends Activity {
    private static final String TAG = "detailsindividual";
    ImageView iv_image;
    LinearLayout ll_eventadd_update, ll_eventadd_delete;
    EditText et_eventadd_name, et_eventadd_date, et_eventadd_contactnos,
            et_eventadd_condby, et_eventadd_orgname, et_eventadd_description;

    DatePickerDialog mDatePicker;
    static String REQUEST_TYPE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_CAM = 2;
    int currentapiVersion;
    ProgressDialog progressDialog1, progressDialog12;
    SharedPreferences sp;
    String originFrom = "";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 111;

    String address = "", eventImage = "", eventid = "", userid = "", eventName = "", eventDate = "", eventConductedBy = "", organizationName = "", phoneNumber = "", description = "";

    View line_view;
    TextView tv_event_name_title, tv_eventname, tv_event_title_date, tv_eventdate,
            tv_contact_number_title, tv_contactnos, tv_address_title, ac_address, tv_eventaddress,
            tv_event_head_title, tv_eventhead, tv_organisation_title, tv_eventorgname, tv_description_title,
            tv_eventdesc, tv_eventadd_update, tv_eventadd_Delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_individual);
        Intent intent = getIntent();
        eventid = intent.getExtras().getString("eventid");
        originFrom = intent.getExtras().getString("from");
        sp = getSharedPreferences("loginprefs", 0);
        userid = sp.getString("user_id", "N/A");
        if (userid.equals("") || userid.equals(null) || userid.equals("null")) {
            MyApplication.getInstance().trackEvent("shared preferences", "userid", "getting null/empty");
        }

        tv_event_name_title = (TextView) findViewById(R.id.tv_event_name_title);
        tv_event_name_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_event_title_date = (TextView) findViewById(R.id.tv_event_title_date);
        tv_event_title_date.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_contact_number_title = (TextView) findViewById(R.id.tv_contact_number_title);
        tv_contact_number_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_address_title = (TextView) findViewById(R.id.tv_address_title);
        tv_address_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_event_head_title = (TextView) findViewById(R.id.tv_event_head_title);
        tv_event_head_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_organisation_title = (TextView) findViewById(R.id.tv_organisation_title);
        tv_organisation_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_description_title = (TextView) findViewById(R.id.tv_description_title);
        tv_description_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        iv_image = (ImageView) findViewById(R.id.iv_image);

        ll_eventadd_update = (LinearLayout) findViewById(R.id.ll_eventadd_update);
        ll_eventadd_delete = (LinearLayout) findViewById(R.id.ll_eventadd_delete);

        et_eventadd_name = (EditText) findViewById(R.id.et_eventadd_name);
        et_eventadd_name.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_eventadd_date = (EditText) findViewById(R.id.et_eventadd_date);
        et_eventadd_date.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_eventadd_contactnos = (EditText) findViewById(R.id.et_eventadd_contactnos);
        et_eventadd_contactnos.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_eventadd_condby = (EditText) findViewById(R.id.et_eventadd_condby);
        et_eventadd_condby.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_eventadd_orgname = (EditText) findViewById(R.id.et_eventadd_orgname);
        et_eventadd_orgname.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_eventadd_description = (EditText) findViewById(R.id.et_eventadd_description);
        et_eventadd_description.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        tv_eventname = (TextView) findViewById(R.id.tv_eventname);
        tv_eventname.setTypeface(Global.setFont(this, Global.REGULARFONT));

        tv_eventdate = (TextView) findViewById(R.id.tv_eventdate);
        tv_eventdate.setTypeface(Global.setFont(this, Global.REGULARFONT));

        tv_contactnos = (TextView) findViewById(R.id.tv_contactnos);
        tv_contactnos.setTypeface(Global.setFont(this, Global.REGULARFONT));

        tv_eventaddress = (TextView) findViewById(R.id.tv_eventaddress);
        tv_eventaddress.setTypeface(Global.setFont(this, Global.REGULARFONT));

        tv_eventhead = (TextView) findViewById(R.id.tv_eventhead);
        tv_eventhead.setTypeface(Global.setFont(this, Global.REGULARFONT));

        tv_eventorgname = (TextView) findViewById(R.id.tv_eventorgname);
        tv_eventorgname.setTypeface(Global.setFont(this, Global.REGULARFONT));

        tv_eventdesc = (TextView) findViewById(R.id.tv_eventdesc);
        tv_eventdesc.setTypeface(Global.setFont(this, Global.REGULARFONT));

        line_view = (View) findViewById(R.id.line_view);

        tv_eventadd_update = (TextView) findViewById(R.id.tv_eventadd_update);
        tv_eventadd_update.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_eventadd_Delete = (TextView) findViewById(R.id.tv_eventadd_Delete);
        tv_eventadd_Delete.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        ac_address = (TextView) findViewById(R.id.ac_address);
        ac_address.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        ac_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog12 = ProgressDialog.show(DetailIndividualOne.this, "", "Please wait..");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(DetailIndividualOne.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent called");

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent failed exception");
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent failed exception");
                }
            }
        });

        if (originFrom.equals("events")) {
            ll_eventadd_update.setVisibility(View.GONE);
            ll_eventadd_delete.setVisibility(View.GONE);
            et_eventadd_name.setVisibility(View.GONE);
            et_eventadd_date.setVisibility(View.GONE);
            et_eventadd_contactnos.setVisibility(View.GONE);
            et_eventadd_condby.setVisibility(View.GONE);
            et_eventadd_orgname.setVisibility(View.GONE);
            et_eventadd_description.setVisibility(View.GONE);
            ac_address.setVisibility(View.GONE);
            tv_eventname.setVisibility(View.VISIBLE);
            tv_eventdate.setVisibility(View.VISIBLE);
            tv_contactnos.setVisibility(View.VISIBLE);
            tv_eventaddress.setVisibility(View.VISIBLE);
            tv_eventhead.setVisibility(View.VISIBLE);
            tv_eventorgname.setVisibility(View.VISIBLE);
            tv_eventdesc.setVisibility(View.VISIBLE);
            line_view.setVisibility(View.INVISIBLE);
        }

        GetEvents1();
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

                mDatePicker = new DatePickerDialog(DetailIndividualOne.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        eventDate = selectedyear + "-" + (selectedmonth + 1) + "-" + selectedday;
                        et_eventadd_date.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);
                        MyApplication.getInstance().trackEvent("et_eventadd_dates", "onClick", "date selected");

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
                mDatePicker.getDatePicker().setMaxDate(mcurrentDate2.getTimeInMillis());
                mDatePicker.show();
            }
        });
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!originFrom.equals("events")) {
                    showFileChooser();
                    MyApplication.getInstance().trackEvent("iv_image", "onClick", "called showFileChooser()");
                }
            }
        });
        ll_eventadd_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkTolerance();
                MyApplication.getInstance().trackEvent("ll_eventadd_update", "onClick", "called checkTolerance()");
            }
        });

        tv_eventadd_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteevent();
                MyApplication.getInstance().trackEvent("tv_eventadd_Delete", "onClick", "called deleteevent()");
            }
        });
    }

    private void deleteevent() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Delete_Event,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                Toast.makeText(DetailIndividualOne.this, "Event Deleted Successfully", Toast.LENGTH_SHORT).show();
                                progressDialog1.dismiss();
                                setResult(Global.ACTIVITY_FOR_RESULT);
                                finish();
                                MyApplication.getInstance().trackEvent("deleteevent", "status=0", "Event Deleted Successfully");
                            } else {
                                Toast.makeText(getApplicationContext(), "" + "Event deletion failed.retry?", Toast.LENGTH_SHORT).show();
                                progressDialog1.dismiss();
                                MyApplication.getInstance().trackEvent("deleteevent", "status=0", "Event deletion failed");
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + "Event deletion failed.retry?", Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("getEvents1", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "" + "Event deletion failed.retry?", Toast.LENGTH_SHORT).show();
                        progressDialog1.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("deleteevent", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventId", eventid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

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

    public void checkTolerance() {
        address = ac_address.getText().toString().trim();
        eventName = et_eventadd_name.getText().toString().trim();
        eventConductedBy = et_eventadd_condby.getText().toString().trim();
        organizationName = et_eventadd_orgname.getText().toString().trim();
        phoneNumber = et_eventadd_contactnos.getText().toString().trim();
        description = et_eventadd_description.getText().toString().trim();
        if (eventName.equals("")) {
            Toast.makeText(this, "Please specify your event name", Toast.LENGTH_SHORT).show();
        } else if (et_eventadd_date.getText().toString().equals("")) {
            Toast.makeText(this, "Please mention event date", Toast.LENGTH_SHORT).show();
        } else if (et_eventadd_contactnos.equals("")) {
            Toast.makeText(this, "Please specify atleast one contact number", Toast.LENGTH_SHORT).show();
        } else if (et_eventadd_contactnos.length() < 10) {
            Toast.makeText(this, "Please enter valid 10 digit contact number", Toast.LENGTH_SHORT).show();
        } else if (ac_address.equals("")) {
            Toast.makeText(this, "Please specify address from drop down / suggestions", Toast.LENGTH_SHORT).show();
        } else if (et_eventadd_condby.equals("")) {
            Toast.makeText(this, "Please specify your event head", Toast.LENGTH_SHORT).show();
        } else if (et_eventadd_description.equals("")) {
            Toast.makeText(this, "Please describe about your event in few words", Toast.LENGTH_SHORT).show();
        } else {
            tv_eventadd_update.setText("Updating...");
            updateEvent();
            ll_eventadd_update.setClickable(false);
            MyApplication.getInstance().trackEvent("checkTolerance()", "checked all the requirements", "called updateEvent()");
        }
    }

    public void updateEvent() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.EventsUpdate_URl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                ll_eventadd_update.setClickable(true);
                                tv_eventadd_update.setText("Event Updated Successfully");
                                Toast.makeText(DetailIndividualOne.this, "Event Updated Successfully", Toast.LENGTH_SHORT).show();
                                GetEvents1();
                                progressDialog1.dismiss();
                                MyApplication.getInstance().trackEvent("updateEvent()", "status=1", "Event Updated Successfully");
                            } else {
                                String message = object.getString("message");
                                ll_eventadd_update.setClickable(true);
                                tv_eventadd_update.setText("Event Update failed.retry?");
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                progressDialog1.dismiss();
                                MyApplication.getInstance().trackEvent("updateEvent()", "status=0", object.getString("message"));
                            }
                        } catch (Exception e) {
                            //Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            tv_eventadd_update.setText("Event Update failed.retry?");
                            ll_eventadd_update.setClickable(true);
                            progressDialog1.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("updateEvent()", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                        tv_eventadd_update.setText("Event Update failed.retry?");
                        ll_eventadd_update.setClickable(true);
                        progressDialog1.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("updateEvent()", "volley error", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userid);
                params.put("eventId", eventid);
                params.put("eventImage", eventImage);
                params.put("eventName", eventName);
                params.put("eventDate", eventDate);
                params.put("address", address);
                params.put("eventConductedBy", eventConductedBy);
                params.put("organizationName", organizationName);
                params.put("phoneNumber", phoneNumber);
                params.put("description", description);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void GetEvents1() {
        progressDialog1 = ProgressDialog.show(DetailIndividualOne.this, "", "please wait..");
        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.Events2_URl + eventid, new Response.Listener<String>() {
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
                                tv_eventname.setText(obj_res.getString("event"));
                            }
                            if (obj_res.getString("eventDate").equals("") || obj_res.getString("eventDate").equals("null") || obj_res.getString("eventDate").equals(null)) {

                            } else {
                                et_eventadd_date.setText(obj_res.getString("eventDate"));
                                tv_eventdate.setText(obj_res.getString("eventDate"));
                            }
                            if (obj_res.getString("phoneNumber").equals("") || obj_res.getString("phoneNumber").equals("null") || obj_res.getString("phoneNumber").equals(null)) {

                            } else {
                                et_eventadd_contactnos.setText(obj_res.getString("phoneNumber"));
                                tv_contactnos.setText(obj_res.getString("phoneNumber"));
                            }
                            if (obj_res.getString("eventAddress").equals("") || obj_res.getString("eventAddress").equals("null") || obj_res.getString("eventAddress").equals(null)) {

                            } else {
                                ac_address.setText(obj_res.getString("eventAddress"));
                                tv_eventaddress.setText(obj_res.getString("eventAddress"));
                            }
                            if (obj_res.getString("eventConductedBy").equals("") || obj_res.getString("eventConductedBy").equals("null") || obj_res.getString("eventConductedBy").equals(null)) {

                            } else {
                                et_eventadd_condby.setText(obj_res.getString("eventConductedBy"));
                                tv_eventhead.setText(obj_res.getString("eventConductedBy"));
                            }
                            if (obj_res.getString("organizationName").equals("") || obj_res.getString("organizationName").equals("null") || obj_res.getString("organizationName").equals(null)) {

                            } else {
                                et_eventadd_orgname.setText(obj_res.getString("organizationName"));
                                tv_eventorgname.setText(obj_res.getString("organizationName"));
                            }
                            if (obj_res.getString("description").equals("") || obj_res.getString("description").equals("null") || obj_res.getString("description").equals(null)) {

                            } else {
                                et_eventadd_description.setText(obj_res.getString("description"));
                                tv_eventdesc.setText(obj_res.getString("description"));
                            }
                            String img = obj_res.getString("eventImage");
                            if (!(img.equals("")) && !(img.equals("null"))) {
                                String url = Global.Events_image + img;
                                Picasso.with(DetailIndividualOne.this).load(url).error(R.mipmap.no_image).into(iv_image);
                            }
                            progressDialog1.dismiss();
                            tv_eventadd_update.setText(" Update ");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("getEvents1", "status=1", "get into some exception");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        progressDialog1.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents1", "status=0", jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getEvents1", "mainjsonparsingexception", "get into some exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getEvents1", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    private void showFileChooser() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailIndividualOne.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    REQUEST_TYPE = "CAMERA";
                    if (currentapiVersion >= Build.VERSION_CODES.M) {
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

                    int currentapiVersion = Build.VERSION.SDK_INT;
                    if (currentapiVersion >= Build.VERSION_CODES.M) {
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
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (REQUEST_TYPE.equals("CAMERA")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                    MyApplication.getInstance().trackEvent("onReqPermission", "camera permission", "accepted & called respective intent");
                }
            } else if (REQUEST_TYPE.equals("GALLERY")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
                MyApplication.getInstance().trackEvent("onReqPermission", "gallery permission", "accepted & called respective intent");
            }
        } else {
            Toast.makeText(this, "you denied permission? you can't add your service pic.", Toast.LENGTH_SHORT).show();
            MyApplication.getInstance().trackEvent("onReqPermission", "camera/gallery permission", "denied by user");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestPermission() {
        if (REQUEST_TYPE.equals("GALLERY")) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Toast.makeText(this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        DetailIndividualOne.this);
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
                        DetailIndividualOne.this);
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
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Bundle extras = data.getExtras();
                Bitmap bitmap1 = (Bitmap) extras.get("data");
                eventImage = encodeTobase64(bitmap1);
                iv_image.setImageBitmap(bitmap1);
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
                        iv_image.setImageBitmap(rotatedBitmap);
                        eventImage = encodeTobase64(rotatedBitmap);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got resized bitmap for samsung");
                    } else {
                        iv_image.setImageBitmap(bitmap2);
                        eventImage = encodeTobase64(bitmap2);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got bitmap");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "get into some exception");
                }
            }
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                address = "" + place.getAddress();
                ac_address.setText(address);
                ac_address.setTextColor(Color.BLACK);
                progressDialog12.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "got address");

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
                address = "";
                progressDialog12.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "error in getting address");

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                address = "";
                progressDialog12.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "cancelled(getting address)");
            }
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

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tempbyte = getBytesFromBitmap(resize(image, 500, 500));
// immagex.compress(Bitmap.CompressFormat.PNG, 70, baos);
//byte[] b = baos.toByteArray();
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
    public void onBackPressed() {
        startActivity(new Intent(DetailIndividualOne.this, EventRelatedClass.class));
        MyApplication.getInstance().trackEvent("onBackPressed", "back actrion requested", "by user");
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            progressDialog12.dismiss();
        } catch (Exception e) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("detailsindividual");
    }
}
