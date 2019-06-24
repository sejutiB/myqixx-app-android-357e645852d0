package qix.app.qix.fragments.qixbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.xojan.numpad.NumPadView;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.QixBuyFlowInterface;
import qix.app.qix.models.BalanceResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QixBuyFragment extends Fragment {

    private String currency;

    private HashMap<String, Object> buyData;

    private QixBuyFlowInterface buyFlowListener;
    private QixViewPager pager;

    private Integer amount = 0;

    @BindView(R.id.numPadView)
    NumPadView numPad;

    @BindView(R.id.amountButton)
    Button amountButton;

    @BindView(R.id.qixAmount)
    TextView qixAmountTextView;

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qixbuy, container, false);
        ButterKnife.bind(this, view);

        amountButton.setText("--");
        qixAmountTextView.setText("--");

        numPad.setNumberPadClickListener(button -> {
            switch (button) {
                case CUSTOM_BUTTON_1:
                    this.amount = 0;
                    updateButtonText();
                    break;
                case CUSTOM_BUTTON_2:
                    if (amount > 0) {
                        Double value = amount / 100.0;
                        buyData.put("value", String.format("%.2f", value));
                        buyData.put("currency", "EUR");
                        buyData.put("fragmentIndex", Constants.QIXBUY_RESULT_PAGE_INDEX);

                        buyFlowListener.onDataReceived(buyData);
                        pager.setCurrentItem(Constants.QIXBUY_RESULT_PAGE_INDEX);
                    } else {
                        Helpers.presentToast(getResources().getString(R.string.empty_field_error), Toast.LENGTH_SHORT);
                    }
                    break;
                default:

                    if (amount < 50001) {
                        amount = Integer.valueOf(amount != null ? amount + button.name().replace("NUM_", "") : button.name().replace("NUM_", ""));
                        updateButtonText();
                    }
            }
        });

        pager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);

        buyData = new HashMap<>();

        getBalance();

        return view;

    }

    @SuppressLint("DefaultLocale")
    private void updateButtonText() {

        Double value = amount / 100.0;
        String amountString = String.format("%.2f %s", value, currency.isEmpty() ? "EUR" : currency);

        amountButton.setText(amountString);
        qixAmountTextView.setText(String.format("%d %s", amount, "QIX"));

    }

    private void getBalance() {
        AsyncRequest.getBalace(getActivity(), new Callback<BalanceResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<BalanceResponse> call, @NonNull Response<BalanceResponse> response) {
                if (response.isSuccessful()) {
                    BalanceResponse result = response.body();
                    assert result != null;
                    currency = result.getQixCurrency().replace("QIX", "");
                    updateButtonText();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BalanceResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("Cannot get balance", Toast.LENGTH_SHORT);
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            buyFlowListener = (QixBuyFlowInterface) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
