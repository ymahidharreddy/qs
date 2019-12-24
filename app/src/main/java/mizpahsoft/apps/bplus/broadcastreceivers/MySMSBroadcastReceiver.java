package mizpahsoft.apps.bplus.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import mizpahsoft.apps.bplus.app.MyApplication;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        String otp = ((String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE)).substring(4, 10);
                        ((MyApplication) context.getApplicationContext()).getSmSRetrieverListener().onSMSReceived(otp);
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }
        } catch (Exception e) {

        }

    }
}