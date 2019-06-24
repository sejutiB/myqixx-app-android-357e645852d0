package qix.app.qix;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;

        if(!Helpers.getBooleanPreference(R.string.preference_is_first_run_key)){
            activityIntent = new Intent(this, IntroActivity.class);
            startActivityForResult(activityIntent, Constants.INTRO_ACTIVITY_CODE);
            Helpers.setPreference(R.string.preference_is_first_run_key, true);
        }else{
            if(Helpers.shouldRequestCode()){
                Helpers.requestUnlockScreen(this, Constants.LOCK_REQUEST_CODE);
            }else {
                if (!Configuration.isLogged()) {
                    activityIntent = new Intent(this, LoginActivity.class);
                } else {
                    activityIntent = new Intent(this, MainActivity.class);
                }
                startActivity(activityIntent);
                new Handler().postDelayed(this::finish, 500);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.LOCK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                performLogin();
            } else {
                Helpers.presentToast("Accesso negato.", Toast.LENGTH_SHORT);
                Helpers.startLoginActivity();
            }
            finish();
        }else if(requestCode == Constants.INTRO_ACTIVITY_CODE){
            Helpers.startLoginActivity();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }

    private void performLogin() {
        AsyncRequest.login(this, Helpers.getPreference(R.string.preference_username_key), Helpers.getPreference(R.string.preference_password_key), new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                if(response.isSuccessful()){
                    LoginResponse result = response.body();
                    assert result != null;
                    Helpers.writePreferences(result);
                    Helpers.startNewActivity(MainActivity.class);
                }else{
                    Helpers.startLoginActivity();
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Helpers.startLoginActivity();
                finish();
            }
        });
    }
}
