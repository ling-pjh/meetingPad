package com.example.meetingpad.entity;

import java.io.Serializable;

public class Person implements Serializable {
	private String pId = null;
	private String pPass = null;
	private String pName = null;
	private String pGender = null;
	private String pRole = null;
	private String pDept = null;
	private int pPrivilege = -1;
	private byte[] pFace = "face".getBytes();
	private byte[] pIcon = "icon".getBytes();
	private String pTel = null;

	// FIXME 集合映射Group

	public Person() {
		super();
	}

	public Person(String pId, String pPass, String pName) {
		super();
		this.pId = pId;
		this.pPass = pPass;
		this.pName = pName;
	}

	public Person(String pId, String pPass) {
		super();
		this.pId = pId;
		this.pPass = pPass;
		this.pName = "--";
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getpPass() {
		return pPass;
	}

	public void setpPass(String pPass) {
		this.pPass = pPass;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getpGender() {
		return pGender;
	}

	public void setpGender(String pGender) {
		this.pGender = pGender;
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

	public int getpPrivilege() {
		return pPrivilege;
	}

	public void setpPrivilege(int pPrivilege) {
		this.pPrivilege = pPrivilege;
	}

	public byte[] getpFace() {
		return pFace;
	}

	public void setpFace(byte[] face) {
		this.pFace = face;
	}

	public byte[] getpIcon() {
		return pIcon;
	}

	public void setpIcon(byte[] pIcon) {
		this.pIcon = pIcon;
	}

	public String getpTel() {
		return pTel;
	}

	public void setpTel(String pTel) {
		this.pTel = pTel;
	}



}
