package mizpahsoft.apps.bplus.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Mizpah-DEV on 07-Nov-16.
 */

public class ChangePasswordBD extends Activity {
    EditText et_current_pass,et_New_Password,et_confirm_pass;
    Button bt_pass_submit;
    String current_pass, new_pass, confirm_pass, userId;
    public static final String CURRENT_PASS = "currentPassword";
    public static final String NEW_PASS = "newPassword";
    public static final String CONFIRM_PASS = "ConfirmNewPassword";
    public static final String USERID = "userId";
    SharedPreferences sp;
    SharedPreferences sp1;
    SharedPreferences.Editor et1;
    TextView tv_cp_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);

        sp = getSharedPreferences("loginprefs", 0);
        userId = sp.getString("user_id", " ");


        if (userId.equals("")||userId.equals(null)||userId.equals("null"))
        {
            MyApplication.getInstance().trackEvent("shared preferences","userid","getting null/empty");
        }

        sp1 = getSharedPreferences("rememberprefs", 0);
        et1 = sp1.edit();

        et_current_pass=(EditText)findViewById(R.id.et_current_pass);
        et_current_pass.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_New_Password=(EditText)findViewById(R.id.et_New_Password);
        et_New_Password.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_confirm_pass=(EditText)findViewById(R.id.et_confirm_pass);
        et_confirm_pass.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        bt_pass_submit=(Button)findViewById(R.id.bt_pass_submit);
        bt_pass_submit.setTypeface(Global.setFont(this,Global.LIGHTFONT));
        bt_pass_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_pass = et_current_pass.getText().toString().trim();
                new_pass = et_New_Password.getText().toString().trim();
                confirm_pass = et_confirm_pass.getText().toString().trim();
                if (current_pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter Current Password", Toast.LENGTH_SHORT).show();
                } else if (new_pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter New Password", Toast.LENGTH_SHORT).show();
                } else if (new_pass.length() < 6) {
                    Toast.makeText(getApplicationContext(), "New Password length should be greater than 6 characters", Toast.LENGTH_SHORT).show();
                } else if (new_pass.length() > 18) {
                    Toast.makeText(getApplicationContext(), "New Password length should be less than 18 characters", Toast.LENGTH_SHORT).show();
                } else if (confirm_pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (!confirm_pass.equals(new_pass)) {
                    Toast.makeText(getApplicationContext(), "New Password and Confirm Password should be same", Toast.LENGTH_SHORT).show();
                } else {
                    if (isNetworkAvailable()) {
                        Changepassword();
                        MyApplication.getInstance().trackEvent("accessing network","tried changing password","called changepassword method");
                    } else {
                        Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        MyApplication.getInstance().trackEvent("network condition","no internet","cannot proceed further");
                    }
                }
            }

        });
    }
    public void Changepassword() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.CHangepwd_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int status = jsonObject.getInt("status");
                            if (status == 1) {
                                SharedPreferences.Editor edt = sp.edit();
                                edt.remove("logincheck").commit();
                                Toast.makeText(getApplicationContext(), "Password updated successfully. Please sign in with new password", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                               // finish();
                                if (sp1.getString("check", " ").equals("1")) {
                                    et1.remove("pass");
                                    et1.commit();
                                    et1.putString("pass", new_pass);
                                    et1.commit();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("changepassword","status=0",jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("changepassword","mainjsonparsingexception","get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("changepassword","volleyerror","get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USERID, userId);
                params.put(CURRENT_PASS, current_pass);
                params.put(NEW_PASS, new_pass);
                params.put(CONFIRM_PASS, confirm_pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("ChangePassword");
    }
}
