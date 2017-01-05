package com.jeecms.cms.entity.main;

public class Focus {
	private Integer focusId;
	private Integer userId;
	private Integer focusUserId;
	private String focusUserName;
	private String focusTime;
	
	public Focus(){
		
	}
	public Focus(
			Integer userId,
			Integer focusUserId,
			String focusUserName,
			String focusTime){
		this.userId = userId;
		this.focusUserId = focusUserId;
		this.focusUserName = focusUserName;
		this.focusTime = focusTime;
	}
	
	public Focus(
			Integer focusId,
			Integer userId,
			Integer focusUserId,
			String focusUserName,
			String focusTime){
		this.focusId = focusId;
		this.userId = userId;
		this.focusUserId = focusUserId;
		this.focusUserName = focusUserName;
		this.focusTime = focusTime;
	}
	
	public Integer getFocusId() {
		return focusId;
	}

	public void setFocusId(Integer focusId) {
		this.focusId = focusId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getFocusUserId() {
		return focusUserId;
	}

	public void setFocusUserId(Integer focusUserId) {
		this.focusUserId = focusUserId;
	}

	public String getFocusUserName() {
		return focusUserName;
	}

	public void setFocusUserName(String focusUserName) {
		this.focusUserName = focusUserName;
	}

	public String getFocusTime() {
		return focusTime;
	}

	public void setFocusTime(String focusTime) {
		this.focusTime = focusTime;
	}
}
