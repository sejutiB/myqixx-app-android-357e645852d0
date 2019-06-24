package qix.app.qix;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.username)
    AppCompatEditText username;

    @BindView(R.id.password)
    AppCompatEditText password;

    @BindView(R.id.loginProgressBar)
    ProgressBar loginSpinner;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Login");




        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        loginSpinner.setVisibility(View.GONE);

        if(Configuration.isAlreadyLogged()){
            username.setText(Configuration.getUsername());
            password.setText(Configuration.getPassword());
        }

        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);
        Button recoveryPasswordButton = findViewById(R.id.passwordRecoveryButton);

        recoveryPasswordButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);


        Intent activityIntent;

        if(!Helpers.getBooleanPreference(R.string.preference_is_first_run_key)){
            activityIntent = new Intent(this, IntroActivity.class);
            startActivity(activityIntent);
           //* Helpers.setPreference(R.string.preference_is_first_run_key, true);
        }else{
            if(Helpers.shouldRequestCode()){
                Helpers.requestUnlockScreen(this, Constants.LOCK_REQUEST_CODE);
            }else {
                if (Configuration.isLogged()){
                    activityIntent = new Intent(this, MainActivity.class);
                    startActivity(activityIntent);
                    new Handler().postDelayed(this::finish, 1000);
                }
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
        }/*else if(requestCode == Constants.INTRO_ACTIVITY_CODE){
            Helpers.startLoginActivity();
        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                login();
                break;
            case R.id.createAccountButton:
                Helpers.startNewActivity(SignupActivity.class);
                break;
            case R.id.passwordRecoveryButton:
                Helpers.startNewActivity(RecoveryPasswordActivity.class);
                break;
        }
    }

    private void performLogin(String username, String password) {
        AsyncRequest.login(this,username, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                if(response.isSuccessful()){
                    LoginResponse result = response.body();
                    assert result != null;
                    Helpers.writePreferences(result);
                    Helpers.startNewActivity(MainActivity.class);
                    new Handler().postDelayed(() -> finish(), 1000);
                }

            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                /*Helpers.startLoginActivity();
                finish();*/
            }
        });
    }

    private void performLogin() {
        this.performLogin(Helpers.getPreference(R.string.preference_username_key), Helpers.getPreference(R.string.preference_password_key));
    }

    private void login(){
        Boolean needverification= false;
        if(!username.getText().toString().isEmpty() || !password.getText().toString().isEmpty()) {
            loginSpinner.setVisibility(View.VISIBLE);
            loginSpinner.animate();

            AsyncRequest.login(this, username.getText().toString(), password.getText().toString(), new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if(response.isSuccessful()) {
                        LoginResponse loginData = response.body();

                        assert loginData != null;
                        Helpers.writePreferences(loginData);
                        Helpers.setPreference(R.string.preference_username_key, username.getText().toString());
                        Helpers.setPreference(R.string.preference_password_key, password.getText().toString());

                        //Log.d(TAG, "Logging in with " + username + ", " + password);

                        loginSpinner.setVisibility(View.GONE);

                        Helpers.startNewActivity(MainActivity.class);
                        new Handler().postDelayed(() -> finish(), 1000);
                    }else{
                        loginSpinner.setVisibility(View.GONE);

                         //*   Helpers.presentToast(getResources().getString(R.string.invalid_value_error), Toast.LENGTH_SHORT);
                       //* Helpers.presentToast(" "+ response.code(), Toast.LENGTH_SHORT);
                        if(response.code()==404){
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setView(R.layout.layout_alertdialog)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton("Ok", null)
                                    .show();
                        }
                        if(response.code()==406){
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setView(R.layout.layout_resend_verification_link)

                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent= new Intent(LoginActivity.this, VerificationLinkActivity.class);
                                            startActivity(intent);
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton("No", null)
                                    .show();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Log.d("Retrofit","Getting response from server : "+t.getLocalizedMessage());
                    loginSpinner.setVisibility(View.GONE);

                    Helpers.presentToast(getString(R.string.no_connectio_error_message), Toast.LENGTH_SHORT);
                }
            });

        }else{
            Helpers.presentToast(getResources().getString(R.string.empty_field_error), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
