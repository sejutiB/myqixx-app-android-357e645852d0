package qix.app.qix.fragments.qixnow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.QixnowActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.models.QIXPayment;
import qix.app.qix.models.SuccessResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QixnowMainFragment  extends Fragment {

    @BindView(R.id.qixnowTextField)
    TextView qixnowTextView;

    @BindView(R.id.totalReviewButton)
    Button reviewButton;

    @BindView(R.id.qixnowAnimation)
    LottieAnimationView animationView;

    @SuppressLint({"SetTextI18n", "ResourceType", "StringFormatMatches"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qixnow, container, false);

        ButterKnife.bind(this, view);

        QIXPayment data = ((QixnowActivity) Objects.requireNonNull(getActivity())).getQrCodeData();

        reviewButton.setText(String.format("%s %s",data.getQixMerchantAmount(), "QIX"));

        AsyncRequest.startQuickTransaction(getActivity(), data.toMap(), new Callback<SuccessResponse>() {
            @Override
            public void onResponse(@NonNull Call<SuccessResponse> call,@NonNull Response<SuccessResponse> response) {
                SuccessResponse result = response.body();
                String animationName;

                if(result != null && result.isSuccessful()){
                    qixnowTextView.setText("Payment completed");
                    animationName = "check-animation";
                }else{
                    qixnowTextView.setText(result != null ? result.getErrorMessage() : "Payment error");
                    animationName = "error-animation";
                }

                animationView.setAnimation(animationName + ".json");
                animationView.playAnimation();

                new Handler().postDelayed(() -> Objects.requireNonNull(getActivity()).finish(), 1500);
            }

            @Override
            public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                qixnowTextView.setText("Payment failed");
            }
        });

        return view;
    }

}
