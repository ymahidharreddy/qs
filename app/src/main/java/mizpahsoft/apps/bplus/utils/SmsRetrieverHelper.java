package mizpahsoft.apps.bplus.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.broadcastreceivers.SmSRetrieverCallBacks;

public class SmsRetrieverHelper {


    public void startClient(final Activity context, final SmSRetrieverCallBacks smSRetrieverCallBacks) {
        SmsRetrieverClient client = SmsRetriever.getClient(context);

        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(smSRetrieverCallBacks!=null)
                {
                    smSRetrieverCallBacks.TaskOnSuccess();
                }
                // successfully started an SMS Retriever for one SMS message
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(smSRetrieverCallBacks!=null)
                {
                    smSRetrieverCallBacks.TaskOnSuccess();
                }
            }
        });
    }

}




