package mizpahsoft.apps.bplus.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

/**
 * Created by Mizpah-DEV on 08-Nov-16.
 */

public class SuggestSaviour extends Activity {
    EditText et_ss_mobile, et_ss_email;
    String str_mobiles,str_emails,message,sub_email;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    int i11,i22;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
   TextView textView13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Spread News");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textView13 = (TextView) findViewById(R.id.textView13);
        textView13.setTypeface(Global.setFont(this,Global.REGULARFONT));

        et_ss_mobile = (EditText) findViewById(R.id.et_ss_mobile);
        et_ss_mobile.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_ss_email = (EditText) findViewById(R.id.et_ss_email);
        et_ss_email.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        et_ss_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                str_mobiles = et_ss_mobile.getText().toString().trim();
                i11 = str_mobiles.length();
                if(str_mobiles.length() == 10){

                    str_mobiles = str_mobiles+",";
                    et_ss_mobile.setText(str_mobiles);
                    et_ss_mobile.setSelection(et_ss_mobile.getText().length());
                    i22 = str_mobiles.length();;

                }else if(i11 == i22+10){

                    str_mobiles = str_mobiles+",";
                    et_ss_mobile.setText(str_mobiles);
                    i22 = str_mobiles.length();
                    et_ss_mobile.setSelection(et_ss_mobile.getText().length());
                }
            }
        });
        /*et_ss_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                sub_email = "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                str_emails = et_ss_email.getText().toString();
                if(str_emails.matches(emailPattern)){

                    str_emails = str_emails+",";
                    et_ss_email.setText(str_emails);
                    et_ss_email.setSelection(et_ss_mobile.getText().length());
                    sub_email = et_ss_email.getText().toString();//.replace(sub_email,"");

                }else if(!sub_email.equals("") && sub_email.matches(emailPattern)){

                    str_emails = str_emails+",";
                    et_ss_email.setText(str_emails);
                    et_ss_email.setSelection(et_ss_mobile.getText().length());
                    sub_email = et_ss_email.getText().toString();
                }else{
                    Log.d(TAG, "afterTextChanged: ");
                }
            }
        });*/
    }

    public void invite_frnd_Button_Click (View view){

        str_mobiles = et_ss_mobile.getText().toString();
        str_emails = et_ss_email.getText().toString();

        if(str_mobiles.equals("") || str_mobiles.length() < 10){
            TastyToast.makeText(SuggestSaviour.this, "Please enter at least one mobile number", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }else if (str_emails.equals("")){
            TastyToast.makeText(SuggestSaviour.this, "Please enter at least one email id", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }else{
            progressDialog = ProgressDialog.show(SuggestSaviour.this, "", "Please wait..");
            sendToDataBase();
            MyApplication.getInstance().trackEvent("invite_frnd_Button_Click","method","calling sendToDataBase after all tolerances");
        }
    }


 /*   public boolean isValidEmail(String target) {
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
    }*/

    public void sendToDataBase() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.suggestdonor,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int success = jsonObject.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(SuggestSaviour.this, "Thank you for suggesting a saviour. We will intimate your friends", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                finish();
                                MyApplication.getInstance().trackEvent("sendToDataBase","status=1","data updated successfully");
                            } else {
                              message = jsonObject.getString("message");
                                TastyToast.makeText(SuggestSaviour.this, message, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                MyApplication.getInstance().trackEvent("sendToDataBase","status=0",message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("sendToDataBase","mainjsonparsingexception","get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("sendToDataBase","volley error listener","get into some exception");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("SuggestingUserId", sp.getString("user_id",""));
                params.put("phoneNumber", str_mobiles);
                params.put("emailAddress", str_emails);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("SuggestSaviour");
    }
}
