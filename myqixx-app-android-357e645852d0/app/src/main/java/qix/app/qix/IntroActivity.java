package qix.app.qix;

import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import qix.app.qix.helpers.adapters.QixFragmentPagerAdapter;
import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.intro_slides.IntroFirstSlide;
import qix.app.qix.intro_slides.IntroSecondSlide;
import qix.app.qix.intro_slides.IntroThirdSlide;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        QixViewPager viewPager = findViewById(R.id.introViewPager);
        viewPager.disableSwipe(false);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new IntroFirstSlide());
        fragments.add(new IntroSecondSlide());
        fragments.add(new IntroThirdSlide());

        QixFragmentPagerAdapter adapter = new QixFragmentPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}