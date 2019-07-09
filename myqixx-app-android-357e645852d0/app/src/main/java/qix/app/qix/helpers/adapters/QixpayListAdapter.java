package qix.app.qix.helpers.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import qix.app.qix.MainActivity;
import qix.app.qix.R;
import qix.app.qix.fragments.qixpay.QixpayListFragment;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.PartnerResponse;

public class QixpayListAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "QixpayListAdapter";

    private final FragmentActivity context;

    private QixpayFilter qixpayFilter;
    private List<PartnerResponse> partners;
    private List<PartnerResponse> filteredList;

    public QixpayListAdapter(FragmentActivity context, List<PartnerResponse> partners) {
        this.context = context;
        this.partners = partners;
        this.filteredList = partners;
    }

    // new method added
    public Context getcontext(){
        return context;
    }

    @Override
    public int getCount() {
        return filteredList == null ? 0 : filteredList.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_qixpay_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PartnerResponse partner = (PartnerResponse) getItem(position);
        String distance = Helpers.getFormattedDistance(partner.getDistance());
        viewHolder.name.setText(partner.getName().toUpperCase());

        viewHolder.category.setText(String.format("%s / %s, %s", distance, partner.getAddress(), partner.getCategory()));

        Picasso.get().load(partner.getImageUrl()).error(R.drawable.qix_app_icon_small).into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        TextView name ;
        ImageView imageView;
        TextView category;

        ViewHolder(View view) {
            name =  view.findViewById(R.id.transactionPartnerName);
            imageView =  view.findViewById(R.id.transactionPartnerImage);
            category =  view.findViewById(R.id.qixpayListItemCategoryText);
        }
    }

    @Override
    public Filter getFilter() {
        if (qixpayFilter == null) {
            qixpayFilter = new QixpayFilter(partners,this);
        }

        return qixpayFilter;
    }


    /**
     * Custom filter for shops list
     * Filter content in shop list according to the search text
     */
    public class QixpayFilter extends Filter {

        private String category;
        private List<PartnerResponse> filter;
        private QixpayListAdapter adapter;

        public QixpayFilter(List<PartnerResponse> filter, QixpayListAdapter adapter){
            this.filter=filter;
            this.adapter=adapter;
        }

     /* @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            List<PartnerResponse> tempList = new ArrayList<>();

            if (constraint != null && constraint.length() > 0) {

                String searchString = constraint.toString().toLowerCase();

                if(category != null && !category.isEmpty()){ // categoria selezionata

                    addItemsWithCategory(category, qixPartners, tempList); // filtro per categoria

                    if(!tempList.isEmpty()) { // filtro tutti gli elementi di quella categoria

                        List<PartnerResponse> tempListCategory = new ArrayList<>();

                        addItemsWithName(searchString, tempList, tempListCategory);

                        addItemsWithStreet(searchString, tempList, tempListCategory);


                        if(tempListCategory.isEmpty()){
                            filterResults.count = tempList.size();
                            filterResults.values = tempList;
                        }else{
                            filterResults.count = tempListCategory.size();
                            filterResults.values = tempListCategory;
                        }


                        return filterResults;
                    }

                }else{

                    addItemsWithCategory(searchString, qixPartners, tempList);

                    //TODO: Gestire elementi con la categoria nel nome o nella via (vengono ripetuti)
                    addItemsWithName(searchString, qixPartners, tempList);

                    addItemsWithStreet(searchString, qixPartners, tempList);

                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {

                if(category != null && !category.isEmpty()){
                    addItemsWithName(category, qixPartners, tempList);

                    filterResults.count = tempList.size();
                    filterResults.values = tempList;
                }else{
                    filterResults.count = qixPartners.size();
                    filterResults.values = qixPartners;
                }
            }

            return filterResults;
        }*/

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i("inside filtering", "");
            //RESULTS
            FilterResults results=new FilterResults();

            //VALIDATION
            if(constraint != null && constraint.length()>0)
            {

                //CHANGE TO UPPER FOR CONSISTENCY
                constraint=constraint.toString().toUpperCase();

                List<PartnerResponse> tempList=new ArrayList<>();

                //LOOP THRU FILTER LIST
                for(int i=0;i<filter.size();i++)
                {
                    //FILTER
                    if(filter.get(i).getName().toUpperCase().contains(constraint))
                    {
                        tempList.add(filter.get(i));
                    }
                }

                results.count=tempList.size();
                results.values=tempList;
            }else
            {
                results.count=filter.size();
                results.values=filter;
            }

            return results;
        }

        private void addItemsWithCategory(String c, List<PartnerResponse> fromArray, List<PartnerResponse> toArray){
            for (PartnerResponse partner : fromArray) {
                if (partner.getCategory().toLowerCase().contains(c)) {
                    if(!checkIfExist(partner.getId(), toArray)){
                        toArray.add(partner);
                    }
                }
            }
        }

        private void addItemsWithName(String name, List<PartnerResponse> fromArray, List<PartnerResponse> toArray){
            for (PartnerResponse partner : fromArray) {
                if (partner.getName().toLowerCase().contains(name)) {
                    if(!checkIfExist(partner.getId(), toArray)) {
                        toArray.add(partner);
                    }
                }
            }
        }

        private void addItemsWithStreet(String street, List<PartnerResponse> fromArray, List<PartnerResponse> toArray){
            for (PartnerResponse partner : fromArray) {
                if (partner.getAddress().toLowerCase().contains(street)) {
                    if(!checkIfExist(partner.getId(), toArray)) {
                        toArray.add(partner);
                    }
                }
            }
        }

        private boolean checkIfExist(String id, List<PartnerResponse> toArray) {
            for(PartnerResponse element: toArray){
                if(element.getId().equals(id)){
                    return true;
                }
            }
            return false;
        }

        public void setCategory(String category){
            this.category = category;
            notifyDataSetChanged();
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredList = (List<PartnerResponse>) results.values;
            notifyDataSetChanged();
        }
    }
}
