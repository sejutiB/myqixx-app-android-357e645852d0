package qix.app.qix.fragments.qixpay.payment_flow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ramotion.fluidslider.FluidSlider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.models.PartnerResponse;

public class QixpaySelectFragment extends Fragment implements PaymentFlowInterface {

    private PaymentFlowInterface paymentFlowListener;
    private ViewPager pager;

    @BindView(R.id.slider)
    FluidSlider slider;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.totalReviewButton)
    Button reviewButton;

    @BindView(R.id.continueButton)
    Button continueButton;

    @BindView(R.id.minusButton)
    Button minusButton;

    @BindView(R.id.plusButton)
    Button plusButton;

    private Double amountValue;
    private Integer userTotOfQix;
    private boolean shouldPayWithQix;
    private Double currencyRate;

    private static final double QIX_VALUE = 0.01;
    //private static final double QIX_BUY_PRICE = 0.009;

    private double MAX_QIX_PERCENTAGE = 20.0;
    private double MAX_QIX_ACCRUAL = 10.0;

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_qixpay_select, container, false);

        ButterKnife.bind(this, view);

        pager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);

        slider.setBeginTrackingListener(() -> {
            textView.setVisibility(View.INVISIBLE);
            return Unit.INSTANCE;
        });

        slider.setEndTrackingListener(() -> {
            textView.setVisibility(View.VISIBLE);
            return Unit.INSTANCE;
        });

        return view;
    }

    private Integer getMaxNumberOfQixAccepted(){
        //return  ((Double) Math.ceil(getAmountOfQix() * ((shouldPayWithQix ? MAX_QIX_PERCENTAGE : MAX_QIX_ACCRUAL) / 100))).intValue();
        double percent = (shouldPayWithQix ? MAX_QIX_PERCENTAGE : MAX_QIX_ACCRUAL) / 100;
        double qixPrice = 1 / QIX_VALUE;
        return ((Double)Math.ceil(amountValue * percent * qixPrice)).intValue();
    }

    private Integer getAmountOfQix(){
        return ((Double)(amountValue * (1 / QIX_VALUE))).intValue();
    }

    private Integer getMaxQixAllowedWithMerchantCurrency(){
        int totalQixInMerchantCurrency = ((Double)Math.ceil(userTotOfQix * currencyRate)).intValue();
        return Math.min(getMaxNumberOfQixAccepted(), totalQixInMerchantCurrency);
    }

    /*@SuppressLint("DefaultLocale")
    private Double getFiatToPay(){

        double fiatTopay;

        if(getCurrentQixAmountWithUserCurrency() >= getMaxQixAllowedWithUserCurrency()){
            fiatTopay = amountValue - (getMaxQixAllowedWithMerchantCurrency() * QIX_VALUE);
        }else{
            fiatTopay = amountValue - ((getCurrentQixAmountWithUserCurrency() * currencyRate) * QIX_VALUE);
        }

        return Double.valueOf(String.format("%.2f", fiatTopay));
    }*/

    @SuppressLint("DefaultLocale")
    private Integer getFiatToPayInteger(){

        int qixAmount = getCurrentQixAmountWithMerchantCurrency();

        int roundedFiatAmount = ((Double)(this.amountValue * 1000)).intValue();
        int intQixUsed = ((Double)((qixAmount * QIX_VALUE) * 1000)).intValue();
        int total = roundedFiatAmount - intQixUsed;

        Log.d("NUM", qixAmount + ", " + roundedFiatAmount+", "+intQixUsed+", " + total);

        return  total / 10;
    }

    @SuppressLint("DefaultLocale")
    private String getFiatToPayString(){
        return String.format("%.2f", (double) getFiatToPayInteger() / 100);
    }

    /**
     * Ritorna il massimo numero di qix utilizzabili dall'utente nella sua valuta, guardando anche la disponibilità dell'utente
     * @return Int
     */
    private Integer getMaxQixAllowedWithUserCurrency(){
        double maxQixNum =  Math.ceil((getAmountOfQix() * ((shouldPayWithQix ? MAX_QIX_PERCENTAGE : MAX_QIX_ACCRUAL) / 100)) / currencyRate);
        return ((Double)(Math.min(maxQixNum, userTotOfQix))).intValue();
    }

    /**
     * Return the current user Qix Amount in the currency of the user
     * @return Double
     */
    private Integer getCurrentQixAmountWithUserCurrency(){
        return ((Double) Math.ceil((getCurrentQixAmountWithMerchantCurrency() / currencyRate))).intValue();
    }

    /**
     * Return the current user Qix Amount in the currency of the merchant
     * @return Double
     */
    private Integer getCurrentQixAmountWithMerchantCurrency(){
        return (int)(slider.getPosition() * getMaxQixAllowedWithMerchantCurrency());
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            paymentFlowListener = (PaymentFlowInterface)context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }



    @Override
    public void onAmountReceived(final Map<String, Object> data, final PartnerResponse partner) {

        amountValue = Double.valueOf((String) Objects.requireNonNull(data.get("amountValue")));
        shouldPayWithQix = (boolean)data.get("useQix");
        currencyRate = (Double)data.get("rate");
        userTotOfQix = (Integer)data.get("userNumberOfQix");
        //String userQixCurrency = (String) data.get("userQixCurrency");

        MAX_QIX_PERCENTAGE = partner.getRateRedemption();
        MAX_QIX_ACCRUAL = partner.getRateAccrual();

        final Integer max = getMaxQixAllowedWithMerchantCurrency();

        slider.setStartText(String.valueOf(1));
        slider.setEndText(String.valueOf(max));

        slider.setPositionListener(pos -> {
            Integer num = Helpers.map(pos, 0.0f, 1.0f, 1, max);
            //final String value = String.valueOf( (int)(pos * max) == 0 ? 1 : (int)(pos * max));
            slider.setBubbleText(String.valueOf(num));
            reviewButton.setText(String.format("%s %s", getFiatToPayString(), partner.getCurrency()));
            return Unit.INSTANCE;
        });

        slider.setPosition(0.5f);

        reviewButton.setText(String.format("%s %s", getFiatToPayString(), partner.getCurrency()));

        minusButton.setOnClickListener(view -> {
            Float pos = slider.getPosition();
            int value = ((int)(pos * max) == 0 ? 1 : (int)(pos * max));
            if(value > 1){
                slider.setPosition((value - 1) / Float.valueOf(max));
            }
        });

        plusButton.setOnClickListener(view -> {
            if(slider.getPosition() < max){
                Float pos = slider.getPosition();
                int value = ((int)(pos * max) == 0 ? 1 : (int)(pos * max)) + 1;
                slider.setPosition(value / Float.valueOf(max));
            }
        });

        continueButton.setOnClickListener(view -> {
            //int value = ((int)(slider.getPosition() * max) == 0 ? 1 : (int)(slider.getPosition() * max));
            data.put("fragmentIndex", Constants.QIXPAY_CHART_PAGE_INDEX);
            data.put("selectedQixValue", getCurrentQixAmountWithMerchantCurrency());

            paymentFlowListener.onSelectQixNumber(data, partner);
            pager.setCurrentItem(Constants.QIXPAY_CHART_PAGE_INDEX);
        });

       /* amountValue = Double.valueOf((String)data.get("amountValue"));
        shouldPayWithQix = (boolean)data.get("useQix");
        currencyRate = (Double)data.get("rate");
        userTotOfQix = (Integer)data.get("userNumberOfQix");
        userQixCurrency = (String)data.get("userQixCurrency");

        MAX_QIX_PERCENTAGE = partner.getRateRedemption();
        MAX_QIX_ACCRUAL = partner.getRateAccrual();

        balanceRecapTextView.setText(String.format(getString(R.string.qixpay_balance_review_format), Helpers.getIntNumberFormattedString(userTotOfQix), userQixCurrency));

        setCrollerValues();
        generateLegend();

        if(shouldPayWithQix){
            croller.setEnabled(true);
            eurTextView.setText(String.format(getString(R.string.qixpay_eur_value_format), getFiatToPay(), partner.getCurrency()));
            savingTextView.setText(String.format(getString(R.string.qixpay_saving_value_format), getSavingPrice(), partner.getCurrency()));
            grapthTitleTextView.setText(getString(R.string.qixpay_chart_total_amount));
        }else{
            croller.setEnabled(false);
            grapthTitleTextView.setText("");
            savingTextView.setText(String.format(getString(R.string.qixpay_qix_return_message_format), partner.getRateAccrual()));
            legendText2.setText(String.format(getString(R.string.qixpay_num_of_qix_format), getMaxNumberOfQixAccepted()));
            eurTextView.setText(String.format(getString(R.string.qixpay_plus_qix_format), getMaxNumberOfQixAccepted()));
        }

        createPiGraph();*/
    }

    @Override
    public void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner) {

    }

    @Override
    public void onPaymentResult(Map<String, Object> data, PartnerResponse partner) {

    }

    @Override
    public void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner) {

    }

    @Override
    public void onTransactionStarted(Map<String, Object> data, PartnerResponse partnerResponse) {

    }
}
