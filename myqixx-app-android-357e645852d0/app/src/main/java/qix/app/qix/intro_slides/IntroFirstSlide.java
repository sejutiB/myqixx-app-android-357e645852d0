package qix.app.qix.intro_slides;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.custom_views.QixViewPager;

public class IntroFirstSlide extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first_slide, container, false);
      Button getStartedbutton = view.findViewById(R.id.getStartedButton);

        final QixViewPager viewPager = getActivity().findViewById(R.id.introViewPager);

        getStartedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(Constants.QIXINTRO_SECOND_PAGE_INDEX);
            }
        });


        return view;
    }
}
