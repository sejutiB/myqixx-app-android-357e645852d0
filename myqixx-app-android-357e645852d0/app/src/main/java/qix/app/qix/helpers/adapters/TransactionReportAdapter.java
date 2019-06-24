package qix.app.qix.helpers.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;

import qix.app.qix.models.ShakeTransaction;
import qix.app.qix.models.Transaction;

public class TransactionReportAdapter extends BaseAdapter {

    private final FragmentActivity context;

    private List<Transaction> transactions;
    private Constants.TransactionType type;

    public TransactionReportAdapter(FragmentActivity context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    public void setTransactionData(List<Transaction> transactions){
        this.transactions = transactions;
        this.type = Constants.TransactionType.PAY_OR_BUY;
    }

    @Override
    public int getCount() {
        return transactions == null ? 0 : transactions.size();
    }

    @Override
    public Object getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TransactionReportAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_transaction_report_list_item, parent, false);
            viewHolder = new TransactionReportAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (TransactionReportAdapter.ViewHolder) convertView.getTag();
        }

        if(type == Constants.TransactionType.PAY_OR_BUY){
            Transaction t = (Transaction) getItem(position);

            viewHolder.totSpent.setText(t.getFiatUserAmount() + " " + t.getFiatUserType());



            if (t.getType() == Constants.TransactionType.PAY || t.getType() == Constants.TransactionType.PAY_IN_STORE) {

                viewHolder.name.setText(t.getName().toUpperCase());

                if (t.getQixUserReward() == 0) {
                    viewHolder.value.setTextColor(context.getResources().getColor(R.color.colorDanger));
                    viewHolder.value.setText(String.format(context.getString(R.string.numer_negative_format), Helpers.getIntNumberFormattedString(t.getQixUserAmount())));
                } else {
                    viewHolder.value.setTextColor(context.getResources().getColor(R.color.greenStart));
                    viewHolder.value.setText(String.format(context.getResources().getString(R.string.numer_positive_format), Helpers.getIntNumberFormattedString(t.getQixUserReward())));
                }

                Picasso.get().load(t.getImageUrl()).error(R.drawable.qix_app_icon_small).into(viewHolder.imageView);

            } else if(t.getType() == Constants.TransactionType.BUY) {
                viewHolder.value.setTextColor(context.getResources().getColor(R.color.greenStart));
                viewHolder.value.setText(String.format(context.getString(R.string.numer_positive_format), Helpers.getIntNumberFormattedString(t.getQixUserAmount())));

                viewHolder.name.setText("QIXBUY");
                viewHolder.totSpent.setText(t.getFiatUserAmount() != null ? t.getFiatUserAmount().toString() : "0");

                Picasso.get().load(R.drawable.qix_app_icon_small).into(viewHolder.imageView);

            }else if(t.getType() == Constants.TransactionType.SHAKE) {
                //SHAKE
                viewHolder.value.setTextColor(context.getResources().getColor(R.color.greenStart));
                viewHolder.value.setText(String.format(context.getString(R.string.numer_positive_format), Helpers.getIntNumberFormattedString(t.getQixUserReward())));

                viewHolder.name.setText("QIXSHAKE");

                 viewHolder.totSpent.setText("");


                Picasso.get().load(R.drawable.qix_app_icon_small).into(viewHolder.imageView);
            }else if(t.getType() == Constants.TransactionType.TRAVEL){
                //TRAVEL
                viewHolder.value.setTextColor(context.getResources().getColor(R.color.greenStart));
                viewHolder.value.setText(String.format(context.getString(R.string.numer_positive_format), Helpers.getIntNumberFormattedString(t.getQixUserAmount())));

                viewHolder.name.setText("QIXTRAVEL");
                viewHolder.totSpent.setText(t.getFiatUserAmount() != null ? t.getFiatUserAmount().toString() : "0");


                if(t.getImageUrl().isEmpty()){
                    Picasso.get().load(R.drawable.qix_app_icon_small).into(viewHolder.imageView);
                }else{
                    Picasso.get().load(t.getImageUrl()).error(R.drawable.qix_app_icon_small).into(viewHolder.imageView);

                }
            }else{
                //MARKETPLACE
                viewHolder.value.setTextColor(context.getResources().getColor(R.color.greenStart));
                viewHolder.value.setText(String.format(context.getString(R.string.numer_positive_format), Helpers.getIntNumberFormattedString(t.getQixUserAmount())));

                viewHolder.name.setText("QIXMARKETPLACE");
                viewHolder.totSpent.setText(t.getFiatUserAmount() != null ? t.getFiatUserAmount().toString() : "0");
                Picasso.get().load(R.drawable.qix_app_icon_small).into(viewHolder.imageView);

            }

            try {
                viewHolder.totSpent.setText(viewHolder.totSpent.getText().toString() + " - " + Helpers.getStringFrom(Helpers.getDateFrom(t.getTimestamp(), "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"), "dd/MM/yyyy HH:mm"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else if(type == Constants.TransactionType.SHAKE){
            ShakeTransaction st = (ShakeTransaction) getItem(position);
            viewHolder.name.setText(st.getPrizeTitle());

            viewHolder.value.setTextColor(context.getResources().getColor(R.color.greenStart));
            viewHolder.value.setText(String.format(context.getResources().getString(R.string.numer_positive_format), Helpers.getIntNumberFormattedString(st.getQix())));

            Picasso.get().load(R.drawable.qix_app_icon_small).into(viewHolder.imageView);
        }

        return convertView;
    }


    private class ViewHolder {
        TextView name;
        ImageView imageView;
        TextView value;
        TextView totSpent;

        ViewHolder(View view) {
            name =  view.findViewById(R.id.transactionPartnerName);
            imageView =  view.findViewById(R.id.transactionPartnerImage);
            value =  view.findViewById(R.id.transactionImportText);
            totSpent = view.findViewById(R.id.totSpentTransaction);
        }
    }
}
