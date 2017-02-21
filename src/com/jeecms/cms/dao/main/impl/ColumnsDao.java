package com.jeecms.cms.dao.main.impl;

import java.util.List;

import org.hibernate.Query;

import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.assist.CmsPostilInfo;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.ContentCheck;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.core.entity.CmsUser;

public class ColumnsDao extends HibernateBaseDao<Columns, Integer>{

	@SuppressWarnings("unchecked")
	public List<Columns> getColumnsByUserId(Integer userId){
		Finder f = Finder.create("select bean from Columns bean");
			f.append(" where bean.userId=:userId ORDER BY bean.orderId DESC");
			f.setParam("userId", userId);
		return find(f);
	}
	
	public Columns addColumns (Columns c){
		getSession().save(c);
		return c;
	}
	
	public Integer deleteColumns(Integer columnId) {
		StringBuilder hql = new StringBuilder("delete Columns bean");
		hql.append(" where bean.columnId=:columnId ");
		Query query = getSession().createQuery(hql.toString());
		query.setParameter("columnId", columnId);
		query.executeUpdate();
		return columnId;
	}
	
	public Columns updateColumns (Columns c){
		getSession().update(c);
		return c;
	}
	@Override
	protected Class<Columns> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public int saveJoinGroup(CmsJoinGroup cjg) {
		try {
			getSession().save(cjg);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int checkJoinState(String code, CmsUser user) {
		String hql = "select count(*) from CmsJoinGroup bean"
				+ " where 1=1"
				+ " and bean.joinCode='"+code+"'"
				+ " and bean.joinUserId.id="+user.getId();
		Query query = getSession().createQuery(hql);
		if(null != query.iterate() && null != query.iterate().next()){
			return ((Number) (query.iterate().next())).intValue();
		}else{
			return 0 ;
		}
	}

	public Columns findInfoByCode(String code) {
		String hql = "select bean from Columns bean where bean.uniqueCode='"+code+"'";
		Query query = getSession().createQuery(hql);
		if(null != query.iterate() && null != query.iterate().next()){
			return ((Columns) (query.iterate().next()));
		}else{
			return null ;
		}
	}

	public int saveAddTpHtml(CmsPostilInfo cpi) {
		try {
			getSession().save(cpi);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CmsPostilInfo> findList(Integer contentId) {
		Finder f = Finder.create("select bean from CmsPostilInfo bean");
		f.append(" where bean.contentId=:contentId");
		f.setParam("contentId", contentId);
		return find(f);
	}

	public int updateDragCoordinate(String leftX, String topY, String postilId,String tbHtml) {
		try {
			StringBuilder hql = new StringBuilder("update CmsPostilInfo bean");
			hql.append(" set bean.addHtml=:tbHtml");
			hql.append(" where bean.divId=:divId ");
			Query query = getSession().createQuery(hql.toString());
			query.setParameter("divId", postilId);
			query.setParameter("tbHtml", tbHtml);
			query.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int delAddHtml(String postilId) {
		try {
			StringBuilder hql = new StringBuilder("delete CmsPostilInfo bean");
			hql.append(" where bean.divId=:divId ");
			Query query = getSession().createQuery(hql.toString());
			query.setParameter("divId", postilId);
			query.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
