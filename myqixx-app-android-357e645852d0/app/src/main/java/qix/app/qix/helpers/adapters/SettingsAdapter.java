package qix.app.qix.helpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import qix.app.qix.R;

public class SettingsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;

    public SettingsAdapter(@NonNull Context context, String[] values) {
        super(context,-1, values);
        this.context=context;
        this.values=values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_myqix_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.expandedListItemText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.listItemIcon);
        textView.setText(values[position]);
            imageView.setImageResource(R.drawable.arrow_gray);

        return rowView;
    }
}
