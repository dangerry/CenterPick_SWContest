package ddwucom.contest.centerpick.subway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;

public class AddAdapter  extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> list;
    LayoutInflater inflater;
    ListView listView;


    public AddAdapter(Context context, ArrayList<String> list, ListView listView) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.listView = listView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = (View)inflater.inflate(R.layout.user_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvId = convertView.findViewById(R.id.tvId);
            viewHolder.tvStation = convertView.findViewById(R.id.tvStation);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvId.setText(String.valueOf(position + 1));
        viewHolder.tvStation.setText(list.get(position));

        return convertView;
    }

    static class ViewHolder{
        TextView tvId;
        TextView tvStation;
    }
}
