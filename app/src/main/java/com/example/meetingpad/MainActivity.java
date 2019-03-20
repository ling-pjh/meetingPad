package com.example.meetingpad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.meetingpad.HTTPService.StateService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextMeetingRoomID;
    private Button btnOk1,btnOk2,btnOk3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("设置会议室ID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOk1=(Button)findViewById(R.id.btn_ok1);
        btnOk2=(Button)findViewById(R.id.btn_ok2);
        btnOk3=(Button)findViewById(R.id.btn_ok3);

        editTextMeetingRoomID=(EditText)findViewById(R.id.edit_text_meeting_room_id);

        btnOk1.setOnClickListener(this);
        btnOk2.setOnClickListener(this);
        btnOk3.setOnClickListener(this);
    }

    private void startFreeActivity(String meetingRoomId){
        //启动空闲activity
        Intent intent1 = new Intent(this, StateService.class);
        startService(intent1);
        Intent intent=new Intent(MainActivity.this,FreeActivity.class);
        intent.putExtra("meetingRoomId",meetingRoomId);
        startActivity(intent);
    }

    private void startCheckingActivity(String meetingRoomId){
        //启动签到中activity
        Intent intent=new Intent(MainActivity.this,CheckingActivity.class);
        intent.putExtra("meetingRoomId",meetingRoomId);
        startActivity(intent);
    }

    private void startInMeetingActivity(String meetingRoomId){
        //启动签到中activity
        Intent intent=new Intent(MainActivity.this,InMeetingActivity.class);
        intent.putExtra("meetingRoomId",meetingRoomId);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
            if(editTextMeetingRoomID.getText().toString()=="")return;
            switch(v.getId()){
                case R.id.btn_ok1:
                    startFreeActivity(editTextMeetingRoomID.getText().toString());
                    break;
                case R.id.btn_ok2:
                    startCheckingActivity(editTextMeetingRoomID.getText().toString());
                    break;
                case R.id.btn_ok3:
                    startInMeetingActivity(editTextMeetingRoomID.getText().toString());
                    break;
            }
    }
}
