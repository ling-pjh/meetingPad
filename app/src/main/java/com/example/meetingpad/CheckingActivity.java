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
import com.example.meetingpad.HTTPService.StateService;
import com.example.meetingpad.entity.Meeting;
import com.example.meetingpad.entity.PersonLight;

import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CheckingActivity extends AppCompatActivity {

    private TextView tvMeetingRoomId;
    private RecyclerView checkedPeopleImages,notCheckPeopleImages;
    private AttendMeetingListAdapter checkedPeopleAdapter,notCheckPeopleAdapter;
    private List<PersonLight> checkedPeople,notCheckPeople;
    private TextView infomationTV;
    private Meeting meeting;//用来保存这个页面的一切动态信息
    //CheckingActivity不断刷新签到名单，并在十分钟过后自动跳转到InMeetingActivity
    TimerTask toInMeetingState= new TimerTask() {//转换为签到中时的任务
        @Override
        public void run() {
            Intent intent=new Intent(CheckingActivity.this, InMeetingActivity.class);
            intent.putExtra("meetingRoomId","A001");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Log.i("CheckingActivity","toInMeetingState");
//            intent.putExtra("mNo",mNo);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Timer timer = new Timer();
            //设置周期性刷新签到表
            timer.schedule(toInMeetingState,new Date(System.currentTimeMillis()+5000));
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
}
