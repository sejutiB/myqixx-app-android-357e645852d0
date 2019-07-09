package qix.app.qix.fragments.myqix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.MessageResponse;
import qix.app.qix.models.ProfileResponse;
import qix.app.qix.models.UpdateProfileRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
PersonalDataFragment extends Fragment {

    private Button genderMale, genderFemale, genderNeutral;
    private String gender = "Male";

    @NotEmpty
    @BindView(R.id.personalNameEditText)
    EditText nameEditText;

    @NotEmpty
    @BindView(R.id.personalSurnameEditText)
    EditText surnameEditText;


    @BindView(R.id.personalBirthdateEditText)
    TextView birthdateEditText;

    @BindView(R.id.personalCountryEditText)
    TextView countryText;

    @BindView(R.id.contactEmailEditText)
    TextView emailEditText;

    @BindView(R.id.contactPhoneEditText)
    TextView phoneEditText;

    @OnClick(R.id.personalSaveButton)
    void save() {
        updateUserData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);
        ButterKnife.bind(this, view);

        genderMale = view.findViewById(R.id.buttonmalePersonal);
        genderFemale = view.findViewById(R.id.buttonfemalePersonal);
        genderNeutral = view.findViewById(R.id.buttonneutralPersonal);

        genderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "M";
                genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_p));
                genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_a));
                genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_a));
            }
        });

        genderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "F";
                genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_a));
                genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_p));
                genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_a));
            }
        });

        genderNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "N";
                genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_a));
                genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_a));
                genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_p));
            }
        });

        getUserData();

        return view;
    }

    private void getUserData() {
        AsyncRequest.getProfileData(getActivity(), new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse p = response.body();
                    assert p != null;
                    nameEditText.setText(p.getName());
                    surnameEditText.setText(p.getFamilyName());
                    //* genderEditText.setText(p.getGender());
                    birthdateEditText.setText(p.getBirthdate());
                    countryText.setText(p.getLocale());
                    emailEditText.setText(p.getEmail());
                    phoneEditText.setText(p.getPhoneNumber());
                    switch (p.getGender()) {
                        case "M":
                            genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_p));
                            genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_a));
                            genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_a));
                            break;
                        case "F":
                            genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_a));
                            genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_p));
                            genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_a));
                            break;
                        case "N":
                            genderMale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_male_a));
                            genderFemale.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_female_a));
                            genderNeutral.setBackground(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.button_neutral_p));
                            break;

                    }

                } else {
                    Helpers.presentToast("Check your connection", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("Check your connection", Toast.LENGTH_SHORT);
            }
        });
    }

    private void updateUserData() {
        Helpers.presentSimpleDialog(getActivity(), getString(R.string.warning_alert_title), getString(R.string.personal_data_signuot_advise), "OK", "cancel", false, (dialogInterface, i) -> startUpdate(), (dialogInterface, i) -> dialogInterface.cancel());

    }

    private void startUpdate() {
        UpdateProfileRequest r = new UpdateProfileRequest();
        r.setName(nameEditText.getText().toString());
        r.setFamilyName(surnameEditText.getText().toString());
        r.setGender(gender);
        r.setBirthdate(birthdateEditText.getText().toString());
        r.setLocale(countryText.getText().toString());
        r.setEmail(emailEditText.getText().toString());
        r.setPhoneNumber(phoneEditText.getText().toString());

        AsyncRequest.updateProfileData(getActivity(), r, new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Helpers.presentToast("Data updated!", Toast.LENGTH_SHORT);
                    Objects.requireNonNull(getActivity()).finish();
                } else {
                    Helpers.presentToast("Error! Please insert valid data", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("No Connection", Toast.LENGTH_SHORT);
            }
        });
    }

}
