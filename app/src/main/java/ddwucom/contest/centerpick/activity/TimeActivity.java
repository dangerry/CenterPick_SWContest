package ddwucom.contest.centerpick.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.adapter.TimeAdapter;
import ddwucom.contest.centerpick.data.TimeData;
import ddwucom.contest.centerpick.data.TimeDataManager;

import static ddwucom.contest.centerpick.activity.MapActivity.latitude;
import static ddwucom.contest.centerpick.activity.MapActivity.longitude;
import static ddwucom.contest.centerpick.activity.MapActivity.touch_x;
import static ddwucom.contest.centerpick.activity.MapActivity.touch_y;
import static ddwucom.contest.centerpick.activity.MapActivity.time;

public class TimeActivity extends AppCompatActivity {

    private ODsayService odsayService;
    final static String TAG = "ABC";
    //테스트용
    private TextView tv_data;
    private ListView listView;
    private TimeAdapter timeAdapter;
    private ArrayList<TimeData> timeList;
    TimeDataManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

//        tv_data = findViewById(R.id.tv_data);

//        tv_data.setText(time.get(0).toString());

        listView = findViewById(R.id.listView);

        manager = new TimeDataManager();
        timeList = manager.getTimeDataList();
        timeAdapter = new TimeAdapter(this, R.layout.time_list, timeList);
        listView.setAdapter(timeAdapter);

        Log.d(TAG, time.get(0).toString());
        Log.d(TAG, time.get(1).toString());

    }
}