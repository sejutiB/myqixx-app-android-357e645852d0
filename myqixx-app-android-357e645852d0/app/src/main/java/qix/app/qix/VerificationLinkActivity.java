package qix.app.qix;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.LoginResponse;
import qix.app.qix.models.VerificationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerificationLinkActivity extends AppCompatActivity {
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_link);
        email = findViewById(R.id.emailLink);


    }

    public void resendLink(View view) {


        AsyncRequest.resendVerifyLink(this, email.getText().toString(), new Callback<VerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<VerificationResponse> call, @NonNull Response<VerificationResponse> response) {
                if(response.isSuccessful()) {
                    VerificationResponse verifyresponse = response.body();
                    //Helpers.presentToast("success: "+ response.code() + response.message(), Toast.LENGTH_SHORT);
                    new AlertDialog.Builder(VerificationLinkActivity.this)
                            .setView(R.layout.layout_confrim_verification)

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new Handler().postDelayed(() -> finish(), 1000);
                                }
                            })
                            .show();
                   //* new Handler().postDelayed(() -> finish(), 1000);
                }else{

                    //*   Helpers.presentToast(getResources().getString(R.string.invalid_value_error), Toast.LENGTH_SHORT);
                    Helpers.presentToast(" Something went wrong! Try again ", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VerificationResponse> call, @NonNull Throwable t) {
                Log.d("Retrofit","Getting response from server : "+t.getLocalizedMessage());
              //  loginSpinner.setVisibility(View.GONE);

                Helpers.presentToast(getString(R.string.no_connectio_error_message), Toast.LENGTH_SHORT);
            }
        });
    }
}
