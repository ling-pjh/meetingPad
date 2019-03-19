package com.example.meetingpad.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {

	public int mNo=0;
	public String rId ="";
	public String startDate;
	public String startTime;
	public String endTime;

	
	public Meeting meeting;//DONE Event到Meeting关联映射
	
	
	
	public Event(int mNo, String rId, LocalDate startDate, LocalTime startTime, LocalTime endTime) {
		super();
		this.mNo = mNo;
		this.rId = rId;
	}
	public Event() {
		super();
	}
	public int getmNo() {
		return mNo;
	}
	public void setmNo(int mNo) {
		this.mNo = mNo;
	}
	public String getrId() {
		return rId;
	}
	public void setrId(String rId) {
		this.rId = rId;
	}


	public Meeting getMeeting() {
		return meeting;
	}
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public static Event fromJSONObject(JSONObject event) {
		Event eve = new Event();
		try {
			JSONObject startTime = event.getJSONObject("startTime");
			JSONObject startDate = event.getJSONObject("startDate");
			JSONObject endTime = event.getJSONObject("endTime");
			eve.mNo = event.getInt("mNo");
			eve.startDate = startDate.getInt("year")+"-"+
					startDate.getInt("monthValue")+"-"+startDate.getInt("dayOfMonth");
			eve.startTime = startTime.getInt("hour")+":"+startTime.getInt("minute");
			eve.endTime = endTime.getInt("hour")+":"+endTime.getInt("minute");
			JSONObject meeting = event.getJSONObject("meeting");
			eve.meeting = Meeting.fromJSONObject(meeting);
			return eve;
		} catch (JSONException e) {
			e.printStackTrace();
			return new Event();
		}
	}

	public static List<Event> fromJSONArray(JSONArray eventlist) {
	    List<Event> list = new ArrayList<Event>();
		try {
			for(int i=0;i<eventlist.length();++i){
			    JSONObject event = eventlist.getJSONObject(i);
                Event eve = fromJSONObject(event);
                list.add(eve);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<Event>();
		}
	}



}
