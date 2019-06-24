package qix.app.qix.fragments.shaketutorial;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

import butterknife.BindView;
import qix.app.qix.AudioScanningActivity;
import qix.app.qix.MainActivity;
import qix.app.qix.R;
import qix.app.qix.ShakeDetectorActivity;
import qix.app.qix.ShakeTutorial;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.custom_views.QixViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class shaketutorial_third extends Fragment {
    public shaketutorial_third() {
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Nullable
    @SuppressLint({"SetTextI18n", "ResourceType", "StringFormatMatches"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_shaketutorial_third, container, false);

        QixViewPager viewPager = getActivity().findViewById(R.id.shakeTutorialViewPager);
        ((ShakeTutorial)getActivity()).onshakedetected();
        return view;
    }


}
