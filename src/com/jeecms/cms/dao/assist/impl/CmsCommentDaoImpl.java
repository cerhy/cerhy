package com.jeecms.cms.dao.assist.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.assist.CmsCommentDao;
import com.jeecms.cms.dao.assist.CmsMysqlDataBackDao;
import com.jeecms.cms.entity.assist.CmsComment;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;

@Repository
public class CmsCommentDaoImpl extends HibernateBaseDao<CmsComment, Integer>
		implements CmsCommentDao {
	public Pagination getPage(Integer siteId, Integer contentId,
			Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinder(siteId, contentId,null,null,null,greaterThen, checked,
				recommend, desc, cacheable);
		return find(f, pageNo, pageSize);
	}
	public Pagination getPage(Integer siteId,Integer channelId, Integer contentId,
			Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, int pageNo, int pageSize, boolean cacheable) {
		Finder f = getFinder(siteId, channelId,contentId,null,null,null,greaterThen, checked,
				recommend, desc, cacheable);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<CmsComment> getList(Integer siteId, Integer contentId,
			Integer parentId,Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, int count, boolean cacheable) {
		Finder f = getFinder(siteId, contentId,parentId,null,null,greaterThen, checked,
				recommend, desc, cacheable);
		f.setMaxResults(count);
		return find(f);
	}
	/**
	 * 栏目评论列表
	 */
	@SuppressWarnings("unchecked")
	public List<CmsComment> getList(Integer siteId,Integer channelId, Integer contentId,
			Integer parentId,Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, int count, boolean cacheable) {
		Finder f = getFinder(siteId,channelId, contentId,parentId,null,null,greaterThen, checked,
				recommend, desc, cacheable);
		f.setMaxResults(count);
		return find(f);
	}
	
	public Pagination getPageForMember(Integer siteId, Integer contentId,Integer toUserId,Integer fromUserId,
			Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, int pageNo, int pageSize,boolean cacheable){
		Finder f = getFinder(siteId, contentId,null, toUserId,fromUserId,greaterThen, checked,
				recommend, desc, cacheable);
		return find(f, pageNo, pageSize);
	}
	@SuppressWarnings("unchecked")
	public List<CmsComment> getListForDel(Integer siteId, Integer userId,
			Integer commentUserId, String ip){
		Finder f = Finder.create("from CmsComment bean where 1=1");
		if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		if(commentUserId!=null){
			f.append(" and bean.commentUser.id=:commentUserId");
			f.setParam("commentUserId", commentUserId);
		}
		if(userId!=null){
			f.append(" and bean.content.user.id=:fromUserId");
			f.setParam("fromUserId", userId);
		}
		f.setCacheable(false);
		return find(f);
	}

	private Finder getFinder(Integer siteId, Integer contentId,
			Integer parentId,Integer toUserId,Integer fromUserId,
			Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, boolean cacheable) {
		Finder f = Finder.create("from CmsComment bean where 1=1");
		if(parentId!=null){
			f.append(" and bean.parent.id=:parentId");
			f.setParam("parentId", parentId);
		}else if (contentId != null) {
			//按照内容ID来查询对内容的直接评论
			f.append(" and (bean.content.id=:contentId and bean.parent is null )");
			f.setParam("contentId", contentId);
		} else if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		if(toUserId!=null){
			f.append(" and bean.commentUser.id=:commentUserId");
			f.setParam("commentUserId", toUserId);
		}else if(fromUserId!=null){
			f.append(" and (bean.content.user.id=:fromUserId");
			f.append(" or bean.channel.id is not null)");
			f.setParam("fromUserId", fromUserId);
		}
		if (greaterThen != null) {
			f.append(" and bean.ups>=:greatTo");
			f.setParam("greatTo", greaterThen);
		}
		if (checked != null) {
			f.append(" and bean.checked=:checked");
			f.setParam("checked", checked);
		}
		if(recommend!=null){
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		if (desc) {
			f.append(" order by bean.id desc");
		} else {
			f.append(" order by bean.id asc");
		}
		f.setCacheable(cacheable);
		return f;
	}

	/**
	 * 栏目评论
	 */
	private Finder getFinder(Integer siteId, Integer channelId,Integer contentId,
			Integer parentId,Integer toUserId,Integer fromUserId,
			Integer greaterThen, Boolean checked, Boolean recommend,
			boolean desc, boolean cacheable) {
		Finder f = Finder.create("from CmsComment bean where 1=1");
		if(parentId!=null){
			f.append(" and bean.parent.id=:parentId");
			f.setParam("parentId", parentId);
		}else if (contentId != null) {
			//按照内容ID来查询对内容的直接评论
			f.append(" and (bean.content.id=:contentId and bean.parent is null )");
			f.setParam("contentId", contentId);
		} else if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		else if (channelId != null) {
			//按照栏目ID来查询对栏目的直接评论
			if(channelId==98||channelId==99||channelId==100||channelId==101
					||channelId==102||channelId==103||channelId==104){
				Channel cc = channelMng.findById(channelId);
				List<Channel> topList = new ArrayList<Channel>();
				topList.add(cc);
				List<Channel> channelList = Channel.getListForSelect(topList, null, true);
				channelId=channelList.get(2).getId();
			}
			f.append(" and (bean.channel.id=:channelId and bean.parent is null )");
			f.setParam("channelId", channelId);
		}
		if(toUserId!=null){
			f.append(" and bean.commentUser.id=:commentUserId");
			f.setParam("commentUserId", toUserId);
		}else if(fromUserId!=null){
			f.append(" and bean.content.user.id=:fromUserId");
			f.setParam("fromUserId", fromUserId);
		}
		if (greaterThen != null) {
			f.append(" and bean.ups>=:greatTo");
			f.setParam("greatTo", greaterThen);
		}
		if (checked != null) {
			f.append(" and bean.checked=:checked");
			f.setParam("checked", checked);
		}
		if(recommend!=null){
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		if (desc) {
			f.append(" order by bean.id desc");
		} else {
			f.append(" order by bean.id asc");
		}
		f.setCacheable(cacheable);
		return f;
	}
	
	public CmsComment findById(Integer id) {
		CmsComment entity = get(id);
		return entity;
	}

	public CmsComment save(CmsComment bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsComment deleteById(Integer id) {
		CmsComment entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public int deleteByContentId(Integer contentId) {
		String hql = "delete from CmsComment bean where bean.content.id=:contentId";
		return getSession().createQuery(hql).setParameter("contentId",
				contentId).executeUpdate();
	}
	
	@Override
	protected Class<CmsComment> getEntityClass() {
		return CmsComment.class;
	}
	@Autowired
	private ChannelMng channelMng;
	
	private CmsMysqlDataBackDao dao;

	@Autowired
	public void setDao(CmsMysqlDataBackDao dao) {
		this.dao = dao;
	}
	
	@Override
	public void updateByUserId(Integer sid) {
		//String hql = " update CmsComment bean set bean.isOrNo=1 where (bean.content.id is not null and bean.content.user.id=:userIds) ";
		String sql=" update jc_comment jcc "
				  +" left join jc_content jct on jcc.content_id=jct.content_id "
				  +" set jcc.isOrNo=1 "
				  +" where jct.user_id="+sid;
		dao.executeSQL(sql);
	}
}