package ddwucom.contest.centerpick.subway;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ddwucom.contest.centerpick.R;

public class ODsayConnect extends AppCompatActivity {
    EditText edit, edit2;
    TextView text;
    Button btn;

    StringBuffer sb;

    String startStation, endStation = null;
    int startStationId, endStaionId;
    double startX, startY, endX, endY;

    String odsay_key = "UQpy5z5jYlkG7opjycDDO26k1WqH5NwUshX6iB5fir8";
    static ODsayData odsayData1, odsayData2, odsayData3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subway_connect);

        StrictMode.enableDefaults();

        text= findViewById(R.id.result);
        btn = findViewById(R.id.button);
        edit = findViewById(R.id.edit);
        edit2 = findViewById(R.id.edit2);

        sb = new StringBuffer();

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(this, odsay_key);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        //콜백 함수 구현 (세부정보)
        final OnResultCallbackListener onResultCallbackListenerInfo = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(final ODsayData odsayData, API api) {
                Log.v("current", "호출 성공시 콜백함수 안(세부정보)");
                // API Value 는 API 호출 메소드 명을 따라갑니다.
                odsayData2 = odsayData;
                if (api == API.SUBWAY_STATION_INFO) {
                    try {
                        doJSONParserInfo(odsayData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                Log.v("current", "호출 실패시 콜백함수 안(세부정보)");
                if (api == API.SUBWAY_STATION_INFO) { text.setText("실패임~~~");}
            }
        };

        //콜백 함수 구현 (시작역 아이디)
        final OnResultCallbackListener onResultCallbackListenerStation_start = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                Log.v("current", "호출 성공시 콜백함수 안(시작역 아이디)");
                odsayData3 = oDsayData;
                if (api == API.SEARCH_STATION) {
                    try {
                        doJSONParserStartId(oDsayData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SEARCH_STATION) { text.setText("실패임~~~");}
            }
        };

        //콜백 함수 구현 (도착역 아이디)
        final OnResultCallbackListener onResultCallbackListenerStation_end = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                Log.v("current", "호출 성공시 콜백함수 안(도착역 아이디)");
                odsayData3 = oDsayData;
                if (api == API.SEARCH_STATION) {
                    try {
                        doJSONParserEndId(oDsayData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SEARCH_STATION) { text.setText("실패임~~~");}
            }
        };

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("current", "버튼 리스너(메인 안)");
                startStation = edit.getText().toString();
                endStation = edit2.getText().toString();

                odsayService.requestSearchStation(startStation, "1000","2", "","", "", onResultCallbackListenerStation_start);
                odsayService.requestSearchStation(endStation, "1000","2", "","", "", onResultCallbackListenerStation_end);
                Log.v("current", "역ID api호출");

//                odsayService.requestSubwayStationInfo(startStation, onResultCallbackListenerInfo);
//                Log.v("current", "세부정보 api호출");
//
//                odsayService.requestSubwayPath("1000", startStation, endStation, "2", onResultCallbackListenerPath); //EID: 133
//                Log.v("current", "경로검색 api호출");
            }
        });
    }

    //시작역, 도착역 입력
    public void resultStationPath() {
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
                odsayData1 = odsayData;
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
                if (api == API.SUBWAY_PATH) { text.setText("실패임~~~");}
            }
        };

        if(startStation != null && endStation != null){
            odsayService.requestSubwayPath("1000", startStation, endStation, "2", onResultCallbackListenerPath); //EID: 133
            Log.v("current", startStation + "과" + endStation);
        }
    }

    //Start station Id
    void doJSONParserStartId(ODsayData odsayData) throws JSONException{
        Log.v("current", "json파서 시작 (시작역 아이디)");
        JSONObject result = odsayData.getJson().getJSONObject("result");
        JSONArray station = (JSONArray) result.get("station");

        for(int i = 0; i < station.length(); i++){
            JSONObject obj = station.getJSONObject(i);
            String stationID = obj.getString("stationID");

            startStation = stationID;
            Log.v("current", "json파서 끝 (시작역 아이디)" + stationID);
        }
        resultStationPath();
    }

    //End station Id
    void doJSONParserEndId(ODsayData odsayData) throws JSONException{
        Log.v("current", "json파서 시작 (도착역 아이디)");
        JSONObject result = odsayData.getJson().getJSONObject("result");
        JSONArray station = (JSONArray) result.get("station");

        for(int i = 0; i < station.length(); i++){
            JSONObject obj = station.getJSONObject(i);
            String stationID = obj.getString("stationID");
            endStation = stationID;
            Log.v("current", "json파서 끝 (도착역 아이디)" + stationID);
        }
        resultStationPath();
    }

    //station Info
    void doJSONParserInfo(ODsayData odsayData) throws JSONException {
        Log.v("current", "json파서 시작 (세부정보)");
        JSONObject result = odsayData.getJson().getJSONObject("result");
        String stationName = result.getString("stationName");
        int stationID = result.getInt("stationID");
        int type = result.getInt("type");
        String laneName = result.getString("laneName");
        String laneCity = result.getString("laneCity");
        double x = result.getDouble("x");
        double y = result.getDouble("y");

        sb.append(
                "<< 지하철역 세부 정보 조회 >>\n" +
                        "지하철역 명 : " + stationName + "\n" +
                        "지하철역 ID : " + stationID + "\n" +
                        "노선 종류 : " + type + "\n" +
                        "노선명 : " + laneName + "\n" +
                        "노선지역명 : " + laneCity + "\n" +
                        "x좌표(경위도) : " + x + "\n" +
                        "y좌표(경위도) : " + y + "\n"
        );
        text.setText(sb.toString());
        Log.v("current", "json파서 끝 (세부정보)");
    }

    //subwayPath
    void doJSONParserPath(ODsayData odsayData) throws JSONException {
        Log.v("current", "json파서 시작 (경로)");
        JSONObject result = odsayData.getJson().getJSONObject("result");
        String globalStartName = result.getString("globalStartName");
        String globalEndName = result.getString("globalEndName");
        int globalTravelTime = result.getInt("globalTravelTime");
        int globalDistance = result.getInt("globalDistance");
        int globalStationCount = result.getInt("globalStationCount");
        int fare = result.getInt("fare");
        int cashFare = result.getInt("cashFare");

        JSONObject driveInfoSet = result.getJSONObject("driveInfoSet");
        JSONArray driveInfo = (JSONArray)driveInfoSet.get("driveInfo");

        JSONObject stationSet = result.getJSONObject("stationSet");
        JSONArray stations = (JSONArray)stationSet.get("stations");

        sb.append(
                "<< 지하철 경로 검색 조회 >>\n" +
                        "출발역 명 : " + globalStartName + "\n" +
                        "도착역 명 : " + globalEndName + "\n" +
                        "전체 운행소요시간(분) : " + globalTravelTime + "\n" +
                        "전체 운행거리(Km) : " + globalDistance + "\n" +
                        "전체 정차역 수 : " + globalStationCount + "\n" +
                        "카드요금(성인기준) : " + fare + "\n" +
                        "현금요금(성인기준) : " + cashFare + "\n"
        );
        text.setText(sb.toString());

        for (int i = 0; i < driveInfo.length(); i++) {
            JSONObject djo = driveInfo.getJSONObject(i);

            String laneID = djo.getString("laneID");
            String laneName = djo.getString("laneName");
            String startName = djo.getString("startName");
            int stationCount = djo.getInt("stationCount");
            int wayCode = djo.getInt("wayCode");
            String wayName = djo.getString("wayName");

            sb.append(
                    "-- 현금요금(성인기준) --\n" +
                            "승차역 ID : " + laneID + "\n" +
                            "승차역 호선명 : " + laneName + "\n" +
                            "승차 역명 : " + startName + "\n" +
                            "이동 역 수 : " + stationCount + "\n" +
                            "방면코드 (1:상행, 2:하행) : " + wayCode + "\n" +
                            "방면 명 : " + wayName + "\n"
            );
            text.setText(sb.toString());
        }

        if (result.length() == 10) {
            JSONObject exChangeInfoSet = result.getJSONObject("exChangeInfoSet");
            JSONArray exChangeInfo = (JSONArray)exChangeInfoSet.get("exChangeInfo");

            for (int i = 0; i < exChangeInfo.length(); i++) {
                JSONObject ejo = exChangeInfo.getJSONObject(i);

                String elaneName = ejo.getString("laneName");
                String estartName = ejo.getString("startName");
                String exName = ejo.getString("exName");
                int exSID = ejo.getInt("exSID");
                int fastTrain = ejo.getInt("fastTrain");
                int fastDoor = ejo.getInt("fastDoor");
                int exWalkTime = ejo.getInt("exWalkTime");

                sb.append(
                        "-- 환승 정보(환승경로 있을경우 제공) --\n" +
                                "승차노선 명 : " + elaneName + "\n" +
                                "승차역 명 : " + estartName + "\n" +
                                "환승역 명 : " + exName + "\n" +
                                "환승역 ID : " + exSID + "\n" +
                                "빠른 환승 객차 번호 : " + fastTrain + "\n" +
                                "빠른 환승 객차 문 번호 : " + fastDoor + "\n" +
                                "환승소요시간 (초) : " + exWalkTime + "\n"
                );
                text.setText(sb.toString());
            }
        }

        for (int i = 0; i < stations.length(); i++) {
            JSONObject sjo = stations.getJSONObject(i);

            int startID = sjo.getInt("startID");
            String startName_s = sjo.getString("startName");
            int endSID = sjo.getInt("endSID");
            String endName = sjo.getString("endName");
            int travelTime = sjo.getInt("travelTime");

            sb.append(
                    "-- 이동역 정보 --\n" +
                            "출발역 ID : " + startID + "\n" +
                            "출발역명 : " + startName_s + "\n" +
                            "도착역 ID : " + endSID + "\n" +
                            "도착역명 : " + endName + "\n" +
                            "누적 운행시간(분) : " + travelTime + "\n"
            );
            text.setText(sb.toString());
        }
        sb.append("\n");
        text.setText(sb.toString());
        Log.v("current", "json파서 끝 (경로)");
    }
}
