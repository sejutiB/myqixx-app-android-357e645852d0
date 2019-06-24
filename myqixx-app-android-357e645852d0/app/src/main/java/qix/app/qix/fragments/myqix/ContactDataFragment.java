package qix.app.qix.fragments.myqix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class ContactDataFragment extends Fragment {

    @BindView(R.id.contactEmailEditText)
    EditText emailEditText;

    @BindView(R.id.contactPhoneEditText)
    EditText phoneEditText;

    @OnClick(R.id.saveButton) void save() {
        updateUserData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_data, container, false);
        ButterKnife.bind(this, view);

        getUserData();

        return view;
    }

    private void getUserData(){
        AsyncRequest.getProfileData(getActivity(), new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if(response.isSuccessful()){
                    ProfileResponse p = response.body();
                    assert p != null;
                    emailEditText.setText(p.getEmail());
                    phoneEditText.setText(p.getPhoneNumber());
                }else{
                    Helpers.presentToast("Check your connection", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("Check your connection", Toast.LENGTH_SHORT);
            }
        });
    }

    private void updateUserData(){
        Helpers.presentSimpleDialog(getActivity(), getString(R.string.warning_alert_title), getString(R.string.personal_data_signuot_advise), "OK", "cancel", false, (dialogInterface, i) -> startUpdate(), (dialogInterface, i) -> dialogInterface.cancel());
    }

    private void startUpdate(){
        UpdateProfileRequest r = new UpdateProfileRequest();
        r.setEmail(emailEditText.getText().toString());
        r.setPhoneNumber(emailEditText.getText().toString());

        AsyncRequest.updateProfileData(getActivity(), r, new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if(response.isSuccessful()){
                    Helpers.presentToast("Data updated!", Toast.LENGTH_SHORT);
                    Objects.requireNonNull(getActivity()).finish();
                }else{
                    Helpers.presentToast("Errore! Inserisci dei dati validi", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("No Connection", Toast.LENGTH_SHORT);
            }
        });
    }
}
