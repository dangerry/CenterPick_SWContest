package ddwucom.contest.centerpick.subway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.subwayStationViewHolder> {
    Context context;
    ArrayList<Item> items;
    LayoutInflater inflater;
    EditText editText;

    public StationAdapter(Context context, ArrayList<Item> items, EditText editText) {
        this.context = context;
        this.items = items;
        this.editText = editText;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(Item item) {
        items.add(item);
    }


    public void clear() {
        items.clear();
    }

    @NonNull
    @Override
    public subwayStationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_layout, viewGroup, false);
        subwayStationViewHolder viewHolder = new subwayStationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull subwayStationViewHolder holder, int i) {
        final Item model = items.get(i);
        holder.subwayRouteName.setText(model.getSubwayRouteName());
        holder.stationNameText.setText(model.getSubwayStationName());
        holder.stationNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(model.getSubwayStationName());
            }
        });
        holder.subwayRouteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(model.getSubwayStationName());
            }
        });
    }


    public class subwayStationViewHolder extends RecyclerView.ViewHolder {
        TextView subwayRouteName;
        TextView stationNameText;

        public subwayStationViewHolder(@NonNull final View itemView) {
            super(itemView);
            subwayRouteName = itemView.findViewById(R.id.tvRoute);
            stationNameText = itemView.findViewById(R.id.tvStName);
        }
    }
}
