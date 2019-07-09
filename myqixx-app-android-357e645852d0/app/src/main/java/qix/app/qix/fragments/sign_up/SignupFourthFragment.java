package qix.app.qix.fragments.sign_up;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.SignupFlowInterface;
import qix.app.qix.models.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupFourthFragment extends Fragment implements SignupFlowInterface {

    private ImageView resultImageView;
    private TextView signupTitle;
    private TextView signupmessage;
    private TextView messageTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_fourth, container, false);

      //  resultImageView = view.findViewById(R.id.signupImageView);
        signupTitle = view.findViewById(R.id.SignupTitle);
        signupmessage = view.findViewById(R.id.SignupMessage);
        messageTextView = view.findViewById(R.id.signupMessageTextView);

        Button closeButton = view.findViewById(R.id.signupCloseButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }


    @Override
    public void onFirstPageComplete(HashMap<String, String> data) {}

    @Override
    public void onSecondPageComplete(HashMap<String, String> data) {}

    @Override
    public void onSignupComplete(boolean success) {
      //  resultImageView.setImageResource(success ? R.drawable.qix_app_icon_new : R.drawable.sad);
        signupTitle.setText(success? "ACCOUNT CREATED":" \u26A0 Unable to create account");
       // signupmessage.setText(success? "Now you can login with the new password.": "");
        messageTextView.setText(getResources().getString(success ? R.string.signup_success_message : R.string.signup_failed_message));
    }

}
