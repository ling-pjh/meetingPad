package com.example.meetingpad.entity;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import org.springframework.web.multipart.MultipartFile;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/*
 * 参照数据库中的表定义
 * 属性名于字段名保持一致
 * 属性类型与字段类型保持一致
 * 实现序列化
 */

public class PersonLight implements Serializable {
	private String pId = null;
	private String pName = null;
	private String pRole = null;
	private String pDept = null;
	private byte[] pIcon = null;
	private String state = null;
	
	public PersonLight() {
		super();
	}
	public PersonLight(String pId, String pPass) {
		super();
		this.pId = pId;
		this.setpName("--");
	}
	public static PersonLight fromJSONObject(JSONObject jo) {
		PersonLight pl = new PersonLight();
		try {
			pl.pId =jo.getString("pId");
			pl.pDept=jo.getString("pDept");
			String iconStr = jo.getString("pIcon");
			pl.pIcon = Base64.decode(iconStr,Base64.NO_WRAP);
			//pl.pIcon=jo.get("pId");//FIXME 头像怎么存
			pl.pName=jo.getString("pName");
			pl.pRole=jo.getString("pRole");
//			pl.state=jo.getString("state");
		} catch (JSONException e) {
			e.printStackTrace();
			return new PersonLight();
		}
		return pl;

	}

    public static List<PersonLight> fromJSONArray(JSONArray ja) {
		List<PersonLight> list = new ArrayList<>();
		try {
			for(int i=0;i<ja.length();++i){
				JSONObject jo = ja.getJSONObject(i);
				PersonLight bean = fromJSONObject(jo);
				list.add(bean);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
    }

    public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getpName() {
		return pName;
	}
	public void setpName(String pName) {
		this.pName = pName;
	}
	public String getpRole() {
		return pRole;
	}
	public void setpRole(String pRole) {
		this.pRole = pRole;
	}
	public String getpDept() {
		return pDept;
	}
	public void setpDept(String pDept) {
		this.pDept = pDept;
	}
	public byte[] getpIcon() {
		return pIcon;
	}
	public void setpIcon(byte[] pIcon) {
		this.pIcon = pIcon;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}



}
