package qix.app.qix.fragments.qixpay.payment_flow;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.sdsmdg.harjot.crollerTest.Croller;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.pieChart.PieGraph;
import qix.app.pieChart.PieSlice;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.models.PartnerResponse;

public class QixpayChartFragment extends Fragment implements PaymentFlowInterface {

    @BindView(R.id.pieGraph)
    PieGraph pie;

    @BindView(R.id.circle_wheel)
    Croller croller;

    @BindView(R.id.legendBlock1)
    View legendBlock1;

    @BindView(R.id.legendBlock3)
    View legendBlock3;

    @BindView(R.id.legendText1)
    TextView legendText1;

    @BindView(R.id.legendText2)
    TextView legendText2;

    @BindView(R.id.eurTextField)
    TextView eurTextView;

    @BindView(R.id.qixpaySavingText)
    TextView savingTextView;

    @BindView(R.id.balanceRecapText)
    TextView balanceRecapTextView;

    @BindView(R.id.grapthTitleTextView)
    TextView grapthTitleTextView;

    @OnClick(R.id.paybutton) void pay(){
        if(getMaxQixAllowedWithUserCurrency() >= getCurrentQixAmountWithUserCurrency() || !shouldPayWithQix){
            HashMap<String, Object> data = new HashMap<>();
            data.put("shopperReference", partner.getName());

            /*if(getCurrentQixAmountWithUserCurrency() >= getMaxQixAllowedWithUserCurrency()){
                data.put("qixMerchantAmount", shouldPayWithQix ? getMaxQixAllowedWithMerchantCurrency() : 0); // numero di qix
            }else{
                data.put("qixMerchantAmount", shouldPayWithQix ? ((Double)Math.ceil(getCurrentQixAmountWithUserCurrency() * currencyRate)).intValue(): 0); // numero di qix
            }*/

            data.put("qixMerchantAmount", shouldPayWithQix ? currentQixValue : 0); // numero di qix

            data.put("qixUserAmount", shouldPayWithQix ? getCurrentQixAmountWithUserCurrency() : 0); // numero di qix
            data.put("qixMerchantReward", shouldPayWithQix ? 0 : getCurrentQixAmountWithMerchantCurrency()); // Solo se qixMerchantAmount == 0; (vinci qix) sconto commerciante
            data.put("fiatUserAmount", Helpers.getTwoDecimalNumber((shouldPayWithQix ? getFiatToPayInteger() / 100 : amountValue) / currencyRate));
            data.put("fiatMerchantAmount", shouldPayWithQix ? ((double) getFiatToPayInteger()) / 100 : amountValue);
            data.put("fiatMerchantType", partner.getCurrency());
            data.put("partnerPosId", partner.getId());
            data.put("totFiatMerchantAmount", amountValue);
            data.put("fragmentIndex", Constants.QIXPAY_REVIEW_PAGE_INDEX);

            showReviewView(data);
        }else{
            Helpers.presentToast("You don't have enough QIX", Toast.LENGTH_SHORT);
        }
    }

    @OnClick(R.id.plusButton) void plusButtonPressed(){
        int pos = croller.getProgress();
        if(pos < getMaxQixAllowedWithMerchantCurrency())
            croller.setProgress(pos + 1);
    }

    @OnClick(R.id.minusButton) void minusButtonPressed(){
        int pos = croller.getProgress();
        if(pos > 1)
            croller.setProgress(pos - 1);
    }

    private ViewPager pager;
    private boolean shouldPayWithQix;
    private PartnerResponse partner;
    private Double currencyRate;
    private Integer currentQixValue;
    private static final double QIX_VALUE = 0.01;
    private static final double QIX_BUY_PRICE = 0.009;
    private double MAX_QIX_PERCENTAGE = 20.0;
    private double MAX_QIX_ACCRUAL = 10.0;
    private PaymentFlowInterface paymentDataReceivedListener;
    private Integer userTotOfQix;
    private Double amountValue;


    @SuppressLint({"SetTextI18n", "ResourceType", "StringFormatMatches"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_qixpay_chart, container, false);
        ButterKnife.bind(this, view);

        pager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);

        croller.setProgressPrimaryCircleSize(10.0f);

        croller.setSweepAngle(360);
        croller.setStartOffset(0);

        croller.setIndicatorWidth(10);
        croller.setLabel("");

        croller.setEnabled(true);

        return view;
    }

  /*private void updateSlices(double value1, double value2){
        if(croller.isEnabled()) {
            int i = 0;
            for (PieSlice s : pie.getSlices()){
                s.setValue((float) (i > 0 ? value2 : value1));
                i++;
            }
            pie.postInvalidate();
        }
    }*/

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            paymentDataReceivedListener = (PaymentFlowInterface) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    private void showReviewView(HashMap<String, Object> data){
        paymentDataReceivedListener.onFinalAmountSelected(data, partner);
        pager.setCurrentItem(Constants.QIXPAY_REVIEW_PAGE_INDEX);
    }

    /**
     * Crea la legenda del grafico, assegnando colori e testi appropriati
     */
    private void generateLegend(){
        float radius = 15.0f;
        if(shouldPayWithQix){
            GradientDrawable shape1 =  new GradientDrawable();
            shape1.setCornerRadius(radius);
            shape1.setColor(getResources().getColor(R.color.colorQixBlue));

            GradientDrawable shape3 =  new GradientDrawable();
            shape3.setCornerRadius(radius);
            shape3.setColor(getResources().getColor(R.color.redEnd));

            legendBlock1.setBackground(shape1);
            legendBlock3.setBackground(shape3);

            legendText1.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorQixBlue));
            legendText2.setTextColor(ContextCompat.getColor(getContext(), R.color.redEnd));

        }else{

            GradientDrawable shape4 =  new GradientDrawable();
            shape4.setCornerRadius(radius);
            shape4.setColor(getResources().getColor(R.color.lightBlueEnd));

            GradientDrawable shape6 =  new GradientDrawable();
            shape6.setCornerRadius(radius);
            shape6.setColor(getResources().getColor(R.color.redStart));

            legendBlock1.setBackground(shape4);
            legendBlock3.setBackground(shape6);

            legendText1.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.lightBlueEnd));
            legendText2.setTextColor(ContextCompat.getColor(getContext(), R.color.redStart));

        }

        refreshLegendValues();
    }

    /**
     * Aggiorna i testi della legenda con i nuovi valori numerici
     */
    private void refreshLegendValues(){
        if(amountValue != null){
            legendText1.setText(String.format(getString(R.string.qixpay_amount_spent_format), Helpers.getTwoDecimalNumber(amountValue), partner.getCurrency()));

            if(shouldPayWithQix){
                legendText2.setText(String.format(getString(R.string.qixpay_num_of_qix_format), getCurrentQixAmountWithUserCurrency()));
                eurTextView.setText(String.format("%s %s",  getFiatToPayString(), partner.getCurrency()));
                savingTextView.setText(String.format(getString(R.string.qixpay_saving_value_format), getSavingPrice(), partner.getCurrency()));
            }
        }
    }

    /**
     * Returns the price that the user should pay
     * @return Double priceToPay
     */
    /*@SuppressLint("DefaultLocale")
    private Double getFiatToPay(){

        double fiatTopay;

        if(getCurrentQixAmountWithUserCurrency() >= getMaxQixAllowedWithUserCurrency()){
            fiatTopay = amountValue - (getMaxQixAllowedWithMerchantCurrency() * QIX_VALUE);
        }else{
            fiatTopay = amountValue - ((getCurrentQixAmountWithUserCurrency() * currencyRate) * QIX_VALUE);
        }

        *//*Log.d(TAG,"Amount: " + amountValue +
                "\nmaxQixValue: " + MAX_QIX_PERCENTAGE +
                //"\nmaxQixNum: "+maxQixNum+
                //"\nmaxQixNumAllowed: "+maxQixNumAllowed+
                //"\ncurrentNumOfQix: "+currentNumOfQix+
                "\nfiatTopay 2 decimals: " + String.format("%.2f",fiatTopay) +
                "\nfiatTopay Normal: " +fiatTopay +
                //"\nqixDifferencePrice: "+qixDifferencePrice+
                "\ncurrent merchant qix:" + getCurrentQixAmountWithMerchantCurrency() +
                "\ncurrent Max : "+getMaxQixAllowedWithMerchantCurrency());*//*

        return Double.valueOf(String.format("%.2f",fiatTopay));//Math.floor(fiatTopay * 100) / 100; // elimina le cifre dopo il terzo decimale senza influenzare le prime 2

    }*/

    @SuppressLint("DefaultLocale")
    private Integer getFiatToPayInteger(){

        int qixAmount = getCurrentQixAmountWithMerchantCurrency();

        int roundedFiatAmount = ((Double)(amountValue * 1000)).intValue();
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
     * Ritora il massimo numero di Qix accettati dal mercante, nella sua valuta
     * @return Double Qix
     */
    private Integer getMaxNumberOfQixAccepted(){
        return  ((Double) Math.ceil(getAmountOfQix() * ((shouldPayWithQix ? MAX_QIX_PERCENTAGE : MAX_QIX_ACCRUAL) / 100))).intValue();
    }

    /**
     * Ritorna il massimo numero di qix utilizzabili dall'utente nella sua valuta, guardando anche la disponibilità dell'utente
     * @return Int
     */
    private Integer getMaxQixAllowedWithUserCurrency(){
        Double maxQixNum =  Math.ceil((getAmountOfQix() * ((shouldPayWithQix ? MAX_QIX_PERCENTAGE : MAX_QIX_ACCRUAL) / 100)) / currencyRate);
        return shouldPayWithQix ? ((Double)(Math.min(maxQixNum, userTotOfQix))).intValue() : maxQixNum.intValue();
    }

    /**
     * Ritorna il massimo numero di qix utilizzabili dall'utente nella valuta del merchant, guardando anche la disponibilità dell'utente
     * @return Int
     */
    private Integer getMaxQixAllowedWithMerchantCurrency(){
        int totalQixInMerchantCurrency = ((Double)Math.ceil(userTotOfQix * currencyRate)).intValue();
        return shouldPayWithQix ? Math.min(getMaxNumberOfQixAccepted(), totalQixInMerchantCurrency) : getMaxNumberOfQixAccepted();
    }

    /**
     * Trasforma un valore in una valuta in QIX
     * @return Double Qix
     */
    private Integer getAmountOfQix(){
        return ((Double)(amountValue * (1 / QIX_VALUE))).intValue();
    }

    /**
     * Return the current user Qix Amount in the currency of the user
     * @return Double
     */
    private Integer getCurrentQixAmountWithUserCurrency(){
        return ((Double) (croller.getProgress() / currencyRate)).intValue();
    }

    /**
     * Return the current user Qix Amount in the currency of the merchant
     * @return Double
     */
    private Integer getCurrentQixAmountWithMerchantCurrency(){
        return croller.getProgress();
    }

    /**
     * Returns the total of the saving
     * @return Double
     */
    private Double getSavingPrice(){
        /*Log.d(TAG, "SAVING\n" +
                "Amount: "+amountValue+"\n"
                +"eurToBePaidRound: "+getFiatToPay()+"\n"
                +"eurToBePaid: "+getFiatToPay()+"\n");*/
        return amountValue - ((double) getFiatToPayInteger() / 100);
    }

    /**
     * Crea le fette del grafico e le anima
     */
    private void createPiGraph(){
        pie.removeSlices();

        PieSlice slice = new PieSlice();
        PieSlice slice2 = new PieSlice();

        if(shouldPayWithQix){
            croller.setBackCircleColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorQixBlue))));
            slice.setColor(getResources().getColor(R.color.redEnd));
            slice2.setColor(getResources().getColor(R.color.greenStart));

            slice.setGoalValue(getMaxQixAllowedWithMerchantCurrency());
            slice2.setGoalValue(getAmountOfQix() - getMaxQixAllowedWithMerchantCurrency());
        }else{
            croller.setBackCircleColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.redEnd))));
            slice.setColor(getResources().getColor(R.color.redEnd));
            slice2.setColor(getResources().getColor(R.color.light_gray));

            slice.setGoalValue((int)MAX_QIX_ACCRUAL);
            slice2.setGoalValue(100 - (int)MAX_QIX_ACCRUAL);
        }

        pie.addSlice(slice);
        pie.addSlice(slice2);

        pie.setInnerCircleRatio(225);

        pie.setDuration(1000);
        pie.setInterpolator(new AccelerateDecelerateInterpolator());

        pie.animateToGoalValues();
    }

    private void setCrollerValues(){

        croller.setMax(getMaxQixAllowedWithMerchantCurrency());

        if(shouldPayWithQix){
            croller.setBackCircleDisabledColor(getResources().getColor(R.color.colorQixBlue));//Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainApplication.getAppContext(), R.color.colorQixBlue))));
            croller.setBackCircleColor(getResources().getColor(R.color.colorQixBlue));//Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainApplication.getAppContext(), R.color.colorQixBlue))));

            croller.setMainCircleColor(Color.WHITE);
            croller.setMainCircleDisabledColor(Color.WHITE);

            croller.setProgressPrimaryColor(Color.TRANSPARENT);
            croller.setProgressPrimaryDisabledColor(Color.TRANSPARENT);

            croller.setProgressSecondaryColor(Color.TRANSPARENT);
            croller.setProgressSecondaryDisabledColor(Color.TRANSPARENT);

            croller.setIndicatorColor(Color.TRANSPARENT);
            croller.setIndicatorDisabledColor(Color.TRANSPARENT);

            croller.setProgress(currentQixValue);
        }else{
            croller.setBackCircleDisabledColor(getResources().getColor(R.color.lightBlueEnd));//Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainApplication.getAppContext(), R.color.colorQixBlue))));
            croller.setBackCircleColor(getResources().getColor(R.color.lightBlueEnd));//Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainApplication.getAppContext(), R.color.colorQixBlue))));

            croller.setMainCircleColor(Color.WHITE);
            croller.setMainCircleDisabledColor(Color.WHITE);

            croller.setProgressPrimaryColor(Color.TRANSPARENT);
            croller.setProgressPrimaryDisabledColor(Color.TRANSPARENT);

            croller.setProgressSecondaryColor(Color.TRANSPARENT);
            croller.setProgressSecondaryDisabledColor(Color.TRANSPARENT);

            croller.setIndicatorColor(Color.TRANSPARENT);
            croller.setIndicatorDisabledColor(Color.TRANSPARENT);

            croller.setProgress(getMaxQixAllowedWithMerchantCurrency());
        }

        /*croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                refreshLegendValues();
                updateSlices(croller.getProgress(), getAmountOfQix().intValue() - croller.getProgress());
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {}

            @Override
            public void onStopTrackingTouch(Croller croller) {}
        });*/
    }


    @Override
    public void onAmountReceived(Map<String, Object> data, PartnerResponse partner) {

    }

    @Override
    public void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner) {
        if(isAdded()) {
            this.partner = partner;
            amountValue = Double.valueOf((String) Objects.requireNonNull(data.get("amountValue")));
            shouldPayWithQix = (boolean) data.get("useQix");
            currencyRate = (Double) data.get("rate");
            userTotOfQix = (Integer) data.get("userNumberOfQix");
            String userQixCurrency = (String) data.get("userQixCurrency");

            MAX_QIX_PERCENTAGE = partner.getRateRedemption();
            MAX_QIX_ACCRUAL = partner.getRateAccrual();

            balanceRecapTextView.setText(String.format(getString(R.string.qixpay_balance_review_format), Helpers.getIntNumberFormattedString(userTotOfQix), userQixCurrency));

            currentQixValue = (Integer) data.get("selectedQixValue");

            setCrollerValues();
            generateLegend();

            if (shouldPayWithQix) {
                eurTextView.setText(String.format("%s %s", getFiatToPayString(), partner.getCurrency()));
                savingTextView.setText(String.format(getString(R.string.qixpay_saving_value_format), getSavingPrice(), partner.getCurrency()));
                grapthTitleTextView.setText(getString(R.string.qixpay_chart_total_amount));
            } else {
                grapthTitleTextView.setText("");
                savingTextView.setText(String.format(getString(R.string.qixpay_qix_return_message_format), partner.getRateAccrual()));
                legendText2.setText(String.format(getString(R.string.qixpay_num_of_qix_get_format), getMaxNumberOfQixAccepted()));
                eurTextView.setText(String.format(getString(R.string.qixpay_plus_qix_format), getMaxNumberOfQixAccepted()));
            }

            createPiGraph();

            croller.setEnabled(false);
        }
    }

    @Override
    public void onPaymentResult(Map<String, Object> data, PartnerResponse partner) {}

    @Override
    public void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner) {}

    @Override
    public void onTransactionStarted(Map<String, Object> data, PartnerResponse partner) {}

}
