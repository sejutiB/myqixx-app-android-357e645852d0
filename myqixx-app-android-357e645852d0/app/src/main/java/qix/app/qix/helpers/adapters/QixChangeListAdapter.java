package qix.app.qix.helpers.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import qix.app.qix.R;


public class QixChangeListAdapter extends ArraySwipeAdapter<String> {

    private static final  String TAG = "QixChangeAdpter";
    private final FragmentActivity context;
    private final String[] itemname;
    private final Integer[] imgid;
    private final Integer[] values;


    public QixChangeListAdapter(FragmentActivity context, String[] itemname, Integer[] imgid, Integer[] values) {
        super(context, R.layout.layout_qixchange_list_item, itemname);

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
        this.values = values;

    }

    @NonNull
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_qixchange_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.bottom_wrapper);

        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Log.d(TAG, "OPEN: " + position);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) { }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) { }
        });

        viewHolder.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: exchange qix function
                viewHolder.swipeLayout.close();
            }
        });

        viewHolder.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.swipeLayout.close();
            }
        });

        viewHolder.numOfQix.setText(String.format("%d QIX", values[position]));
        viewHolder.txtTitle.setText(itemname[position]);
        viewHolder.imageView.setImageResource(imgid[position]);

        return convertView;
    }



    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.qixchangeSwipeLayout;
    }

    private class ViewHolder {
        SwipeLayout swipeLayout;
        RelativeLayout bottom_wrapper;
        TextView txtTitle;
        ImageView imageView;
        TextView numOfQix;
        Button yesButton;
        Button noButton;

        ViewHolder(View view) {
            bottom_wrapper = view.findViewById(R.id.bottom_wrapper);
            swipeLayout = view.findViewById(R.id.qixchangeSwipeLayout);
            txtTitle = view.findViewById(R.id.qixchangeListItemText);
            imageView = view.findViewById(R.id.qixchangeListItemImage);
            numOfQix = view.findViewById(R.id.qixchangeValueText);
            yesButton = view.findViewById(R.id.exchangeSlideButtonYes);
            noButton = view.findViewById(R.id.exchangeSlideButtonNo);
        }
    }


}
