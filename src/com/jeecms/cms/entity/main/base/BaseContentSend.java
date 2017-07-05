package com.jeecms.cms.entity.main.base;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseContentSend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.util.Date sendTime;
	private java.lang.Integer contentId;
	private java.lang.Integer sendUserId;
	private java.lang.Integer recieveUserId;
	private java.lang.Integer columnId;

	public java.lang.Integer getId() {
		return id;
	}
	

	
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.util.Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(java.util.Date sendTime) {
		this.sendTime = sendTime;
	}

	public java.lang.Integer getContentId() {
		return contentId;
	}

	public void setContentId(java.lang.Integer contentId) {
		this.contentId = contentId;
	}

	public java.lang.Integer getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(java.lang.Integer sendUserId) {
		this.sendUserId = sendUserId;
	}

	public java.lang.Integer getRecieveUserId() {
		return recieveUserId;
	}

	public void setRecieveUserId(java.lang.Integer recieveUserId) {
		this.recieveUserId = recieveUserId;
	}

	public BaseContentSend() {
		initialize();
	}

	
	public java.lang.Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(java.lang.Integer columnId) {
		this.columnId = columnId;
	}

	public BaseContentSend(Integer id, Date sendTime, Integer contentId,
			Integer sendUserId, Integer recieveUserId, Integer columnId) {
		this.id = id;
		this.sendTime = sendTime;
		this.contentId = contentId;
		this.sendUserId = sendUserId;
		this.recieveUserId = recieveUserId;
		this.columnId = columnId;
		initialize();
	}

	protected void initialize() {
	}

	public String toString() {
		return super.toString();
	}
}