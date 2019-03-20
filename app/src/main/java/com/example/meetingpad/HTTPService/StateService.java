package com.example.meetingpad.HTTPService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.meetingpad.CheckingActivity;
import com.example.meetingpad.FreeActivity;
import com.example.meetingpad.InMeetingActivity;
import com.example.meetingpad.entity.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StateService extends Service {
    public static final String TAG = "StateService";
    private static final List<Event> eventList = new ArrayList<>();
    private static final Timer timer = new Timer();

    TimerTask toCheckInState = new TimerTask() {//转换为签到中时的任务
        @Override
        public void run() {
            Intent intent=new Intent(StateService.this, CheckingActivity.class);
//            intent.putExtra("mNo",mNo);
            intent.putExtra("meetingRoomId","A001");
            Log.i(TAG,"toCheckInState");
            startActivity(intent);
        }
    };
    TimerTask toInMeetingState= new TimerTask() {//转换为签到中时的任务
        @Override
        public void run() {
            Intent intent=new Intent(StateService.this, InMeetingActivity.class);
            intent.putExtra("meetingRoomId","A001");
            Log.i(TAG,"toInMeetingState");
//            intent.putExtra("mNo",mNo);
            startActivity(intent);
        }
    };
    TimerTask toFreeState= new TimerTask() {//转换为签到中时的任务
        @Override
        public void run() {
            Intent intent=new Intent(StateService.this, FreeActivity.class);
            intent.putExtra("meetingRoomId","A001");
//            intent.putExtra("mNo",mNo);
            startActivity(intent);
        }
    };


    public StateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        Log.i(TAG,"call onCreate...");
        //向服务器请求该房间的EventList
        eventList.add(new Event(2,"A001",new Date(2019,2,20,17,32),
                new Date(2019,2,20,17,45)));
        //按照eventList schedule切换事件
        schedule(eventList);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"call onStartCommand...");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"call onDestroy...");
    }

    //
    public void schedule(List<Event> eventList){
        for(Event e:eventList){
//            十分钟：600000;
            Date startTime = e.startTimeDate;
            Date checkinTime = new Date(startTime.getTime()-60000);
            Date endTime = e.endTimeDate;
            timer.schedule(toCheckInState,checkinTime);
            timer.schedule(toInMeetingState,startTime);
            timer.schedule(toFreeState,endTime);
            Log.i(TAG,"schedule");
        }
    }


}
