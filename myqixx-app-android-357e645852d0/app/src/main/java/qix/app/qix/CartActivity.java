package qix.app.qix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.RetryHandler;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Input;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.adapters.CartListAdapter;
import qix.app.qix.models.BalanceResponse;
import qix.app.qix.models.Cart;
import qix.app.qix.models.CartItem;
import qix.app.qix.models.SuccessResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.cartListView)
    RecyclerView cartListView;

    @BindView(R.id.totalTextView)
    TextView totalTextView;

    @BindView(R.id.qixNumText)
    TextView qixTotalTextView;

    @BindView(R.id.checkoutButton)
    Button checkoutButton;

    @BindView(R.id.text_empty)
    TextView emptyTextView;

    @BindView(R.id.bottomView)
    ConstraintLayout bottomView;


    private ID currentCheckoutId;
    private List<CartItem> items;
    private CartListAdapter adapter;

    @OnClick(R.id.checkoutButton) void checkout() {
        if(items.size() > 0){
            AsyncRequest.getBalace(this, new Callback<BalanceResponse>() {
                @Override
                public void onResponse(@NonNull Call<BalanceResponse> call, @NonNull Response<BalanceResponse> response) {
                    if(response.isSuccessful()){
                        BalanceResponse result = response.body();

                        assert result != null;
                        if(Cart.get().qixTotal() < result.getBalance()){
                            createCheckout();
                        }else{
                            Helpers.presentToast("Not enough QIX", Toast.LENGTH_SHORT);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BalanceResponse> call, @NonNull Throwable t) {
                    Helpers.presentToast("Cannot retrieve balance", Toast.LENGTH_SHORT);
                }
            });
        }else{
            Helpers.presentToast("Add some products in your cart...", Toast.LENGTH_SHORT);
        }

    }

    private void createCheckout(){

        Storefront.CheckoutCreateInput input = this.getCheckoutInput();
        Storefront.MutationQuery query = this.getCheckoutQuery(input);

        AsyncRequest.getClient(this).mutateGraph(query).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.data().getCheckoutCreate().getUserErrors().isEmpty()) {
                    // handle user friendly errors

                    Log.d("TAG", "Errore");
                } else {

                    Storefront.Checkout checkout = response.data().getCheckoutCreate().getCheckout();
                    currentCheckoutId = checkout.getId();

                    String checkoutWebUrl = checkout.getWebUrl();

                    Intent i = new Intent(getApplicationContext(), WebviewActivity.class);

                    i.putExtra("paymentUrl", checkoutWebUrl);

                    startActivityForResult(i, 2);
                }
            }

            @Override public void onFailure(@NonNull GraphError error) {
                // handle errors
            }
        });
    }

    @SuppressLint({"WrongConstant", "DefaultLocale"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        items = Cart.get().cartItems();

        if(items.size() == 0){
            bottomView.setVisibility(View.INVISIBLE);
        }

        adapter = new CartListAdapter(items);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
                refreshTotals();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
                refreshTotals();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
                refreshTotals();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        cartListView.setHasFixedSize(true);
        cartListView.setLayoutManager(llm);
        cartListView.setAdapter(adapter);

        refreshTotals();
        checkEmpty();
    }

    @SuppressLint("DefaultLocale")
    private void refreshTotals(){
        totalTextView.setText(String.format("%.2f %s\n+", Cart.get().totalPrice(), Constants.getShopInfo().currency));
        qixTotalTextView.setText(String.valueOf(Cart.get().qixTotal()));
    }

    private void checkEmpty(){
        emptyTextView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private Storefront.CheckoutCreateInput getCheckoutInput(){

        List<Storefront.CheckoutLineItemInput> inputItems = new ArrayList<>();

        for(CartItem i : items){
            inputItems.add(new Storefront.CheckoutLineItemInput(i.quantity, new ID(i.productVariantId)));
        }

        return new Storefront.CheckoutCreateInput()
                .setLineItemsInput(Input.value(inputItems));
    }

    private Storefront.MutationQuery getCheckoutQuery(Storefront.CheckoutCreateInput input){
        return Storefront.mutation(mutationQuery -> mutationQuery
            .checkoutCreate(input, createPayloadQuery -> createPayloadQuery
                .checkout(Storefront.CheckoutQuery::webUrl)
                .userErrors(userErrorQuery -> userErrorQuery
                    .field()
                    .message()
                )
            )
        );
    }

    private Storefront.QueryRootQuery getPaymentStatusQuery(ID checkoutId){
        return Storefront.query(rootQuery -> rootQuery
            .node(checkoutId, nodeQuery -> nodeQuery
                .onCheckout(checkoutQuery -> checkoutQuery
                    .order(orderQuery -> orderQuery
                        .orderNumber()
                        .processedAt()
                        .totalPrice()
                    )
                )
            )
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){
            Storefront.QueryRootQuery query = this.getPaymentStatusQuery(currentCheckoutId);

            AsyncRequest.getClient(this).queryGraph(query).enqueue(
                    new GraphCall.Callback<Storefront.QueryRoot>() {
                        @Override public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                            Storefront.Checkout checkout = (Storefront.Checkout) response.data().getNode();

                            if(checkout.getOrder() != null){
                                String orderIdBase64 = checkout.getOrder().getId().toString();

                                byte[] data = Base64.decode(orderIdBase64, Base64.DEFAULT);
                                Uri orderIdURL = Uri.parse(new String(data, StandardCharsets.UTF_8));

                                String path = orderIdURL.getPath();

                                String orderId = path.substring(path.lastIndexOf('/') + 1);
                                startTransaction(orderId);

                            }

                        }
                        @Override public void onFailure(@NonNull GraphError error) {

                        }
                    }, null,
                    RetryHandler.exponentialBackoff(500, TimeUnit.MILLISECONDS, 1.2f)
                        .whenResponse(
                                response -> ((Storefront.Checkout) ((GraphResponse<Storefront.QueryRoot>) response).data().getNode()).getOrder() == null
                        )
                        .maxCount(5)
                        .build()
            );
        }
    }

    private void startTransaction(String orderId){
        AsyncRequest.startPaypalTransaction(this, orderId, new Callback<SuccessResponse>() {
            @Override
            public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {

                if(response.isSuccessful()) {
                    SuccessResponse result = response.body();
                    assert result != null;
                    if(result.isSuccessful()){
                        Cart.get().clear();
                        adapter.items.clear();
                        adapter.notifyDataSetChanged();
                        Helpers.presentToast("Pagamento completato!", Toast.LENGTH_SHORT);
                        new Handler().postDelayed(() -> finish(), 1500);
                    }else{
                        Helpers.presentToast("Errore di pagamento!", Toast.LENGTH_SHORT);
                    }
                }else{
                    Helpers.presentToast("Errore", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("No connection", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
