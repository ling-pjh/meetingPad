package com.example.meetingpad;

import android.util.Log;

import com.example.meetingpad.Service.OKHTTP;
import com.example.meetingpad.entity.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getEvent() {
        OKHTTP.BASE_URL = OKHTTP.BASE_URL_TEST;
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
//                        JSONTokener jsonParser = new JSONTokener(jsStr);
//                        JSONObject noteResult = (JSONObject) jsonParser.nextValue();
//                        JSONArray joeventList = noteResult.getJSONArray("data");
//                        List<Event> elist = Event.fromJSONArray(joeventList);
                        System.err.println(response.toString());
                        //按照eventList schedule切换事件
//                        schedule(elist);//TODO 成员变量eventList是否还需要？

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                toastHandler.sendEmptyMessage(REQUESTNULL);
                    System.err.println("null!");
                }
            }
        };
        OKHTTP.postForm("/pad/findEventList.do",map,scheduleEvents);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void timing() {

        Timer timer = new Timer();
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                System.out.println("Main2Activity,timertask");
            }
        };
        assertEquals(4, 2 + 2);
        Date date = new Date(System.currentTimeMillis()+6000);
        //查到一个Event，取其开始时间减去10分钟设置切换为签到中task，全局变量timer，每新查到一个event，就schedule一系列切换行为
        timer.schedule(task,date);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}