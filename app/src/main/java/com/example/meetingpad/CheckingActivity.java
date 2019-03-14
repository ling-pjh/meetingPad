package com.example.meetingpad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class CheckingActivity extends AppCompatActivity {

    private TextView tvMeetingRoomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_checking);
            getSupportActionBar().setTitle("签到中");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tvMeetingRoomId=(TextView)findViewById(R.id.tv_meeting_room_id_checking);
            tvMeetingRoomId.setText(getIntent().getStringExtra("meetingRoomId"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
