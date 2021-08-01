package ddwucom.contest.centerpick.subway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;

public class AddCheckActivity extends AppCompatActivity {

    final int FIND_COD = 101;
    ListView listView;
    AddAdapter adapter;
    ArrayList<String> users;
    Intent intent;

    String odsay_key = "UQpy5z5jYlkG7opjycDDO26k1WqH5NwUshX6iB5fir8";
    String calInform_startStationId = null;
    ArrayList<Item> addStation;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        listView  = findViewById(R.id.user_adapter);

        addStation = new ArrayList<>();
        intent = getIntent();
        users = (ArrayList<String>)intent.getSerializableExtra("users");

        adapter = new AddAdapter(this, users, listView);
        listView.setAdapter(adapter);

        //롱클릭 -> user삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCheckActivity.this);
                builder.setTitle(R.string.dialog_title)
                        .setMessage(users.get(pos) + " 를 삭제하시겠습니까?" )
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                users.remove(pos);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_find:
                if(users.size() < 2){
                    Toast.makeText(AddCheckActivity.this, "2명이상 추가해야 가능합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    for(int i = 0; i < users.size(); i++)
                        Odsayinform(users.get(i), i);
//                    if(addStation.size() == users.size()) {
//                        Intent checkIntent = new Intent(AddCheckActivity.this, FindCenterActivity.class);
//                        checkIntent.putExtra("addStation", addStation);
//                        startActivityForResult(checkIntent, FIND_COD);
//                    }
                }
                break;
            case R.id.btn_cancel://확인만하고 뒤로 갈때(여기서 취소눌러야지 뒤로가기 누르면 앱꺼짐,,)
                Intent cancelIntent = new Intent();
                cancelIntent.putExtra("users" , users);
                setResult(RESULT_OK, cancelIntent);
                finish();
                break;
        }
    }

    //좌표값 정보
    public void Odsayinform (String addStation, final int num){
        Log.v("current", "OdsayInform들어옴");

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(this, odsay_key);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        // 콜백 함수 구현 (경로)
        final OnResultCallbackListener onResultCallbackListener_calInform_startId = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(final ODsayData odsayData, API api) {
                Log.v("current", "호출 성공시 콜백함수 안(경로)");
                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.SEARCH_STATION) {
                    try {
                        doJSONParserStartId(odsayData, num);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                Log.v("current", "호출 실패시 콜백함수 안(경로)");
                if (api == API.SEARCH_STATION)
                    Log.v("check", "실패임~");
            }
        };
        Log.v("current", "여기 호출부터 실행");
        odsayService.requestSearchStation(addStation, "1000", "2", "", "", "", onResultCallbackListener_calInform_startId);
    }

    //Start station Id
    void doJSONParserStartId (ODsayData odsayData, int num) throws JSONException {
        Log.v("current", "json파서 시작 (시작역 아이디)");
        String stationID = null;
        JSONObject result = odsayData.getJson().getJSONObject("result");
        JSONArray station = (JSONArray) result.get("station");

        for (int i = 0; i < station.length(); i++) {
            JSONObject obj = station.getJSONObject(i);
            stationID = obj.getString("stationID");
        }

        calInform_startStationId = stationID;
        Log.v("current", num + "번째 추가역 id:" + stationID);

        middleInform();
    }

    //중간값 구하려고 만든 api호출 메소드
    public void middleInform () {
        Log.v("current", "middleInform수행 메소드 안");

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(this, odsay_key);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        // 콜백 함수 구현 (경로)
        final OnResultCallbackListener onResultCallbackListenerMiddleInform = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(final ODsayData odsayData, API api) {
                Log.v("current", "MiddleInform 수행 성공");
                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.SUBWAY_STATION_INFO) {
                    try {
                        doJSONParserCalInfo(odsayData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                Log.v("current", "MiddleInform 수행 실패");
                if (api == API.SUBWAY_STATION_INFO)
                    Log.v("check", "실패임~~");
            }
        };

        Log.v("currnet", "middleInfrom 수행");
        //API호출
        odsayService.requestSubwayStationInfo(calInform_startStationId, onResultCallbackListenerMiddleInform);
    }

    // 받아올 필수 정보(역이름으로 x좌표, y좌표, 노선명)
    public void doJSONParserCalInfo (ODsayData odsayData) throws JSONException {
        item = new Item();
        Log.v("current", "calInfo파서 수행");

        JSONObject result = odsayData.getJson().getJSONObject("result");
        String stationID = result.getString("stationID");
        String stationName = result.getString("stationName");
        String laneName = result.getString("laneName");
        double x = result.getDouble("x");
        double y = result.getDouble("y");
        Log.v("current",stationName+":"+laneName+"," + x + ","+y );
//        calInform = new String[]{Double.toString(x), Double.toString(y), laneName};  //String.valueOf(x)이 맞음
        item.setSubwayStationId(stationID);
        item.setSubwayStationName(stationName);
        item.setSubwayRouteName(laneName);
        item.setStationX(x);
        item.setStationY(y);
        addStation.add(item);

        Log.v("current","addStation에 item객체 추가완료" );
        Log.v("current", item.toString());
        Log.v("currnet", addStation.get(0).toString());
        Log.v("currnet", addStation.get(1).toString());

        if(addStation.size() == users.size()) {     //내가 user와 item의값의 개수가 일치할때 파서 완료이므로 이곳에서
            intent = new Intent(AddCheckActivity.this, FindCenterActivity.class);
            intent.putExtra("addStation", addStation);
            startActivity(intent);
            //addStation.clear();
            //startActivityForResult(intent, FIND_COD);
        }
    }

}
