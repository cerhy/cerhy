package com.jeecms.cms.entity.main.base;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseContentStick implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.util.Date contentTime;//文章发布时间
	private java.util.Date stickTime;//置顶时间
	private java.lang.Integer contentId;//文章id
	private java.lang.String contentTitle;//文章标题
	private java.lang.Integer stickUserId;//置顶人id
	private java.lang.String path;//访问路径

	/*// one to one
	private com.jeecms.cms.entity.main.Channel channel;*/
	
	public BaseContentStick(Integer id, Date contentTime, Date stickTime,
			Integer contentId, String contentTitle, Integer stickUserId,
			String path) {
		this.id = id;
		this.contentTime = contentTime;
		this.stickTime = stickTime;
		this.contentId = contentId;
		this.contentTitle = contentTitle;
		this.stickUserId = stickUserId;
		this.path = path;
		initialize();
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.util.Date getContentTime() {
		return contentTime;
	}

	public void setContentTime(java.util.Date contentTime) {
		this.contentTime = contentTime;
	}

	public java.util.Date getStickTime() {
		return stickTime;
	}

	public void setStickTime(java.util.Date stickTime) {
		this.stickTime = stickTime;
	}

	public java.lang.Integer getContentId() {
		return contentId;
	}

	public void setContentId(java.lang.Integer contentId) {
		this.contentId = contentId;
	}

	public java.lang.String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(java.lang.String contentTitle) {
		this.contentTitle = contentTitle;
	}

	public java.lang.Integer getStickUserId() {
		return stickUserId;
	}

	public void setStickUserId(java.lang.Integer stickUserId) {
		this.stickUserId = stickUserId;
	}

	public java.lang.String getPath() {
		return path;
	}

	public void setPath(java.lang.String path) {
		this.path = path;
	}

	public BaseContentStick() {
		initialize();
	}

	protected void initialize() {
	}

	public String toString() {
		return super.toString();
	}

	/*public com.jeecms.cms.entity.main.Channel getChannel() {
		return channel;
	}

	public void setChannel(com.jeecms.cms.entity.main.Channel channel) {
		this.channel = channel;
	}
*/
}