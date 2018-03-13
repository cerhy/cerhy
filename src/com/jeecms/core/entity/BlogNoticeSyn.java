package com.jeecms.core.entity;

import java.io.Serializable;

/**
 * 博客公告及简介实体类
 * @author lihao 2018-3-8
 *
 */
public class BlogNoticeSyn implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int userId;
	private String notice;
	private String synopsis;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
