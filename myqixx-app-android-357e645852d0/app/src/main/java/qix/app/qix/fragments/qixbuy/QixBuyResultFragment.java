package qix.app.qix.fragments.qixbuy;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode;
import com.oppwa.mobile.connect.checkout.meta.CheckoutStorePaymentDetailsMode;
import com.oppwa.mobile.connect.provider.Connect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.interfaces.QixBuyFlowInterface;
import qix.app.qix.models.CheckoutResponse;
import qix.app.qix.models.StatusResponse;
import qix.app.qix.models.TransactionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class QixBuyResultFragment extends Fragment implements QixBuyFlowInterface {

    @BindView(R.id.qixpayResultTextField)
    TextView resultTextView;

    @BindView(R.id.qixpayResultImageView)
    ImageView resultImageView;

    @BindView(R.id.qixpayResultCloseButton)
    Button closeButton;

    private String currentTransactionId;
    private Map<String, Object> amount;
    private String currentCheckuotId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_payment_result, container, false);
        ButterKnife.bind(this, view);

        closeButton.setVisibility(View.GONE);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateResultView(getResources().getString(R.string.payment_start_message) );
        }else{
            updateResultView(true, false, getResources().getString(R.string.payment_start_message) );
        } */

        updateResultView(R.drawable.qix_app_icon, false, getResources().getString(R.string.payment_start_message));

        amount = new HashMap<>();

        closeButton.setOnClickListener(view1 -> Objects.requireNonNull(getActivity()).finish());

        return view;
    }

    private void requestCheckoutId() {

        AsyncRequest.getCheckoutId(getActivity(), (String)amount.get("value"), (String)amount.get("currency"), new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckoutResponse> call, @NonNull Response<CheckoutResponse> response) {
                if(response.isSuccessful()){
                    CheckoutResponse c = response.body();
                    assert c != null;
                    Timber.d(c.getId());
                    currentCheckuotId = c.getId();
                    startPayment(c.getId());
                }else{
                    Timber.d("Errore");

                    try {
                        assert response.errorBody() != null;
                        Timber.d(new JSONObject(response.errorBody().string()).toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<CheckoutResponse> call, @NonNull Throwable t) {
                Helpers.presentToast(R.string.no_connectio_error_message, Toast.LENGTH_LONG);
            }

        });
    }

    private void startPayment(String checkoutId){
        Set<String> paymentBrands = new LinkedHashSet<>();

        paymentBrands.add("VISA");
        paymentBrands.add("MASTER");
        paymentBrands.add("PAYPAL");

        CheckoutSettings checkoutSettings = new CheckoutSettings(checkoutId, paymentBrands, Connect.ProviderMode.TEST)
                .setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
                .setStorePaymentDetailsMode(CheckoutStorePaymentDetailsMode.PROMPT)
                .setTotalAmountRequired(true)
                .setShopperResultUrl("https://myqix.com");

        Intent intent = checkoutSettings.createCheckoutActivityIntent(getActivity());

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CheckoutActivity.REQUEST_CODE_CHECKOUT) {

            switch (resultCode) {
                case CheckoutActivity.RESULT_OK:
                    updateResultView("Checking the payment...");
                    startBuyTransaction(currentCheckuotId);
                    break;
                case CheckoutActivity.RESULT_CANCELED:
                    updateResultView("Payment cancelled...");
                    new Handler().postDelayed(() -> Objects.requireNonNull(getActivity()).finish(),1500);
                    break;
                case CheckoutActivity.RESULT_ERROR:
                    updateResultView("Payment error...");
                    new Handler().postDelayed(() -> Objects.requireNonNull(getActivity()).finish(),1500);
                    break;
            }
        }
    }

    private void startBuyTransaction(final String checkoutId){
        AsyncRequest.buy(getActivity(), checkoutId, new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if(response.isSuccessful()){
                    TransactionResponse result = response.body();
                    assert result != null;
                    Timber.d(result.getTransactionId());
                    currentTransactionId = result.getTransactionId();
                    checkBuyTransaction(currentTransactionId, 1);
                }else{
                    Timber.d("Errore %s", response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                Timber.d("Errore %s", t.getLocalizedMessage());
            }
        });
    }

    /**
     * Aggiorna l'immagine, il testo e la visibilità del bottone nel fragment
     * @param success boolean
     * @param showButton boolean
     * @param message String
     */
    private void updateResultView(boolean success, boolean showButton, String message){
        resultTextView.setText(message);
        resultImageView.setImageResource(success ? R.drawable.trophy : R.drawable.sad);
        if (showButton)
            closeButton.setVisibility(View.VISIBLE);

    }

    private void updateResultView(@DrawableRes int image, boolean showButton, String message){
        resultTextView.setText(message);
        resultImageView.setImageResource(image);
        if (showButton)
            closeButton.setVisibility(View.VISIBLE);
    }

    /**
     * Aggiorna il testo e inizia un'animazione nel fragment
     * @param message String
     */
    private void updateResultView(String message){
        resultTextView.setText(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(Objects.requireNonNull(getContext()), R.drawable.loading_animated);
            resultImageView.setImageDrawable(drawable);

            assert drawable != null;
            drawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable d) {
                    super.onAnimationEnd(d);
                    resultImageView.post(drawable::start);
                }
            });

            drawable.start();
        }

    }

    private void checkBuyTransaction(final String transactionId, Integer times){
        if (getActivity() == null) {
            return;
        }

        if(times < 20){
            AsyncRequest.getBuyStatus(getActivity(), transactionId, new Callback<StatusResponse>() {
                @Override
                public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                    if(response.isSuccessful()){
                        StatusResponse result = response.body();

                        assert result != null;
                        String status = result.getStatus();
                        switch(status) {
                            case "ACCEPTED":
                                Timber.d("TRANSAZIONE COMPLETATA");
                                updateResultView(true, true, "Payment complete!");
                                break;
                            case "":
                            case "PENDING":
                                Timber.d("TRANSAZIONE IN CORSO");
                                new Handler().postDelayed(() -> checkBuyTransaction(transactionId, times+1), 5000);
                                break;
                            case "REJECTED":
                                Timber.d("TRANSAZIONE FALLITA");
                                updateResultView(false, true, "Payment error!");
                                break;
                        }
                    }else{
                        Timber.d("Errore %s", response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                    Timber.d("Errore %s", t.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public void onDataReceived(Map<String, Object> data) {
        amount = data;
        requestCheckoutId();
    }
}
