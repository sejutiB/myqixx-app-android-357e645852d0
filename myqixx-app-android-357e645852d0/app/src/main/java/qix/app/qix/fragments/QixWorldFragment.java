package qix.app.qix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.WebviewActivity;
import qix.app.qix.helpers.Helpers;

public class QixWorldFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qixmondo, container, false);

       /* Helpers.setCustomButtonData(view, R.id.firstButton, getResources().getString(R.string.intro_second_smart_button), R.drawable.smart_city, R.drawable.button_red_background);
        Helpers.setCustomButtonData(view, R.id.secondButton, getResources().getString(R.string.intro_second_travel_button), R.drawable.travel, R.drawable.button_orange_background);
        Helpers.setCustomButtonData(view, R.id.thirdButton, getResources().getString(R.string.intro_second_qix_market), R.drawable.qix_market, R.drawable.button_violet_background);
        Helpers.setCustomButtonData(view, R.id.fourButton, getResources().getString(R.string.intro_second_media_button), R.drawable.media, R.drawable.button_azure_background);
        Helpers.setCustomButtonData(view, R.id.fifthButton, getResources().getString(R.string.intro_second_smart_stadium_button), R.drawable.smart_stadium, R.drawable.button_light_green_background);
        Helpers.setCustomButtonData(view, R.id.sixButton, getResources().getString(R.string.intro_second_shops_button), R.drawable.qix_shops, R.drawable.button_water_background);
        Helpers.setCustomButtonData(view, R.id.sevenButton, getResources().getString(R.string.intro_second_send_button), R.drawable.qix_request, R.drawable.button_yellow_background);
        Helpers.setCustomButtonData(view, R.id.eigthButton, getResources().getString(R.string.intro_second_smart_driving), R.drawable.smart_driving, R.drawable.button_pink_background);
        Helpers.setCustomButtonData(view, R.id.nineButton, getResources().getString(R.string.intro_second_airport_button), R.drawable.airport, R.drawable.button_light_blue_background);
        Helpers.setCustomButtonData(view, R.id.tenButton, getResources().getString(R.string.intro_second_smart_life_button), R.drawable.smart_life, R.drawable.button_gold_background);
*/
        view.findViewById(R.id.secondButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), WebviewActivity.class);

                startActivity(i);
            }
        });

        return view;

    }
}
