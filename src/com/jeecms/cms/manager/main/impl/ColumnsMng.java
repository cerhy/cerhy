package com.jeecms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jeecms.cms.dao.main.impl.ColumnsDao;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.assist.CmsPersonalChannel;
import com.jeecms.cms.entity.assist.CmsPostilInfo;
import com.jeecms.cms.entity.assist.CmsReportDoc;
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
	public int saveAddTpHtml(CmsPostilInfo cpi) {
		return columnsDao.saveAddTpHtml(cpi);
	}
	public List<CmsPostilInfo> findList(Integer contentId) {
		return columnsDao.findList(contentId);
	}
	
	public int updateDragCoordinate(String leftX, String topY, String postilId,String tbHtml) {
		return columnsDao.updateDragCoordinate(leftX,topY,postilId,tbHtml);
	}
	public int delAddHtml(String postilId) {
		return columnsDao.delAddHtml(postilId);
	}
	public int checkCode(String no) {
		return columnsDao.checkCode(no);
	}
	public int signOutGroup(String groupId) {
		return columnsDao.signOutGroup(groupId);
	}
	public void updateUFO(CmsUser user) {
		columnsDao.updateUFO(user);
	}
	public List<CmsPersonalChannel> getPersonChannel(CmsUser user) {
		return columnsDao.getPersonChannel(user);
	}
	public int delGroup(String groupId, CmsUser user) {
		return columnsDao.delGroup(groupId,user);
	}
	
	public int saveReportDoc(CmsReportDoc crd) {
		return columnsDao.saveReportDoc(crd);
	}
	
	@Autowired
	private ColumnsDao columnsDao;

	public Columns findById(Integer parentId) {
		return columnsDao.findById(parentId);
	}

	public List<Columns> getOneColumnsByUserId(Integer id) {
		return columnsDao.getOneColumnsByUserId(id);
	}

	public List<Columns> findTwoByParentId(Integer columnId) {
		return columnsDao.findTwoByParentId(columnId);
	}

	public List<Columns>  findInfoByCodeAndUserid(String code, Integer id) {
		return columnsDao.findInfoByCodeAndUserid(code,id);
	}

	public List<CmsJoinGroup> getJoinGroupByUserIdAndCode(Integer userId,
			String code) {
		return columnsDao.getJoinGroupByUserIdAndCode(userId,code);
	}

	public  List<Columns> findInfoByCodeAndUserids(String code, Integer id,
			Integer columnId) {
		return columnsDao.findInfoByCodeAndUserids(code,id,columnId);
	}

	public int signOutGroups(Columns cc, CmsUser user) {
		return columnsDao.signOutGroups(cc,user);
	}

}
