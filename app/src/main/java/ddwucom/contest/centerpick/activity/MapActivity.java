package ddwucom.contest.centerpick.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;
import android.Manifest;
import android.location.LocationManager;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import ddwucom.contest.centerpick.model.category_search.CategoryResult;
import ddwucom.contest.centerpick.model.category_search.Document;
import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.api.ApiClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import ddwucom.contest.centerpick.api.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener,
        MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, MapView.POIItemEventListener,
        MapView.OpenAPIKeyAuthenticationResultListener, View.OnClickListener {
    final static String TAG = "MapTAG";
    private MapView mapView;

    private ODsayService odsayService;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    public static MapPOIItem.CalloutBalloonButtonType MainButton;


    final int REQ_CODE = 100;
    final int REQ_USER_CODE = 200;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};


    public static ArrayList<String> latitude = new ArrayList<>(); //위도 담을 배열 리스트
    public static ArrayList<String> longitude = new ArrayList<>(); //경도 담을 배열 리스트

    public static ArrayList<String> remove_latitude = new ArrayList<>(); //제거할 위도
    public static ArrayList<String> remove_longitude = new ArrayList<>(); //제거할 경도



    public static double touch_x; //터치 위도 ->timeActivity
    public static double touch_y; //터치 경도 ->timeActivity

    //Double형 위경도
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> lon = new ArrayList<>();

    public static ArrayList<String> pick_addressList = new ArrayList<>(); //출발 주소 저장
    public static ArrayList<String> pick_placeNameList = new ArrayList<>();

    public static ArrayList<MapPOIItem> pois = new ArrayList<>();

    public static String pick_address; //선택한 주소
    public static String pick_placeName; //선택한 장소 이름

    public static ArrayList<Integer> remove_pos = new ArrayList<>(); //삭제 pos들

    static String center_pick = null;

    public static ArrayList<Integer> time = new ArrayList<>(); //사람들의 최솟값
    static int min = 100000;

    //유경's turn
    private FloatingActionButton fab, fab1, fab2, fab3;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    public static int count = 1;

    ArrayList<Document> bigMartList = new ArrayList<>(); //대형마트 MT1
    ArrayList<Document> gs24List = new ArrayList<>(); //편의점 CS2
    ArrayList<Document> subwayList = new ArrayList<>(); //지하철 SW8
    ArrayList<Document> bankList = new ArrayList<>(); //은행 BK9
    ArrayList<Document> cafeList = new ArrayList<>(); //카페
    ArrayList<Document> restaurantList = new ArrayList<>(); //음식점 FD6

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Context context = null;

        getHashKey();

        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService = ODsayService.init(this, "UQpy5z5jYlkG7opjycDDO26k1WqH5NwUshX6iB5fir8");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setCurrentLocationEventListener(this);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
//버튼 설정
        initView();

    }

    private void initView() {

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);

//        //버튼리스너
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

    }

    protected void onDestroy(){
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setShowCurrentLocationMarker(false);
    }

    private void getHashKey(){    //해쉬키 구함
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    /*
    ActivityCompat.requestPermissions 사용한 퍼미션 요청의 결과 리턴받는 메소드
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length){
            //요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신

            boolean check_result = true;

            //모든 퍼미션 허용했는지 체크
            for (int result : grantResults){
                if (result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if (check_result){
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
            else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])){
                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    void checkRunTimePermission(){
        //런타임 퍼미션 처리

        //1. 위치 퍼미션 가지고 있는지 체크
        int hasFindLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFindLocationPermission == PackageManager.PERMISSION_GRANTED){
            //이미 퍼미션을 가지고 있다면

            //위치 값 가져오기 가능
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        }
        else{
            //퍼미션 요청 허용한 적 없다면 퍼미션 요청 필요

            //거부한 적 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])){
                Toast.makeText(MapActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else{
                //퍼미션 거부 한 적 없는 경우
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int size = latitude.size();
        double lat;
        double lon;

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        checkRunTimePermission();
                        return;
                    }
                }
                break;

            case REQ_CODE:
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                //현위치 인식 끔, 계속 켜놓으면 현재 위치 반복 설정됨

                switch(resultCode){
                    case RESULT_OK:
                        lat = Double.parseDouble(latitude.get(size - 1));
                        lon = Double.parseDouble(longitude.get(size - 1));

                        Log.d(TAG, "정보 제대로 받아왔음");
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lon), true); //등록한 주소로 중심점 이동

                        //마커 세우기
                        MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(lat, lon);
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName("사용자");
                        marker.setTag(0);
                        marker.setMapPoint(MARKER_POINT);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                        mapView.addPOIItem(marker);
                        pois.add(marker);
                        break;
//                    case RESULT_CANCELED:
//                        Toast.makeText(this, "사용자 정보 추가 취소", Toast.LENGTH_SHORT).show();
//                        break;
                }
                break;

            case REQ_USER_CODE:

                switch(resultCode){
                    case RESULT_OK:

                        for (int i = 0; i < pois.size(); i++){
                            for (int j = 0; j < remove_pos.size(); j++){
                                mapView.removePOIItem(pois.get(remove_pos.get(j)));
                            }
                        }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab1: //아래버튼에서부터 1~3임
                Toast.makeText(this, "사용자 삭제 및 보기", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, UserListActivity.class);
                startActivityForResult(intent, REQ_USER_CODE);

                anim();
                break;
            case R.id.fab2:

                //중간지점 구하기
                double max_x = -1; //다각형의 x좌표 중 가장 큰 값
                double min_x = 1000000000; //다각형의 x좌표 중 가장 작은 값

                double max_y = -1; //다각형의 y좌표 중 가장 큰 값
                double min_y = 1000000000; //다각형의 y좌표 중 가장 작은 값

                //다각형 x좌표 중 가장 큰 값 구하기
                for (int i = 0; i < latitude.size(); i++){
                    lat.add(Double.parseDouble(latitude.get(i)));
                    if (max_x < lat.get(i)){
                        max_x = lat.get(i);
                    }
                }

                //다각형 x좌표 중 가장 작은 값 구하기
                for (int i = 0; i < latitude.size(); i++){
                    if (min_x > lat.get(i)){
                        min_x = lat.get(i);
                    }
                }

                //다각형 y좌표 중 가장 큰 값 구하기
                for (int i = 0; i < longitude.size(); i++){
                    lon.add(Double.parseDouble(longitude.get(i)));
                    if (max_y < lon.get(i)){
                        max_y = lon.get(i);
                    }
                }

                for (int i = 0; i < longitude.size(); i++){
                    if (min_y > lon.get(i)){
                        min_y = lon.get(i);
                    }
                }

                double center_x = min_x + ((max_x - min_x) / 2); //x좌표 중간
                double center_y = min_y + ((max_y - min_y) / 2); //y좌표 중간

                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(center_x, center_y), true); //등록한 주소로 중심점 이동

                requestSearchLocal(center_y, center_x);

                Toast.makeText(this, "만날 장소를 선택해주세요.", Toast.LENGTH_LONG).show();

//                mapView.setMapViewEventListener(this);
                mapView.setPOIItemEventListener(this);


                break;

            case R.id.fab3:
                intent = new Intent(this, SearchActivity.class);
//                startActivity(intent);
                startActivityForResult(intent, REQ_CODE);
                break;

        }
    }

    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        //지도 터치 이벤트
//        touch_x = mapPoint.getMapPointGeoCoord().longitude; //터치 부분 위도
//        touch_y = mapPoint.getMapPointGeoCoord().longitude; //터치 부분 경도
//
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        List<Address> addresses = null;
//        Address address = null;
//
//        try{
//            addresses = geocoder.getFromLocation(touch_x, touch_y, 1);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (addresses == null || addresses.size() == 0){
//            //주소 미발견
//        }
//        else{
//            address = addresses.get(0);
//        }
//
//        Toast.makeText(this, String.valueOf(touch_x), Toast.LENGTH_LONG).show();
//        Log.d(TAG, "onMapViewSingleTapped");





    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {


    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
//        touch_x = mapPoint.getMapPointGeoCoord().longitude; //터치 부분 위도
//        touch_y = mapPoint.getMapPointGeoCoord().longitude; //터치 부분 경도

        touch_x = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        touch_y = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

        center_pick = mapPOIItem.getItemName();

        Log.d(TAG, String.valueOf(latitude.size()));
        Log.d(TAG, String.valueOf(longitude.size()));
        Log.d(TAG, String.valueOf(touch_x));
        Log.d(TAG, String.valueOf(touch_y));

        Log.d(TAG, "말풍선 터치");
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("중간 지점 설정")
                .setMessage(center_pick + " 를 중간 지점으로 설정하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        odsayParsing();

                    }
                })
                .setNegativeButton("취소", null)
                .setCancelable(false)
                .show();
    }

    public void odsayParsing(){


        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    if (api == API.SEARCH_PUB_TRANS_PATH) {
                        doJSONParser(odsayData);
                        count++;
                        Log.d(TAG,"사이즈정1 최소시간: " + min);
                        time.add(min);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String errorMessage, API api) {
                //에러 발생
                if (api == API.SEARCH_PUB_TRANS_PATH) {
//                    tv_data.setText("API : " + api.name() + "\n" + errorMessage);
                }
            }
        };

        String x = String.valueOf(touch_x);
        String y = String.valueOf(touch_y);

        Log.d(TAG, "사이즈5" + String.valueOf(time.size()));

        for (int i = 0; i < latitude.size(); i++){
            min = 100000;
            odsayService.requestSearchPubTransPath(longitude.get(i), latitude.get(i), y, x, "0", "0", "0", onResultCallbackListener);

            Log.d(TAG, String.valueOf("사이즈1" + time.size()));

        }

        Log.d(TAG, String.valueOf("사이즈2" + time.size()));



    }


    public void doJSONParser(ODsayData odsayData) throws JSONException{

        ArrayList<Integer> times = new ArrayList<>(); //한 사람이 걸리는 시간의 경우들

//        min = 100000;
        times.clear();
        Log.d(TAG, String.valueOf(time.size()));

        JSONObject result = odsayData.getJson().getJSONObject("result");

        JSONArray path = (JSONArray)result.get("path");

        for (int i = 0; i < path.length(); i++){
            JSONObject j = path.getJSONObject(i);

//            String pathType = j.getString("pathType");
            JSONObject info = j.getJSONObject("info");

            String totalTime = info.getString("totalTime");
            int t = Integer.parseInt(totalTime);
            times.add(t);


        }
        for (int k = 0; k < times.size(); k++){
            if (min > times.get(k)){
                min = times.get(k);
            }
        }

        Log.d(TAG, "최소시간: " + min);

        if (count % latitude.size() == 0){
            Intent intent = new Intent(MapActivity.this, TimeActivity.class);

            startActivity(intent);
        }



//        time.add(min);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    private void requestSearchLocal(double x, double y) {
        bigMartList.clear();
        gs24List.clear();
        subwayList.clear();
        bankList.clear();
        restaurantList.clear();
        cafeList.clear();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CategoryResult> call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "MT1", x + "", y + "", 1000);
        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getDocuments() != null) {
                        Log.d(TAG, "bigMartList Success");
                        bigMartList.addAll(response.body().getDocuments());
                    }
                    call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "CS2", x + "", y + "", 1000);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                Log.d(TAG, "gs24List Success");
                                gs24List.addAll(response.body().getDocuments());

                                call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "SW8", x + "", y + "", 1000);
                                call.enqueue(new Callback<CategoryResult>() {
                                    @Override
                                    public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            Log.d(TAG, "subwayList Success");
                                            subwayList.addAll(response.body().getDocuments());

                                            call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "FD6", x + "", y + "", 1000);
                                            call.enqueue(new Callback<CategoryResult>() {
                                                @Override
                                                public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                    if (response.isSuccessful()) {
                                                        assert response.body() != null;
                                                        Log.d(TAG, "restaurantList Success");
                                                        restaurantList.addAll(response.body().getDocuments());
                                                        call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "CE7", x + "", y + "", 1000);
                                                        call.enqueue(new Callback<CategoryResult>() {
                                                            @Override
                                                            public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                                if (response.isSuccessful()) {
                                                                    assert response.body() != null;
                                                                    Log.d(TAG, "cafeList Success");
                                                                    cafeList.addAll(response.body().getDocuments());
                                                                    //모두 통신 성공 시 circle 생성
                                                                    MapCircle circle1 = new MapCircle(
                                                                            MapPoint.mapPointWithGeoCoord(y, x), // center
                                                                            1000, // radius
                                                                            Color.argb(128, 255, 0, 0), // strokeColor
                                                                            Color.argb(128, 0, 255, 0) // fillColor
                                                                    );
                                                                    circle1.setTag(100);
                                                                    mapView.addCircle(circle1);
                                                                    Log.d(TAG, "원 생성");

                                                                    Log.d("SIZE1", bigMartList.size() + "");
                                                                    Log.d("SIZE2", gs24List.size() + "");
                                                                    Log.d("SIZE3", subwayList.size() + "");
                                                                    Log.d("SIZE4", bankList.size() + "");
                                                                    Log.d("SIZE5", restaurantList.size() + "");
                                                                    //마커 생성
                                                                    int tagNum = 10;
                                                                    for (Document document : bigMartList) {
                                                                        MapPOIItem marker = new MapPOIItem();
                                                                        marker.setItemName(document.getPlaceName()); //눌렀을 때 이름 나오게 하는 코드

                                                                        marker.setTag(tagNum++);
                                                                        double x = Double.parseDouble(document.getY());
                                                                        double y = Double.parseDouble(document.getX());
                                                                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                        marker.setMapPoint(mapPoint);
//                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_big_mart_marker); // 마커 이미지.
                                                                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
                                                                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                                                                        marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                                                                        mapView.addPOIItem(marker);

                                                                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                                                                    }
                                                                    for (Document document : gs24List) {
                                                                        MapPOIItem marker = new MapPOIItem();
                                                                        marker.setItemName(document.getPlaceName());

                                                                        marker.setTag(tagNum++);
                                                                        double x = Double.parseDouble(document.getY());
                                                                        double y = Double.parseDouble(document.getX());
                                                                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                        marker.setMapPoint(mapPoint);
//                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_24_mart_marker); // 마커 이미지.
                                                                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
                                                                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                        mapView.addPOIItem(marker);

                                                                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                                                                    }
                                                                    for (Document document : subwayList) {
                                                                        MapPOIItem marker = new MapPOIItem();
                                                                        marker.setItemName(document.getPlaceName());

                                                                        marker.setTag(tagNum++);
                                                                        double x = Double.parseDouble(document.getY());
                                                                        double y = Double.parseDouble(document.getX());
                                                                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                        marker.setMapPoint(mapPoint);
//                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_subway_marker); // 마커 이미지.
                                                                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                                                                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                        mapView.addPOIItem(marker);

                                                                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                                                                    }
                                                                    for (Document document : bankList) {
                                                                        MapPOIItem marker = new MapPOIItem();
                                                                        marker.setItemName(document.getPlaceName());

                                                                        marker.setTag(tagNum++);
                                                                        double x = Double.parseDouble(document.getY());
                                                                        double y = Double.parseDouble(document.getX());
                                                                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                        marker.setMapPoint(mapPoint);
//                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_bank_marker); // 마커 이미지.
                                                                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                                                                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                        mapView.addPOIItem(marker);

                                                                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                                                                    }
                                                                    for (Document document : restaurantList) {
                                                                        MapPOIItem marker = new MapPOIItem();
                                                                        marker.setItemName(document.getPlaceName());

                                                                        marker.setTag(tagNum++);
                                                                        double x = Double.parseDouble(document.getY());
                                                                        double y = Double.parseDouble(document.getX());
                                                                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                        marker.setMapPoint(mapPoint);
//                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_pharmacy_marker); // 마커 이미지.
                                                                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                                                                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                        mapView.addPOIItem(marker);

                                                                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
                                                                        //자세히보기 fab 버튼 보이게
//                                                                                                                        mLoaderLayout.setVisibility(View.GONE);
//                                                                                                                        searchDetailFab.setVisibility(View.VISIBLE);
                                                                    }
                                                                    for (Document document : cafeList) {
                                                                        MapPOIItem marker = new MapPOIItem();
                                                                        marker.setItemName(document.getPlaceName());

                                                                        marker.setTag(tagNum++);
                                                                        double x = Double.parseDouble(document.getY());
                                                                        double y = Double.parseDouble(document.getX());
                                                                        //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                        marker.setMapPoint(mapPoint);
//                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_cafe_marker); // 마커 이미지.
                                                                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                                                                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                        mapView.addPOIItem(marker);

                                                                        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);

                                                                    }

                                                                }

                                                            }

                                                            @Override
                                                            public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<CategoryResult> call, Throwable t) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

            }
        });
    }
}