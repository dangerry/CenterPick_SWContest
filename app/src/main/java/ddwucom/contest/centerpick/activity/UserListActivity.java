package ddwucom.contest.centerpick.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ddwucom.contest.centerpick.R;
import ddwucom.contest.centerpick.adapter.UserListAdapter;
import ddwucom.contest.centerpick.data.UserData;
import ddwucom.contest.centerpick.data.UserListManager;

import static ddwucom.contest.centerpick.activity.MapActivity.latitude;
import static ddwucom.contest.centerpick.activity.MapActivity.longitude;
import static ddwucom.contest.centerpick.activity.MapActivity.pick_addressList;
import static ddwucom.contest.centerpick.activity.MapActivity.remove_latitude;
import static ddwucom.contest.centerpick.activity.MapActivity.remove_longitude;
import static ddwucom.contest.centerpick.activity.MapActivity.remove_pos;

public class UserListActivity extends AppCompatActivity {

    ArrayList<UserData> userDataList;
    ListView listView;
    UserListAdapter adapter;
    UserListManager userListManager;
    final String TAG="AAA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        listView = findViewById(R.id.listView);

        userListManager = new UserListManager();
        userDataList = userListManager.getUserList();


        adapter = new UserListAdapter(this,R.layout.item_location, userDataList);
        //adapter = new LocationAdapter(this, R.layout.item_location, userDataList);

        listView.setAdapter((ListAdapter) adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                final int pos = position;

                Intent intent = new Intent();
                intent.putExtra("result", position);

                AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
                builder.setTitle("삭제 처리");
                builder.setMessage(userDataList.get(position).getAddress_number()
                        +"삭제 하시겠습니까?");
                builder.setPositiveButton("삭제버튼", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        userListManager.removeList(pos);
                        remove_pos.add(pos);
                        Log.d(TAG, String.valueOf(pos));

                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("취소 버튼", null);
                builder.show();

//                setResult(RESULT_OK, intent);

                return true;
            }
        });

//예시를 넣어본다면
        Log.d("you2", pick_addressList.get(0));
//        Log.d("you3", pick_addressList.get(1));
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_update:

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();

                break;
        }
    }
}