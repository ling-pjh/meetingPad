package com.example.meetingpad.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.meetingpad.CheckingActivity;
import com.example.meetingpad.FreeActivity;
import com.example.meetingpad.InMeetingActivity;
import com.example.meetingpad.entity.Event;
import com.example.meetingpad.entity.Meeting;

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

public class StateService extends Service {
    public static final String TAG = "StateService";
    private static final List<Event> eventList = new ArrayList<>();
    private static Timer timer = null;
    private static String rId;

    Thread startChecking=new Thread(){
        @Override
        public void run() {
            super.run();

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
        timer = null;
        rId = "default";
        Log.i(TAG,"call onCreate...");

    }
//    public boolean isServiceRunning(/*final String className*/) {
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
//        if (info == null || info.size() == 0) return false;
//        for (ActivityManager.RunningServiceInfo aInfo : info) {
//            if ("com.example.meetingpad.Service.StateService".equals(aInfo.service.getClassName())) return true;
//        }
//        return false;
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //我们在onStartCommand方法中通过intent参数获取activity传过来的值。


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"call onDestroy...");
        if(timer==null)return;
        timer.cancel();//关闭服务时取消所有已安排的任务
        timer.purge();
        timer	= null;
    }



}
