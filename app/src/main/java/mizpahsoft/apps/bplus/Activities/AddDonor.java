package mizpahsoft.apps.bplus.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
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
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class AddDonor extends AppCompatActivity {

    //mobile number screen
    TextView td1, td2;
    EditText et_number;
    Spinner sp_country_code;
    Button bt_number_next;
    String[] countriesCodes = {"+91"};
    String countryCode = "", mobile = "", otp = "";

    //otp screen
    TextView td3, td4, td5, tv_number_text, tv_resend;
    EditText et_otp;
    Button bt_otp_continue;
    public String text1 = "One Time Password has sent to your mobile";
    public String text2 = "Please enter the same here to login.";

    ViewFlipper viewFlipLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donor);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Donor Registration");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewFlipLayout = (ViewFlipper) findViewById(R.id.viewFlipLayout);
        intialisationMobileNumberScreen();
        OTPLayoutIntialisation();

    }

    public void intialisationMobileNumberScreen() {
        td1 = (TextView) findViewById(R.id.td1);
        td1.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        td1.setText(getString(R.string.donor_mobile));

        td2 = (TextView) findViewById(R.id.td2);
        td2.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_number = (EditText) findViewById(R.id.et_number);
        et_number.setTypeface(Global.setFont(this, Global.REGULARFONT));
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, countriesCodes);
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

        bt_number_next = (Button) findViewById(R.id.bt_number_next);
        bt_number_next.setTypeface(Global.setFont(this, Global.REGULARFONT));
        bt_number_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = et_number.getText().toString().trim();
                if (Global.isInternetPresent(getApplicationContext())) {
                    SentOTP();
                } else {
                    Toast.makeText(AddDonor.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void OTPLayoutIntialisation() {

        td3 = (TextView) findViewById(R.id.td3);
        td3.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        td4 = (TextView) findViewById(R.id.td4);
        td4.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        td5 = (TextView) findViewById(R.id.td4);
        td5.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_number_text = (TextView) findViewById(R.id.tv_number_text);
        tv_number_text.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_resend = (TextView) findViewById(R.id.tv_resend);
        tv_resend.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_otp = (EditText) findViewById(R.id.et_otp);
        et_otp.setTypeface(Global.setFont(this, Global.LIGHTFONT));
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

        bt_otp_continue = (Button) findViewById(R.id.bt_otp_continue);
        bt_otp_continue.setTypeface(Global.setFont(this, Global.REGULARFONT));

        bt_otp_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = et_otp.getText().toString().trim();
                VerifyOTP(et_otp.getText().toString().trim());
            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
            }
        });

    }

    private void VerifyOTP(final String str_input_otp) {
        bt_otp_continue.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_otp_continue.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.VerifiAdddonorOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONArray arr = new JSONArray(response);
                            JSONObject object = new JSONObject(response);
                            int success = object.getInt("status");
                            if (success == 1) {
                                //TastyToast.makeText(AddDonor.this, object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                // Global.saveSP(getApplicationContext(), "phone_number", mobile);
                                // if (object.has("userDetails")) {
                                //Global.saveSP(getApplicationContext(), "logincheck", "1");
                                showAlertDialog(getString(R.string.donor_added));
                               /* } else {
                                    MyApplication.getInstance().trackEvent("AddDonor screen - VefifyOTP", "status=1", "otp verified");
                                }*/
                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(AddDonor.this, message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                MyApplication.getInstance().trackEvent("AddDonor screen - VefifyOTP", "status=0", message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //TastyToast.makeText(AddDonor.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("AddDonor screen - VefifyOTP", "mainjsonparsingexception", "get into some exception");
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
                        //TastyToast.makeText(AddDonor.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("AddDonor screen - VefifyOTP", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", str_input_otp);
                params.put("phoneNumber", mobile);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
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

    private void SentOTP() {
        bt_number_next.setBackgroundResource(R.drawable.bt_ash_bg);
        bt_number_next.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.AddDonor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONArray arr = new JSONArray(response);
                            JSONObject object = new JSONObject(response);
                            int success = object.getInt("status");
                            if (success == 1) {
                                //Global.saveSP(getApplicationContext(), "user_id", object.getString("userId"));
                                TastyToast.makeText(AddDonor.this, "Please check your mobile for OTP", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                                viewFlipLayout.showNext();

                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                SpannableString redSpannable = new SpannableString(text1);
                                redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textcolor99999)), 0, text1.length(), 0);
                                builder.append(redSpannable);

                                builder.append(" ");

                                SpannableString blackSpannable = new SpannableString(mobile);
                                blackSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), 0, mobile.length(), 0);
                                builder.append(blackSpannable);

                                builder.append(" ");

                                SpannableString blueSpannable = new SpannableString(text2);
                                blueSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textcolor99999)), 0, text2.length(), 0);
                                builder.append(blueSpannable);

                                tv_number_text.setText(builder.toString());

                            } else if (success == 2) {
                                TastyToast.makeText(AddDonor.this, object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                MyApplication.getInstance().trackEvent("AddDonor screen - SentOTP", "status=2", object.getString("message"));
                                finish();
                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(AddDonor.this, message, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                MyApplication.getInstance().trackEvent("AddDonor screen - SentOTP", "status=0", message);
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(AddDonor.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("AddDonor screen - SentOTP", "mainjsonparsingexception", "get into some exception");
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
                        //TastyToast.makeText(AddDonor.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("AddDonor screen - SentOTP", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNumber", mobile);
                params.put("countryCode", countryCode);
                params.put("bloodBankId", Global.getSP(getApplicationContext(), Global.USER_ID));
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.ADD_DONOR_RESEND_OTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            int success = object.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(AddDonor.this, "Please check your mobile for OTP", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

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

                            } else if (success == 2) {
                                TastyToast.makeText(AddDonor.this, object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                MyApplication.getInstance().trackEvent("AddDonor screen - SentOTP", "status=2", object.getString("message"));
                                finish();
                            } else {
                                String message = object.getString("message");
                                TastyToast.makeText(AddDonor.this, message, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                MyApplication.getInstance().trackEvent("SentOTP", "status=0", message);
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(AddDonor.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
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
                params.put("bloodBankId", Global.getSP(getApplicationContext(), Global.USER_ID));
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
        MyApplication.getInstance().trackScreenView("Add donor screen");
    }
}
