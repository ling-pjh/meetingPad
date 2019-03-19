package com.example.meetingpad;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FreeActivity extends AppCompatActivity {

    private ImageView qrCodeView;
    private TextView titleTV,timeTV,informationTV;
    private View goMoreMeetingView;
    private TextView tvMeetingRoomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("空闲中");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvMeetingRoomId=(TextView)findViewById(R.id.tv_meeting_room_id_free);
        Intent i=getIntent();
        tvMeetingRoomId.setText(i.getStringExtra("meetingRoomId"));


        qrCodeView=(ImageView)findViewById(R.id.qr_code_view_in_free);
        titleTV=(TextView)findViewById(R.id.tv_meeting_title_in_free);
        timeTV=(TextView)findViewById(R.id.tv_meeting_time_in_free);
        informationTV=(TextView)findViewById(R.id.tv_meeting_information_in_free);
        goMoreMeetingView=(View)findViewById(R.id.go_more_meeting_view_in_free);


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
