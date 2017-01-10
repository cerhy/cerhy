package com.jeecms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.dao.main.impl.ColumnsDao;
import com.jeecms.cms.entity.main.Columns;

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
	@Autowired
	private ColumnsDao columnsDao;
}
