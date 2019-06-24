package qix.app.qix.helpers.interfaces;

import java.util.HashMap;

public interface SignupFlowInterface {

    void onFirstPageComplete(HashMap<String, String> data);

    void onSecondPageComplete(HashMap<String, String> data);

    void onSignupComplete(boolean success);
}
