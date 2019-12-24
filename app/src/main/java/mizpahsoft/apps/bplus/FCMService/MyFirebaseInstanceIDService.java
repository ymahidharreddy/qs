package mizpahsoft.apps.bplus.FCMService;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.app.MyApplication;

/**
 * Created by Mizpah on 11/11/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    SharedPreferences sp;
    SharedPreferences.Editor edt;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "veda Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(final String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();
        edt.putString("Token_regId", token);
        edt.commit();
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.UPDATE_FCM_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("UpdateFCMToken", "volleyerror", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("deviceId", android_id);
                params.put("fcm_token", token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}