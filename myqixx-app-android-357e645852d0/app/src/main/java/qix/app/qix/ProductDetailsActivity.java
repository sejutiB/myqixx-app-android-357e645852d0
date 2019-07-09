package qix.app.qix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.shopify.buy3.Storefront;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.helpers.Constants;

import qix.app.qix.helpers.adapters.GlideImageAdapter;
import qix.app.qix.models.Cart;
import qix.app.qix.models.CartItem;

public class ProductDetailsActivity extends AppCompatActivity {

    private Storefront.Product product;
    private double rate = 1.0;
    private int numOfProducts = 1;
    public TextView cartSize;
    private int productVariant = 0;

    //*  @BindView(R.id.main_backdrop)
    //* ImageView productImageView;

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
                getproductVariant().getId().toString(), product.getTitle(),
                getproductVariant().getTitle(), new BigDecimal(getproductVariant().getPrice().doubleValue() * rate),
                getProductImage().getTransformedSrc());
        int count = 0;
        while (count < numOfProducts) {
            Cart.get().add(item);
            count++;
        }

        if (numOfProducts == 1) {
            Snackbar.make(findViewById(R.id.snackbar), "Product added",
                    Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            Snackbar.make(findViewById(R.id.snackbar), "Products added",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
        cartSize.setText(String.valueOf(Cart.get().totalQuantity()));
        if (cartSize.getText().toString().equals("0")) {
            cartSize.setVisibility(View.INVISIBLE);
        } else
            cartSize.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        cartSize = findViewById(R.id.cartsizeproduct);
        Toolbar toolbar = findViewById(R.id.toolbarProductDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getProduct();
        rate = getIntent().getDoubleExtra("rate", 1.0);

        //* setTitle(product.getTitle());
        productTitle.setText(product.getTitle());
        setData();

        Spinner selectModel = findViewById(R.id.selectModel);
        if(productTitle().length==1){
            selectModel.setVisibility(View.INVISIBLE);
        }else
            selectModel.setVisibility(View.VISIBLE);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, productTitle());
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectModel.setAdapter(aa);
        selectModel.setPrompt("Select Model");
        selectModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productVariant = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                productVariant = 0;
            }
        });

    }


    private void getProduct() {
        Intent i = getIntent();
        product = (Storefront.Product) i.getSerializableExtra("product");
    }

    @SuppressLint("DefaultLocale")
    private void setData() {
        //* Picasso.get().load(getFirstImage().getTransformedSrc()).into(productImageView);
        // Glide.with(getBaseContext()).load(product.getImages().getEdges().get(0).getNode().getTransformedSrc()).into(productImageView);

        int number_of_photos = product.getImages().getEdges().size();
        String[] photoUrls = new String[number_of_photos];
        for (int p = 0; p < number_of_photos; p++) {
            photoUrls[p] = product.getImages().getEdges().get(p).getNode().getTransformedSrc();
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_photogallery);

        if (viewPager != null) {
            viewPager.setAdapter(new GlideImageAdapter(getBaseContext(), photoUrls));
        }

        priceTextView.setText(String.format("%.2f %s \n+", getFirstVariant().getPrice().doubleValue() * rate, Constants.getShopInfo().currency));
        String percent = null;
        int qixNum = 0;
        try {
            percent = getFirstVariant().getTitle().split("/")[1].replaceAll("\\D+", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (percent != null && !percent.isEmpty()) {
            qixNum = (int) Integer.valueOf(percent);
        }

        qixPriceTextView.setText(String.valueOf(qixNum));

        description.setText(Html.fromHtml(product.getDescriptionHtml()));
    }

    private Storefront.Image getFirstImage() {
        return product.getImages().getEdges().get(0).getNode();
    }

    private Storefront.Image getProductImage() {
        return product.getImages().getEdges().get(productVariant).getNode();
    }

    private Storefront.ProductVariant getFirstVariant() {
        return product.getVariants().getEdges().get(0).getNode();
    }

    private Storefront.ProductVariant getproductVariant() {
        return product.getVariants().getEdges().get(productVariant).getNode();
    }

    private String[] productTitle() {
        String[] title = new String[product.getVariants().getEdges().size()];
        for (int t = 0; t < product.getVariants().getEdges().size(); t++) {
            title[t] = product.getVariants().getEdges().get(t).getNode().getTitle();
            Log.i("product item", title[t]);
        }
        return title;
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


    public void goToCart(View view) {
        Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        cartSize.setText(String.valueOf(Cart.get().totalQuantity()));
        if (String.valueOf(Cart.get().totalQuantity()).equals("0")) {
            cartSize.setVisibility(View.INVISIBLE);
        } else {
            cartSize.setVisibility(View.VISIBLE);
        }

    }
}
