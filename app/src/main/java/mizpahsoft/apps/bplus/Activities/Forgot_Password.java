package mizpahsoft.apps.bplus.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;


/*** Created by Mizpah_DEV on 10/15/2016. */

public class Forgot_Password extends Activity {
    EditText et_phone, et_otp, et_new_pass, et_confirm_pass;
    Button bt_continue, bt_otp_submit;
    String email, otp, new_pass, confirm_pass;
    LinearLayout ll_fp_layout_phone, ll_fp_layout_otp;
    public static final String EMAIL = "phoneNumber";
    public static final String OTP = "otp";
    public static final String NEW_PASS = "password";
    public static final String CONFIRM_PASS = "confirmPassword";
    CoordinatorLayout coordinatorLayout;
    TextView textView, textView1;
    SharedPreferences sp;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        ll_fp_layout_phone = (LinearLayout) findViewById(R.id.ll_fp_layout_phone);
        ll_fp_layout_otp = (LinearLayout) findViewById(R.id.ll_fp_layout_otp);
        ll_fp_layout_phone.setVisibility(View.VISIBLE);
        ll_fp_layout_otp.setVisibility(View.GONE);
        et_phone = (EditText) findViewById(R.id.et_phone);

        et_otp = (EditText) findViewById(R.id.et_otp);
        et_otp.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_new_pass = (EditText) findViewById(R.id.et_new_pass);
        et_new_pass.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        et_confirm_pass = (EditText) findViewById(R.id.et_confirm_pass);
        et_confirm_pass.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_continue = (Button) findViewById(R.id.bt_continue);
        bt_continue.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        bt_otp_submit = (Button) findViewById(R.id.bt_otp_submit);
        bt_otp_submit.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        textView = (TextView) findViewById(R.id.textView);
        textView.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_phone.getText().toString().trim();
                if (email.equals("")) {
                    Toast.makeText(Forgot_Password.this, "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (email.length() < 10) {
                    Toast.makeText(Forgot_Password.this, "Please enter valid Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNetworkAvailable()) {
                        SendNumber();
                    } else {
                        Toast.makeText(Forgot_Password.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
                MyApplication.getInstance().trackEvent("bt_continue", "onClick", "clicked and specified action done");
            }
        });
        bt_otp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = et_otp.getText().toString().trim();
                new_pass = et_new_pass.getText().toString().trim();
                confirm_pass = et_confirm_pass.getText().toString().trim();
                if (otp.equals("")) {
                    Toast.makeText(Forgot_Password.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else if (new_pass.equals("")) {
                    Toast.makeText(Forgot_Password.this, "Please enter New Password", Toast.LENGTH_SHORT).show();
                } else if (new_pass.length() < 6) {
                    Toast.makeText(Forgot_Password.this, "New Password length should be greater than 6 characters", Toast.LENGTH_SHORT).show();
                } else if (new_pass.length() > 18) {
                    Toast.makeText(Forgot_Password.this, "New Password length should be less than 18 characters", Toast.LENGTH_SHORT).show();
                } else if (confirm_pass.equals("")) {
                    Toast.makeText(Forgot_Password.this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!confirm_pass.equals(new_pass)) {
                    Toast.makeText(Forgot_Password.this, "New Password and Confirm Password should be same", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNetworkAvailable()) {
                        SendPassword();
                    } else {
                        Toast.makeText(Forgot_Password.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
                MyApplication.getInstance().trackEvent("bt_otp_submit", "onClick", "clicked and specified action done");
            }
        });
    }

    public void SendNumber() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.sendno_forgotpass,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int success = jsonObject.getInt("status");
                            if (success == 1) {
                                Toast.makeText(Forgot_Password.this, "Please check your Inbox for OTP", Toast.LENGTH_SHORT).show();
                                ll_fp_layout_phone.setVisibility(View.GONE);
                                ll_fp_layout_otp.setVisibility(View.VISIBLE);
                                MyApplication.getInstance().trackEvent("SendNumber", "status=1", "numbersentrequesttoserver");
                            } else {
                                Toast.makeText(Forgot_Password.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("SendNumber", "status=0", jsonObject.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SendNumber", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("SendNumber", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(EMAIL, email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void SendPassword() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.sendpass_forgotpass,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int success = jsonObject.getInt("status");
                            if (success == 1) {
                                Toast.makeText(Forgot_Password.this, "Password changed successfully.Please Sign In with new password", Toast.LENGTH_SHORT).show();
                                finish();
                                MyApplication.getInstance().trackEvent("SendPassword", "status=1", "password changed successfully");
                            } else {
                                String msg = jsonObject.getString("message");
                                Toast.makeText(Forgot_Password.this, "" + msg, Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("SendPassword", "status=0", jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SendPassword", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("SendPassword", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(OTP, otp);
                params.put(NEW_PASS, new_pass);
                params.put(CONFIRM_PASS, confirm_pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean isValidEmail(String target) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String emailPattern_2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
        // onClick of button perform this simplest code.
        if (target.matches(emailPattern)) {
            return false;
        } else if (target.matches(emailPattern_2)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        if (ll_fp_layout_phone.isShown()) {
            finish();
            MyApplication.getInstance().trackEvent("onBackPressed", "onclick", "finish");
        } else {
            ll_fp_layout_otp.setVisibility(View.INVISIBLE);
            ll_fp_layout_phone.setVisibility(View.VISIBLE);
            MyApplication.getInstance().trackEvent("onBackPressed", "onclick", "otp-phone navigation");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("ForgotPassword");
    }
}
