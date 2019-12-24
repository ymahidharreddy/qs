package mizpahsoft.apps.bplus.broadcastreceivers;

public interface SmSRetrieverCallBacks {
    void onSMSReceived(String otp);
    void TaskOnSuccess();
    void TaskOnFailure();
}
