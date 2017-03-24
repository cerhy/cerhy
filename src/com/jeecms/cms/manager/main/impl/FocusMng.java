package com.jeecms.cms.manager.main.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.dao.main.impl.FocusDao;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;

public class FocusMng {

	public Focus add(Integer userId,String username,Integer focusUserId, String focusUserName, String focusTime) {
		Focus f = new Focus(userId,username,focusUserId,focusUserName,focusTime);
		focusDao.add(f);
		return f;
	}

	public Focus delete(Integer userId,Integer focusUserId) {
		 return focusDao.delete(userId,focusUserId);
	}
	
	@SuppressWarnings("unchecked")
	public Pagination pagingQueryFocus(Integer userId, int pageNo, int pageSize){
		Pagination p = focusDao.pagingQueryFocus(userId, pageNo, pageSize);
		List<Focus> list = (List<Focus>) p.getList();
		if(null != list){
			List<CmsUser> u = new ArrayList<CmsUser>();
				for(Focus f :list){
					if(cmsUserMng.findById(f.getFocusUserId())!=null){
						u.add(cmsUserMng.findById(f.getFocusUserId()));
					}
				}
			p.setList(u);
		}
		return p;
	}
	
	@SuppressWarnings("unchecked")
	public Pagination pagingQueryFans(Integer userId, int pageNo, int pageSize){
		Pagination p = focusDao.pagingQueryFans(userId, pageNo, pageSize);
		List<Focus> list = (List<Focus>) p.getList();
		if(null != list){
			List<CmsUser> u = new ArrayList<CmsUser>();
			for(Focus f :list){
				if(cmsUserMng.findById(f.getUserId())!=null){
					u.add(cmsUserMng.findById(f.getUserId()));
				}
			}
			p.setList(u);
		}
		return p;
	}
	
	
/*	public List<Focus> findMaxFocusCount(){
		return focusDao.findMaxFocusCount();
	}*/

	public List<Focus> find(Integer userId,Integer focusUserId) {
		return focusDao.find(userId,focusUserId);
	}
	
	public List<Focus> findFocusByUserId(Integer userId){
		return focusDao.findFocusByUserId(userId);
	}
	
	public List<Focus> findFansByUserId(Integer userId) {
		return focusDao.findFansByUserId(userId);
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
	protected CmsUserMng cmsUserMng;
}
