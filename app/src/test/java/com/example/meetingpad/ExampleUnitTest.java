package com.example.meetingpad;

import android.util.Log;

import org.junit.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
        assertEquals(4, 2 + 2);
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