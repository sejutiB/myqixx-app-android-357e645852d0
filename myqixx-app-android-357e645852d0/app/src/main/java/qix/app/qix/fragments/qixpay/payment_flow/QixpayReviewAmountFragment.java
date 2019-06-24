package qix.app.qix.fragments.qixpay.payment_flow;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.QixpayActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.models.BalanceResponse;
import qix.app.qix.models.PartnerResponse;
import qix.app.qix.models.QIXPayment;
import qix.app.qix.models.SuccessResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class QixpayReviewAmountFragment extends Fragment implements PaymentFlowInterface {

    @BindView(R.id.qixNumText)
    TextView qixPoints;

    @BindView(R.id.qixpayReviewCurrencyText)
    TextView currencyText;

    @BindView(R.id.totalReviewText)
    TextView totalAmountText;

    @BindView(R.id.qixReviewText)
    TextView qixAmountText;

    @BindView(R.id.reviewTotalAmountTextView)
    TextView totalAmountTextPhrase;

    @BindView(R.id.reviewQixAmountTextView)
    TextView qixTotalAmountTextPhrase;

    @BindView(R.id.reviewToPayTextView)
    TextView totalToPayTextPhrase;

    @BindView(R.id.toPayReviewText)
    TextView toPayAmountText;

    @OnClick(R.id.qixpayReviewPayButton) void payButtonPressed(){
        startTransaction(paymentData, Constants.PaymentType.IN_APP_PAYMENT);
    }

    @OnClick(R.id.payInStoreButton) void payInStoreButtonPressed(){
        startTransaction(paymentData, Constants.PaymentType.IN_STORE_PAYMENT);
    }

    private String qixNum = "--";
    private String qixCurrency;
    private HashMap<String, Object> paymentData;
    private PaymentFlowInterface paymentDataReceivedListener;
    private ViewPager pager;
    private PartnerResponse partner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_payment_review, container, false);
        ButterKnife.bind(this, view);

        Timber.plant(new Timber.DebugTree());

        qixCurrency = String.format(getString(R.string.qixchange_currency_label_format), "--");

        pager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);

        qixPoints.setText(qixNum);
        currencyText.setText(qixCurrency);
        totalAmountText.setText("--");
        qixAmountText.setText("--");
        toPayAmountText.setText("--");

        getBalance();

        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            paymentDataReceivedListener = (PaymentFlowInterface) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
    
    private void getBalance(){
        AsyncRequest.getBalace(getActivity(), new Callback<BalanceResponse>() {

            @Override
            public void onResponse(@NonNull Call<BalanceResponse> call, @NonNull Response<BalanceResponse> response) {
                if(response.isSuccessful()){
                    BalanceResponse result = response.body();

                    assert result != null;
                    qixNum = Helpers.getIntNumberFormattedString(Math.floor(result.getBalance()));
                    qixCurrency = String.format(getString(R.string.qixchange_currency_label_format), result.getQixCurrency());
                    qixPoints.setText(qixNum);
                    currencyText.setText(qixCurrency);
                }else{
                    Helpers.presentToast("No connection", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BalanceResponse> call, @NonNull Throwable t) {
                Timber.d("Errore sconosciuto: %s", t.getLocalizedMessage());
                Helpers.presentToast("No connection", Toast.LENGTH_SHORT);
            }
        });
    }

    private void startTransaction(final HashMap<String, Object> data, final Constants.PaymentType paymentType){
        AsyncRequest.startTransaction(getActivity(), paymentType, data, new Callback<SuccessResponse>() {
            @Override
            public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                paymentData.put("isInstorePayment", paymentType == Constants.PaymentType.IN_STORE_PAYMENT);

                if(response.isSuccessful()){
                    SuccessResponse result = response.body();
                    assert result != null;

                    if(result.isSuccessful()){
                        showResultView(false, true, getResources().getString(R.string.waiting_merchant_message));
                    }else{
                        showResultView(true, false, getResources().getString(R.string.qixpay_start_transaction_error));
                    }
                }else{
                    showResultView(true, false, getResources().getString(R.string.qixpay_start_transaction_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                Timber.d("Errore Transazione %s", t.getLocalizedMessage());
                showResultView(true,false, getResources().getString(R.string.qixpay_start_transaction_error));
            }
        });
    }

    private void showResultView(boolean isPaymentFinished, boolean success, String message){
        paymentData.put("imageResource", success ? R.drawable.trophy : R.drawable.sad);
        paymentData.put("fragmentIndex", Constants.QIXPAY_RESULT_PAGE_INDEX);
        paymentData.put("isPaymentFinished", isPaymentFinished);
        //paymentData.put("transactionId", currentTransactionId);
        paymentData.put("message", message);
        paymentDataReceivedListener.onTransactionStarted(paymentData, partner);
        pager.setCurrentItem(Constants.QIXPAY_RESULT_PAGE_INDEX);
    }

    @Override
    public void onAmountReceived(Map<String, Object> data, PartnerResponse partner) {}

    @Override
    public void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner) {

    }

    @Override
    public void onPaymentResult(Map<String, Object> data, PartnerResponse partner) {}

    @Override
    public void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner) {

        this.partner = partner;

        paymentData = (HashMap<String, Object>) data;
        paymentData.put("fragmentIndex", Constants.QIXPAY_RESULT_PAGE_INDEX);

        QIXPayment payment = ((QixpayActivity) Objects.requireNonNull(getActivity())).getQrCodeData();
        paymentData.put("transactionId", payment.getTransactionId());
        //PartnerResponse partner = ((QixpayActivity) getActivity()).getShop();
        String currency =  (String)data.get("fiatMerchantType");
        //String name =  (String)data.get("shopperReference");

        Double tot =  (Double)data.get("totFiatMerchantAmount");
        Integer totQix = (Integer)data.get("qixUserAmount");
        Double toPay = (Double)data.get("fiatMerchantAmount");

        int qixMerchantReward =  (int) data.get("qixMerchantReward");

        totalAmountText.setText(String.format(getString(R.string.qixpay_eur_value_format),tot, currency));
        toPayAmountText.setText(String.format(getString(R.string.qixpay_eur_value_format), toPay, currency));

        totalToPayTextPhrase.setText(getString(R.string.qixpay_amount_to_pay_phrase));
        totalAmountTextPhrase.setText(getString(R.string.qix_pay_total_amount_phrase));

        if(qixMerchantReward == 0){
            qixTotalAmountTextPhrase.setText(getString(R.string.qixpay_qix_total_amount_phrase));
            qixAmountText.setText(String.format(getString(R.string.qixpay_num_qix_currency_format), totQix, Helpers.getPreference(R.string.preference_user_qix_currency)));
        }else{
            qixTotalAmountTextPhrase.setText(getString(R.string.qixpay_amount_qix_earned));
            qixAmountText.setText(String.format(getString(R.string.qixpay_num_qix_currency_format), qixMerchantReward, Helpers.getPreference(R.string.preference_user_qix_currency)));
        }
    }

    @Override
    public void onTransactionStarted(Map<String, Object> data, PartnerResponse partner) {}

}
