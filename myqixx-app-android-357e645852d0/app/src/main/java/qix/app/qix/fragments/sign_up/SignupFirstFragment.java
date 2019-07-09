package qix.app.qix.fragments.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.SignupFlowInterface;
import qix.app.qix.models.EmailVerifiyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFirstFragment extends Fragment implements Validator.ValidationListener, View.OnFocusChangeListener {

    @NotEmpty
    @Order(1)
    @Email(messageResId = R.string.email_validation_error)
    private TextInputEditText emailEditText;

  /*  @NotEmpty
    @Order(2)
    @Pattern(regex = "[a-zA-Z\\s]+", messageResId = R.string.name_validation_error)
    private TextInputEditText nameEditText;

    @NotEmpty
    @Order(3)
    @Pattern(regex = "[a-zA-Z\\s]+", messageResId = R.string.surname_validation_error)
    private TextInputEditText surnameEditText;

    @NotEmpty
    @Order(4)
    @Pattern(regex = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", messageResId = R.string.phone_validation_error)
    private TextInputEditText phoneNumberEditText;*/

    private Button nextButton, cancelButton;
    private Validator validator;
    private QixViewPager pager;
    private boolean nextPressed = false;
    private Spinner phonePrefixSpinner;

    TextInputLayout emailTextInputLayout, nameTextInputLayout, surnameTextInputLayout, phoneNumberTextInputLayout;
    List<TextInputLayout> inputLayouts;
    private SignupFlowInterface signupFlow;
    private ProgressBar progressBar;
    private CountryCodePicker ccp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_first, container, false);
        emailEditText = view.findViewById(R.id.emailEditText);
       //* nameEditText = view.findViewById(R.id.nameEditText_y);
      //* surnameEditText = view.findViewById(R.id.surnameEditText);
      //*  phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
      //*  ccp = view.findViewById(R.id.countryCodePicker);

        emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);
      //*  nameTextInputLayout = view.findViewById(R.id.nameInputTextLayout_);
      //*  surnameTextInputLayout = view.findViewById(R.id.surnameInputTextLayout_);
       //* phoneNumberTextInputLayout = view.findViewById(R.id.phoneInpuTextLayout);

        inputLayouts = new ArrayList<>();
        inputLayouts.add(emailTextInputLayout);
       //* inputLayouts.add(nameTextInputLayout);
       //* inputLayouts.add(surnameTextInputLayout);
       //* inputLayouts.add(phoneNumberTextInputLayout);

        emailEditText.setOnFocusChangeListener(this);
       //* nameEditText.setOnFocusChangeListener(this);
        //*surnameEditText.setOnFocusChangeListener(this);
       //* phoneNumberEditText.setOnFocusChangeListener(this);

        nextButton = view.findViewById(R.id.signupNextButton);

        pager = getActivity().findViewById(R.id.signupViewPager);
        progressBar = getActivity().findViewById(R.id.signupProgress);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPressed = true;
                validator.validate();
            }
        });

        return view;
    }

    @Override
    public void onValidationSucceeded() {
        if (nextPressed) {
            progressBar.setVisibility(View.VISIBLE);
            AsyncRequest.verifyEmail(getActivity(), getData().get("email"), new Callback<EmailVerifiyResponse>() {
                @Override
                public void onResponse(@NonNull Call<EmailVerifiyResponse> call, @NonNull Response<EmailVerifiyResponse> response) {
                    if (response.isSuccessful()) {
                        EmailVerifiyResponse emailVerifiyResponse = response.body();
                        assert emailVerifiyResponse != null;
                        if (emailVerifiyResponse.isAvailable()) {
                            pager.setCurrentItem(1);
                            //pager.disableSwipe(true);
                            signupFlow.onFirstPageComplete(getData());
                        } else {
                            Helpers.presentToast("Esiste già un account con questa email!", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Helpers.presentToast("Esiste già un account con questa email!", Toast.LENGTH_SHORT);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<EmailVerifiyResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Helpers.presentToast("Esiste già un account con questa email!", Toast.LENGTH_SHORT);
                }
            });

            nextPressed = false;
        }
    }

    private HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", emailEditText.getText().toString());
       //* data.put("name", nameEditText.getText().toString());
       //* data.put("family_name", surnameEditText.getText().toString());
        //data.put("phonePrefix", .getText().toString());
       //* data.put("phone_number", ccp.getSelectedCountryCodeWithPlus() + phoneNumberEditText.getText().toString());

        return data;
    }

    private void clearErrors() {
        for (TextInputLayout layout : inputLayouts) {
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
                Log.d("FirstSignup", "YES");
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
        if (!hasFocus) {
            clearErrors();
            validator.validate();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            signupFlow = (SignupFlowInterface) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
