package qix.app.qix.fragments.sign_up;

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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.SignupFlowInterface;
import qix.app.qix.models.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupThirdFragment extends Fragment implements Validator.ValidationListener, SignupFlowInterface, View.OnFocusChangeListener {

    @NotEmpty
    @Order(1)
    @Password(scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, messageResId = R.string.signup_password_error)
    private TextInputEditText password;

    @NotEmpty
    @Order(2)
    @ConfirmPassword(messageResId = R.string.confirm_password_error_message)
    private TextInputEditText repassword;

    private Validator validator;
    private QixViewPager pager;
    private boolean signupButtonPressed = false;
    private HashMap<String, String> secondPageData;
    private List<TextInputLayout> inputLayouts;
    private ProgressBar progressBar;
    private SignupFlowInterface signupFlow;
    private Button signupButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_third, container, false);

        password = view.findViewById(R.id.passwordEditText);
        repassword = view.findViewById(R.id.repasswordEditText);

        password.setOnFocusChangeListener(this);
        repassword.setOnFocusChangeListener(this);

        TextInputLayout passwordTextInputLayout = view.findViewById(R.id.passwordTextInputLayout);
        TextInputLayout repasswordTextInputLayout = view.findViewById(R.id.repasswordTextInputLayout);

        inputLayouts = new ArrayList<>();
        inputLayouts.add(passwordTextInputLayout);
        inputLayouts.add(repasswordTextInputLayout);

        signupButton = view.findViewById(R.id.signupButton);
        progressBar = getActivity().findViewById(R.id.signupProgress);

        pager = getActivity().findViewById(R.id.signupViewPager);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupButtonPressed = true;
                validator.validate();
            }
        });
        return view;
    }

    @Override
    public void onValidationSucceeded() {
        if(signupButtonPressed){
            signupButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            secondPageData.put("password", password.getText().toString());
            AsyncRequest.signUp(getActivity(), secondPageData, new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if(response.isSuccessful()){
                        MessageResponse m = response.body();
                        Log.d("third", m.getMessage());
                        signupFlow.onSignupComplete(true);
                    }else{
                        Log.d("third","Errore");
                        signupFlow.onSignupComplete(false);

                    }
                    Log.d("third", "JSON:"+(new JSONObject(secondPageData).toString()));
                    progressBar.setVisibility(View.GONE);
                    pager.setCurrentItem(Constants.QIXSIGNUP_FOURTH_PAGE_INDEX);
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Log.d("third","Errore "+t.getLocalizedMessage());
                    signupFlow.onSignupComplete(false);
                    pager.setCurrentItem(Constants.QIXSIGNUP_FOURTH_PAGE_INDEX);
                }
            });
            signupButtonPressed = false;
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
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            signupFlow = (SignupFlowInterface)context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFirstPageComplete(HashMap<String, String> data) {
        //never called
    }

    @Override
    public void onSecondPageComplete(HashMap<String, String> data) {
        secondPageData = data;
    }

    @Override
    public void onSignupComplete(boolean success) {}

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            clearErrors();
            validator.validate();
        }
    }
}
