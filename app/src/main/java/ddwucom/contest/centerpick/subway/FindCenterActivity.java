package ddwucom.contest.centerpick.subway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class FindCenterActivity extends AppCompatActivity {

    ArrayList<Item> items;
    ArrayList<Double> x;
    ArrayList<Double> y;
    Intent intent, intent2;
    TextView tv_middle;
    ArrayList<String> centArr;
    ArrayList<String> centIdArr;
    ArrayAdapter<String> adapter;
    StringBuffer sb;
    ListView listView;

    String odsay_key = "UQpy5z5jYlkG7opjycDDO26k1WqH5NwUshX6iB5fir8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        StrictMode.enableDefaults();
        sb = new StringBuffer();
        tv_middle = findViewById(R.id.textView4);       //중간역 띄울 textView
        listView = findViewById(R.id.listView);

        intent = getIntent();
        items = (ArrayList<Item>)intent.getSerializableExtra("addStation"); //AddCheckActivity에서 받아온 intent값

        x = new ArrayList<>();
        y = new ArrayList<>();

        //사용자가 선택한 역들의 x,y좌표 집합
        for(int i = 0; i < items.size(); i++){
            x.add(items.get(i).getStationX());
            y.add(items.get(i).getStationY());
        }
        centerPoint(x, y);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent2 = new Intent(FindCenterActivity.this, ResultCenterActivity.class);
                intent2.putExtra("resultStation", (String)parent.getItemAtPosition(position)); //중간역 이름
                intent2.putExtra("resultStationId", centIdArr.get(position)); //중간역 id
                intent2.putExtra("addStation", items); //원래 역(여러 사용자가 클릭한 역)
                startActivity(intent2);
            }
        });
    }

    //중간역 구하기
    public void centerPoint(ArrayList<Double> x, ArrayList<Double> y){
        Log.v("current", "centerPoint 안");
        double x1 = 2000, y1 = 2000;
        double x2 = 0, y2 = 0;
        double centX, centY;

        for(int i = 0; i < x.size(); i++) {
            if (x.get(i) < x1)
                x1 = x.get(i);  //x중 가장 작은값
            if(x2 < x.get(i))
                x2 = x.get(i);  //x중 가장 큰값
        }
        for(int i = 0; i < y.size(); i++) {
            if (y.get(i) < y1)
                y1 = y.get(i);  //y중 가장 작은 값
            if(y2 < y.get(i))
                y2 = y.get(i);  //y중 가장 큰값
        }

        centX = x1 + ((x2-x1)/2);
        centY = y1 + ((y2-y1)/2);

        Log.v("current", "중간값 결과:" + centX + "," + centY);
        centPointStation(centX, centY);
    }

    //오디세이 이용해서 중간역 주변에 있는 지하철역 구하기
    public void centPointStation(double x, double y){

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(this, odsay_key);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        // 콜백 함수 구현 (중간좌표 역 받아오기)
        final OnResultCallbackListener onResultCallbackListener_centStation = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(final ODsayData odsayData, API api) {
                Log.v("current", "호출 성공시 콜백함수 안(중간좌표)");
                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.POINT_SEARCH) {
                    try {
                        doJSONParserCentStation(odsayData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                Log.v("current", "호출 실패시 콜백함수 안(경로)");
                if (api == API.POINT_SEARCH)
                    Log.v("check", "실패임~");
            }
        };

        odsayService.requestPointSearch(String.valueOf(x), String.valueOf(y), "3000", "2", onResultCallbackListener_centStation);  //3000m 근방에 있는 역
    }

    // 받아올 필수 정보(역이름으로 x좌표, y좌표, 노선명)
    public void doJSONParserCentStation (ODsayData odsayData) throws JSONException {
        Log.v("current", "centStation파서 수행");
        String centStation = "";
        String centStationId = null;    //얘네는
        String centStationLane = null;  //혹시나 경로구할때 필요할까봐
        centArr = new ArrayList<String>(); //얘는 추천 중간역 5개 받으려고 만든 리스트
        centIdArr = new ArrayList<String>();
        int check = 0;

        //정보 받아오기
        JSONObject result = odsayData.getJson().getJSONObject("result");
        JSONArray station = (JSONArray) result.get("station");

        //추천 중간역 5개 받기
        for(int i = 0; i < station.length(); i++){        //중간역받아온 개수만큼 for문을 돌리지만
            JSONObject obj = station.getJSONObject(i);
            centStation = obj.getString("stationName");
            centStationId = obj.getString("stationID");
            centStationLane = obj.getString("laneName");
            for(int j = 0; j < i; j++){
                if(centArr.get(j).equals(centStation)) {
                    check = 1;
                    break;
                }
            }
            if(check == 0) {
                centArr.add(centStation);
                centIdArr.add(centStationId);
            }
            check = 0;
            if(i == 4)                                    //5개가 넘어가면 멈춰야 하므로
                break;
        }

        adapter = new ArrayAdapter<String>(FindCenterActivity.this, android.R.layout.simple_list_item_1, centArr);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

}
