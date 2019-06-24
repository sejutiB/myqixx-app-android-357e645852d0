package qix.app.qix;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import qix.app.qix.fragments.qixbuy.QixBuyFragment;
import qix.app.qix.fragments.qixbuy.QixBuyResultFragment;

import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.adapters.QixbuyFragmentPagerAdapter;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.QixBuyFlowInterface;
import qix.app.qix.models.PartnerResponse;


public class QixbuyActivity extends AppCompatActivity implements QixBuyFlowInterface {

    //private static final String TAG = "QixpayActivity";
    private PartnerResponse partner;
    private QixViewPager pager;
    private QixbuyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       //* setTitle("QixBuy");
        //*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       //* getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_viewpager_container);

        /* Creo le istanze dei fragment */
        ArrayList<Fragment> pages = new ArrayList<>();
        pages.add(new QixBuyFragment());
        pages.add(new QixBuyResultFragment());

        adapter = new QixbuyFragmentPagerAdapter(getSupportFragmentManager(), pages);

        pager = findViewById(R.id.viewpager);
        pager.disableSwipe(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            if(pager.getCurrentItem() > 0){
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }else{
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataReceived(Map<String, Object> data) {
        Integer fragmentIndex = (Integer) data.get("fragmentIndex");
        getSupportActionBar().setDisplayHomeAsUpEnabled(fragmentIndex != Constants.QIXBUY_RESULT_PAGE_INDEX);
        getSupportActionBar().setHomeButtonEnabled(fragmentIndex != Constants.QIXBUY_RESULT_PAGE_INDEX);
        adapter.onDataReceived(data);
    }

    /*@Override
    public void onAmountReceived(Map<String, Object> data) {
        Log.d("QixpayActivity", "data received to Activity... send to view pager");

    }

    @Override
    public void onPaymentResult(Map<String, Object> data) {
        adapter.onPaymentResult(data);
    }

    @Override
    public void onFinalAmountSelected(Map<String, Object> data) {
        adapter.onFinalAmountSelected(data);
    }

    @Override
    public void onTransactionStarted(Map<String, Object> data) {
        Integer fragmentIndex = (Integer) data.get("fragmentIndex");
        getSupportActionBar().setDisplayHomeAsUpEnabled(fragmentIndex != Constants.QIXPAY_RESULT_PAGE_INDEX);
        getSupportActionBar().setHomeButtonEnabled(fragmentIndex != Constants.QIXPAY_RESULT_PAGE_INDEX);
        adapter.onTransactionStarted(data);
    }*/

}
