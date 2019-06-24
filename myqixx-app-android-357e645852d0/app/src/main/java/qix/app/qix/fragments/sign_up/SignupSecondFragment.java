package qix.app.qix.fragments.sign_up;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.SignupFlowInterface;

import static android.app.Activity.RESULT_OK;
import static java.lang.String.format;

public class SignupSecondFragment extends Fragment implements Validator.ValidationListener, View.OnFocusChangeListener, SignupFlowInterface {

    @NotEmpty
    @BindView(R.id.nameEditText)
    @Order(1)
    @Pattern(regex = "[a-zA-Z\\s]+", messageResId = R.string.name_validation_error)
    TextInputEditText nameEditText;

    @NotEmpty
    @BindView(R.id.surnameEditText)
    @Order(2)
    @Pattern(regex = "[a-zA-Z\\s]+", messageResId = R.string.surname_validation_error)
    TextInputEditText surnameEditText;

    @BindView(R.id.countryCodePicker)
    CountryCodePicker firstCountryCodePicker;

    @BindView(R.id.countryCodePickerconfirm)
    CountryCodePicker firstCountryCodePickerconfirm;

    @NotEmpty
    @BindView(R.id.phoneNumberEditText)
    @Order(3)
    @Length(min = 6, message = "Invalid length")
    @Pattern(regex = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", messageResId = R.string.phone_validation_error)
    TextInputEditText phoneNumberEditText;

    @NotEmpty
    @BindView(R.id.phoneNumberEditTextconfirm)
    @Order(4)
    @Length(min = 6, message = "Invalid length")
    @Pattern(regex = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", messageResId = R.string.phone_validation_error)
    TextInputEditText phoneNumberEditTextconfirm;

   /* @BindView(R.id.addressEditText)
    @NotEmpty
    @Order(4)
    TextInputEditText addressEditText;

    @BindView(R.id.cityEditText)
    @NotEmpty
    @Order(5)
    TextInputEditText cityEditText;

    @BindView(R.id.postCodeEditText)
    @NotEmpty
    @Order(6)
    TextInputEditText postCodeNumberEditText;*/

    @BindView(R.id.birthDateEditText)
    @NotEmpty
    @Order(5)
   //@Pattern(regex = "(0[1-9]|1[0-9]|2[0-9]|3[0-1])\\/(0[1-9]|1[0-2])\\/((19|[2-9][0-9])\\d{2})")
    TextInputEditText birthDateEditText;

   /* @BindView(R.id.currencyEditText)
    @NotEmpty
    @Order(8)
    TextInputEditText currencyNumberEditText;*/

    @BindView(R.id.secondCountryCodePicker)
    CountryCodePicker countryCodePicker;

   /* @BindView(R.id.genderSpinner)
    Spinner genderSpinner;*/

    @BindView(R.id.nameTextInputLayout)
    TextInputLayout nameTextInputLayout;

    @BindView(R.id.surnameTextInputLayout)
    TextInputLayout surnameTextInputLayout;

    @BindView(R.id.phoneInpuTextLayout)
    TextInputLayout phoneInpuTextLayout;

    @BindView(R.id.phoneInpuTextLayoutconfirm)
    TextInputLayout phoneInpuTextLayoutconfirm;

  /*  @BindView(R.id.addressTextInputLayout)
    TextInputLayout addressInputLayout;

    @BindView(R.id.cityInputTextLayout)
    TextInputLayout cityInputLayout;

    @BindView(R.id.postCodeInputTextLayout)
    TextInputLayout postCodeNumberInputLayout;*/

    @BindView(R.id.birthDateInputTextLayout)
    TextInputLayout dayNumberInputLayout;

  /*  @BindView(R.id.currencyInputTextLayout)
    TextInputLayout currencyNumberInputLayout;

    @OnClick(R.id.mapButton)
    void mapPressed() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            progressBar.setVisibility(View.VISIBLE);
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            e.printStackTrace();
        }
    }*/

    @OnClick(R.id.secondNextButton)
    void nexPressed() {
        if (Helpers.isAdult(birthDateEditText.getText().toString(), "dd/MM/yyyy") && phoneNumberEditTextconfirm.getText().toString().equals(phoneNumberEditText.getText().toString())) {
            nextPressed = true;
            validator.validate();
        } else if (!Helpers.isAdult(birthDateEditText.getText().toString(), "dd/MM/yyyy")) {
            Helpers.presentToast("You should be adult!", Toast.LENGTH_SHORT);
        } else if (!phoneNumberEditTextconfirm.getText().toString().equals(phoneNumberEditText.getText().toString())) {
            Helpers.presentToast("Please check your phone number", Toast.LENGTH_SHORT);
        }

    }

    private List<TextInputLayout> inputLayouts;
    private Validator validator;
    private boolean nextPressed = false;
    private HashMap<String, String> firstPageData;
    private SignupFlowInterface signupFlow;
    private QixViewPager pager;
    private Calendar calendar;
    private GeoDataClient mGeoDataClient;
    private CurrencyPicker currencyPicker;
    private ProgressBar progressBar;
    private String gender = "Male";
    private Button genderMale, genderFemale, genderNeutral;
    private DatePickerDialog datepicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_second, container, false);
        ButterKnife.bind(this, view);
        genderMale = view.findViewById(R.id.buttonmale);
        genderFemale = view.findViewById(R.id.buttonfemale);
        genderNeutral = view.findViewById(R.id.buttonneutral);

        genderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
                genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_p));
                genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_a));
                genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_a));
            }
        });

        genderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Female";
                genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_a));
                genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_p));
                genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_a));
            }
        });

        genderNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Neutral";
                genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_a));
                genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_a));
                genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_p));
            }
        });


        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        calendar = Calendar.getInstance();

        nameEditText.setOnFocusChangeListener(this);
        surnameEditText.setOnFocusChangeListener(this);
        phoneNumberEditText.setOnFocusChangeListener(this);
        phoneNumberEditTextconfirm.setOnFocusChangeListener(this);
        // addressEditText.setOnFocusChangeListener(this);
        // cityEditText.setOnFocusChangeListener(this);
        //  postCodeNumberEditText.setOnFocusChangeListener(this);
        birthDateEditText.setOnFocusChangeListener(this);
        // currencyNumberEditText.setOnFocusChangeListener(this);

        inputLayouts = new ArrayList<>();
        inputLayouts.add(nameTextInputLayout);
        inputLayouts.add(surnameTextInputLayout);
        inputLayouts.add(phoneInpuTextLayout);
        inputLayouts.add(phoneInpuTextLayoutconfirm);
        // inputLayouts.add(addressInputLayout);
        // inputLayouts.add(cityInputLayout);
        //  inputLayouts.add(postCodeNumberInputLayout);
        inputLayouts.add(dayNumberInputLayout);
        //  inputLayouts.add(currencyNumberInputLayout);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        pager = getActivity().findViewById(R.id.signupViewPager);
        progressBar = getActivity().findViewById(R.id.signupProgress);

       birthDateEditText.setKeyListener(null);
        // currencyNumberEditText.setKeyListener(null);

      /*  currencyPicker = CurrencyPicker.newInstance("Select Currency");  // dialog title
        currencyPicker.setListener((name, code, symbol, flagDrawableResID) -> {
            currencyNumberEditText.setText("QIX" + code.toUpperCase());
            currencyPicker.dismiss();
        });

        currencyPicker.onCancel(new DialogInterface() {
            @Override
            public void cancel() {
                currencyNumberEditText.clearFocus();
            }

            @Override
            public void dismiss() {
                currencyNumberEditText.clearFocus();
            }
        });*/

       /* currencyNumberEditText.setOnFocusChangeListener((view12, hasFocus) -> {
            if (hasFocus) {
                currencyPicker.show(getActivity().getSupportFragmentManager(), "CURRENCY_PICKER");
            }
        });*/

      /*  DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateText();
        };*/

        //   final DatePickerDialog datePicker = new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // datePicker.setOnCancelListener(dialogInterface -> birthDateEditText.clearFocus());


        birthDateEditText.setOnFocusChangeListener((view13, hasFocus) -> {
            if (hasFocus) {

               final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                View dateview = inflater.inflate(R.layout.layout_datepicker_custom, null, false);
                DatePicker pick = dateview.findViewById(R.id.datepicker);

                datepicker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                                int day= pick.getDayOfMonth();
                                int month = pick.getMonth()+1;
                                int year = pick.getYear();
                                String m= format("%02d", month);
                                birthDateEditText.setText(day+"/"+m+"/"+year);
                            }
                        }, year, month, day);

                datepicker.setView(dateview);
                datepicker.show();

            }
        });

        return view;
    }


    private void updateDateText() {
        String myFormat = "dd/MM/yyyy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        Log.d("Signup", calendar.getTime() + "");
        birthDateEditText.setText(sdf.format(calendar.getTime()));
    }

    private String selectCurrency() {
        String Countrycode = countryCodePicker.getSelectedCountryCode();
        String currency;

        switch (Countrycode) {
            case "1":
                currency = "QIXUSD";
                break;
            case "7":
                currency = "QIXRUB";
                break;
            case "27":
                currency = "QIXZAR";
                break;
            case "52":
                currency = "QIXMXN";
                break;
            case "65":
                currency = "QIXSGD";
                break;
            case "234":
                currency = "QIXNGN";
                break;
            default:
                currency = "QIXEUR";

        }
        //Log.i("currency", currency);
        return currency;
    }

    @Override
    public void onValidationSucceeded() {
        if (nextPressed) {

            firstPageData.put("name", nameEditText.getText().toString());
            firstPageData.put("family_name", surnameEditText.getText().toString());
            firstPageData.put("phone_number", firstCountryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumberEditText.getText().toString());
            // firstPageData.put("address_street", addressEditText.getText().toString());
            // firstPageData.put("address_city", cityEditText.getText().toString());
            // firstPageData.put("address_postcode", postCodeNumberEditText.getText().toString());
            firstPageData.put("qixCurrency", selectCurrency());
            firstPageData.put("birthdate", birthDateEditText.getText().toString());
            firstPageData.put("address_country", countryCodePicker.getSelectedCountryName());
            firstPageData.put("gender", gender);
            firstPageData.put("locale", countryCodePicker.getSelectedCountryNameCode());

            pager.setCurrentItem(2);
            signupFlow.onSecondPageComplete(firstPageData);

            nextPressed = false;
        }
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
                TextInputLayout textInputLayout = (TextInputLayout) getActivity().findViewById(view.getId()).getParent().getParent();
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(message);
            } else {
                Helpers.presentToast(message, Toast.LENGTH_SHORT);
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

    @Override
    public void onFirstPageComplete(HashMap<String, String> data) {
        firstPageData = data;
    }

    @Override
    public void onSecondPageComplete(HashMap<String, String> data) {
    }

    @Override
    public void onSignupComplete(boolean success) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(Objects.requireNonNull(getContext()), data);
                Geocoder geocoder = new Geocoder(getContext());

                try {
                    Address a = geocoder.getFromLocationName((String) place.getAddress(), 1).get(0);
                    if (a != null) {
                        // addressEditText.setText(place.getName());
                        //  cityEditText.setText(a.getLocality());
                        countryCodePicker.setCountryForNameCode(a.getCountryCode());

                        // postCodeNumberEditText.setText(a.getPostalCode());
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);

                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.

                Log.i("Signup_second", status.getStatusMessage());
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
