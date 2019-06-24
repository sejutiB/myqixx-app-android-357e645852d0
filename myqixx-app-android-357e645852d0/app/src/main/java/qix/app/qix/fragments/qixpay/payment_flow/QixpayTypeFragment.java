package qix.app.qix.fragments.qixpay.payment_flow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import qix.app.qix.QixpayActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.models.BalanceResponse;
import qix.app.qix.models.ExchangeRateResponse;
import qix.app.qix.models.PartnerResponse;
import qix.app.qix.models.QIXPayment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class QixpayTypeFragment extends Fragment {

    private PaymentFlowInterface paymentFlowListener;
    private PartnerResponse partner;
    private ViewPager pager;
    private Integer userNumberOfQix;
    private String userQixCurrency;
    private Double currentRate;
    private QIXPayment data;


    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_qixpay_type, container, false);

        final Button getButton = view.findViewById(R.id.getButton);
        final Button useButton = view.findViewById(R.id.useButton);

        getButton.setEnabled(false);
        useButton.setEnabled(false);

        data = ((QixpayActivity) Objects.requireNonNull(getActivity())).getQrCodeData();


        pager = getActivity().findViewById(R.id.viewpager);

        getButton.setOnClickListener(view1 -> {
            ((QixpayActivity) Objects.requireNonNull(getActivity())).setIsGettingQix(true);
            setBalance(Constants.QIXPAY_CHART_PAGE_INDEX, currentRate, false);
        });

        useButton.setOnClickListener(view12 -> {
            ((QixpayActivity) Objects.requireNonNull(getActivity())).setIsGettingQix(false);
            setBalance(Constants.QIXPAY_SELECT_PAGE_INDEX, currentRate, true);
        });

        Location l = ((QixpayActivity) Objects.requireNonNull(getActivity())).getUserLocation();

        AsyncRequest.getPartners(getActivity(), l.getLatitude(), l.getLongitude(), Constants.MAX_RADIUS, new Callback<List<PartnerResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<PartnerResponse>> call, @NonNull Response<List<PartnerResponse>> response) {

                if(response.isSuccessful()){
                    List<PartnerResponse> result = response.body();

                    assert result != null;

                    partner = getPartner(result, data.getPartnerPosId());

                    if(partner != null){
                        String currency = Helpers.getPreference(R.string.preference_user_qix_currency);
                        if(partner.getQixCurrency().equals(currency)){
                            currentRate = 1.0;
                            getButton.setEnabled(true);
                            useButton.setEnabled(true);
                        }else{

                            AsyncRequest.getExchangeRate(getActivity(), currency == null ? "QIXEUR" : currency, partner.getQixCurrency(), new Callback<ExchangeRateResponse>() {

                                @Override
                                public void onResponse(@NonNull Call<ExchangeRateResponse> call, @NonNull Response<ExchangeRateResponse> response) {
                                    if (response.isSuccessful()) {
                                        ExchangeRateResponse result = response.body();
                                        assert result != null;
                                        currentRate = result.getRate();
                                        getButton.setEnabled(true);
                                        useButton.setEnabled(true);
                                    } else {
                                        Helpers.presentToast(getResources().getString(R.string.error_message_cannotConnectToHost), Toast.LENGTH_SHORT);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ExchangeRateResponse> call, @NonNull Throwable t) {
                                    Helpers.presentToast(getResources().getString(R.string.error_message_cannotConnectToHost), Toast.LENGTH_SHORT);
                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }else{
                        Helpers.presentToast("Partner not found", Toast.LENGTH_SHORT);
                        new Handler().postDelayed(() -> Objects.requireNonNull(getActivity()).finish(), 1000);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PartnerResponse>> call, @NonNull Throwable t) {
                Timber.d("Errore");
            }
        });

        return view;
    }

    private PartnerResponse getPartner(List<PartnerResponse> partners, String id){
        for(PartnerResponse partner : partners){
            Timber.d("PARTNER %s", partner.toString());
            if(partner.getId().equals(id)){
                return partner;
            }
        }
        return null;
    }

    private void setBalance(Integer view, Double rate, Boolean useQix){
        HashMap<String, Object> d = new HashMap<>();
        d.put("fragmentIndex", view);
        d.put("amountValue", ((Double)(Double.valueOf(data.getQixMerchantAmount()) / 100.0)).toString());
        d.put("useQix", useQix);
        d.put("rate", rate);

        getBalance(view, d);
    }

    private void getBalance(final Integer view, final HashMap<String, Object> data){
        AsyncRequest.getBalace(getActivity(), new Callback<BalanceResponse>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<BalanceResponse> call, @NonNull Response<BalanceResponse> response) {
                if(response.isSuccessful()){
                    BalanceResponse result = response.body();

                    assert result != null;
                    userNumberOfQix = ((Double)Math.ceil(result.getBalance())).intValue();
                    userQixCurrency = result.getQixCurrency();

                    data.put("userNumberOfQix", userNumberOfQix);
                    data.put("userQixCurrency", userQixCurrency);
                    data.put("maxQixAllowed", userNumberOfQix);

                    if(view == Constants.QIXPAY_CHART_PAGE_INDEX){
                        paymentFlowListener.onSelectQixNumber(data, partner);
                    }else{
                        paymentFlowListener.onAmountReceived(data, partner);
                    }

                    pager.setCurrentItem(view);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BalanceResponse> call, @NonNull Throwable t) {
                Timber.d("Cannot get Balance %s", t.getLocalizedMessage());
            }
        });

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

}