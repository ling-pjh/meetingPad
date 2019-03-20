package com.example.meetingpad.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

	public int mNo=0;
	public String rId ="";
	public String startDate;
	public String startTime;
	public String endTime;
	public Date startTimeDate;
	public Date endTimeDate;

	
	public Meeting meeting;//DONE Event到Meeting关联映射


	public Event(int mNo, String rId, Date startTimeDate, Date endTimeDate) {
		super();
		this.mNo = mNo;
		this.rId = rId;
		this.startTimeDate = startTimeDate;
		this.endTimeDate = endTimeDate;
	}
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
			int startYear = startDate.getInt("year");
			int startMonth = startDate.getInt("monthValue");
			int startDay = startDate.getInt("dayOfMonth");
			int startHour = startTime.getInt("hour");
			int startMin = startTime.getInt("minute");
			int endHour = endTime.getInt("hour");
			int endMin = endTime.getInt("minute");

			eve.startDate = startYear+"-"+startMonth+"-"+startDay;
			eve.startTime = startHour+":"+startMin;
			eve.endTime = endHour+":"+endMin;
			eve.endTimeDate = new Date(startYear,startMonth,startDay,startHour,startMin);
			eve.startTimeDate = new Date(startYear,startMonth,startDay,endHour,endMin);
			try {
				JSONObject meeting = event.getJSONObject("meeting");
				eve.meeting = Meeting.fromJSONObject(meeting);
			} catch (JSONException e) {
				eve.meeting = null;
			}
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
