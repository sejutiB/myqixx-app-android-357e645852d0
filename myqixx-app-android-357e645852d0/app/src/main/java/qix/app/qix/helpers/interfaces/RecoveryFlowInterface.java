package qix.app.qix.helpers.interfaces;

public interface RecoveryFlowInterface {
    void onEmailReceived(String email);
    void onRecoveryCompleted(boolean success);
}
