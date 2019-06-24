package qix.app.qix.fragments.recovery_password;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.RecoveryFlowInterface;
import qix.app.qix.models.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecoverySecondFragment extends Fragment implements Validator.ValidationListener, RecoveryFlowInterface, View.OnFocusChangeListener {


    @NotEmpty
    @Order(1)
    @Length(min = 6, max = 6, messageResId = R.string.confirmation_code_error_message)
    private TextInputEditText confirmationCode;

    @NotEmpty
    @Order(2)
    @Password(scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, messageResId = R.string.signup_password_error)
    private TextInputEditText password;

    @NotEmpty
    @Order(3)
    @ConfirmPassword(messageResId = R.string.confirm_password_error_message)
    private TextInputEditText repassword;

    private String userEmail;
    private Validator validator;
    private QixViewPager pager;
    private boolean recoveryButtonPressed = false;
    private List<TextInputLayout> inputLayouts;
    private ProgressBar progressBar;
    private RecoveryFlowInterface recoveryFlow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recovery_second, container, false);

        password = view.findViewById(R.id.passwordEditText);
        repassword = view.findViewById(R.id.repasswordEditText);
        confirmationCode = view.findViewById(R.id.confirmationCodeEditText);

        password.setOnFocusChangeListener(this);
        repassword.setOnFocusChangeListener(this);
        confirmationCode.setOnFocusChangeListener(this);

        TextInputLayout passwordTextInputLayout = view.findViewById(R.id.passwordTextInputLayout);
        TextInputLayout repasswordTextInputLayout = view.findViewById(R.id.repasswordTextInputLayout);
        TextInputLayout confirmationCodeTextInputLayout = view.findViewById(R.id.confirmationCodeTextInputLayout);

        inputLayouts = new ArrayList<>();
        inputLayouts.add(confirmationCodeTextInputLayout);
        inputLayouts.add(passwordTextInputLayout);
        inputLayouts.add(repasswordTextInputLayout);

        Button recoveryButton = view.findViewById(R.id.signupButton);
        progressBar = getActivity().findViewById(R.id.signupProgress);

        pager = getActivity().findViewById(R.id.signupViewPager);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);

        recoveryButton.setOnClickListener(view1 -> {
            recoveryButtonPressed = true;
            validator.validate();
        });

        return view;
    }

    @Override
    public void onValidationSucceeded() {
        if(recoveryButtonPressed){
            progressBar.setVisibility(View.VISIBLE);
            AsyncRequest.changePassword(getActivity(), userEmail, password.getText().toString(), confirmationCode.getText().toString(), new Callback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                    if(response.isSuccessful()){
                        MessageResponse m = response.body();
                        Log.d("third", m.getMessage());
                        recoveryFlow.onRecoveryCompleted(true);
                    }else{
                        try {
                            JSONObject errorBody = new JSONObject(response.errorBody().toString());
                            String error = errorBody.getString("errorMessage");
                            switch (error){
                                case "CodeMismatchException":
                                    Helpers.presentToast("Il codice inserito è errato", Toast.LENGTH_LONG);
                                    break;
                                case "ExpiredCodeException":
                                    Helpers.presentToast("Sembra ci sia stato un errore", Toast.LENGTH_LONG);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        recoveryFlow.onRecoveryCompleted(false);
                    }

                    progressBar.setVisibility(View.GONE);
                    pager.setCurrentItem(Constants.QIXSIGNUP_FOURTH_PAGE_INDEX);
                }

                @Override
                public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                    Log.d("third","Errore "+t.getLocalizedMessage());
                    recoveryFlow.onRecoveryCompleted(false);
                    pager.setCurrentItem(Constants.QIXSIGNUP_FOURTH_PAGE_INDEX);
                }
            });
            recoveryButtonPressed = false;
        }
    }

    private void clearErrors(){
        for(TextInputLayout layout : inputLayouts){
            layout.setErrorEnabled(false);
            layout.setError(null);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        clearErrors();

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            if (getActivity().findViewById(view.getId()) instanceof TextInputEditText) {
                TextInputLayout textInputLayout = (TextInputLayout) getActivity().findViewById(view.getId()).getParent().getParent();
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            recoveryFlow = (RecoveryFlowInterface)context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onEmailReceived(String email) {
        this.userEmail = email;
    }

    @Override
    public void onRecoveryCompleted(boolean success) {}

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            clearErrors();
            validator.validate();
        }
    }
}