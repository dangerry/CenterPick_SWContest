package ddwucom.contest.centerpick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.data.TimeData;

import static ddwucom.contest.centerpick.activity.MapActivity.pick_addressList;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_placeNameList;
import static ddwucom.contest.centerpick.activity.MapActivity.time;

public class TimeAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<TimeData> timeArrayList;
    private LayoutInflater inflater;

    public TimeAdapter(Context context, int layout, ArrayList<TimeData> timeArrayList) {
        this.context = context;
        this.layout = layout;
        this.timeArrayList = timeArrayList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return timeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return timeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return timeArrayList.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int ppos = position;
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = inflater.inflate(layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.start_address = convertView.findViewById(R.id.start_address);
            viewHolder.take_time = convertView.findViewById(R.id.take_time);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.start_address.setText(pick_placeNameList.get(position));
        viewHolder.take_time.setText(time.get(position).toString());

        return convertView;
    }
    static class ViewHolder {
        TextView start_address;
        TextView take_time;
    }
}
