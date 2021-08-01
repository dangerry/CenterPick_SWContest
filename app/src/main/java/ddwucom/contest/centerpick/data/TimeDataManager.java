package ddwucom.contest.centerpick.data;

import java.util.ArrayList;

import static ddwucom.contest.centerpick.activity.MapActivity.pick_addressList;
import static ddwucom.contest.centerpick.activity.MapActivity.time;

public class TimeDataManager {
    ArrayList<TimeData> timeDataList;

    public TimeDataManager(){
        timeDataList = new ArrayList<TimeData>();

        for(int i = 0; i < pick_addressList.size(); i++){
            timeDataList.add(new TimeData(pick_addressList.get(i), time.get(i)));
        }

    }
    public ArrayList<TimeData> getTimeDataList(){
        return timeDataList;
    }
}
