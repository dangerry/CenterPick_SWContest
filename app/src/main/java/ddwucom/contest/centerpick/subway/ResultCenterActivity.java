package ddwucom.contest.centerpick.subway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;

public class ResultCenterActivity extends AppCompatActivity {
    Intent intent;
    ListView listView;
    ArrayList<Item> users;
    ArrayList<Integer> usersTime;   //사용자들의 역부터 중간역까지 걸리는 시간
    ArrayAdapter<String> resultAdapter;
    ArrayList<String> result;

    String rslt ="";
    String resultStation;
    String resultStationId;
    TextView tv_result;
    Button btn_ok;
    Button btn_cancle;

    StringBuffer sb;
    String odsay_key = "UQpy5z5jYlkG7opjycDDO26k1WqH5NwUshX6iB5fir8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listView = findViewById(R.id.listViews);
        btn_ok = findViewById(R.id.button2);
        btn_cancle = findViewById(R.id.button3);
        tv_result = findViewById(R.id.tv_result);
        usersTime = new ArrayList<Integer>();
        result = new ArrayList<String>();

        intent = getIntent();
        users = (ArrayList<Item>) intent.getSerializableExtra("addStation");
        resultStation = intent.getStringExtra("resultStation");
        resultStationId = intent.getStringExtra("resultStationId");
        Log.v("check", users.toString());
        Log.v("check", resultStation + "," + resultStationId);

        tv_result.setText(resultStation);
        sb = new StringBuffer();

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(this, odsay_key);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        // 콜백 함수 구현 (경로)
        final OnResultCallbackListener onResultCallbackListenerPath = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(final ODsayData odsayData, API api) {
                Log.v("current", "호출 성공시 콜백함수 안(경로)");
                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.SUBWAY_PATH) {
                    try {
                        doJSONParserPath(odsayData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                Log.v("current", "호출 실패시 콜백함수 안(경로)");
                if (api == API.SUBWAY_PATH) { Log.v("current", "실패임~~~");}
            }
        };

        for(int i = 0; i < users.size(); i++){
            Log.v("current", "오디세이path 호출" + i +"번째");
            odsayService.requestSubwayPath("1000", users.get(i).getSubwayStationId(), resultStationId, "2", onResultCallbackListenerPath);
        }

    }

    void totalResult(){
        for (int i = 0; i < users.size(); i++) {
            rslt = (i + 1) + ". " + users.get(i).getSubwayStationName() + "-> " + resultStation + ": " + usersTime.get(i) + "분";
            result.add(rslt);
        }
        resultAdapter = new ArrayAdapter<String>(ResultCenterActivity.this, android.R.layout.simple_list_item_1, result);
        listView.setAdapter(resultAdapter);
    }

    //subwayPath
    void doJSONParserPath(ODsayData odsayData) throws JSONException {
        Log.v("current", "json파서 시작 (경로)");

        JSONObject result = odsayData.getJson().getJSONObject("result");
        int globalTravelTime = result.getInt("globalTravelTime");

        usersTime.add(globalTravelTime);
        Log.v("current", "json파서 끝 (경로)");

        if(users.size() == usersTime.size()) {
            Log.v("current", "각 사용자 별로 운행시간 받아오기 완료");
            totalResult();
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button2: //확인
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
                break;
            case R.id.button3: //취소Intent cancelIntent = new Intent();
                Intent cancelIntent = new Intent();
                cancelIntent.putExtra("users" , users);
                setResult(RESULT_OK, cancelIntent);
                finish();
                break;
        }
    }
}
