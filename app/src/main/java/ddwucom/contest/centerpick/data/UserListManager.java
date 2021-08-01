package ddwucom.contest.centerpick.data;

import java.util.ArrayList;

import static ddwucom.contest.centerpick.activity.MapActivity.latitude;
import static ddwucom.contest.centerpick.activity.MapActivity.longitude;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_addressList;

public class UserListManager {
    ArrayList<UserData> userDataList;

    public UserListManager(){
        userDataList = new ArrayList<UserData>();

        for(int i = 0; i < pick_addressList.size(); i++){
            userDataList.add(new UserData( pick_addressList.get(i)));       }
    }

    public  ArrayList<UserData> getUserList(){return userDataList;}
//    public void addList(UserData userData){userList.add(userData);}

    public void removeList(int pos){
        userDataList.remove(pos);
        pick_addressList.remove(pos);
        latitude.remove(pos);
        longitude.remove(pos);
    }

}