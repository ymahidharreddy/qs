package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.sdsmdg.tastytoast.TastyToast;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.Model.SearchModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class SearchDonorActivity extends AppCompatActivity {
    TextView td6, td1, td8, tv_male, tv_female, tv_others, td9, et_location, td4;
    NumberPicker number_picker, age_number_picker;
    Button bt_blood_next, bt_age_next, bt_gender_next, bt_request;
    EditText et_message;
    ArrayList<String> ar_bloodtype = new ArrayList<>();
    ArrayList<String> ar_bloodtypeid = new ArrayList<>();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    ProgressDialog progressDialog1;
    ViewFlipper viewFlipLayout;
    String[] values;
    public String bloodGroup = "1", bloodGroupId = "", gender = "",
            location = "", age = "", message = "";
    ProgressDialog progressDialog;
    ArrayList<SearchModel> arrayList = new ArrayList<>();
    String[] ages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donor);
        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Search Donor");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ages = getResources().getStringArray(R.array.age_ranges);

        td1 = (TextView) findViewById(R.id.td1);
        td1.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td4 = (TextView) findViewById(R.id.td4);
        td4.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td6 = (TextView) findViewById(R.id.td6);
        td6.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td8 = (TextView) findViewById(R.id.td8);
        td8.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td9 = (TextView) findViewById(R.id.td9);
        td9.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_male = (TextView) findViewById(R.id.tv_male);
        tv_male.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_female = (TextView) findViewById(R.id.tv_female);
        tv_female.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_others = (TextView) findViewById(R.id.tv_others);
        tv_others.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        et_message = (EditText) findViewById(R.id.et_message);
        et_message.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        et_location = (TextView) findViewById(R.id.et_location);
        et_location.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        number_picker = (NumberPicker) findViewById(R.id.number_picker);
        age_number_picker = (NumberPicker) findViewById(R.id.age_number_picker);

        viewFlipLayout = (ViewFlipper) findViewById(R.id.viewFlipLayout);


        bt_blood_next = (Button) findViewById(R.id.bt_blood_next);
        bt_blood_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_gender_next = (Button) findViewById(R.id.bt_gender_next);
        bt_gender_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_age_next = (Button) findViewById(R.id.bt_age_next);
        bt_age_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_request = (Button) findViewById(R.id.bt_request);
        bt_request.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        GetBloodGroups();

        et_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog1 = ProgressDialog.show(SearchDonorActivity.this, "", "Please wait..");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(SearchDonorActivity.this);
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

        // set divider color
        number_picker.setDividerColor(getResources().getColor(R.color.dividerColor));
        number_picker.setDividerColorResource(R.color.dividerColor);

        // set formatter
        number_picker.setFormatter(getString(R.string.number_picker_formatter));
        number_picker.setFormatter(R.string.number_picker_formatter);

        // set text color
        number_picker.setTextColor(getResources().getColor(R.color.textColorBloodGroup));
        number_picker.setTextColorResource(R.color.textColorBloodGroup);

        // set text size
        number_picker.setTextSize(getResources().getDimension(R.dimen.text_size));
        number_picker.setTextSize(R.dimen.text_size);

        // set typeface
        number_picker.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        number_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                bloodGroup = "" + newVal;
            }
        });

        // set divider color
        age_number_picker.setDividerColor(getResources().getColor(R.color.dividerColor));
        age_number_picker.setDividerColorResource(R.color.dividerColor);

        // set formatter
        age_number_picker.setFormatter(getString(R.string.number_picker_formatter));
        age_number_picker.setFormatter(R.string.number_picker_formatter);

        // set text color
        age_number_picker.setTextColor(getResources().getColor(R.color.textColorBloodGroup));
        age_number_picker.setTextColorResource(R.color.textColorBloodGroup);

        // set text size
        age_number_picker.setTextSize(getResources().getDimension(R.dimen.text_size));
        age_number_picker.setTextSize(R.dimen.text_size);

        // set typeface
        age_number_picker.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        //Populate NumberPicker values from String array values
        //Set the minimum value of NumberPicker
        age_number_picker.setMinValue(0); //from array first value
        //Specify the maximum value/number of NumberPicker
        age_number_picker.setMaxValue(ages.length - 1); //to array last value

        //Specify the NumberPicker data source as array elements
        age_number_picker.setDisplayedValues(ages);

        try {
            if (ages.length == 1)
                age = ages[0];
            else
                age = ages[1];
        } catch (Exception e) {
            e.printStackTrace();
        }

        age_number_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                age = ages[newVal];
            }
        });

        tv_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "1";
                tv_male.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_female.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_others.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_male.setTextColor(getResources().getColor(R.color.textColorGreen));
                tv_female.setTextColor(getResources().getColor(R.color.textColorDDD));
                tv_others.setTextColor(getResources().getColor(R.color.textColorDDD));

                bt_gender_next.setBackgroundResource(R.drawable.bt_yellow_bg);
            }
        });
        tv_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "2";
                tv_male.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_female.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_others.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_male.setTextColor(getResources().getColor(R.color.textColorDDD));
                tv_female.setTextColor(getResources().getColor(R.color.textColorGreen));
                tv_others.setTextColor(getResources().getColor(R.color.textColorDDD));

                bt_gender_next.setBackgroundResource(R.drawable.bt_yellow_bg);
            }
        });
        tv_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "0";
                tv_male.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_female.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_others.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_male.setTextColor(getResources().getColor(R.color.textColorDDD));
                tv_female.setTextColor(getResources().getColor(R.color.textColorDDD));
                tv_others.setTextColor(getResources().getColor(R.color.textColorGreen));

                bt_gender_next.setBackgroundResource(R.drawable.bt_yellow_bg);
            }
        });

        bt_blood_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodGroupId = ar_bloodtypeid.get(Integer.parseInt(bloodGroup));
                if (bloodGroupId.equals("")) {
                    Toast.makeText(SearchDonorActivity.this, "Please select Blood Group", Toast.LENGTH_SHORT).show();
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });

        bt_gender_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.equals("")) {
                    Toast.makeText(SearchDonorActivity.this, "Please select Gender", Toast.LENGTH_SHORT).show();
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });

        bt_age_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (age.equals("")) {
                    Toast.makeText(SearchDonorActivity.this, "Please select Age", Toast.LENGTH_SHORT).show();
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });

        bt_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = et_location.getText().toString().trim();
                message = et_message.getText().toString().trim();
                if (location.equals("")) {
                    Toast.makeText(SearchDonorActivity.this, "Please select Location", Toast.LENGTH_SHORT).show();
                } else {
                    ShowDialog();
                }
            }
        });

    }

    private void GetBloodGroups() {
        progressDialog = ProgressDialog.show(SearchDonorActivity.this, "", "Loading...");
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
                                    ar_bloodtype.add(ob_results.getString("bloodgroup"));
                                    ar_bloodtypeid.add(ob_results.getString("id"));
                                }
                                /*ArrayAdapter sp_btypes = new ArrayAdapter(LoginAndRegistrationActivity.this, R.layout.myspinner_style, ar_bloodtype);
                                spinner_bloodtypes.setAdapter(sp_btypes);*/
                                values = new String[ar_bloodtype.size()];
                                values = ar_bloodtype.toArray(values);

                                //Populate NumberPicker values from String array values
                                //Set the minimum value of NumberPicker
                                number_picker.setMinValue(0); //from array first value
                                //Specify the maximum value/number of NumberPicker
                                number_picker.setMaxValue(values.length - 1); //to array last value

                                //Specify the NumberPicker data source as array elements
                                number_picker.setDisplayedValues(values);
/*
                                //Gets whether the selector wheel wraps when reaching the min/max value.
                                np.setWrapSelectorWheel(true);
                                //Set a value change listener for NumberPicker*/
                                MyApplication.getInstance().trackEvent("GetBloodGroups", "status=1", "got all blood groups");
                            } else {
                                String message = object.getString("message");
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("GetBloodGroups", "status=0", message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("GetBloodGroups", "mainjsonparsingexception", "get into some exception");
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("GetBloodGroups", "volley error listener", "get into some exception");
                        //Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                //Log.i(TAG, "Place: " + place.getName());
                location = "" + place.getAddress();
                et_location.setText(location);
                et_location.setTextColor(Color.BLACK);
                progressDialog1.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "got address");

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                //Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());
                location = "";
                et_location.setText(location);
                progressDialog1.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "error in getting address");

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                location = "";
                et_location.setText(location);
                progressDialog1.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "cancelled(getting address)");
            }
        }
    }

    private void ShowDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchDonorActivity.this);
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder
                    .setMessage("We are going to send Notification to all our Donor.?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendRequest();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            TastyToast.makeText(getApplicationContext(), "Error occurred", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    public void sendRequest() {
        bt_request.setBackgroundResource(R.drawable.bt_ash_bg);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.RequestBlood_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                JSONArray donorlist = object.getJSONArray("donors");
                                if (donorlist.length() != 0) {
                                    for (int i = 0; i < donorlist.length(); i++) {
                                        JSONObject object1 = donorlist.getJSONObject(i);
                                        SearchModel sm = new SearchModel();
                                        sm.setUserId(object1.getString("userId"));
                                        sm.setName(object1.getString("name"));
                                        sm.setAddress(object1.getString("address"));
                                        sm.setProfilePicture(object1.getString("profilePicture"));
                                        sm.setBloodgroup(object1.getString("bloodGroupName"));
                                        sm.setDistance(object1.getString("distance"));
                                        arrayList.add(sm);
                                    }
                                    Bundle b = new Bundle();
                                    b.putParcelableArrayList("DATA", arrayList);
                                    b.putString("Lat", object.getString("lat"));
                                    b.putString("Lon", object.getString("lon"));
                                    b.putString("start", object.getString("offset"));
                                    b.putString("BLOOD_GROUP", bloodGroupId);
                                    b.putString("ADDRESS", location);
                                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                    i.putExtras(b);
                                    startActivity(i);
                                    finish();
                                    MyApplication.getInstance().trackEvent("sendRequest", "status=1", "requested donors page navigation");
                                } else {
                                    TastyToast.makeText(SearchDonorActivity.this, "No donors available for this blood group try again later!", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                    finish();
                                    MyApplication.getInstance().trackEvent("sendRequest", "status=1", "No donors available");
                                }

                            } else {
                                String message = object.getString("message");
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("sendRequest", "status=0", message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("sendRequest", "mainjsonparsingexception", "get into some exception");
                        }
                        bt_request.setBackgroundResource(R.drawable.bt_yellow_bg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_request.setBackgroundResource(R.drawable.bt_yellow_bg);
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("sendRequest", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bloodGroup", bloodGroupId);
                params.put("age", age);
                params.put("gender", gender);
                params.put("address", location);
                params.put("message", message);
                params.put("userId", getSharedPreferences("loginprefs", 0).getString("user_id", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Search donor screen");
    }

}
