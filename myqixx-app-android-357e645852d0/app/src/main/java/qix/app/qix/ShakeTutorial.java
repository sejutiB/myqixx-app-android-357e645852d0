package qix.app.qix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import qix.app.qix.fragments.shaketutorial.shaketutorial_first;
import qix.app.qix.fragments.shaketutorial.shaketutorial_fourth;
import qix.app.qix.fragments.shaketutorial.shaketutorial_second;
import qix.app.qix.fragments.shaketutorial.shaketutorial_third;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.adapters.QixFragmentPagerAdapter;
import qix.app.qix.helpers.custom_views.QixViewPager;

public class ShakeTutorial extends ShakeDetectorActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private  QixViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_tutorial);


        pager = findViewById(R.id.shakeTutorialViewPager);
        pager.disableSwipe(false);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new shaketutorial_first());
        fragments.add(new shaketutorial_second());
        fragments.add(new shaketutorial_third());
        fragments.add(new shaketutorial_fourth());

        QixFragmentPagerAdapter adapter = new QixFragmentPagerAdapter(getSupportFragmentManager(), fragments);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAdapter(adapter);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        assert mSensorManager != null;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void onshakedetected(){
        setOnShakeListener(count -> {
            Vibrator v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else{
                v.vibrate(500);
            }
          pager.setCurrentItem(Constants.QIXSHAKE_FOURTH_PAGE_INDEX);

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        return;
    }
}
