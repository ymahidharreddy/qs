package mizpahsoft.apps.bplus.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.utils.AppSignatureHelper;

public class SplashScreen extends Activity {

    SharedPreferences sp;
    ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AppSignatureHelper(this).getAppSignatures();
        // hash code is UjbCzTb2j2S
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        sp = getSharedPreferences("loginprefs", 0);
        SharedPreferences.Editor edt = sp.edit();
        edt.putString("deviceId", android_id);
        edt.apply();

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mizpahsoft.com/"));
                startActivity(browserIntent);
            }
        });

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                sp = getSharedPreferences("loginprefs", 0);
                SharedPreferences.Editor edt = sp.edit();
                String logincheck = sp.getString("logincheck", "");

                if (logincheck.equals("1")) {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, BeforeLoginActivity.class);
                    startActivity(i);
                    finish();
                }
                // close this activity
                finish();
            }
        }, 2000);
    }
}
