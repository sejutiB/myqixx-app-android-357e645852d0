package qix.app.qix.fragments.recovery_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.interfaces.RecoveryFlowInterface;


public class RecoveryThirdFragment extends Fragment implements RecoveryFlowInterface {

    private ImageView resultImageView;
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_fourth, container, false);

       //* resultImageView = view.findViewById(R.id.signupImageView);
        messageTextView = view.findViewById(R.id.signupMessageTextView);

        Button closeButton = view.findViewById(R.id.signupCloseButton);

        closeButton.setOnClickListener(view1 -> getActivity().finish());
        return view;
    }

    @Override
    public void onEmailReceived(String email) {}

    @Override
    public void onRecoveryCompleted(boolean success) {
       //* resultImageView.setImageResource(success ? R.drawable.trophy : R.drawable.sad);
        messageTextView.setText(getResources().getString(success ? R.string.recovery_success_message : R.string.recovery_failed_message));
    }
}
