package com.example.meetingpad;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.meetingpad.Adapter.AttendMeetingListAdapter;
import com.example.meetingpad.entity.PersonLight;

import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class CheckingActivity extends AppCompatActivity {

    private TextView tvMeetingRoomId;
    private RecyclerView checkedPeopleImages,notCheckPeopleImages;
    private AttendMeetingListAdapter checkedPeopleAdapter,notCheckPeopleAdapter;
    private List<PersonLight> checkedPeople,notCheckPeople;
    private TextView infomationTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
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
