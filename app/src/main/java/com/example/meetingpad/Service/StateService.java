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
        if(timer!=null){//FIXME 就算这样也不能阻止它执行两遍
            return super.onStartCommand(intent, flags, startId);
        }
        //我们在onStartCommand方法中通过intent参数获取activity传过来的值。
        rId =intent.getStringExtra("rId");//FIXME 读到的是空值，所以这里暂时没采用
        Log.i(TAG,"call onStartCommand...");
        //向服务器请求该房间的EventList
        HashMap<String, String> map = new HashMap();
        map.put("rId", "A001");//传进来的rId
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

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"call onDestroy...");
        timer.cancel();//关闭服务时取消所有已安排的任务
        timer.purge();
        timer	= null;
    }

    //
    private void schedule(List<Event> eventList){
        timer = new Timer();
        for(final Event e:eventList){
            Log.i(TAG,"schedule："+e.toString());
            timer.schedule(new TimerTask() {//转换为签到中时的任务
                @Override
                public void run() {
                    Intent intent=new Intent(StateService.this, CheckingActivity.class);
                    intent.putExtra("mNo",e.mNo);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.i(TAG,"toCheckInState");
                    getApplication().startActivity(intent);
                }
            },new Date(e.startTimeDate.getTime()-6000));//设置开会前10分钟开启签到//现在是6秒
        }
    }


}
