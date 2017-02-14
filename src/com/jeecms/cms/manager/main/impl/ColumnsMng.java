package com.jeecms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.dao.main.impl.ColumnsDao;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.core.entity.CmsUser;

public class ColumnsMng {

	public List<Columns> getColumnsByUserId(int userId){
		return columnsDao.getColumnsByUserId(userId);
	}
	
	public Columns addColumns(Columns c){
		return columnsDao.addColumns(c);
	}
	
	public Integer deleteColumns(int id){
		return columnsDao.deleteColumns(id);
	}
	
	public Columns updateColumns(Columns c){
		return columnsDao.updateColumns(c);
	}
	public int saveJoinGroup(CmsJoinGroup cjg) {
		return columnsDao.saveJoinGroup(cjg);
	}
	public int checkJoinState(String code, CmsUser user) {
		return columnsDao.checkJoinState(code,user);
	}
	public Columns findInfoByCode(String code) {
		return columnsDao.findInfoByCode(code);
	}
	@Autowired
	private ColumnsDao columnsDao;
}
