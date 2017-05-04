package com.jeecms.cms.dao.main.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.main.ChannelDao;
import com.jeecms.cms.entity.assist.CmsBlogVisitor;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;

@Repository
public class ChannelDaoImpl extends HibernateBaseDao<Channel, Integer>
		implements ChannelDao {
	@SuppressWarnings("unchecked")
	public List<Channel> getTopList(Integer siteId,boolean hasContentOnly,
			boolean displayOnly, boolean cacheable) {
		Finder f = getTopFinder(siteId, hasContentOnly, displayOnly, cacheable);
		return find(f);
	}

	public Pagination getTopPage(Integer siteId, boolean hasContentOnly,
			boolean displayOnly, boolean cacheable, int pageNo, int pageSize) {
		Finder f = getTopFinder(siteId, hasContentOnly, displayOnly, cacheable);
		return find(f, pageNo, pageSize);
	}

	private Finder getTopFinder(Integer siteId, boolean hasContentOnly,
			boolean displayOnly, boolean cacheable) {
		Finder f = Finder.create("from Channel bean");
		f.append(" where bean.site.id=:siteId and bean.parent.id is null");
		f.setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		if (displayOnly) {
			f.append(" and bean.display=true");
		}
		f.append(" order by bean.priority asc,bean.id asc");
		f.setCacheable(cacheable);
		return f;
	}

	@SuppressWarnings("unchecked")
	public List<Channel> getTopListByRigth(Integer userId, Integer siteId,
			boolean hasContentOnly) {
		Finder f = Finder.create("select bean from Channel bean");
		f.append(" join bean.users user");
		f.append(" where user.id=:userId and bean.site.id=:siteId");
		f.setParam("userId", userId).setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" and bean.parent.id is null");
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<Channel> getTopListForDepartId(Integer departId,Integer siteId,boolean hasContentOnly){
		Finder f = Finder.create("select distinct bean from Channel bean");
		f.append(" left join bean.departments depart");
		f.append(" where 1=1 ");
		if(departId!=null){
			f.append(" and depart.id =:departId");
			f.setParam("departId", departId);
		}
		f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" and bean.parent.id is null");
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<Channel> getControlTopListForDepartId(Integer departId,Integer siteId,boolean hasContentOnly){
		Finder f = Finder.create("select distinct bean from Channel bean");
		f.append(" left join bean.controlDeparts depart");
		f.append(" where 1=1 ");
		if(departId!=null){
			f.append(" and depart.id =:departId");
			f.setParam("departId", departId);
		}
		f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" and bean.parent.id is null");
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<Channel> getChildList(Integer parentId, boolean hasContentOnly,
			boolean displayOnly, boolean cacheable) {
		Finder f = getChildFinder(parentId, hasContentOnly, displayOnly,
				cacheable);
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<Channel> getBottomList(Integer siteId,boolean hasContentOnly){
		Finder f = Finder.create("select  bean from Channel bean");
		f.append(" where bean.site.id=:siteId").setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" and size(bean.child) <= 0");
	//	f.append(" and bean.id not in(select channel.parent.id from Channel channel where channel.parent.id is not null)");
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}

	public Pagination getChildPage(Integer parentId, boolean hasContentOnly,
			boolean displayOnly, boolean cacheable, int pageNo, int pageSize) {
		Finder f = getChildFinder(parentId, hasContentOnly, displayOnly,
				cacheable);
		return find(f, pageNo, pageSize);
	}

	private Finder getChildFinder(Integer parentId, boolean hasContentOnly,
			boolean displayOnly, boolean cacheable) {
		Finder f = Finder.create("from Channel bean");
		f.append(" where bean.parent.id=:parentId");
		f.setParam("parentId", parentId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		if (displayOnly) {
			f.append(" and bean.display=true");
		}
		f.append(" order by bean.priority asc,bean.id asc");
		return f;
	}

	@SuppressWarnings("unchecked")
	public List<Channel> getChildListByRight(Integer userId, Integer parentId,
			boolean hasContentOnly) {
		Finder f = Finder.create("select bean from Channel bean");
		f.append(" join bean.users user");
		f.append(" where user.id=:userId and bean.parent.id=:parentId");
		f.setParam("userId", userId).setParam("parentId", parentId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<Channel> getChildListByDepartId(Integer departId,Integer siteId,
			Integer parentId, boolean hasContentOnly){
		Finder f = Finder.create("select distinct bean from Channel bean");
		f.append(" left join bean.departments depart");
		f.append(" where  bean.parent.id=:parentId");
		f.setParam("parentId", parentId);
		if(departId!=null){
			f.append(" and depart.id =:departId ");
			f.setParam("departId", departId);
		}
		f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<Channel> getControlChildListByDepartId(Integer departId,Integer siteId,
			Integer parentId, boolean hasContentOnly){
		Finder f = Finder.create("select distinct bean from Channel bean");
		f.append(" left join bean.controlDeparts depart");
		f.append(" where  bean.parent.id=:parentId");
		f.setParam("parentId", parentId);
		if(departId!=null){
			f.append(" and depart.id =:departId ");
			f.setParam("departId", departId);
		}
		f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		if (hasContentOnly) {
			f.append(" and bean.hasContent=true");
		}
		f.append(" order by bean.priority asc,bean.id asc");
		return find(f);
	}

	public Channel findById(Integer id) {
		Channel entity = get(id);
		return entity;
	}

	public Channel findByPath(String path, Integer siteId, boolean cacheable) {
		String hql = "from Channel bean where bean.path=? and bean.site.id=?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, path).setParameter(1, siteId);
		// 做一些容错处理，因为毕竟没有在数据库中限定path是唯一的。
		query.setMaxResults(1);
		return (Channel) query.setCacheable(cacheable).uniqueResult();
	}

	public Channel save(Channel bean) {
		getSession().save(bean);
		return bean;
	}

	public Channel deleteById(Integer id) {
		Channel entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public void initWorkFlow(Integer workflowId){
		String hql = "update Channel bean set bean.workflow.id=null  where bean.workflow.id=:workflowId";
		Query query = getSession().createQuery(hql).setParameter("workflowId", workflowId);
		query.executeUpdate();
	}

	@Override
	protected Class<Channel> getEntityClass() {
		return Channel.class;
	}

	
	public void saveLink(String hyperlink,CmsUser user) {
		String hql = "update CmsUser bean set bean.hyperlink=:hyperlink  where bean.id=:userId";
		Query query = getSession().createQuery(hql).setParameter("hyperlink", hyperlink).setParameter("userId", user.getId());
		query.executeUpdate();
	}

	@Override
	public void saveFriends(String friends, CmsUser user) {
		String hql = "update CmsUser bean set bean.Friends=:friends  where bean.id=:userId";
		Query query = getSession().createQuery(hql).setParameter("friends", friends).setParameter("userId", user.getId());
		query.executeUpdate();
	}

	@Override
	public CmsUser findUserImage(String name) {
		String hql = "from CmsUser bean where bean.username=:name";
		Query query = getSession().createQuery(hql).setParameter("name", name);
		return (CmsUser) query.uniqueResult();
	}

	@Override
	public CmsUser findUserId(String userName) {
		String hql = "from CmsUser bean where bean.username=:userName";
		Query query = getSession().createQuery(hql).setParameter("userName",userName);
		return (CmsUser) query.uniqueResult();
	}

	@Override
	public void updateBlogVisitNum(CmsUser userT) {
		String visnum=userT.getBlogVisitNum();
		if(null==visnum){
			visnum=String.valueOf(0);
		}
		Integer totnum=Integer.valueOf(visnum)+1;
		String hql = "update CmsUser bean set bean.blogVisitNum=:blogVisitNum  where bean.id=:userId";
		Query query = getSession().createQuery(hql).setParameter("blogVisitNum", String.valueOf(totnum)).setParameter("userId", userT.getId());
		query.executeUpdate();
	}

	@Override
	public void updateBlogVisitorTime(CmsUser user,CmsUser userT) {
		String hql ="select count(*) from CmsBlogVisitor bean where 1=1 and bean.visitorId.id=:userId and bean.byVisitorId.id=:userTId";
		Query query = getSession().createQuery(hql);
		query.setParameter("userId", user.getId());
		query.setParameter("userTId", userT.getId());
		if(((Number) (query.iterate().next())).intValue()!=0){
			String hql2 = "update CmsBlogVisitor bean set bean.visitorTime=:visitorTime  where bean.visitorId.id=:userId and bean.byVisitorId.id=:userTId ";
			Query query2 = getSession().createQuery(hql2).setParameter("visitorTime", new Date()).setParameter("userId", user.getId()).setParameter("userTId", userT.getId());
			query2.executeUpdate();
		}else{
			CmsBlogVisitor cv=new CmsBlogVisitor();
			cv.setVisitorId(user);
			cv.setByVisitorId(userT);
			cv.setVisitorTime(new Date());
			getSession().save(cv);
		}
	}
	
}