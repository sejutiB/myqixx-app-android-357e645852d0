package qix.app.qix;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import qix.app.qix.fragments.recovery_password.RecoveryFirstFragment;
import qix.app.qix.fragments.recovery_password.RecoverySecondFragment;
import qix.app.qix.fragments.recovery_password.RecoveryThirdFragment;
import qix.app.qix.helpers.adapters.QixFragmentPagerAdapter;
import qix.app.qix.helpers.adapters.QixRecoveryPasswordFragmentPagerAdapter;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.RecoveryFlowInterface;

public class RecoveryPasswordActivity extends AppCompatActivity implements RecoveryFlowInterface {

    private QixViewPager pager;
    private QixRecoveryPasswordFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Recovery password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_signup);

        pager = findViewById(R.id.signupViewPager);
        pager.disableSwipe(!BuildConfig.DEBUG);

        pager.setOffscreenPageLimit(3);
        List<Fragment> pages = new ArrayList<>();
        pages.add(new RecoveryFirstFragment());
        pages.add(new RecoverySecondFragment());
        pages.add(new RecoveryThirdFragment());

        ProgressBar progressBar = findViewById(R.id.signupProgress);
        progressBar.setVisibility(View.GONE);

        adapter = new QixRecoveryPasswordFragmentPagerAdapter(getSupportFragmentManager(), pages);
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
    public void onBackPressed() {
        /*if(pager.getCurrentItem() != Constants.QIXSIGNUP_FOURTH_PAGE_INDEX){
            if(pager.getCurrentItem() > 0){
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }else{
                super.onBackPressed();
            }
        }*/
    }

    @Override
    public void onEmailReceived(String email) {
        adapter.onEmailReceived(email);
    }

    @Override
    public void onRecoveryCompleted(boolean success) {
        adapter.onRecoveryCompleted(success);
    }
}
