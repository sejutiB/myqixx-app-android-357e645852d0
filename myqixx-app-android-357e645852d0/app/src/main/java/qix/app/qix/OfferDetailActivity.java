package qix.app.qix;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import qix.app.qix.models.OfferResponse;

public class OfferDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //*  getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_offer_detail);

        Intent i = getIntent();

        OfferResponse offer = (OfferResponse) i.getSerializableExtra("offer");

        //* setTitle(offer.getTitle());
        TextView offertitle = findViewById(R.id.TextView_offerTitle);
        TextView desc = findViewById(R.id.descTextView);
        TextView discont = findViewById(R.id.discountTextView);
        ImageView partnerImageView = findViewById(R.id.partnerImageView);

        offertitle.setText(offer.getTitle());
        desc.setText(offer.getDescription());
        discont.setText(String.format("%1$s %2$s", offer.getDiscount(), offer.getDiscountType()));
        Picasso.get().load(offer.getOfferImageUrl()).error(R.drawable.qix_app_icon).into(partnerImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
