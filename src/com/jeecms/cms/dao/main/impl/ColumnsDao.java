package com.jeecms.cms.dao.main.impl;

import java.util.List;

import org.hibernate.Query;

import com.jeecms.cms.entity.main.Columns;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;

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

}
