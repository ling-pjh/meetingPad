package com.example.meetingpad;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetingpad.HTTPService.OKHTTP;
import com.example.meetingpad.entity.Event;
import com.example.meetingpad.entity.Meeting;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InMeetingActivity extends AppCompatActivity {
    private TextView tvMeetingRoomId;
    private TextView titleTV,timeTV,informationTV,nextMeetingTimeTV,nextMeetingTitleTV,timeToEndTV;
    private View qrCodeImageView,goMoreMeetingView;
    private int mNo;//由CheckInActivity传来
    private Meeting meetingDetail;//向服务器发请求查到

    TimerTask toFreeState= new TimerTask() {//转换为空闲
        @Override
        public void run() {
            //直接关掉自己就行了
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取参数
        mNo=getIntent().getIntExtra("mNo",0);
        //发请求查到meeting
        HashMap<String, String> map = new HashMap();
        map.put("mNo", mNo+"");//传进来的mNo
        //查询对应会议详情
        OKHTTP.postForm("/meeting/detail.do", map, fillMeetingDetail);


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
    final Callback fillMeetingDetail = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

//            toastHandler.sendEmptyMessage(REQUESTFAIL);
            System.err.println("请求出错");
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
//            toastHandler.sendEmptyMessage(REQUESTSUCCESS);
            System.err.println("请求成功");
            if (response != null) {
                String jsStr = response.body().string();//查看返回的response内容
                System.err.println(jsStr);
                try {
                    JSONTokener jsonParser = new JSONTokener(jsStr);
                    JSONObject noteResult = (JSONObject) jsonParser.nextValue();
                    JSONObject joMeeting = noteResult.getJSONObject("data");
                    meetingDetail = Meeting.fromJSONObjectDetail(joMeeting);
                    Event event = meetingDetail.getmEventList().get(0);
                    //会议结束时转入空闲中状态
                    Timer timer = new Timer();
                    timer.schedule(toFreeState,event.endTimeDate);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                toastHandler.sendEmptyMessage(REQUESTNULL);
                System.err.println("null!");
            }
        }
    };
}
