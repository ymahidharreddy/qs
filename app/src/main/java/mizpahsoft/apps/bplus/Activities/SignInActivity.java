package mizpahsoft.apps.bplus.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.broadcastreceivers.SmSRetrieverCallBacks;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.utils.SmsRetrieverHelper;

public class SignInActivity extends AppCompatActivity implements SmSRetrieverCallBacks {
    //mobile number screen
    TextView td1, td2, tv_forgot_password;
    EditText et_number;
    Spinner sp_country_code;
    Button bt_number_next;
    String login_type = "", countryCode = "", mobile = "", otp = "", password = "";

    //password
    TextView tv_password_txt_field;
    EditText et_password_field;
    Button bt_password_continue_field;

    //password create
    TextView tv_password_txt;
    EditText et_password, et_confirm_password, et_enter_otp;
    Button bt_password_continue;
    ViewFlipper viewFlipLayout;

    //user login type
    TextView tv_login_type_title, tv_donor_type, tv_blood_bank_type;
    Button btn_login_type_next;

    String android_id = "";
    String refreshedToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        viewFlipLayout = (ViewFlipper) findViewById(R.id.viewFlipLayout);

        intialisationLoginType();
        intialisationMobileNumberScreen();
        intialisePasswordField();
        intialiseCreatePasswordLayout();
        // OTPLayoutIntialisation();
    }

    public void intialisationLoginType() {
        tv_login_type_title = (TextView) findViewById(R.id.tv_login_type_title);
        tv_login_type_title.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_donor_type = (TextView) findViewById(R.id.tv_donor_type);
        tv_donor_type.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_donor_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_type = "1";

                tv_donor_type.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_donor_type.setTextColor(getResources().getColor(R.color.textColorGreen));

                tv_blood_bank_type.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_blood_bank_type.setTextColor(getResources().getColor(R.color.textColor777));

                btn_login_type_next.setBackgroundResource(R.drawable.bt_yellow_bg);

            }
        });

        tv_blood_bank_type = (TextView) findViewById(R.id.tv_blood_bank_type);
        tv_blood_bank_type.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_blood_bank_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_type = "2";

                tv_blood_bank_type.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_blood_bank_type.setTextColor(getResources().getColor(R.color.textColorGreen));

                tv_donor_type.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_donor_type.setTextColor(getResources().getColor(R.color.textColor777));

                btn_login_type_next.setBackgroundResource(R.drawable.bt_yellow_bg);
            }
        });

        btn_login_type_next = (Button) findViewById(R.id.btn_login_type_next);
        btn_login_type_next.setTypeface(Global.setFont(this, Global.REGULARFONT));
        btn_login_type_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login_type.equals("")) {
                    TastyToast.makeText(SignInActivity.this, "Please select login type.", Toast.LENGTH_SHORT, TastyToast.WARNING);
                } else {
                    viewFlipLayout.showNext();
                }

            }
        });
    }

    public void intialisationMobileNumberScreen() {
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setVisibility(View.VISIBLE);
        tv_forgot_password.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = et_number.getText().toString().trim();
                if (mobile.equals("")) {
                    TastyToast.makeText(SignInActivity.this, "Please enter mobile number.", Toast.LENGTH_SHORT, TastyToast.WARNING);

                } else if (et_number.getText().toString().length() != 10) {
                    TastyToast.makeText(SignInActivity.this, "Please enter valid mobile number.", Toast.LENGTH_SHORT, TastyToast.WARNING);

                } else if (!Global.isInternetPresent(getApplicationContext())) {
                    TastyToast.makeText(SignInActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT, TastyToast.WARNING);
                } else {
                    checkPasswordApi("1");
                }

            }
        });

        td1 = (TextView) findViewById(R.id.td1);
        td1.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        td2 = (TextView) findViewById(R.id.td2);
        td2.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_number = (EditText) findViewById(R.id.et_number);
        et_number.setTypeface(Global.setFont(this, Global.LIGHTFONT));
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

        sp_country_code = (Spinner) findViewById(R.id.sp_country_code);
        sp_country_code.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, Global.countriesCodes);
        sp_country_code.setAdapter(adapter);
        if (Global.countriesCodes.length == 1)
            countryCode = "+91";

        sp_country_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCode = Global.countriesCodes[position];
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

        bt_number_next = (Button) findViewById(R.id.bt_number_next);
        bt_number_next.setTypeface(Global.setFont(this, Global.REGULARFONT));
        bt_number_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = et_number.getText().toString().trim();
                if (mobile.equals("")) {
                    TastyToast.makeText(SignInActivity.this, "Please enter mobile number.", Toast.LENGTH_SHORT, TastyToast.ERROR);
                    //TastyToast.makeText(SignInActivity.this, "Please enter password", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                } else if (!Global.isInternetPresent(getApplicationContext())) {
                    Toast.makeText(SignInActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else {
                    checkPasswordApi("0");
                }

            }
        });

    }


    public void intialiseCreatePasswordLayout() {

        tv_password_txt = (TextView) findViewById(R.id.tv_password_txt);
        tv_password_txt.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_password_txt.setText("Create your password.");

        et_password = (EditText) findViewById(R.id.et_password);
        et_password.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        et_confirm_password.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_enter_otp = (EditText) findViewById(R.id.et_enter_otp);
        et_enter_otp.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        et_enter_otp.setVisibility(View.VISIBLE);

        bt_password_continue = (Button) findViewById(R.id.bt_password_continue);
        bt_password_continue.setTypeface(Global.setFont(this, Global.REGULARFONT));
        bt_password_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_enter_otp.getText().toString().equals("")) {
                    TastyToast.makeText(SignInActivity.this, "Enter OTP.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    //TastyToast.makeText(SignInActivity.this, "Please enter password", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                } else if (et_password.getText().toString().equals("")) {
                    TastyToast.makeText(SignInActivity.this, "Enter password.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else if (et_password.getText().toString().length() < 6) {
                    TastyToast.makeText(SignInActivity.this, "Enter valid password.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else if (!et_password.getText().toString().equals(et_confirm_password.getText().toString())) {
                    TastyToast.makeText(SignInActivity.this, "Passwords does not match.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else {
                    password = et_password.getText().toString();
                    otp = et_enter_otp.getText().toString();
                    createPassword();
                }
            }
        });

        new SmsRetrieverHelper().startClient(SignInActivity.this, this);
        ((MyApplication) getApplicationContext()).setSmSRetrieverListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication) getApplicationContext()).setSmSRetrieverListener(null);
    }

    @Override
    public void onSMSReceived(String otp1) {
        et_enter_otp.setText(otp1.trim());

        if (et_enter_otp.getText().toString().equals("")) {
            TastyToast.makeText(SignInActivity.this, "Enter OTP.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            //TastyToast.makeText(SignInActivity.this, "Please enter password", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
        } else if (et_password.getText().toString().equals("")) {
            TastyToast.makeText(SignInActivity.this, "Enter password.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else if (et_password.getText().toString().length() < 6) {
            TastyToast.makeText(SignInActivity.this, "Enter valid password.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else if (!et_password.getText().toString().equals(et_confirm_password.getText().toString())) {
            TastyToast.makeText(SignInActivity.this, "Passwords does not match.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else {
            password = et_password.getText().toString();
            otp = et_enter_otp.getText().toString();
            createPassword();
        }

    }

    @Override
    public void TaskOnSuccess() {
        //String ss = "";

    }

    @Override
    public void TaskOnFailure() {
        //String ss = "";
    }

    public void intialisePasswordField() {

        tv_password_txt_field = (TextView) findViewById(R.id.tv_password_txt_field);
        tv_password_txt_field.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_password_field = (EditText) findViewById(R.id.et_password_field);
        et_password_field.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_password_continue_field = (Button) findViewById(R.id.bt_password_continue_field);
        bt_password_continue_field.setTypeface(Global.setFont(this, Global.REGULARFONT));
        bt_password_continue_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_password_field.getText().toString().equals("")) {
                    TastyToast.makeText(SignInActivity.this, "Please enter password", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else if (!Global.isInternetPresent(getApplicationContext())) {
                    TastyToast.makeText(SignInActivity.this, getString(R.string.no_internet), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                } else {
                    password = et_password_field.getText().toString().trim();
                    loginAuthentication();
                }

            }
        });

    }

    private void loginAuthentication() {
        bt_password_continue_field.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_password_continue_field.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Lgoin_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(SignInActivity.this, object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                JSONObject main = object.getJSONObject("result");

                                //donor response
                                if (main.has("userId")) {
                                    Global.saveSP(getApplicationContext(), Global.USER_ID, main.getString("userId"));
                                }
                                if (main.has("phoneNumber")) {
                                    Global.saveSP(getApplicationContext(), Global.PHONE_NUMBER, main.getString("phoneNumber"));
                                }

                                //blood bank response
                                if (main.has("b_bankId")) {
                                    Global.saveSP(getApplicationContext(), Global.USER_ID, main.getString("b_bankId"));
                                }
                                if (main.has("b_bankName")) {
                                    Global.saveSP(getApplicationContext(), Global.BLOOD_BANK_NAME, main.getString("b_bankName"));
                                }
                                if (main.has("establishedYear")) {
                                    Global.saveSP(getApplicationContext(), Global.EST_YEAR, main.getString("establishedYear"));
                                }
                                if (main.has("address")) {
                                    Global.saveSP(getApplicationContext(), Global.ADDRESS, main.getString("address"));
                                }
                                if (main.has("b_bank_phoneNo")) {
                                    Global.saveSP(getApplicationContext(), Global.PHONE_NUMBER, main.getString("b_bank_phoneNo"));
                                }
                                if (main.has("b_bank_emailId")) {
                                    Global.saveSP(getApplicationContext(), Global.EMAIL_ID, main.getString("b_bank_emailId"));
                                }
                                if (main.has("b_bank_websitUrl")) {
                                    Global.saveSP(getApplicationContext(), Global.WEBSITE_URL, main.getString("b_bank_websitUrl"));
                                }

                                Global.saveSP(getApplicationContext(), Global.USER_TYPE, object.getString("typeOfLogin"));
                                // Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");

                                if (main.has("isUserProfileUpdated")) {
                                    //Donor login checking
                                    if (main.getString("isUserProfileUpdated").equals("0")) {
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.putExtra("isUserProfileUpdated", main.getString("isUserProfileUpdated"));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    } else {
                                        Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }

                                } else {
                                    //blood bank login
                                    Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }


                            } else {
                                TastyToast.makeText(SignInActivity.this, "" + object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(SignInActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("Signin", "mainjsonparsingexception", "get into some exception");
                        }
                        bt_password_continue_field.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_password_continue_field.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_password_continue_field.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_password_continue_field.setClickable(true);
                        TastyToast.makeText(SignInActivity.this, "" + getString(R.string.server_error), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("Signin", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone_or_name", mobile);
                params.put("password", password);
                params.put("fcm_token", refreshedToken);
                params.put("deviceId", android_id);
                params.put("loginType", login_type);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void checkPasswordApi(final String isForgorPW) {
        bt_number_next.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_number_next.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.CHECK_PASSWORD_EXISTS_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                if (object.getBoolean("isPasswordExists") && !isForgorPW.equals("1")) {
                                    //password was already set by the user, so show the password field layout
                                    viewFlipLayout.setDisplayedChild(2);
                                } else {
                                    //create password layout
                                    viewFlipLayout.setDisplayedChild(3);
                                }
                            } else {
                                alertDialog(object.getString("message"), "LOGIN");
                                MyApplication.getInstance().trackEvent("Signin", "status=0", object.getString("message"));
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(SignInActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("Signin", "mainjsonparsingexception", "get into some exception");
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
                        //TastyToast.makeText(LoginAndRegistrationActivity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("Signin", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNumber", mobile);
                params.put("isForgotPwd", isForgorPW);
                params.put("loginType", login_type);
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

    public void alertDialog(String message, final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*if (key.equals("LOGIN")) {
                            Intent i = new Intent(getApplicationContext(), LoginAndRegistrationActivity.class);
                            startActivity(i);
                            finish();
                        }*/
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert");
        alert.show();
    }

    private void createPassword() {
        bt_password_continue.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_password_continue.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.CREATE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);

                            int success = object.getInt("status");
                            if (success == 1) {
                                JSONObject main = null;
                                if (object.has("userDetails")) {
                                    main = object.getJSONObject("userDetails");
                                    Global.saveSP(getApplicationContext(), Global.USER_ID, main.getString("userId"));
                                    Global.saveSP(getApplicationContext(), Global.PHONE_NUMBER, main.getString("phoneNumber"));
                                    Global.saveSP(getApplicationContext(), Global.USER_TYPE, object.getString("typeOfLogin"));
                                } else {
                                    main = object.getJSONObject("bloodBankDetails");
                                    Global.saveSP(getApplicationContext(), Global.USER_ID, main.getString("b_bankId"));
                                    Global.saveSP(getApplicationContext(), Global.PHONE_NUMBER, main.getString("b_bank_phoneNo"));
                                    Global.saveSP(getApplicationContext(), Global.USER_TYPE, object.getString("typeOfLogin"));
                                    Global.saveSP(getApplicationContext(), Global.BLOOD_BANK_NAME, main.getString("b_bankName"));
                                    Global.saveSP(getApplicationContext(), Global.EST_YEAR, main.getString("establishedYear"));
                                    Global.saveSP(getApplicationContext(), Global.ADDRESS, main.getString("address"));
                                    Global.saveSP(getApplicationContext(), Global.PHONE_NUMBER, main.getString("b_bank_phoneNo"));
                                    Global.saveSP(getApplicationContext(), Global.EMAIL_ID, main.getString("b_bank_emailId"));
                                    Global.saveSP(getApplicationContext(), Global.WEBSITE_URL, main.getString("b_bank_websitUrl"));

                                }

                                // Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");

                                if (main.has("isUserProfileUpdated")) {
                                    //Donor login checking
                                    if (main.getString("isUserProfileUpdated").equals("0")) {
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.putExtra("isUserProfileUpdated", main.getString("isUserProfileUpdated"));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    } else {
                                        Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }

                                } else {
                                    //blood bank login
                                    Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }

                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(SignInActivity.this, message, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                MyApplication.getInstance().trackEvent("Signin", "status=0", message);
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(SignInActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("Signin", "mainjsonparsingexception", "get into some exception");
                        }
                        bt_password_continue.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_password_continue.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_password_continue.setBackgroundResource(R.drawable.bt_yellow_bg);
                        bt_password_continue.setClickable(true);
                        //TastyToast.makeText(LoginAndRegistrationActivity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("Signin", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("password", password);
                params.put("loginType", login_type);
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
        MyApplication.getInstance().trackScreenView("Login screen");
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
