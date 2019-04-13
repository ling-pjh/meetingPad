package com.example.meetingpad;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.example.meetingpad.Service.OKHTTP;
import com.example.meetingpad.Service.StateService;
import com.example.meetingpad.Service.TimerService;
import com.example.meetingpad.entity.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FreeActivity extends AppCompatActivity {


    private static String rId;
    private static final List<Event> eventList = new ArrayList<>();
    private static Timer timer = null;
    public static final String TAG = "StateService";
    private ImageView qrCodeView;
    private TextView titleTV,timeTV,informationTV;
    private View goMoreMeetingView;
    private TextView tvMeetingRoomId;
    private Intent intentForStateService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取数据
        Intent i=getIntent();
        String rId = i.getStringExtra("meetingRoomId");
        //启动空闲activity时启动状态管理服务
//        intentForStateService = new Intent(this, StateService.class);
//        intentForStateService.putExtra("rId",rId);
//        startService(intentForStateService);
        getSupportActionBar().setTitle("空闲中");
        setContentView(R.layout.activity_free);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvMeetingRoomId=(TextView)findViewById(R.id.tv_meeting_room_id_free);
        tvMeetingRoomId.setText(rId);

        qrCodeView=(ImageView)findViewById(R.id.qr_code_image_view_in_free);
        titleTV=(TextView)findViewById(R.id.tv_meeting_title_in_free);
        timeTV=(TextView)findViewById(R.id.tv_meeting_time_in_free);
        informationTV=(TextView)findViewById(R.id.tv_meeting_information_in_free);
        goMoreMeetingView=(View)findViewById(R.id.go_more_meeting_view_in_free);

        Log.i(TAG,"call onStartCommand...");
        //向服务器请求该房间的EventList
        HashMap<String, String> map = new HashMap();
        map.put("rId", rId);//传进来的rId
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        map.put("date", simpleDateFormat.format(date));
        final Callback scheduleEvents = new Callback() {
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
                        JSONArray joeventList = noteResult.getJSONArray("data");
                        List<Event> elist = Event.fromJSONArray(joeventList);
                        //按照eventList schedule切换事件
                        schedule(elist);//TODO 成员变量eventList是否还需要？

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
//                toastHandler.sendEmptyMessage(REQUESTNULL);
                    System.err.println("null!");
                }
            }
        };
        OKHTTP.postForm("/pad/findEventList.do",map,scheduleEvents);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //关闭空闲activity时关闭状态管理服务

//        stopService(intentForStateService);//停止服务
    }

    private void schedule(List<Event> eventList){
        timer = new Timer();

        for(final Event e:eventList){
            Log.i(TAG,"schedule："+e.toString());
            System.out.println("schedule：Event"+e.toString());
            System.out.println("new Date(e.startTimeData.getTime()-6000的值为:"+new Date(e.startTimeDate.getTime()-6000));
            if(e.getmNo()==23){
                timer.schedule(new TimerTask() {//转换为签到中时的任务
                    @Override
                    public void run() {
                        Intent intent=new Intent(FreeActivity.this, CheckingActivity.class);
                        intent.putExtra("mNo",e.mNo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i(TAG,"toCheckInState");
                        getApplication().startActivity(intent);
                    }
                },new Date(new Date().getTime()+6000));//设置开会前10分钟开启签到//现在是6秒
            }

        }
    }

}
