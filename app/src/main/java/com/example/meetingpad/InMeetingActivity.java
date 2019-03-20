package com.example.meetingpad;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InMeetingActivity extends AppCompatActivity {
    private TextView tvMeetingRoomId;
    private TextView titleTV,timeTV,informationTV,nextMeetingTimeTV,nextMeetingTitleTV,timeToEndTV;
    private View qrCodeImageView,goMoreMeetingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);
        getSupportActionBar().setTitle("会议进行中");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvMeetingRoomId=(TextView)findViewById(R.id.tv_meeting_room_id_in_meeting);
        tvMeetingRoomId.setText(getIntent().getStringExtra("meetingRoomId"));

        titleTV=(TextView)findViewById(R.id.tv_meeting_title_in_meeting);
        timeTV=(TextView)findViewById(R.id.tv_meeting_time_in_meeting);
        informationTV=(TextView)findViewById(R.id.tv_meeting_information_in_meeting);
        nextMeetingTimeTV=(TextView)findViewById(R.id.tv_next_meeting_time_in_meeting);
        nextMeetingTitleTV=(TextView)findViewById(R.id.tv_next_meeting_title_in_meeting);
        timeToEndTV=(TextView)findViewById(R.id.tv_time_to_the_end_of_meeting);
        qrCodeImageView=(View)findViewById(R.id.qr_code_image_view_in_meeting);
        goMoreMeetingView=(View)findViewById(R.id.go_more_meeting_view_in_meeting);



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
