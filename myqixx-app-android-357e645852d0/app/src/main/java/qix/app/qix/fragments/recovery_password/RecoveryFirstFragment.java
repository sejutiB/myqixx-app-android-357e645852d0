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
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.RecoveryFlowInterface;
import qix.app.qix.models.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoveryFirstFragment extends Fragment implements Validator.ValidationListener, View.OnFocusChangeListener {

    @NotEmpty
    @Order(1)
    @Email(messageResId = R.string.email_validation_error)
    private TextInputEditText emailEditText;

    private Validator validator;
    private QixViewPager pager;
    private boolean nextPressed = false;

    private TextInputLayout emailTextInputLayout;

    private List<TextInputLayout> inputLayouts;
    private RecoveryFlowInterface recoveryFlow;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recovery_first, container, false);
        emailEditText = view.findViewById(R.id.emailEditText);
        emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);

        inputLayouts = new ArrayList<>();
        inputLayouts.add(emailTextInputLayout);

        emailEditText.setOnFocusChangeListener(this);
        Button nextButton = view.findViewById(R.id.signupNextButton);

        pager = getActivity().findViewById(R.id.signupViewPager);
        progressBar = getActivity().findViewById(R.id.signupProgress);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);

        nextButton.setOnClickListener(view1 -> {
            nextPressed = true;
            validator.validate();
        });

        return view;
    }

    @Override
    public void onValidationSucceeded() {
        if(nextPressed){
            progressBar.setVisibility(View.VISIBLE);
            AsyncRequest.getRecoveryPasswordCode(getActivity(), emailEditText.getText().toString(), new Callback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                    if(response.isSuccessful()){
                        pager.setCurrentItem(1);
                        recoveryFlow.onEmailReceived(emailEditText.getText().toString());
                    }else{
                        Helpers.presentToast(getString(R.string.recovery_password_invalid_email_message), Toast.LENGTH_SHORT);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Recovery", t.getLocalizedMessage());
                    Helpers.presentToast(getString(R.string.no_connectio_error_message), Toast.LENGTH_SHORT);
                }
            });

            nextPressed = false;
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
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus){
            clearErrors();
            validator.validate();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            recoveryFlow = (RecoveryFlowInterface) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
