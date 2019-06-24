package qix.app.qix.helpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import qix.app.qix.R;
import qix.app.qix.models.UserData;

public class MyQixProfileAdapter extends BaseAdapter {

    private Context context;
    private UserData user;

    public MyQixProfileAdapter(Context context, UserData user){
        this.context = context;
        this.user = user;
    }

    @Override
    public int getCount() {
        return UserData.numberOfVisibleFields();
    }

    @Override
    public Object getItem(int i) {
        switch(i){
            case 0:
                return user.getName();
            case 1:
                return user.getFamilyName();
            case 2:
                return user.getBirthday();
            case 3:
                return user.getEmail();
            case 4:
                return user.getGender();
            case 5:
                return user.getAddress();
            case 6:
                return user.getPhoneNumber();
            case 7:
                return context.getResources().getString(user.getPhoneNumberVerified());
            case 8:
                return context.getResources().getString(user.getEmailVerified());
            default:
                return "--";
        }
    }

    public String getItemTitle(int i) {
        switch(i){
            case 0:
                return context.getResources().getString(R.string.myqix_item_name);
            case 1:
                return context.getResources().getString(R.string.myqix_item_surname);
            case 2:
                return context.getResources().getString(R.string.myqix_item_birthdate);
            case 3:
                return context.getResources().getString(R.string.myqix_item_email);
            case 4:
                return context.getResources().getString(R.string.myqix_item_gender);
            case 5:
                return context.getResources().getString(R.string.myqix_item_address);
            case 6:
                return context.getResources().getString(R.string.myqix_item_phone_number);
            case 7:
                return context.getResources().getString(R.string.myqix_item_phone_number_verified);
            case 8:
                return context.getResources().getString(R.string.myqix_item_email_verified);
            default:
                return "--";
        }
    }

    public void setUserData(UserData user){
        this.user = user;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_myqix_profile_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String field;

        if(user != null){
            field = (String) getItem(position);
        }else{
            field = "--";
        }

        String title = getItemTitle(position);
        viewHolder.title.setText(title);
        viewHolder.field.setText(field);

        return convertView;
    }

    private class ViewHolder {
        TextView field;
        TextView title;

        ViewHolder(View view) {
            title = view.findViewById(R.id.myqixTitleText);
            field = view.findViewById(R.id.myqixFieldText);
        }
    }
    
}
