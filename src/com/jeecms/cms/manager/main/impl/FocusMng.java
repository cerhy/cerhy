package com.jeecms.cms.manager.main.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.dao.main.impl.FocusDao;
import com.jeecms.cms.entity.main.Focus;

public class FocusMng {

	public Focus add(Integer userId,String username,Integer focusUserId, String focusUserName, String focusTime) {
		Focus f = new Focus(userId,username,focusUserId,focusUserName,focusTime);
		focusDao.add(f);
		return f;
	}

	public Focus delete(Integer userId,Integer focusUserId) {
		 return focusDao.delete(userId,focusUserId);
	}
	
	public List<Focus> findMaxFocusCount(){
		return focusDao.findMaxFocusCount();
	}

	public List<Focus> find(Integer userId,Integer focusUserId) {
		return focusDao.find(userId,focusUserId);
	}
	
	public List<List<Focus>> findByUserId(Integer userId) {
		List<Focus> list = focusDao.findByUserId(userId);
		List<List<Focus>> listAll = new ArrayList<List<Focus>>();
		List<Focus> focus = new ArrayList<Focus>();
		List<Focus> fans = new ArrayList<Focus>();
		for(Focus f :list){
			if(f.getUserId() == userId){
				focus.add(f);
			}else{
				fans.add(f);
			}
		}
		listAll.add(focus);
		listAll.add(fans);
	    return listAll;
	}

	@Autowired
	private FocusDao focusDao;

	@Autowired
	public FocusDao getFocusDao() {
		return focusDao;
	}

	@Autowired
	public void setFocusDao(FocusDao focusDao) {
		this.focusDao = focusDao;
	}
}
