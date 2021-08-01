package ddwucom.contest.centerpick.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.adapter.LocationAdapter;
import ddwucom.contest.centerpick.api.ApiClient;
import ddwucom.contest.centerpick.api.ApiInterface;
import ddwucom.contest.centerpick.model.category_search.CategoryResult;
import ddwucom.contest.centerpick.model.category_search.Document;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ddwucom.contest.centerpick.activity.MapActivity.latitude;
import static ddwucom.contest.centerpick.activity.MapActivity.longitude;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_address;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_addressList;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_placeName;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_placeNameList;

public class SearchActivity extends AppCompatActivity {

    final static String TAG = "MapTAG";
    RecyclerView recyclerView;
    EditText mSearchEdit;
    ArrayList<Document> documentArrayList = new ArrayList<>(); //지역명 검색 결과 리스트

    final Geocoder geocoder = new Geocoder(this); //위경도 변경할 때 씀씀

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);
        search();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_add:
                List<Address> list = null;

//                String str = address.getText().toString();
                try {
                    list = geocoder.getFromLocationName(
                            pick_address, // 지역 이름
                            10); // 읽을 개수

                    pick_addressList.add(pick_address);
                    pick_placeNameList.add(pick_placeName);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0) {
//                        latitude.setText("해당되는 주소 정보는 없습니다");
                    } else {
//                        tv.setText(list.get(0).toString());
                        //          list.get(0).getCountryName();  // 국가명
                        //          list.get(0).getLatitude();        // 위도
                        //          list.get(0).getLongitude();    // 경도

//                        latitude.setText(String.valueOf(list.get(0).getLatitude()));
//                        longitude.setText(String.valueOf(list.get(0).getLongitude()));

                        latitude.add(String.valueOf(list.get(0).getLatitude()));
                        longitude.add(String.valueOf(list.get(0).getLongitude()));
                    }
                }

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();

//                test 용 코드
//                Intent intent = new Intent(this, testActivity.class);
//                startActivity(intent);
                break;
        }
    }

    private void search(){
        mSearchEdit = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.map_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생
        final LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), mSearchEdit, recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);

        //검색기능
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG, "입력 전 상태");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    Log.d(TAG, "검색창 바뀜");

                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation("KakaoAK a58f90fdd5bc2bfbd6f8658473a829aa", charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                            Log.d(TAG, "입력 실패");
                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때

                Log.d(TAG, "입력 끝");
            }
        });

        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        mSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();

            }
        });

    }
}