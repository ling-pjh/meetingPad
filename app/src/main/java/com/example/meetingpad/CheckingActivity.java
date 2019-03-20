package com.example.meetingpad;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.meetingpad.Adapter.AttendMeetingListAdapter;
import com.example.meetingpad.HTTPService.OKHTTP;
import com.example.meetingpad.HTTPService.StateService;
import com.example.meetingpad.entity.Meeting;
import com.example.meetingpad.entity.PersonLight;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CheckingActivity extends AppCompatActivity {

    private TextView tvMeetingRoomId;
    private RecyclerView checkedPeopleImages,notCheckPeopleImages;
    private AttendMeetingListAdapter checkedPeopleAdapter,notCheckPeopleAdapter;
    private List<PersonLight> checkedPeople,notCheckPeople;
    private TextView infomationTV;
    private int mNo;
    private Meeting meetingDetail;//用来保存这个页面的一切动态信息
    //CheckingActivity不断刷新签到名单，并在十分钟过后自动跳转到InMeetingActivity
    TimerTask toInMeetingState= new TimerTask() {//转换为签到中时的任务
        @Override
        public void run() {
            Intent intent=new Intent(CheckingActivity.this, InMeetingActivity.class);
            intent.putExtra("meetingRoomId","A001");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Log.i("CheckingActivity","toInMeetingState");
            intent.putExtra("mNo",mNo);
            startActivity(intent);
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

            //定时操作
            Timer timer = new Timer();
            //设置周期性刷新签到表
            //设置十分钟后转入InMeeting//现在设置的是6秒
            timer.schedule(toInMeetingState,new Date(System.currentTimeMillis()+6000));



            setContentView(R.layout.activity_checking);
            getSupportActionBar().setTitle("签到中");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            int i;
            tvMeetingRoomId=(TextView)findViewById(R.id.tv_meeting_room_id_checking);
            tvMeetingRoomId.setText(getIntent().getStringExtra("meetingRoomId"));

            checkedPeople=new ArrayList<>();
            for(i=1;i<=30;i++)checkedPeople.add(new PersonLight());
            notCheckPeople=new ArrayList<>();
            for(i=1;i<=30;i++)notCheckPeople.add(new PersonLight());


            checkedPeopleAdapter=new AttendMeetingListAdapter(this,checkedPeople);
            notCheckPeopleAdapter=new AttendMeetingListAdapter(this,notCheckPeople);


            checkedPeopleImages=(RecyclerView)findViewById(R.id.recycler_view_checked_in_checking);
            checkedPeopleImages.setLayoutManager(new GridLayoutManager(this,6));
            notCheckPeopleImages=(RecyclerView)findViewById(R.id.recycler_view_not_check_in_checking);
            notCheckPeopleImages.setLayoutManager(new GridLayoutManager(this,6));

            checkedPeopleImages.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.right=20;
                    outRect.bottom=20;
                }
            });

            notCheckPeopleImages.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.right=20;
                    outRect.bottom=20;
                }
            });

            checkedPeopleImages.setAdapter(checkedPeopleAdapter);
            notCheckPeopleImages.setAdapter(notCheckPeopleAdapter);

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
