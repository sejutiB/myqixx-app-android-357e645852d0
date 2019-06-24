package qix.app.qix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.Cart;
import qix.app.qix.models.CartItem;

public class ProductDetailsActivity extends AppCompatActivity {

    private Storefront.Product product;
    private int numOfProducts = 1;

    @BindView(R.id.main_backdrop)
    ImageView productImageView;

    @BindView(R.id.mainTextView)
    TextView description;

    @BindView(R.id.TextView_productTitle)
    TextView productTitle;

    @BindView(R.id.priceTextView)
    TextView priceTextView;

    @BindView(R.id.TextView_numberOfProducts)
    TextView numberofProducts;

    @BindView(R.id.qixNumText)
    TextView qixPriceTextView;

    @OnClick(R.id.productFab)
    void addToCartPressed() {
        CartItem item = new CartItem(product.getId().toString(),
                getFirstVariant().getId().toString(), product.getTitle(),
                getFirstVariant().getTitle(), getFirstVariant().getPrice(),
                getFirstImage().getTransformedSrc());
        int count = 0;
        while (count < numOfProducts) {
            Cart.get().add(item);
            count++;
        }

        if(numOfProducts==1) {
            Snackbar.make(findViewById(R.id.snackbar), "Product added",
                    Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            Snackbar.make(findViewById(R.id.snackbar), "Products added",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }

       // Helpers.presentToast("Product Added!", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //* Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //* getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        getProduct();


        //* setTitle(product.getTitle());
        productTitle.setText(product.getTitle());
        setData();
    }

    private void getProduct() {
        Intent i = getIntent();
        product = (Storefront.Product) i.getSerializableExtra("product");
    }

    @SuppressLint("DefaultLocale")
    private void setData() {
        Picasso.get().load(getFirstImage().getTransformedSrc()).into(productImageView);

        priceTextView.setText(String.format("%.2f %s \n %s", getFirstVariant().getPrice(), Constants.getShopInfo().currency, "+"));

        int qixNum = 0;
        String percent = getFirstVariant().getTitle().split("/")[1].replaceAll("\\D+", "");
        if (!percent.isEmpty()) {
            qixNum = (int) Math.ceil(getFirstVariant().getPrice().doubleValue() * Integer.valueOf(percent).doubleValue());
        }

        qixPriceTextView.setText(String.valueOf(qixNum));

        description.setText(Html.fromHtml(product.getDescriptionHtml()));
    }

    private Storefront.Image getFirstImage() {
        return product.getImages().getEdges().get(0).getNode();
    }

    private Storefront.ProductVariant getFirstVariant() {
        return product.getVariants().getEdges().get(0).getNode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void increaseProductsNumber(View view) {
        if (numOfProducts < 10) {
            numOfProducts = numOfProducts + 1;
            numberofProducts.setText(String.valueOf(numOfProducts));
            // Toast.makeText(this, "increase", Toast.LENGTH_SHORT).show();
        }
    }

    public void decreaseProductsNumber(View view) {
        if (numOfProducts > 1) {
            numOfProducts = numOfProducts - 1;
            numberofProducts.setText(String.valueOf(numOfProducts));
            // Toast.makeText(this, "decrease", Toast.LENGTH_SHORT).show();
        }
    }
}
