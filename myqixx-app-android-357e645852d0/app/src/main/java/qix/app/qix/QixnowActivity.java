package qix.app.qix;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import qix.app.qix.fragments.qixnow.QixnowMainFragment;
import qix.app.qix.helpers.adapters.QixnowFragmentPagerAdapter;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.models.QIXPayment;

public class QixnowActivity extends AppCompatActivity  {

    private QixViewPager pager;
    private QixnowFragmentPagerAdapter adapter;
    private QIXPayment qrCodeData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("QixPay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_viewpager_container);


        /* Creo le istanze dei fragment */
        ArrayList<Fragment> pages = new ArrayList<>();
        pages.add(new QixnowMainFragment());

        Intent i = getIntent();
        qrCodeData = (QIXPayment) i.getSerializableExtra("qrCodeData");


        adapter = new QixnowFragmentPagerAdapter(getSupportFragmentManager(), pages);

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

    public QIXPayment getQrCodeData(){
        return qrCodeData;
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

}
