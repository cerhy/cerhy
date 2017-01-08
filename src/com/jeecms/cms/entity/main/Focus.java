package com.jeecms.cms.entity.main;

public class Focus {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private Integer focusId;
	private Integer userId;
	private String username;
	private Integer focusUserId;
	private String focusUserName;
	private String focusTime;
	private Integer cnt;

	public Focus() {

	}

	public Focus(Integer focusUserId,String focusUserName, Integer cnt) {
		this.focusUserId = focusUserId;
		this.focusUserName = focusUserName;
		this.cnt = cnt;
	}
	
	public Focus(Integer userId,String username, Integer focusUserId, String focusUserName, String focusTime) {
		this.userId = userId;
		this.username = username;
		this.focusUserId = focusUserId;
		this.focusUserName = focusUserName;
		this.focusTime = focusTime;
	}

	public Focus(Integer userId , Integer focusUserId ) {
		this.userId = userId;
		this.focusUserId = focusUserId;
	}

	public Focus(Integer focusId, Integer userId, String username, Integer focusUserId, String focusUserName, String focusTime) {
		this.focusId = focusId;
		this.userId = userId;
		this.username = username;
		this.focusUserId = focusUserId;
		this.focusUserName = focusUserName;
		this.focusTime = focusTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Integer getCnt() {
		return cnt;
	}

	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}
}
