package qix.app.qix.helpers.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import qix.app.qix.R;
import qix.app.qix.models.OfferResponse;

public class HomeGridAdapter extends BaseAdapter {

    private Context context;
    private List<OfferResponse> offers;

    public HomeGridAdapter(Context context, List<OfferResponse> offers) {
        this.context = context;
        this.offers = offers;
    }

    public void setData(List<OfferResponse> offers){
        this.offers = offers;
    }

    @Override
    public int getCount() {
        return offers != null ? offers.size() : 0;
    }

    @Override
    public OfferResponse getItem(int i) {
        return offers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        OfferResponse offer = getItem(position);

        if (convertView == null) {
            assert inflater != null;
            gridView = inflater.inflate(R.layout.layout_image_button_background, null);
        } else {
            gridView = convertView;
        }

        // set image based on selected text
        ImageView backgroundImage =  gridView.findViewById(R.id.mainOfferImage);
        TextView title = gridView.findViewById(R.id.offerTitle);
       // TextView address = gridView.findViewById(R.id.offerAddress);

        title.setText(offer.getTitle());
       // address.setText(offer.getAddress());

        Log.d("IMAGE Name", offer.getTitle());
        Picasso.get()
                .load(offer.getOfferImageUrl())
                .into(backgroundImage/*, new Callback() {
                        @Override
                        public void onSuccess() {
                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progress.setVisibility(View.GONE);
                        }
                    }*/);

            /*Picasso.get()
                    .load(offer.getPartnerImageUrl())
                    .into(logoImge);*/

        return gridView;
    }

}
