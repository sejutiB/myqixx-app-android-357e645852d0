package qix.app.qix.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.InfoActivity;
import qix.app.qix.MainActivity;
import qix.app.qix.OfferDetailActivity;
import qix.app.qix.QixbuyActivity;
import qix.app.qix.QixnowActivity;
import qix.app.qix.QixpayActivity;
import qix.app.qix.R;
import qix.app.qix.TransactionReportActivity;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.adapters.HomeGridAdapter;
import qix.app.qix.helpers.adapters.QixFragmentPagerAdapter;
import qix.app.qix.helpers.interfaces.QixLocationInterface;
import qix.app.qix.models.OfferResponse;
import qix.app.qix.models.ProfileResponse;
import qix.app.qix.models.QIXPayment;
import qix.app.qix.vision.BarcodeCaptureActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, QixLocationInterface {

    @BindView(R.id.homeSwipeLayout)
    SwipeRefreshLayout pullToRefresh;

    @BindView(R.id.gridEmptyText)
    TextView emptyView;

    @BindView(R.id.listView)
    ListView listView;

    private TextView qixPoints;
    private ViewPager pager;

    private HomeGridAdapter listViewAdapter;
    private Timer updateTimer;
    private Location currentLocation;
    private List<OfferResponse> offers;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        Toolbar custom_Toolbar = view.findViewById(R.id.toolbar_custom);
        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(custom_Toolbar);

        pullToRefresh.setEnabled(true);
        pullToRefresh.setOnRefreshListener(this);

        pager = getActivity().findViewById(R.id.view_pager);

        emptyView.setVisibility(View.GONE);

        listViewAdapter = new HomeGridAdapter(getContext(), null);

        listView.setAdapter(listViewAdapter);

        View header = getLayoutInflater().inflate(R.layout.layout_home_header, null);

        qixPoints = header.findViewById(R.id.qixNumText);
        qixPoints.setOnClickListener(view1 -> Helpers.startNewActivity(getActivity(), TransactionReportActivity.class, false, null));

        Helpers.setCustomButtonData(header, R.id.qixpayButton, R.drawable.qix_pay_new, R.drawable.button_violet_background);
        Helpers.setCustomButtonData(header, R.id.qixchangeButton, R.drawable.now_qix, R.drawable.button_orange_background);
        Helpers.setCustomButtonData(header, R.id.qixMarketplaceButton, R.drawable.button_profile_icon, R.drawable.button_yellow2_background);

        (header.findViewById(R.id.qixpayButton)).setOnClickListener(this);
        (header.findViewById(R.id.qixchangeButton)).setOnClickListener(this);
        (header.findViewById(R.id.qixMarketplaceButton)).setOnClickListener(this);

        listView.addHeaderView(header);

        listView.setOnItemClickListener((adapterView, parent, pos, l) -> {
            if (pos > 0) {
                OfferResponse offer = offers.get(pos - 1);

                Intent intent = new Intent(getActivity(), OfferDetailActivity.class);
                intent.putExtra("offer", offer);

                startActivity(intent);
            }
        });

        if (Configuration.isLogged()) {
            Helpers.updateBalance(getActivity(), qixPoints);
        }


        saveUserCountry();

        return view;
    }

    private void saveUserCountry() {
        AsyncRequest.getProfileData(getActivity(), new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse result = response.body();
                    assert result != null;
                    if (result.getCountryCode() != null) {
                        Configuration.setUserCountry(result.getCountryCode());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void getOffers(Location userLocation) {
        if (currentLocation != null) {
            pullToRefresh.setRefreshing(true);
            AsyncRequest.getOffers(getActivity(), userLocation.getLatitude(), userLocation.getLongitude(), Constants.MAX_RADIUS, new Callback<List<OfferResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<OfferResponse>> call, @NonNull Response<List<OfferResponse>> response) {
                    if (response.isSuccessful()) {
                        offers = response.body();
                        Gson g = new Gson();
                        String json = g.toJson(offers);
                        Log.d("TAGGGG", json);
                        listViewAdapter.setData(offers);
                        listViewAdapter.notifyDataSetChanged();
                    } else {
                        Helpers.presentToast("cannot get offers", Toast.LENGTH_SHORT);
                    }
                    pullToRefresh.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<List<OfferResponse>> call, @NonNull Throwable t) {
                  //*  Log.d("ERRORE", t.getLocalizedMessage());
                    Helpers.presentToast(getString(R.string.no_connectio_error_message), Toast.LENGTH_SHORT);
                    pullToRefresh.setRefreshing(false);
                }
            });
        } else {
            Helpers.presentToast("Cannot retrieve position", Toast.LENGTH_SHORT);
            pullToRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qixchangeButton:
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);

                break;
            case R.id.qixpayButton:
                pager.setCurrentItem(Constants.PageIndexes.QIXPAY_PAGE_INDEX.getValue());
                break;
            case R.id.qixMarketplaceButton:
                // Helpers.startNewActivity(QixbuyActivity.class);
                pager.setCurrentItem(3);
                break;
        }
    }


    @Override
    public void onRefresh() {
        if (MainActivity.getCurrentItemIndex() == Constants.PageIndexes.HOME_PAGE_INDEX.getValue()) {
            Helpers.updateBalance(getActivity(), qixPoints);
            getOffers(currentLocation);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                Helpers.startNewActivity(InfoActivity.class);
                break;

           /* case R.id.menu_qr_reader:
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                break;*/

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.LOGIN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Helpers.updateBalance(getActivity(), qixPoints);
                }
                break;
            case Constants.RC_BARCODE_CAPTURE:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        QIXPayment paymentInfo = new QIXPayment(barcode.displayValue);

                        boolean needLocation = false;

                        Intent i;
                        if (paymentInfo.getPaymentType() == QIXPayment.PaymentType.QIXNOW) {
                            i = new Intent(getActivity(), QixnowActivity.class);
                        } else {
                            i = new Intent(getActivity(), QixpayActivity.class);
                            i.putExtra("location", currentLocation);
                            needLocation = currentLocation == null;
                        }

                        i.putExtra("qrCodeData", paymentInfo);

                        if (!needLocation) {
                            new Handler().postDelayed(() -> startActivity(i), 500);
                        } else {
                            Helpers.presentToast("Need user position", Toast.LENGTH_SHORT);
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause() {
        updateTimer.cancel();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Aggiorno il numero di qix ogni 60 sec */
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> Helpers.updateBalance(getActivity(), qixPoints));
            }
        }, 100, 60000);
        if (currentLocation==null) {
            currentLocation=MainActivity.getCurrentLocation();
        }
            getOffers(currentLocation);

    }

    @Override
    public void onLocationUpdate(Location userLocation, boolean isFirstTime) {
        if (userLocation != null) {
            currentLocation = userLocation;
            Log.i("userlocation", userLocation.getLatitude() + "");
        } else
            Log.i("userlocation", "no location");

        if (isFirstTime && pullToRefresh != null) {
            getOffers(userLocation);
        }
    }


}