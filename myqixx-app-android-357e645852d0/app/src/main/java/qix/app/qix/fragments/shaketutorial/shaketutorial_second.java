package qix.app.qix.fragments.shaketutorial;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.custom_views.QixViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class shaketutorial_second extends Fragment {

    public shaketutorial_second() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_shaketutorial_second, container, false);
        final QixViewPager viewPager = getActivity().findViewById(R.id.shakeTutorialViewPager);

        Button next = view.findViewById(R.id.nextbutton2);
        Button skip = view.findViewById(R.id.skip_shake2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(Constants.QIXSHAKE_THIRD_PAGE_INDEX);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        return view;
    }

}
