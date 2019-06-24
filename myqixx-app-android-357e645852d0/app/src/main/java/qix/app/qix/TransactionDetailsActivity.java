package qix.app.qix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import qix.app.qix.helpers.Constants;
import qix.app.qix.models.Transaction;

public class TransactionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Transactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_transaction_details);

        Transaction t = (Transaction) getIntent().getSerializableExtra("transaction");

        ListView listView = findViewById(R.id.detailsListView);
        ImageView transactionImage = findViewById(R.id.transactionImage);

        if(t.getType() != Constants.TransactionType.MARKETPLACE && t.getType() != Constants.TransactionType.SHAKE && t.getType() != Constants.TransactionType.BUY ){
            Picasso.get().load(t.getImageUrl()).error(R.drawable.qix_app_icon).into(transactionImage);
        }

        listView.setAdapter(new TransactionDetailsAdapter(this, t));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class TransactionDetailsAdapter extends BaseAdapter {


        private Context context;

        private ArrayList<Constants.TransactionData> data;

        public TransactionDetailsAdapter(Activity context, Transaction transaction) {
            this.context = context;
            try {
                this.data = transaction.getData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Constants.TransactionData getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            TransactionDetailsAdapter.ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_transaction_details_item, parent, false);
                viewHolder = new TransactionDetailsAdapter.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (TransactionDetailsAdapter.ViewHolder) convertView.getTag();
            }

            Constants.TransactionData tData = getItem(position);

            if(tData.title.equals("Identifier")){
                viewHolder.data.setTextSize(12.0f);
            }else{
                viewHolder.data.setTextSize(14.0f);
            }

            viewHolder.title.setText(getItem(position).title);
            viewHolder.data.setText(getItem(position).value);

            return convertView;
        }


        private class ViewHolder {
            TextView title;
            TextView data;

            ViewHolder(View view) {
                title =  view.findViewById(R.id.titleTextView);
                data =  view.findViewById(R.id.dataTextView);
            }
        }
    }
}
