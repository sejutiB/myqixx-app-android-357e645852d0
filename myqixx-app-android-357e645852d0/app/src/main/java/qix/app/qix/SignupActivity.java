package qix.app.qix;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import qix.app.qix.fragments.sign_up.SignupFirstFragment;
import qix.app.qix.fragments.sign_up.SignupFourthFragment;
import qix.app.qix.fragments.sign_up.SignupSecondFragment;
import qix.app.qix.fragments.sign_up.SignupThirdFragment;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.adapters.QixSignupFragmentPagerAdapter;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.helpers.interfaces.SignupFlowInterface;

public class SignupActivity extends AppCompatActivity implements SignupFlowInterface{

    private QixViewPager pager;
    private QixSignupFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Signup");
      //*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //* getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_signup);

        pager = findViewById(R.id.signupViewPager);
        pager.disableSwipe(true);

        pager.setOffscreenPageLimit(3);
        List<Fragment> pages = new ArrayList<>();
        pages.add(new SignupFirstFragment());
        pages.add(new SignupSecondFragment());
        pages.add(new SignupThirdFragment());
        pages.add(new SignupFourthFragment());

        ProgressBar progressBar = findViewById(R.id.signupProgress);
        progressBar.setVisibility(View.GONE);

        adapter = new QixSignupFragmentPagerAdapter(getSupportFragmentManager(), pages);
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
        if(pager.getCurrentItem() != Constants.QIXSIGNUP_FOURTH_PAGE_INDEX){
            if(pager.getCurrentItem() > 0){
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onFirstPageComplete(HashMap<String, String> data) {
        adapter.onFirstPageComplete(data);
    }

    @Override
    public void onSecondPageComplete(HashMap<String, String> data) {
        adapter.onSecondPageComplete(data);
    }

    @Override
    public void onSignupComplete(boolean success) {
        adapter.onSignupComplete(success);
    }
}
