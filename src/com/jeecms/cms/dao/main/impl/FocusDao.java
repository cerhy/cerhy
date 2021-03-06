package com.jeecms.cms.dao.main.impl;

import java.util.List;
import org.hibernate.Query;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;

public class FocusDao extends HibernateBaseDao<Focus, Integer> {

	public Focus add(Focus f) {
		getSession().save(f);
		return f;
	}
	
	/*  @SuppressWarnings("unchecked")
		public List<Focus> findMaxFocusCount(){
		  return find(Finder.create("SELECT * from (select focusUserId,count(1) 
	cnt from Focus GROUP BY focusUserId) p where  p.cnt in (SELECT max(b.cnt) 
			from (select focusUserId,count(1) cnt from Focus GROUP BY focusUserId) b)"));
		}
		*/
	
    @SuppressWarnings("unchecked")
	public List<Focus> findFocusByUserId(Integer userId){
    	Finder f = Finder.create("select bean from Focus bean");
		if (userId != null) {
			f.append(" where bean.userId=:userId");
			f.setParam("userId", userId);
		} 
		return find(f);
	}
    
    @SuppressWarnings("unchecked")
 	public List<Focus> findFansByUserId(Integer userId){
     	Finder f = Finder.create("select bean from Focus bean");
 		if (userId != null) {
 			f.append(" where bean.focusUserId=:focusUserId ");
 			f.setParam("focusUserId", userId);
 		} 
 		return find(f);
 	}
	
    @SuppressWarnings("unchecked")
	public List<Focus> findByUserId(Integer userId){
    	Finder f = Finder.create("select bean from Focus bean");
		if (userId != null) {
			f.append(" where bean.userId=:userId or bean.focusUserId=:focusUserId ");
			f.setParam("userId", userId);
			f.setParam("focusUserId", userId);
		} 
		return find(f);
	}
    
	@SuppressWarnings("unchecked")
	public List<Focus> find(Integer userId,Integer focusUserId){
		Finder f = Finder.create("select bean from Focus bean");
		if (userId != null) {
			f.append(" where bean.userId=:userId ");
			f.setParam("userId", userId);
		} else {
			f.append(" where 1=1");
		}	
		if(null != focusUserId){
			f.append(" and bean.focusUserId=:focusUserId ");
			f.setParam("focusUserId", focusUserId);
		}
		return find(f);
	}

	public Focus delete(Integer userId, Integer focusUserId) {
		Focus entity = null;
		StringBuilder hql = new StringBuilder("delete Focus bean");
		if (userId != null) {
			hql.append(" where bean.userId=:userId ");
			if (null != focusUserId) {
				hql.append(" and bean.focusUserId=:focusUserId ");
			} else {
				return entity;
			}
		}
		Query query = getSession().createQuery(hql.toString());
		query.setParameter("userId", userId);
		query.setParameter("focusUserId", focusUserId);
		query.executeUpdate();
		entity = new Focus(userId,focusUserId);
		return entity;
	}

	public Pagination pagingQueryFocus(Integer userId, int pageNo, int pageSize){
		Finder f = Finder.create("select  bean from Focus bean");
		if (userId != null) {
			f.append(" where bean.userId=:userId");
			f.setParam("userId", userId);
		} 
		return find(f, pageNo, pageSize);
	}
	
	public Pagination pagingQueryFans(Integer userId, int pageNo, int pageSize){
		Finder f = Finder.create("select  bean from Focus bean");
		if (userId != null) {
			f.append(" where bean.focusUserId=:focusUserId ");
 			f.setParam("focusUserId", userId);
		} 
		return find(f, pageNo, pageSize);
	}
	
	@Override
	protected Class<Focus> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
