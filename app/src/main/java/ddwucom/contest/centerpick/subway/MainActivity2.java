package ddwucom.contest.centerpick.subway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.activity.MainActivity;
import ddwucom.contest.centerpick.activity.MapActivity;

public class MainActivity2 extends Activity {
    final String TAG="AddActivity";

    final int ADD_CODE = 100;

    ImageViewTouchable imageView;
    FrameLayout mainLayout;
    LinearLayout btn_1;
    LinearLayout btn_2;
    TextView tv_btn;
    static float imgSizeWidth, imgSizeHeight, imgSizeHeightGetYHeight, statusBarHeight, softKeyHeight;
    FloatingActionButton fab;
    InputMethodManager imm;

    //search
    EditText mSearchEdit;
    Item subway = null;
    RecyclerView recyclerView;
    ArrayList<Item> list = null; //역 검색 결과 리스트
    private String requestUrl;
    public String key = "HbK4BvjG4moF4d2D74EO7Jswx68CbhkiHPz9JEEVFi42CaRp6%2BvwKFAw7hWFpn4SWnXutx3%2BLdlDOOuUcqyI1w%3D%3D";
    String station;
    String clickStation;


    ArrayList<String> users = new ArrayList<>(10);
    ArrayList<String> users_update = new ArrayList<>(10);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSearchEdit = (EditText)findViewById(R.id.etSearch);
        recyclerView = (RecyclerView)findViewById(R.id.result_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);


        initView();

        fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkIntent = new Intent(MainActivity2.this, AddCheckActivity.class);
                checkIntent.putExtra("users", users);
                startActivityForResult(checkIntent, ADD_CODE);
            }
        });

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.v("check","before");
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("check","textChanged");
                recyclerView.setVisibility(View.VISIBLE);
                if (s.length() >= 1) {
                    try{
                        station = URLEncoder.encode(s.toString(), "UTF-8");
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }

                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();

                }else{
                    if(s.length() <= 0){
                        recyclerView.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.v("check","after");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //AddCheckActivity 에서 받아온 users 반영
        users.clear();
        users.addAll(users_update);
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            requestUrl = "http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getKwrdFndSubwaySttnList?"//요청 URL
                    +"subwayStationName="+ station
                    +"&pageNo=1&numOfRows=1000&ServiceKey=" + key;
            try {
                boolean b_stationName = false;
                boolean b_routeId = false;
                boolean b_stationId = false;

                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));

                String tag;
                int eventType = parser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<Item>();
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                        case XmlPullParser.END_TAG:
                            if(parser.getName().equals("item") && subway != null) {
                                list.add(subway);
                            }
                            break;
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("item")){
                                subway = new Item();
                            }
                            if (parser.getName().equals("subwayRouteName")) b_routeId  = true;
                            if (parser.getName().equals("subwayStationId")) b_stationId = true;
                            if (parser.getName().equals("subwayStationName")) b_stationName = true;

                            break;
                        case XmlPullParser.TEXT:
                            if(b_routeId){
                                subway.setSubwayRouteName(parser.getText());
                                b_routeId = false;
                            } else if(b_stationName) {
                                subway.setSubwayStationName(parser.getText());
                                b_stationName = false;
                            } else if (b_stationId) {
                                subway.setSubwayStationId(parser.getText());
                                b_stationId = false;
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //어답터 연결
            StationAdapter adapter = new StationAdapter(getApplicationContext(), list, mSearchEdit);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initView() {
        //binding
        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);//노선도 띄울 레이아웃
        fab = (FloatingActionButton) findViewById(R.id.fab1);

        //버튼 추가 코드
        btn_1 = new LinearLayout(this);
        btn_1.setPadding(0,0,0,30);
        btn_1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn_1.setOrientation(LinearLayout.VERTICAL);

        btn_2 = new LinearLayout(this);
        btn_2.setLayoutParams(new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn_2.setOrientation(LinearLayout.HORIZONTAL);

        tv_btn = new TextView(this);
        tv_btn.setText(" ▽ 터치하세요! ▽ ");
        tv_btn.setTextSize(20f);
        tv_btn.setTextColor(getResources().getColor(R.color.textColor));
        tv_btn.setGravity(Gravity.CENTER);

        btn_1.setBackgroundResource(R.color.selectionColor);
        btn_1.addView(tv_btn);
        btn_1.addView(btn_2);
        btn_1.setGravity(Gravity.CENTER);
        btn_2.setGravity(Gravity.CENTER);
        mainLayout.addView(btn_1);

        Button buttonAdd_floatingButton = new Button(this);

        buttonAdd_floatingButton.setText("추가");
        buttonAdd_floatingButton.setTextColor(getResources().getColor(R.color.textColor));
        buttonAdd_floatingButton.setTextSize(15);

        buttonAdd_floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStation = imageView.clickStationName;
                if(clickStation.equals("")){
                    Toast.makeText(MainActivity2.this, "역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    users.add(clickStation);
                    Toast.makeText(MainActivity2.this, clickStation + " 추가", Toast.LENGTH_SHORT).show();
                    clickStation = "";
                }
            }
        });

        LinearLayout.LayoutParams position = new LinearLayout.LayoutParams(1000, 100, 2); //1000, 300, 2

        btn_2.addView(buttonAdd_floatingButton, position);
        btn_2.setVisibility(LinearLayout.GONE);
    }

    void setFBText(String text){
        tv_btn.setText(text);
    }


    void setFBVisibility(int visibility){
        if(visibility == View.VISIBLE) {
            btn_1.setVisibility(View.VISIBLE);
            btn_2.setVisibility(View.VISIBLE);
        }else if(visibility == View.INVISIBLE){
            btn_1.setVisibility(View.INVISIBLE);
        }else{
            Log.v("ERR","FloatingButton Visibility 에러");
        }
    }

    //현재 액티비티의 포커스 여부
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ResolutionManager resolutionManager = new ResolutionManager(this);
        imgSizeWidth = resolutionManager.getxWidth();
        imgSizeHeight = resolutionManager.getImageViewSize();
        imgSizeHeightGetYHeight = resolutionManager.getyHeight();
        statusBarHeight = resolutionManager.getStatusBarHeight();
        softKeyHeight = resolutionManager.getSoftKeyHeight();

        imageView = new ImageViewTouchable(this);
        imageView.setImageResource(R.drawable.metro_map50);
        imageView.setDrawingCacheEnabled(false);
        mainLayout.removeAllViews();
        mainLayout.addView(imageView);
        mainLayout.addView(btn_1);
        mainLayout.addView(fab);
//        Log.v("FocusChanged","A");
    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_add:
                String station = mSearchEdit.getText().toString();
                if(station.equals("")){
                    Toast.makeText(MainActivity2.this, "역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    users.add(station);
                    Toast.makeText(MainActivity2.this, station + " 추가", Toast.LENGTH_SHORT).show();
                    mSearchEdit.setText("");
                }
                //버튼 누르면 키보드 내리기
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.btn_map:
                Intent intent = new Intent(MainActivity2.this, MapActivity.class);

                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_CODE:
                if (resultCode == RESULT_OK) {
                    users_update = (ArrayList<String>)data.getSerializableExtra("users");
                }
                break;
        }
    }
}
