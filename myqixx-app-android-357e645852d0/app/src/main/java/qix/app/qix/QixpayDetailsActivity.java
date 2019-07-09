package qix.app.qix;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.PartnerResponse;


public class QixpayDetailsActivity extends AppCompatActivity {

    private PartnerResponse partner;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("QixPay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.fragment_qixpay_details);
       //* ImageView map = findViewById(R.id.mapImageview);
        RoundedImageView map = findViewById(R.id.mapImageview);
        //Button pay = findViewById(R.id.qixpayButton);
        ImageView logo = findViewById(R.id.logoImageView);

        partner = (PartnerResponse) getIntent().getSerializableExtra("partner");
        TextView partnerAdress = findViewById(R.id.partnerAddress);
        partnerAdress.setText(partner.getAddress());

       /* if(partner.getAmount() != null){
            amountText.setText(String.format(getString(R.string.double_string_format), partner.getAmount()));
        }*/

        setTitle(partner.getName());

        Picasso.get().load(partner.getImageUrl()).error(R.drawable.qix_app_icon).into(logo);

        try {
            String url = Helpers.getSignedMapUrl(getApplicationContext(), partner.getLatitude(), partner.getLongitude());
            Picasso.get().load(url).into(map);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
