package qix.app.qix.fragments.myqix;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.LinkedHashMap;
import java.util.List;
import qix.app.qix.R;

public class MyQixExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Integer> expandableListTitle;
    private List<Integer> expandableListIcon;
    private LinkedHashMap<String, List<Integer>> expandableListChildsIcon;

    private LinkedHashMap<String, List<String>> expandableListDetail;

    MyQixExpandableListAdapter(Context context, List<Integer> expandableListTitle, List<Integer> expandableListIcon, LinkedHashMap<String, List<Integer>> expandableListChildsIcon, LinkedHashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.expandableListChildsIcon = expandableListChildsIcon;
        this.expandableListIcon = expandableListIcon;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(context.getString(this.expandableListTitle.get(listPosition))).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

       /* final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.layout_myqix_list_item_child, parent, false);
        }


        ImageView childImage = convertView.findViewById(R.id.myQixListChildImage);
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItemTextChild);
        expandedListTextView.setText(expandedListText);
        childImage.setImageResource(expandableListChildsIcon.get(context.getString(this.expandableListTitle.get(listPosition))).get(expandedListPosition));*/

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
      //* return this.expandableListDetail.get(context.getString(this.expandableListTitle.get(listPosition))).size();
       return 0;
    }

    @Override
    public Object getGroup(int listPosition) {
      return context.getString(this.expandableListTitle.get(listPosition)) + "-" + this.expandableListIcon.get(listPosition);

    }

    @Override
    public int getGroupCount() {

      return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

       String[] data = ((String) getGroup(listPosition)).split("-");
        String listTitle = data[0];
        int iconId = Integer.parseInt(data[1]);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.layout_myqix_list_item, parent, false);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.expandedListItemText);
        ImageView iconImage = convertView.findViewById(R.id.listItemIcon);
        ImageView arrowImage = convertView.findViewById(R.id.arrowImageView);

        if (getChildrenCount(listPosition) > 0){
            arrowImage.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);
        }

        iconImage.setImageResource(iconId);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
