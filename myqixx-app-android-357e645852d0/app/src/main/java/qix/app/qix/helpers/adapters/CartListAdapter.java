package qix.app.qix.helpers.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.models.Cart;
import qix.app.qix.models.CartItem;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ListElement> {

    public List<CartItem> items;

    public CartListAdapter(List<CartItem> items) {
        this.items = items;
    }

    static class ListElement extends RecyclerView.ViewHolder {

        @BindView(R.id.productSwipeLayout)
        SwipeLayout swipeLayout;

        @BindView(R.id.productImageView)
        ImageView productImageView;

        @BindView(R.id.productTitleTextView)
        TextView titleTextView;

        @BindView(R.id.productPriceTextView)
        TextView priceTextView;

        @BindView(R.id.productQixPriceTextView)
        TextView productQixPriceTextView;

        @BindView(R.id.bottom_wrapper)
        RelativeLayout bottomView;

        @BindView(R.id.slideDeleteButton)
        Button deleteButton;

        View view;

        ListElement(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, v);
        }
    }


    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public CartListAdapter.ListElement onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_qixcart_list_item, parent, false);

        return new ListElement(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NotNull ListElement holder, int position) {

        CartItem item = items.get(position);

        Picasso.get().load(item.image).into(holder.productImageView);

        holder.titleTextView.setText(item.productTitle);
        holder.priceTextView.setText(String.format("%.2f %s X %d", item.price, Constants.getShopInfo().currency, item.quantity));

        holder.productQixPriceTextView.setText(String.format("%d %s", item.getQIXPrice(), "QIX"));

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.bottomView);

        holder.deleteButton.setOnClickListener(v -> {
            holder.swipeLayout.close();
            Cart.get().removeAll(item);
            items.remove(position);
            this.notifyDataSetChanged();
        });



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}