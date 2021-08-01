package ddwucom.contest.centerpick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.data.UserData;

import static ddwucom.contest.centerpick.activity.MapActivity.pick_addressList;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_placeNameList;

public class UserListAdapter extends BaseAdapter {
    private Context context;
    private  int layout;
    private ArrayList<UserData> userList;
    private LayoutInflater inflater;

    // 생성자
    public UserListAdapter(Context context, int layout, ArrayList<UserData> userList){
        this.context = context;
        this.layout = layout;
        this.userList = userList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).get_id();
        //  return Long.parseLong(String.valueOf(userList.get(position).get_id()));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.placeNameText = convertView.findViewById(R.id.user_address);//요소데이터 결합
            viewHolder.addressText = convertView.findViewById(R.id.user_number);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //  viewHolder.placeNameText.setText(userList.get(pos).getAddress_name());
        //   viewHolder.addressText.setText(userList.get(pos).getAddress_number());
        viewHolder.addressText.setText(pick_addressList.get(pos));
        viewHolder.placeNameText.setText(pick_placeNameList.get(pos));

        return convertView;
    }
    static class ViewHolder{
        TextView placeNameText;
        TextView addressText;

    }

}
