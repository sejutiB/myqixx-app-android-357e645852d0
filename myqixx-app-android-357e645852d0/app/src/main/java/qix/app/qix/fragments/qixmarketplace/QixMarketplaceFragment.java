package qix.app.qix.fragments.qixmarketplace;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.CartActivity;
import qix.app.qix.MainActivity;
import qix.app.qix.ProductDetailsActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;

public class QixMarketplaceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Storefront.Product> products = new ArrayList<>();
    private ProductsAdapter adapter;

    @BindView(R.id.productsGridView)
    GridView gridView;
    private Toolbar custom_Toolbar;


    @BindView(R.id.qixmarketplaceSwipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qixmarketplace, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        custom_Toolbar = view.findViewById(R.id.toolbar_custom_qixmarketplace);
        adapter = new ProductsAdapter(getActivity(), products);
        gridView.setAdapter(adapter);
        Button toolbarButton = view.findViewById(R.id.button_qixmarketplace_cart);
        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener((adapterView, view1, pos, l) -> {
            Storefront.Product p = products.get(pos);

            Intent i = new Intent(getActivity(), ProductDetailsActivity.class);
            i.putExtra("product", p);

            startActivity(i);
        });

        refreshLayout.setOnRefreshListener(this);

        getProducts();

        return view;
    }

    private void getProducts() {
        GraphClient client = AsyncRequest.getClient(getActivity());

        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .shop(shopQuery -> shopQuery
                        .products(arg -> arg.first(20), productConnectionQuery -> productConnectionQuery
                                .edges(productEdgeQuery -> productEdgeQuery
                                        .node(productQuery -> productQuery
                                                .title()
                                                .productType()
                                                .description()
                                                .descriptionHtml()
                                                .images(arg -> arg.first(250), imageConnectionQuery -> imageConnectionQuery
                                                        .edges(imageEdgeQuery -> imageEdgeQuery
                                                                .node(Storefront.ImageQuery::transformedSrc)
                                                        )
                                                )
                                                .variants(arg -> arg.first(250), variantConnectionQuery -> variantConnectionQuery
                                                        .edges(variantEdgeQuery -> variantEdgeQuery
                                                                .node(variantQuery -> variantQuery
                                                                        .sku()
                                                                        .title()
                                                                        .price()
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        client.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {

            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                assert response.data() != null;
                for (Storefront.ProductEdge productEdge : response.data().getShop().getProducts().getEdges()) {
                    products.add(productEdge.getNode());
                }

                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    adapter.setData(products);
                    adapter.notifyDataSetChanged();
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                });
            }


            @Override
            public void onFailure(@NonNull GraphError error) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

   /* private void getProducts(){
        this.getProducts(20);
    }*/

    @Override
    public void onRefresh() {
        products.clear();
        getProducts();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.marketplace, menu);

        menu.findItem(R.id.cart_button).getActionView().setOnClickListener(v -> {// launch barcode activity.
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });
    }


    public class ProductsAdapter extends BaseAdapter {

        @BindView(R.id.productImageView)
        ImageView backgroundImage;

        @BindView(R.id.productTitleTextView)
        TextView title;

        @BindView(R.id.productPriceTextView)
        TextView price;

        @BindView(R.id.qixNumText)
        TextView qix;

        private Context context;
        private List<Storefront.Product> products;

        ProductsAdapter(Context context, List<Storefront.Product> products) {
            this.context = context;
            this.products = products;
        }

        public void setData(List<Storefront.Product> products) {
            this.products = products;
        }

        @Override
        public int getCount() {
            return products != null ? products.size() : 0;
        }

        @Override
        public Storefront.Product getItem(int i) {
            return products.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        private Storefront.ProductVariant getVariant(Storefront.Product p) {
            List<Storefront.ProductVariant> variants = new ArrayList<>();
            if (p.getVariants() != null) {
                for (Storefront.ProductVariantEdge variantEdge : p.getVariants().getEdges()) {
                    variants.add(variantEdge.getNode());
                }
                return variants.get(0);
            }
            return null;
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView;
            Storefront.Product product = getItem(position);
            Storefront.ProductVariant variant = getVariant(product);

            if (convertView == null) {
                assert inflater != null;
                gridView = inflater.inflate(R.layout.layout_product_view, null);
            } else {
                gridView = convertView;
            }

            ButterKnife.bind(this, gridView);

            title.setText(product.getTitle());

            assert variant != null;
            price.setText(String.format("%.2f %s", variant.getPrice().doubleValue(), Constants.getShopInfo().currency));

            int qixNum = 0;
            String percent = variant.getTitle().split("/")[1].replaceAll("\\D+", "");
            if (!percent.isEmpty()) {
                qixNum = (int) Math.ceil(variant.getPrice().doubleValue() * Integer.valueOf(percent).doubleValue());
            }


            qix.setText(" + "+String.valueOf(qixNum));

            Picasso.get().load(product.getImages().getEdges().get(0).getNode().getTransformedSrc()).into(backgroundImage);

            return gridView;
        }
    }
}
