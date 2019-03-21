package com.example.meetingpad.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Meeting {
	public int mNo=-1;/*唯一且自增，可代表发起的先后顺序*/
	public String mTitle=null;
	public String mInfo=null;
	public int mSize=-1;;/*容纳人数*/
	public int mSpan=-1;/*时长,分钟数*/
	public String tName=null;/*特性信息*/
	public String pId_FQ=null;/*发起人*/
	public String state=null;
	public Timestamp mCreateTime=null;

	public PersonLight creator= new PersonLight();
	//集合映射
	public	List<Event> mEventList= new ArrayList<>();
	public	List<PersonLight> mAttendList = new ArrayList<>();
	public	List<PersonLight> mInformList= new ArrayList<>();
	//DONE 参会&报送名单映射
	
	public Meeting() {
		
	}
	public Meeting(String mTitle, String mInfo, int mSize, int mSpan, String pId_FQ) {
		super();
		this.mTitle = mTitle;
		this.mInfo = mInfo;
		this.mSize = mSize;
		this.mSpan = mSpan;
		this.pId_FQ = pId_FQ;
	}
	//用于更新
	public Meeting(int mNo, String mTitle, String mInfo, int mSize,int mSpan,String tName) {
		super();
		this.mNo = mNo;
		this.mTitle = mTitle;
		this.mInfo = mInfo;
		this.mSize = mSize;
		this.mSpan = mSpan;
		this.tName = tName;
	}
	public Meeting(int mNo, String mTitle, String mInfo, int mSize,int mSpan) {
		super();
		this.mNo = mNo;
		this.mTitle = mTitle;
		this.mInfo = mInfo;
		this.mSize = mSize;
		this.mSpan = mSpan;
	}


    public static Meeting fromJSONObject(JSONObject meeting) {
		Meeting m = new Meeting();
		try {
			m.mNo = meeting.getInt("mNo");
			m.mTitle = meeting.getString("mTitle");
			m.mInfo = meeting.getString("mInfo");
			m.mSize =meeting.getInt("mSize");
			m.mSpan =meeting.getInt("mSpan");
			m.pId_FQ =meeting.getString("pId_FQ");
			JSONArray jsonArray = null;
			try {
				jsonArray = meeting.getJSONArray("mEventList");
				m.mEventList = Event.fromJSONArray(jsonArray);
			} catch (JSONException e) {
				m.mEventList =null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return new Meeting();
		}
		return m;
    }
	public static Meeting fromJSONObjectDetail(JSONObject meeting) {
		Meeting m = new Meeting();
		try {
			m.mNo = meeting.getInt("mNo");
			m.mTitle = meeting.getString("mTitle");
			m.mInfo = meeting.getString("mInfo");
			m.mSize =meeting.getInt("mSize");
			m.mSpan =meeting.getInt("mSpan");
			m.pId_FQ =meeting.getString("pId_FQ");
			JSONObject joCreator = meeting.getJSONObject("creator");
			JSONArray jaEventList= meeting.getJSONArray("mEventList");
			JSONArray jaAttendList= meeting.getJSONArray("mAttendList");
			JSONArray jaInformList= meeting.getJSONArray("mInformList");
			m.creator = PersonLight.fromJSONObject(joCreator);
			m.mEventList = Event.fromJSONArray(jaEventList);
			m.mAttendList = PersonLight.fromJSONArray(jaAttendList);
			m.mInformList = PersonLight.fromJSONArray(jaInformList);
		} catch (JSONException e) {
			e.printStackTrace();
			return new Meeting();
		}
		return m;
	}

    public int getmNo() {
		return mNo;
	}
	public void setmNo(int mNo) {
		this.mNo = mNo;
	}
	public String getmTitle() {
		return mTitle;
	}
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public String getmInfo() {
		return mInfo;
	}
	public void setmInfo(String mInfo) {
		this.mInfo = mInfo;
	}
	public int getmSize() {
		return mSize;
	}
	public void setmSize(int mSize) {
		this.mSize = mSize;
	}
	public int getmSpan() {
		return mSpan;
	}
	public void setmSpan(int mSpan) {
		this.mSpan = mSpan;
	}

	public String getpId_FQ() {
		return pId_FQ;
	}
	public void setpId_FQ(String pId_FQ) {
		this.pId_FQ = pId_FQ;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Timestamp getmCreateTime() {
		return mCreateTime;
	}
	public void setmCreateTime(Timestamp mCreateTime) {
		this.mCreateTime = mCreateTime;
	}
	public String gettName() {
		return tName;
	}
	public void settName(String tName) {
		this.tName = tName;
	}
	public List<Event> getmEventList() {
		if(mEventList!=null) {
			for(Event e: mEventList) {
				if(e.getrId()==null) {
					mEventList.remove(e);
					System.out.println("remove");
					return mEventList;
				}
			}
		}
		return mEventList;
	}
	public void setmEventList(List<Event> mEventList) {
		/*if(mEventList!=null)System.out.println("setmEventList"+mEventList.toString());
		for(Event e: mEventList) {
			if(e.getrId()==null) {
				mEventList.remove(0);
				System.out.println("remove");
			}
			System.out.println("for");
		}*/
		this.mEventList = mEventList;
	}
	public List<PersonLight> getmAttendList() {
		return mAttendList;
	}
	public void setmAttendList(List<PersonLight> mAttendList) {
		this.mAttendList = mAttendList;
	}
	public List<PersonLight> getmInformList() {
		return mInformList;
	}
	public void setmInformList(List<PersonLight> mInformList) {
		this.mInformList = mInformList;
	}
	public PersonLight getCreator() {
		return creator;
	}
	public void setCreator(PersonLight creator) {
		this.creator = creator;
	}


}
