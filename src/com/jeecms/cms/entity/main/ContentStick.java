package com.jeecms.cms.entity.main;

import java.util.Date;

import com.jeecms.cms.entity.main.base.BaseContentStick;

public class ContentStick extends BaseContentStick {
	private static final long serialVersionUID = 1L;

	public ContentStick() {
		super();
	}

	public ContentStick(Integer id, Date contentTime, Date stickTime,
			Integer contentId, String contentTitle, Integer stickUserId,
			String path) {
		super(id, contentTime, stickTime, contentId, contentTitle,stickUserId,path);
	}

	
	
}