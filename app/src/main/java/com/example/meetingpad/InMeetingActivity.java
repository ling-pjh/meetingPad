package com.example.meetingpad;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.meetingpad.Service.OKHTTP;
import com.example.meetingpad.entity.Event;
import com.example.meetingpad.entity.Meeting;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import okhttp3.Call;
import okhttp3.Callback;

public class InMeetingActivity extends AppCompatActivity {
    private final int GET_MEETING_DETAIL_SUCCESS=0;
    private final int COUNT=1;
        private TextView tvMeetingRoomId;
        private TextView titleTV,timeTV,informationTV,nextMeetingTimeTV,nextMeetingTitleTV,timeToEndTV;
        private View qrCodeImageView,goMoreMeetingView;
        private int mNo;//由CheckInActivity传来
        private Meeting meetingDetail;//向服务器发请求查到
        private UIHandler uiHandler=new UIHandler();
        class UIHandler extends Handler {
            @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_MEETING_DETAIL_SUCCESS:
                    titleTV.setText(meetingDetail.getmTitle());
                    timeTV.setText(meetingDetail.getmEventList().get(0).startTimeDate+"--"+meetingDetail.getmEventList().get(0).endTimeDate);
                    informationTV.setText(meetingDetail.getmInfo());
//                    nextMeetingTimeTV
//                    nextMeetingTitleTV
                    Timer countTimer=new Timer();
                    countTimer.schedule(countDownTask,1000,1000);
//                    qrCodeImageView
//                    goMoreMeetingView
                    break;
                case COUNT:
                    timeToEndTV.setText((String)msg.obj);
                    break;

            }
        }
    }

    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(millisecond);
        String timeStr = simpleDateFormat.format(date);
        return timeStr;
    }

    TimerTask countDownTask=new TimerTask() {
        @Override
        public void run() {
            long endTime=meetingDetail.getmEventList().get(0).endTimeDate.getTime();
            long currentTIme=System.currentTimeMillis();
            getTimeFromMillisecond(endTime-currentTIme);
            Message msg=new Message();
            msg.what=COUNT;
            msg.obj=(String)getTimeFromMillisecond(endTime-currentTIme);
            uiHandler.sendMessage(msg);
        }
    };

    TimerTask toFreeState= new TimerTask() {//转换为空闲
        @Override
        public void run() {
            //直接关掉自己就行了
            Intent intent=new Intent(InMeetingActivity.this,FreeActivity.class);
            intent.putExtra("meetingRoomId","CR301");
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取参数
        mNo=getIntent().getIntExtra("mNo",2);
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
        tvMeetingRoomId.setText("会议ID:"+mNo);

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

                    uiHandler.sendEmptyMessage(GET_MEETING_DETAIL_SUCCESS);

//                    titleTV.setText(meetingDetail.getmTitle());
//                    timeTV.setText(meetingDetail.getmEventList().get(0).startTime+"--"+meetingDetail.getmEventList().get(0).endTime);
//                    informationTV.setText(meetingDetail.getmInfo());
//                    nextMeetingTimeTV.setText(meetingDetail);
//                    nextMeetingTitleTV;
//                    timeToEndTV;
//                    qrCodeImageView;
//                    goMoreMeetingView;

                    //会议结束时转入空闲中状态
                    Timer timer = new Timer();
                    timer.schedule(toFreeState,new Date(System.currentTimeMillis()+6000));

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
