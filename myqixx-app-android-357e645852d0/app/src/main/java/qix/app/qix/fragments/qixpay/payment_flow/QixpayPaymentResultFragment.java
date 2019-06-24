package qix.app.qix.fragments.qixpay.payment_flow;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.interfaces.CheckoutIdRequestListener;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.helpers.interfaces.PaymentStatusRequestListener;
import qix.app.qix.models.CheckoutResponse;
import qix.app.qix.models.MessageResponse;
import qix.app.qix.models.PartnerResponse;
import qix.app.qix.models.TransactionStatusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class QixpayPaymentResultFragment extends Fragment implements PaymentFlowInterface, CheckoutIdRequestListener, PaymentStatusRequestListener {

    @BindView(R.id.qixpayResultTextField)
    TextView resultTextView;

    @BindView(R.id.qixpayResultImageView)
    ImageView resultImageView;

    @BindView(R.id.qixpayResultCloseButton)
    Button closeButton;

    @OnClick(R.id.qixpayResultCloseButton) void closePressed(){
        Objects.requireNonNull(getActivity()).finish();
    }

    private String currentTransactionId;
    private JSONObject amount;

    private String currentCheckuotId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_payment_result, container, false);
        ButterKnife.bind(this, view);

        Timber.plant(new Timber.DebugTree());

        closeButton.setVisibility(View.GONE);

        amount = new JSONObject();

        return view;
    }

    private void getTransactionStatus(final boolean paymentComplete, final String transactionId, final int times){
        AsyncRequest.getTransactionStatus(getActivity(), transactionId, new Callback<TransactionStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionStatusResponse> call, @NonNull Response<TransactionStatusResponse> response) {
                if(response.isSuccessful()){
                    TransactionStatusResponse result = response.body();

                    assert result != null;
                    if(result.isSuccessful()){
                        String state = result.getState();
                        switch(state){
                            case"AUTHORIZED":
                                if(!paymentComplete){
                                    requestCheckoutId();
                                }else{
                                    if(times <= 20) {
                                        Timber.d("Transazione autorizzata, tentativo numero " + times + ", tentivi rimati " + (20 - times));
                                        new Handler().postDelayed(() -> getTransactionStatus(true, transactionId, times + 1), 3000);
                                    }else{
                                        updateResultView(false, true, getResources().getString(R.string.qixpay_timeout_error));
                                    }
                                    break;
                                }
                                break;
                            case "ACCEPTED":
                                updateResultView(true, true, getResources().getString(R.string.qixpay_payment_success));
                                break;
                            case "WAIT_FOR_APPROVAL":
                                updateResultView(true, true, getResources().getString(R.string.qixpay_wait_for_approve));
                                break;
                            case "NOT AUTHORIZED":
                                updateResultView(false, true, getResources().getString(R.string.qixpay_anauthorized_request));
                                break;
                            case "REJECTED":
                                updateResultView(false, true, getResources().getString(R.string.qixpay_rejected_request));
                                break;
                            case "":
                            case "PENDING":
                                new Handler().postDelayed(() -> getTransactionStatus(paymentComplete, transactionId, times+1), 3000);
                                break;
                        }
                    }else{
                        if(times <= 5){
                            Log.d("Errore",String.format("Errore 500, tentativo numero %d, tentivi rimati %d", times, 5 - times));
                            new Handler().postDelayed(() -> getTransactionStatus(paymentComplete, transactionId, times+1), 3000);
                        }else{
                            updateResultView(false,true, getResources().getString(R.string.cannot_check_transaction_error));
                        }
                    }

                }else{
                    if(response.code() == 500 && times <= 5){
                        Log.d("Errore",String.format("Errore 500, tentativo numero %d, tentivi rimati %d", times, 10 - times));
                        new Handler().postDelayed(() -> getTransactionStatus(paymentComplete, transactionId, times+1), 3000);
                    }else{
                        updateResultView(false,true, getResources().getString(R.string.error_message_cannotConnectToHost));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionStatusResponse> call, @NonNull Throwable t) {
                Timber.d("Errore status %s", t.getLocalizedMessage());
            }
        });
    }

    private void requestCheckoutId() {
        try {
            AsyncRequest.getCheckoutId(getActivity(), amount.getString("value"), amount.getString("currency"), new Callback<CheckoutResponse>() {
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
                   }

                }

                @Override
                public void onFailure(@NonNull Call<CheckoutResponse> call, @NonNull Throwable t) {
                    Helpers.presentToast(R.string.no_connectio_error_message, Toast.LENGTH_LONG);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        Intent intent = checkoutSettings.createCheckoutActivityIntent(getContext());

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    private void checkPaymentResult(){
        AsyncRequest.sendGatewayCheck(getActivity(), currentTransactionId, currentCheckuotId, new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if(response.isSuccessful()){
                    Helpers.presentToast(R.string.message_successful_payment, Toast.LENGTH_SHORT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        updateResultView("Controllo il pagamento...");
                    }else{
                        updateResultView(true, false, "Controllo il pagamento...");
                    }

                    getTransactionStatus(true, currentTransactionId, 0);
                }else{
                    updateResultView(false, true, getResources().getString(R.string.qixpay_payment_send_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                updateResultView(false, true, getResources().getString(R.string.qixpay_payment_send_error));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CheckoutActivity.REQUEST_CODE_CHECKOUT:
                checkPaymentResult();
                break;
        }
    }

    /**
     * Aggiorna l'immagine, il testo e la visibilità del bottone nel fragment
     * @param success boolean
     * @param showButton boolean
     * @param message String
     */
    private void updateResultView(boolean success, boolean showButton, String message){
        if(this.getView() != null) {
            resultTextView.setText(message);
            resultImageView.setImageResource(success ? R.drawable.trophy : R.drawable.sad);
            if (showButton)
                closeButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Aggiorna il testo e inizia un'animazione nel fragment
     * @param message String
     */
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateResultView(String message){
        if(this.getView() != null) {
            resultTextView.setText(message);

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

    /**
     * Paydoo request callbacks
     */

    @Override
    public void onCheckoutIdReceived(String checkoutId) {
        if (checkoutId != null) {
            Timber.d(checkoutId);
            currentCheckuotId = checkoutId;
            startPayment(checkoutId);
        }else{
            Helpers.presentToast(R.string.no_connectio_error_message, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onErrorOccurred() {

    }

    @Override
    public void onPaymentStatusReceived(String paymentStatus) {
        if ("OK".equals(paymentStatus)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                updateResultView("Controllo il pagamento...");
            }else{
                updateResultView(true, false, "Controllo il pagamento...");
            }

            getTransactionStatus(true, currentTransactionId, 0);
        }else{
            Helpers.presentToast(R.string.message_unsuccessful_payment, Toast.LENGTH_SHORT);
            Timber.d("ERRORE PAGAMENTO: %s", paymentStatus);
            updateResultView(false, true, getResources().getString(R.string.qixpay_payment_failure));
        }
    }

    /**
     * Payment Flow Callbacks {@link PaymentFlowInterface}
     */

    @Override
    public void onAmountReceived(Map<String, Object> data, PartnerResponse partner) {}

    @Override
    public void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner) {

    }

    @Override
    public void onPaymentResult(Map<String, Object> data, PartnerResponse partner) {}

    @Override
    public void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner) {}

    @SuppressLint("DefaultLocale")
    @Override
    public void onTransactionStarted(Map<String, Object> data, PartnerResponse partner) {

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            // do stuff
            Timber.d(key + " " + value);

        }


        boolean isPaymentFinished = (boolean)data.get("isPaymentFinished");
        String message = (String)data.get("message");
        resultTextView.setText(message);

        if(isPaymentFinished){
            resultImageView.setImageResource((int) data.get("imageResource"));
        }else {

            Picasso.get().load(partner.getImageUrl()).error(R.drawable.qix_app_icon).into(resultImageView);

            Double fiatToPay =  (Double)data.get("fiatMerchantAmount");

            try {
                amount.put("value", String.format("%.2f",fiatToPay));
                amount.put("currency", partner.getCurrency());
                amount.put("shopperReference", partner.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            currentTransactionId = (String) data.get("transactionId");

            //boolean isInstorePayment = (boolean) data.get("isInstorePayment");
            new Handler().postDelayed(() -> getTransactionStatus(false, currentTransactionId, 0), 4000);
        }
    }


}
