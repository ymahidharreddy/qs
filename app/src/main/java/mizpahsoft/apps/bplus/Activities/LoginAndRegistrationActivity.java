package mizpahsoft.apps.bplus.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.sdsmdg.tastytoast.TastyToast;
import com.shawnlin.numberpicker.NumberPicker;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.broadcastreceivers.SmSRetrieverCallBacks;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.utils.SmsRetrieverHelper;

public class LoginAndRegistrationActivity extends AppCompatActivity implements SmSRetrieverCallBacks {
    public String text1 = "One Time Password has sent to your mobile";
    public String text2 = "Please enter the same here to login.";
    public String mobile = "", otp = "", password = "", name = "", bloodGroup = "1", bloodGroupId = "", gender = "",
            age = "", countryCode = "", location = "", profilePic = "";
    TextView td1, td2, td3, td4, td5, tv_number_text, tv_resend, td6, td7, td8, tv_male, tv_female,
            tv_others, td9, td10, et_location;
    ImageView iv_profile_pic;
    NumberPicker number_picker, age_number_picker;
    Spinner sp_country_code;
    EditText et_number, et_otp, et_name;
    Button bt_number_next, bt_blood_next, bt_otp_continue, bt_name_next, bt_gender_next,
            bt_location_next, bt_submit;
    String[] countriesCodes = {"+91"};
    ArrayList<String> ar_bloodtype = new ArrayList<>();
    ArrayList<String> ar_bloodtypeid = new ArrayList<>();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    ProgressDialog progressDialog1;
    ViewFlipper viewFlipLayout;
    String[] values;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    static String REQUEST_TYPE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_CAM = 2;
    Bitmap bitmap;
    //password layout
    TextView tv_password_txt;
    EditText et_password, et_confirm_password;
    Button bt_password_continue;
    String refreshedToken;
    ArrayList<String> age_array = new ArrayList<>();
    String[] ages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_registration);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        sp = getSharedPreferences("loginprefs", 0);
        edt.putString("deviceId", android_id);
        edt.apply();

        td1 = (TextView) findViewById(R.id.td1);
        td1.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td2 = (TextView) findViewById(R.id.td2);
        td2.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td3 = (TextView) findViewById(R.id.td3);
        td3.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td4 = (TextView) findViewById(R.id.td4);
        td4.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td5 = (TextView) findViewById(R.id.td5);
        td5.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td6 = (TextView) findViewById(R.id.td6);
        td6.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td7 = (TextView) findViewById(R.id.td7);
        td7.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td8 = (TextView) findViewById(R.id.td8);
        td8.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td9 = (TextView) findViewById(R.id.td9);
        td9.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td10 = (TextView) findViewById(R.id.td10);
        td10.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_number_text = (TextView) findViewById(R.id.tv_number_text);
        tv_number_text.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_resend = (TextView) findViewById(R.id.tv_resend);
        tv_resend.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_male = (TextView) findViewById(R.id.tv_male);
        tv_male.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_female = (TextView) findViewById(R.id.tv_female);
        tv_female.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_others = (TextView) findViewById(R.id.tv_others);
        tv_others.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_number = (EditText) findViewById(R.id.et_number);
        et_number.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        et_otp = (EditText) findViewById(R.id.et_otp);
        et_otp.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        et_name = (EditText) findViewById(R.id.et_name);
        et_name.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        et_location = (TextView) findViewById(R.id.et_location);
        et_location.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        iv_profile_pic = (ImageView) findViewById(R.id.iv_profile_pic);

        sp_country_code = (Spinner) findViewById(R.id.sp_country_code);
        sp_country_code.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        number_picker = (NumberPicker) findViewById(R.id.number_picker);

        age_number_picker = (NumberPicker) findViewById(R.id.age_number_picker);

        viewFlipLayout = (ViewFlipper) findViewById(R.id.viewFlipLayout);

        bt_number_next = (Button) findViewById(R.id.bt_number_next);
        bt_number_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_blood_next = (Button) findViewById(R.id.bt_blood_next);
        bt_blood_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_otp_continue = (Button) findViewById(R.id.bt_otp_continue);
        bt_otp_continue.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_name_next = (Button) findViewById(R.id.bt_name_next);
        bt_name_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_gender_next = (Button) findViewById(R.id.bt_gender_next);
        bt_gender_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_location_next = (Button) findViewById(R.id.bt_location_next);
        bt_location_next.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_submit.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        GetBloodGroups();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, countriesCodes);
        sp_country_code.setAdapter(adapter);
        if (countriesCodes.length == 1)
            countryCode = "+91";

        sp_country_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCode = countriesCodes[position];
                try {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog1 = ProgressDialog.show(LoginAndRegistrationActivity.this, "", "Please wait..");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(LoginAndRegistrationActivity.this);
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


        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_number.getText().toString().length() == 10) {
                    bt_number_next.setBackgroundResource(R.drawable.bt_yellow_bg);
                    bt_number_next.setEnabled(true);
                } else {
                    bt_number_next.setBackgroundResource(R.drawable.bt_ash_bg);
                    bt_number_next.setEnabled(false);
                }
            }
        });
        et_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_otp.getText().toString().length() == 6) {
                    bt_otp_continue.setBackgroundResource(R.drawable.bt_yellow_bg);
                    bt_otp_continue.setEnabled(true);
                } else {
                    bt_otp_continue.setBackgroundResource(R.drawable.bt_ash_bg);
                    bt_otp_continue.setEnabled(false);
                }
            }
        });
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_otp.getText().toString().length() >= 3) {
                    bt_name_next.setBackgroundResource(R.drawable.bt_yellow_bg);
                    bt_name_next.setEnabled(true);
                } else {
                    bt_name_next.setBackgroundResource(R.drawable.bt_ash_bg);
                    bt_name_next.setEnabled(false);
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

        bt_number_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = et_number.getText().toString().trim();
                SentOTP();
            }
        });
        bt_otp_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = et_otp.getText().toString().trim();
                VerifyOTP(et_otp.getText().toString().trim());
            }
        });
        bt_blood_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodGroupId = ar_bloodtypeid.get(Integer.parseInt(bloodGroup));
                if (bloodGroupId.equals("")) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Please select Blood Group", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });
        bt_name_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name.getText().toString().trim();
                viewFlipLayout.showNext();
            }
        });
        bt_gender_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.equals("")) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Please select Gender", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });
        bt_location_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = et_location.getText().toString().trim();
                if (location.equals("")) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Please enter Location", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserDetails();
            }
        });

        iv_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
                MyApplication.getInstance().trackEvent("iv_tochange", "onClick", "showFileChooser");
            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.ReSend = false;
                resendOTP();
                MyApplication.getInstance().trackEvent("ReSendOTP", "onClick", "clicked and specified(SentOTP) action done");
            }
        });

        intialiseAgeFields();
        intialisePasswordLayout();

        new SmsRetrieverHelper().startClient(LoginAndRegistrationActivity.this, this);
        ((MyApplication) getApplicationContext()).setSmSRetrieverListener(this);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication) getApplicationContext()).setSmSRetrieverListener(null);
    }
    @Override
    public void onSMSReceived(String otp) {
        et_otp.setText(otp.trim());
        VerifyOTP(otp.trim());

    }

    @Override
    public void TaskOnSuccess() {
        //String ss = "";

    }

    @Override
    public void TaskOnFailure() {
        //String ss = "";
    }

    public void intialiseAgeFields() {

        TextView td1 = (TextView) findViewById(R.id.td1);
        td1.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        age_array.clear();
        for (int i = 17; i < 100; i++) {
            age_array.add("" + i);
        }

        ages = new String[age_array.size()];
        ages = age_array.toArray(ages);
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


        Button bt_age_next = (Button) findViewById(R.id.bt_age_next);
        bt_age_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age.equals("")) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Please select age.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                } else {
                    viewFlipLayout.showNext();
                }
            }
        });
    }

    private void intialisePasswordLayout() {

        tv_password_txt = (TextView) findViewById(R.id.tv_password_txt);
        tv_password_txt.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_password = (EditText) findViewById(R.id.et_password);
        et_password.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        et_confirm_password.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_password_continue = (Button) findViewById(R.id.bt_password_continue);
        bt_password_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_password.getText().toString().equals("")) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Enter password.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else if (et_password.getText().toString().length() < 6) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Enter valid password.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else if (!et_password.getText().toString().equals(et_confirm_password.getText().toString())) {
                    TastyToast.makeText(LoginAndRegistrationActivity.this, "Passwords does not match.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else {
                    password = et_password.getText().toString();
                    viewFlipLayout.showNext();
                }
            }
        });
    }

    private void GetBloodGroups() {

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
                                TastyToast.makeText(LoginAndRegistrationActivity.this, message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                MyApplication.getInstance().trackEvent("GetBloodGroups", "status=0", message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("GetBloodGroups", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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


    private void SentOTP() {
        bt_number_next.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_number_next.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Registraion_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                edt.putString("user_id", object.getString("userId")).commit();
                                TastyToast.makeText(LoginAndRegistrationActivity.this, "Please check your mobile for OTP", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                viewFlipLayout.showNext();

                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                SpannableString redSpannable = new SpannableString(text1);
                                redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textcolor99999)), 0, text1.length(), 0);
                                builder.append(redSpannable);

                                builder.append(" ");

                                SpannableString blackSpannable1 = new SpannableString(mobile);
                                blackSpannable1.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), 0, mobile.length(), 0);
                                builder.append(blackSpannable1);

                                builder.append(" ");

                                SpannableString blueSpannable = new SpannableString(text2);
                                blueSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textcolor99999)), 0, text2.length(), 0);
                                builder.append(blueSpannable);

                                tv_number_text.setText(builder.toString());

                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(LoginAndRegistrationActivity.this, message, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                MyApplication.getInstance().trackEvent("SentOTP", "status=0", message);
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(LoginAndRegistrationActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SentOTP", "mainjsonparsingexception", "get into some exception");
                        }
                        bt_number_next.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_number_next.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_number_next.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_number_next.setClickable(true);
                        TastyToast.makeText(LoginAndRegistrationActivity.this, "" + getString(R.string.server_error), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("SentOTP", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNumber", mobile);
                params.put("countryCode", countryCode);
                params.put("deviceType", "1"); //1 for android
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void VerifyOTP(final String str_input_otp) {
        bt_otp_continue.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_otp_continue.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.VerifiOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(LoginAndRegistrationActivity.this, "OTP successfully verified", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                                edt.putString("phone_number", mobile).commit();
                                Global.saveSP(getApplicationContext(), Global.USER_TYPE, object.getString("typeOfLogin"));
                                if (object.has("userDetails")) {
                                    dialogUserExits("Your Mobile Number is already registered, please SignIn");
                                    //edt.putString("logincheck", "1").commit();
                                    // startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    //finish();
                                }
                                //else
                                viewFlipLayout.showNext();
                                MyApplication.getInstance().trackEvent("VefifyOTP", "status=1", "otp verified");
                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(LoginAndRegistrationActivity.this, message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                MyApplication.getInstance().trackEvent("VefifyOTP", "status=0", message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //TastyToast.makeText(LoginAndRegistrationActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("VefifyOTP", "mainjsonparsingexception", "get into some exception");
                        }
                        bt_otp_continue.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_otp_continue.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_otp_continue.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_otp_continue.setClickable(true);
                        error.printStackTrace();
                        //TastyToast.makeText(LoginAndRegistrationActivity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("VefifyOTP", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", str_input_otp);
                params.put("userId", sp.getString("user_id", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void dialogUserExits(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert");
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressDialog1.dismiss();
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

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                profilePic = encodeTobase64(bitmap);
                iv_profile_pic.setImageBitmap(getRoundedShape(bitmap));
                MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=1", "got bitmap");
            } else if (requestCode == 2) {
                Bitmap bitmap2 = null;
                try {

                    bitmap2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    Matrix matrix = new Matrix();
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    if (manufacturer.equalsIgnoreCase("Samsung") && model.equalsIgnoreCase("SM-J200G")) {
                        matrix.postRotate(90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap2, 220, 220, true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        iv_profile_pic.setImageBitmap(getRoundedShape(rotatedBitmap));
                        profilePic = encodeTobase64(rotatedBitmap);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got resized bitmap for samsung");
                    } else {
                        iv_profile_pic.setImageBitmap(getRoundedShape(bitmap2));
                        profilePic = encodeTobase64(bitmap2);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got bitmap");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "get into some exception");
                }
            }
        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 300;
        int targetHeight = 300;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2, (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage, new Rect(0, 0, scaleBitmapImage.getWidth(), scaleBitmapImage.getHeight()), new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }


    private void showFileChooser() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAndRegistrationActivity.this);
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
                        LoginAndRegistrationActivity.this);
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
                        LoginAndRegistrationActivity.this);
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


    public static String encodeTobase64(Bitmap image) {
        byte[] tempbyte = getBytesFromBitmap(resize(image, 500, 500));
        return Base64.encodeToString(tempbyte, Base64.DEFAULT);
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

    // update user location and blood group
    private void UpdateUserDetails() {
        bt_submit.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_submit.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.UserLocationandBloodGrup,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(getApplicationContext(), "Thank you for registering with us.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                edt.putString("logincheck", "1").commit();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                MyApplication.getInstance().trackEvent("UpdateUserdetails", "status=1", "get into some exception");
                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                MyApplication.getInstance().trackEvent("UpdateUserdetails", "status=0", message);
                            }
                        } catch (Exception e) {
                            //TastyToast.makeText(NewRegistrationForm_Activity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("UpdateUserdetails", "mainjsonparsingexception", "get into some exception");
                        }
                        bt_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_submit.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_submit.setClickable(true);
                        //TastyToast.makeText(NewRegistrationForm_Activity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("UpdateUserdetails", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bloodGroup", bloodGroupId);
                params.put("userId", sp.getString("user_id", ""));
                params.put("address", location);
                params.put("deviceId", sp.getString("deviceId", ""));
                //params.put("fcm_token", sp.getString("Token_regId", ""));
                params.put("fcm_token", refreshedToken);
                params.put("name", name);
                params.put("gender", gender);
                params.put("age", age);
                params.put("profilePic", profilePic);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void resendOTP() {
        tv_resend.setTextColor(getResources().getColor(R.color.textcolor));
        tv_resend.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Registraion_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                edt.putString("user_id", object.getString("userId")).commit();
                                TastyToast.makeText(LoginAndRegistrationActivity.this, "Please check your mobile for OTP", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                SpannableString redSpannable = new SpannableString(text1);
                                redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textcolor99999)), 0, text1.length(), 0);
                                builder.append(redSpannable);

                                builder.append(" ");

                                SpannableString blackSpannable1 = new SpannableString(mobile);
                                blackSpannable1.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), 0, mobile.length(), 0);
                                builder.append(blackSpannable1);
                                builder.append(" ");

                                SpannableString blueSpannable = new SpannableString(text2);
                                blueSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textcolor99999)), 0, text2.length(), 0);
                                builder.append(blueSpannable);

                                tv_number_text.setText(builder.toString());

                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(LoginAndRegistrationActivity.this, message, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                MyApplication.getInstance().trackEvent("SentOTP", "status=0", message);
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(LoginAndRegistrationActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SentOTP", "mainjsonparsingexception", "get into some exception");
                        }
                        tv_resend.setTextColor(getResources().getColor(R.color.textColorResendOTP));
                        tv_resend.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tv_resend.setTextColor(getResources().getColor(R.color.textColorResendOTP));
                        tv_resend.setClickable(true);
                        //TastyToast.makeText(LoginAndRegistrationActivity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("SentOTP", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNumber", mobile);
                params.put("countryCode", countryCode);
                params.put("deviceType", "1"); //1 for android
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Registration screen screen");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), BeforeLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
