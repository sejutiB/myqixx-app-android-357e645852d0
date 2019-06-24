package qix.app.qix;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayChartFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayPaymentResultFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayReviewAmountFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpaySelectFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayTypeFragment;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.helpers.adapters.QixpayFragmentPagerAdapter;
import qix.app.qix.models.PartnerResponse;
import qix.app.qix.models.QIXPayment;


public class QixpayActivity extends AppCompatActivity implements PaymentFlowInterface {

    //private static final String TAG = "QixpayActivity";
    private QixViewPager pager;
    private QixpayFragmentPagerAdapter adapter;
    private Location location;
    private QIXPayment qrCodeData;
    private Boolean isGettingQix = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*  setTitle("QixPay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/
        setContentView(R.layout.activity_viewpager_container);

        Intent intent = getIntent();
        location = intent.getParcelableExtra("location");
        qrCodeData = (QIXPayment) intent.getSerializableExtra("qrCodeData");


        /* Creo le istanze dei fragment */
        ArrayList<Fragment> pages = new ArrayList<>();
        pages.add(new QixpayTypeFragment());
        pages.add(new QixpaySelectFragment());
        pages.add(new QixpayChartFragment());
        pages.add(new QixpayReviewAmountFragment());
        pages.add(new QixpayPaymentResultFragment());

        adapter = new QixpayFragmentPagerAdapter(getSupportFragmentManager(), pages);

        pager = findViewById(R.id.viewpager);
        pager.disableSwipe(true);

        pager.setOffscreenPageLimit(4);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        pager.setAdapter(adapter);
    }

    public void setIsGettingQix(Boolean value){
        this.isGettingQix = value;
    }

   /* private PartnerResponse getPartner(List<PartnerResponse> partners, String id){
        for(PartnerResponse partner : partners){
            if(partner.getId().equals(id)){
                return partner;
            }
        }
        return null;
    }*/

  /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            if(pager.getCurrentItem() > 0){
                if(pager.getCurrentItem() == Constants.QIXPAY_CHART_PAGE_INDEX && isGettingQix){
                    pager.setCurrentItem(pager.getCurrentItem() - 2);
                }else {
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
                }
            }else{
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }*/

    /*public PartnerResponse getShop(){
        return partner;
    }*/

    public Location getUserLocation(){
        return location;
    }

    public QIXPayment getQrCodeData(){
        return qrCodeData;
    }

    @Override
    public void onAmountReceived(Map<String, Object> data, PartnerResponse partner) {
        Log.d("QixpayActivity", "data received to Activity... send to view pager");
        adapter.onAmountReceived(data, partner);
    }

    @Override
    public void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner) {
        adapter.onSelectQixNumber(data, partner);
    }

    @Override
    public void onPaymentResult(Map<String, Object> data, PartnerResponse partner) {
        adapter.onPaymentResult(data, partner);
    }

    @Override
    public void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner) {
        adapter.onFinalAmountSelected(data, partner);
    }

    @Override
    public void onTransactionStarted(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer) data.get("fragmentIndex");
       //* getSupportActionBar().setDisplayHomeAsUpEnabled(fragmentIndex != Constants.QIXPAY_RESULT_PAGE_INDEX);
      ///*  getSupportActionBar().setHomeButtonEnabled(fragmentIndex != Constants.QIXPAY_RESULT_PAGE_INDEX);
        adapter.onTransactionStarted(data, partner);
    }

}
