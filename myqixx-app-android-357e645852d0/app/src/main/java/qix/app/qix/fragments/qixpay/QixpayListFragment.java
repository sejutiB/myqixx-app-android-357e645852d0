package qix.app.qix.fragments.qixpay;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.MainActivity;
import qix.app.qix.QixpayDetailsActivity;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.adapters.QixpayListAdapter;
import qix.app.qix.helpers.interfaces.QixLocationInterface;
import qix.app.qix.models.CategoryResponse;
import qix.app.qix.models.PartnerResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import qix.app.qix.MapsActivity;
import qix.app.qix.QixpayActivity;
import qix.app.qix.vision.BarcodeCaptureActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class QixpayListFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, QixLocationInterface {

    @BindView(R.id.notFoundTText)
    TextView notFoundText;

    @BindView(R.id.qixpaySwipeLayout)
    SwipeRefreshLayout pullToRefresh;

   // @BindView(R.id.transactionsTabLayout)
   // TabLayout tabLayout;

    private List<PartnerResponse> partners = new ArrayList<>();
    private QixpayListAdapter listAdapter;
    private HashMap<String, String> categories;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_qixpay_list, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        notFoundText.setText(getResources().getString(R.string.error_not_elements_found));

        pullToRefresh.setEnabled(true);
        pullToRefresh.setOnRefreshListener(this);

        listAdapter = new QixpayListAdapter(getActivity(), partners);
        categories = new LinkedHashMap<>();

     /*   tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String c = Objects.requireNonNull(tab.getText()).toString().toLowerCase();
                QixpayListAdapter.QixpayFilter f  = (QixpayListAdapter.QixpayFilter) listAdapter.getFilter();
                f.setCategory(tab.getPosition() == 0 ? null : c);
                filterData(f, tab.getPosition() == 0 ? "" : c);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        ListView list = view.findViewById(R.id.qixpay_list);
        list.setAdapter(listAdapter);
        list.setEmptyView(notFoundText);
        list.setOnItemClickListener(this);

        return view;
    }

    private void getCategories(final Location userLocation){
        if(userLocation != null) {
            pullToRefresh.setRefreshing(true);
            AsyncRequest.getPartnersCategories(getActivity(), new Callback<List<CategoryResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                    if (response.isSuccessful()) {
                        List<CategoryResponse> result = response.body();

                     //   tabLayout.removeAllTabs();
                        categories.clear();

                        assert result != null;
                        addCategoriesToMap(result, categories);

                        for (Object o : categories.entrySet()) {
                            Map.Entry pair = (Map.Entry) o;
                           // tabLayout.addTab(tabLayout.newTab().setText(pair.getValue().toString()));
                        }

                        getPartners(userLocation);
                    } else {
                        pullToRefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable t) {
                    Helpers.presentToast(getString(R.string.no_connectio_error_message), Toast.LENGTH_SHORT);
                    pullToRefresh.setRefreshing(false);
                }
            });
        }else{
            Helpers.presentToast("Turn on Localization", Toast.LENGTH_SHORT);
            pullToRefresh.setRefreshing(false);
        }
    }

    private void addCategoriesToMap(List<CategoryResponse> categoriesList, HashMap<String, String> categoriesMap){
      //*  categoriesMap.put("", getResources().getString(R.string.qixpay_list_all_items_filter));
       categoriesMap.put("", Objects.requireNonNull(listAdapter.getcontext()).getResources().getString(R.string.qixpay_list_all_items_filter));

        CategoryResponse temp;
        for(int i = 0; i < categoriesList.size(); i++){
            temp = categoriesList.get(i);
            categoriesMap.put(temp.getId(), temp.getName());
        }
    }

    private void getPartners(Location userLocation){
        AsyncRequest.getPartners(getActivity(), userLocation.getLatitude(), userLocation.getLongitude(), Constants.MAX_RADIUS, new Callback<List<PartnerResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<PartnerResponse>> call, @NonNull Response<List<PartnerResponse>> response) {

                    if (response.isSuccessful()) {

                        List<PartnerResponse> result = response.body();

                        if (partners.size() > 0) {
                            partners.clear();
                        }

                        PartnerResponse temp;

                        assert result != null;
                        for (int i = 0; i < result.size(); i++) {
                            temp = result.get(i);
                            String category;

                            if (!categories.isEmpty()) {
                                category = categories.get(temp.getCategoryId());
                            } else {
                                category = "";
                            }

                            temp.setCategory(category);
                            partners.add(temp);
                        }

                        listAdapter.notifyDataSetChanged();
                    }

                    pullToRefresh.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<List<PartnerResponse>> call, @NonNull Throwable t) {
                    Timber.d("Errore partner %s", t.getLocalizedMessage());
                    pullToRefresh.setRefreshing(false);
                }
            });
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        MenuItem qrCodeButton = menu.findItem(R.id.qr_code_item);
        qrCodeButton.setOnMenuItemClickListener(menuItem -> {
            // launch barcode activity.
            Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
            return true;
        });

        MenuItem mapItem = menu.findItem(R.id.map_item);
        mapItem.setOnMenuItemClickListener(menuItem -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("shops", (Serializable) partners);

            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);

            return true;
        });

        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView =  (SearchView) searchMenuItem.getActionView();

        assert searchManager != null;

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

    private void filterData(QixpayListAdapter.QixpayFilter f, String s){
        f.filter(s);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        filterData((QixpayListAdapter.QixpayFilter) listAdapter.getFilter(), s);
        return true;
    }

    private PartnerResponse getPartnerById(String partnerId) {
        for(PartnerResponse partner : partners) {
            if(partner.getId().equals(partnerId)) {
                return partner;
            }
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Timber.d("Barcode read: %s", barcode.displayValue);
                    try {
                        JSONObject partnerInfo = new JSONObject(barcode.displayValue);
                        String partnerId = partnerInfo.getString("partner");
                        //Double amount = Double.valueOf(partnerInfo.getString("amount"));

                        final PartnerResponse partner = getPartnerById(partnerId);

                        if(partner != null){
                            Timber.d("Found partner: " + partner.getAmount() + " " + partner.getId());

                            new Handler().postDelayed(() -> {
                                HashMap<String, Serializable> data1 = new HashMap<>();
                                data1.put("partner", partner);
                                Helpers.startNewActivity(QixpayActivity.class,false, true, data1);
                            }, 500);
                        }else{
                            Helpers.presentToast("Partner non trovato!", Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        Helpers.presentToast(getResources().getString(R.string.barcode_error), Toast.LENGTH_SHORT);
                    }
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(getContext(), QixpayDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("partner", partners.get(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if(MainActivity.getCurrentItemIndex() == Constants.PageIndexes.QIXPAY_PAGE_INDEX.getValue()){
            Location userLocation = MainActivity.getCurrentLocation();
            if(userLocation != null) {
                getCategories(userLocation);
            }else{
                pullToRefresh.setRefreshing(false);
                Helpers.presentToast("Turn on localization", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onLocationUpdate(Location location, boolean isFirstTime) {

        if(isFirstTime && pullToRefresh != null){
            getCategories(location);
        }
    }
}
