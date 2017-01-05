package com.jeecms.cms.manager.main.impl;

import com.jeecms.cms.dao.main.impl.FocusDao;
import com.jeecms.cms.entity.main.Focus;

public class FocusMng {

	public void add(Integer userId,Integer focusUserId, String focusUserName, String focusTime) {
		Focus f = new Focus(userId,focusUserId,focusUserName,focusTime);
		focusDao.add(f);
	}

	public void delete(Integer focusUserId) {

	}

	public void find() {

	}

	private FocusDao focusDao;

	public FocusDao getFocusDao() {
		return focusDao;
	}

	public void setFocusDao(FocusDao focusDao) {
		this.focusDao = focusDao;
	}
}
