package ddwucom.contest.centerpick.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ddwucom.contest.centerpick.R;

import static ddwucom.contest.centerpick.activity.MapActivity.latitude;
import static ddwucom.contest.centerpick.activity.MapActivity.longitude;

public class testActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button btn = findViewById(R.id.btn3);

        final EditText la = findViewById(R.id.latitude);
        final EditText lo = findViewById(R.id.longitude);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                la.setText(latitude.get(0));
                lo.setText(longitude.get(0));
            }
        });
    }


}