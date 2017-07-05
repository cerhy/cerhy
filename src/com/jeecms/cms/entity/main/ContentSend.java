package com.jeecms.cms.entity.main;

import java.util.Date;

import com.jeecms.cms.entity.main.base.BaseContentSend;

public class ContentSend extends BaseContentSend {
	private static final long serialVersionUID = 1L;

	public ContentSend() {
		super();
	}

	public ContentSend(Integer id, Date sendTime, Integer contentId,
			Integer sendUserId, Integer recieveUserId,Integer columnId) {
		super(id, sendTime, contentId, sendUserId, recieveUserId,columnId);
	}

	
}