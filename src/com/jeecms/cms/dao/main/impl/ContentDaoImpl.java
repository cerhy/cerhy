package com.jeecms.cms.dao.main.impl;

import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_END;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_EQ;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_GT;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_GTE;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_IN;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LIKE;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LT;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_LTE;
import static com.jeecms.cms.action.directive.abs.AbstractContentDirective.PARAM_ATTR_START;
import static com.jeecms.cms.entity.main.Content.ContentStatus.all;
import static com.jeecms.cms.entity.main.Content.ContentStatus.checked;
import static com.jeecms.cms.entity.main.Content.ContentStatus.contribute;
import static com.jeecms.cms.entity.main.Content.ContentStatus.draft;
import static com.jeecms.cms.entity.main.Content.ContentStatus.passed;
import static com.jeecms.cms.entity.main.Content.ContentStatus.pigeonhole;
import static com.jeecms.cms.entity.main.Content.ContentStatus.prepared;
import static com.jeecms.cms.entity.main.Content.ContentStatus.recycle;
import static com.jeecms.cms.entity.main.Content.ContentStatus.rejected;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.main.ContentDao;
import com.jeecms.cms.entity.assist.CmsBlogVisitor;
import com.jeecms.cms.entity.assist.CmsComment;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.Content.ContentStatus;
import com.jeecms.cms.entity.main.ContentCheck;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.entity.main.ContentSend;
import com.jeecms.cms.service.ContentQueryFreshTimeCache;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;

@Repository
public class ContentDaoImpl extends HibernateBaseDao<Content, Integer>
		implements ContentDao {
	public Pagination getPage(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,Integer modelId,
			Integer channelId,int orderBy, int pageNo, int pageSize,String removeBlog) {
		Finder f = Finder.create("select  bean from Content bean left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
		if (rejected == status) {
			f.append("  join bean.contentCheckSet check ");
		}
		if (prepared == status|| passed == status) {
			f.append("  join bean.eventSet event  ");
		}
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where ((channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			//f.append(" and parent.id=:parentId) or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.lft between parent.lft and parent.rgt and tarChannel.site.id=parent.site.id and parent.id=:parentId))");
			f.append(" and parent.id=:parentId))");
			f.setParam("parentId", channelId);
		} else if (siteId != null) {
			f.append(" where (bean.site.id=:siteId  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and tarChannel.site.id=:siteId))");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		//跳级审核人不应该看到？
		if (passed == status) {
			//操作人不在待审人列表中且非终审 或非发起人
			f.append("  and ((:operateId not in(select eventUser.user.id from CmsWorkflowEventUser eventUser where eventUser.event.id=event.id) and event.initiator.id!=:operateId) or event.initiator.id=:operateId) and event.nextStep!=-1").setParam("operateId", currUserId);
		}
		if (prepared == status) {
			//操作人在待审人列表中
			f.append("  and :operateId in(select eventUser.user.id from CmsWorkflowEventUser eventUser where eventUser.event.id=event.id)").setParam("operateId", currUserId);
		}
		if (rejected == status) {
			f.append(" and check.rejected=true");
		}
		if(modelId!=null){
			f.append(" and bean.model.id=:modelId").setParam("modelId", modelId);
		}
		if(removeBlog!=null){
			f.append(" and bean.channel.id!=280");
		}
		appendQuery(f, title, typeId, inputUserId, status, topLevel, recommend);
		appendOrder(f, orderBy);
		return find(f, pageNo, pageSize);
	}
	
	public Pagination getPage_blog(String title, Integer typeId,Integer userId,
		   boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,Integer modelId,
			Integer channelId,int orderBy, int pageNo, int pageSize,Integer columnId,Integer channelId2) {
		channelId = channelId2;
		Finder f = Finder.create("select  bean from Content bean left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
		if (rejected == status) {
			f.append("  join bean.contentCheckSet check ");
		}
		if (prepared == status|| passed == status) {
			f.append("  join bean.eventSet event  ");
		}
		if (channelId != null) {
			if(channelId.toString().equals("316")||channelId.toString().equals("323")){
				   channelId=305;
			   }
			   //高中、初中、小学音乐
			   if(channelId.toString().equals("315")||channelId.toString().equals("322")){
				   channelId=304;
			   }
			   //高中、初中、小学体育
			   if(channelId.toString().equals("314")||channelId.toString().equals("321")){
				   channelId=303;
			   }
			   //高中、初中、小学综合实践
			   if(channelId.toString().equals("417")||channelId.toString().equals("418")){
				   channelId=416;
			   }
			   //高中、初中地理
			   if(channelId.toString().equals("311")){
				   channelId=300;
			   }
			   //高中、初中历史
			   if(channelId.toString().equals("310")){
				   channelId=299;
			   }
			   //初中、小学信息技术
			   if(channelId.toString().equals("319")){
				   channelId=312;
			   }
			   //初中、小学政治
			   if(channelId.toString().equals("317")){
				   channelId=309;
			   }
			   //高中、初中化学
			   if(channelId.toString().equals("307")){
				   channelId=296;
			   }
			   //高中、初中物理
			   if(channelId.toString().equals("306")){
				   channelId=295;
			   }
			   //高中、初中生物
			   if(channelId.toString().equals("308")){
				   channelId=297;
				}
			   //高中、初中语文
				if(channelId.toString().equals("142")){
					channelId=140;
				}
				//高中、初中、小学心理健康
				if(channelId.toString().equals("421")||channelId.toString().equals("422")){
					channelId=420;
				}
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where ((channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId  )   or ( shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.lft between parent.lft and parent.rgt and tarChannel.site.id=parent.site.id and parent.id=:parentId))");
			f.setParam("parentId", channelId);
		} else if (siteId != null) {
			f.append(" where (bean.site.id=:siteId  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and tarChannel.site.id=:siteId))");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		
		if (rejected == status) {
			f.append(" and check.rejected=true");
		}
		if(modelId!=null){
			f.append(" and bean.model.id=:modelId").setParam("modelId", modelId);
			//f.append(" and bean.model.id in (11,21,24)");
		}/*else{
			f.append(" and bean.model.id in (11,21,24)");
		}*/
		appendQuery_blog(f, title, typeId, userId,status, topLevel, recommend,columnId);
		appendOrder(f, orderBy);
		return find(f, pageNo, pageSize);
	}


	//只能管理自己的数据不能审核他人信息，工作流相关表无需查询
	public Pagination getPageBySelf(String title, Integer typeId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId, Integer userId, int orderBy, int pageNo,
			int pageSize) {
		Finder f = Finder.create("select  bean from Content bean");
		if (prepared == status || passed == status || rejected == status) {
			f.append(" join bean.contentCheckSet check");
		}
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", channelId);
		}else if (siteId != null) {
			f.append(" where bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		f.append(" and bean.user.id=:userId");
		f.setParam("userId", userId);
		if (prepared == status) {
			f.append(" and check.checkStep<:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (passed == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=false");
			f.setParam("checkStep", checkStep);
		} else if (rejected == status) {
			f.append(" and check.checkStep=:checkStep");
			f.append(" and check.rejected=true");
			f.setParam("checkStep", checkStep);
		}
		appendQuery(f, title, typeId, inputUserId, status, topLevel, recommend);
		if (prepared == status) {
			f.append(" order by check.checkStep desc,bean.id desc");
		} else {
			appendOrder(f, orderBy);
		}
		return find(f, pageNo, pageSize);
	}

	public Pagination getPageByRight(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId, Integer departId, Integer userId, boolean selfData, int orderBy,
			int pageNo, int pageSize) {
		Finder f = Finder.create("select  bean from Content bean  left join bean.contentShareCheckSet shareCheck left join  shareCheck.channel tarChannel ");
		if (rejected == status) {
			f.append("  join bean.contentCheckSet check ");
		}
		if (prepared == status|| passed == status) {
			f.append("  join bean.eventSet event  ");
		}
		if (channelId != null) {
			f.append(" join bean.channel channel ");
			if(departId!=null){
				f.append("left join channel.departments depart");
			}
			f.append(",Channel parent");
			f.append(" where ((channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			if(departId!=null){
				f.append(" and depart.id =:departId").setParam("departId", departId);
			}
			f.append(" or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.lft between parent.lft and parent.rgt and tarChannel.site.id=parent.site.id and parent.id=:parentId)))" );
			f.setParam("parentId", channelId);
			
		} else if (siteId != null) {
			if(departId!=null){
				f.append(" join bean.channel channel left join channel.departments depart");
			}
			f.append(" where  (1=1 ");
			if(departId!=null){
				f.append(" and depart.id  =:departId").setParam("departId", departId);
			}
			f.append(" and bean.site.id=:siteId or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and tarChannel.site.id=:siteId and bean.site.id<>:siteId))");
			f.setParam("siteId", siteId);
		} else {
			if(departId!=null){
				f.append(" join bean.channel channel left join channel.departments depart");
			}
			f.append(" where  (1=1 ");
			if(departId!=null){
				f.append(" and depart.id  =:departId").setParam("departId", departId);
			}
		}
		if (selfData) {
			// userId前面已赋值
			f.append(" and bean.user.id=:userId");
			f.setParam("userId", userId);
		}
		//跳级审核人不应该看到？
		if (passed == status) {
			//操作人不在待审人列表中且非终审 或非发起人
			f.append("  and ((:operateId not in(select eventUser.user.id from CmsWorkflowEventUser eventUser where eventUser.event.id=event.id) and event.initiator.id!=:operateId) or event.initiator.id=:operateId) and event.nextStep!=-1").setParam("operateId", currUserId);
		}
		if (prepared == status) {
			//操作人在待审人列表中
			f.append("  and :operateId in(select eventUser.user.id from CmsWorkflowEventUser eventUser where eventUser.event.id=event.id)").setParam("operateId", currUserId);
		}
		if (rejected == status) {
			f.append(" and check.rejected=true");
		}
		appendQuery(f, title, typeId, inputUserId, status, topLevel, recommend);
		appendOrder(f, orderBy);
		return find(f, pageNo, pageSize);
	}
	

	public Pagination getPageForCollection(Integer siteId, Integer memberId, int pageNo, int pageSize){
		Finder f = Finder.create("select bean from Content bean join bean.collectUsers user where user.id=:userId").setParam("userId", memberId);
		if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		f.append(" and bean.status<>:status");
		f.setParam("status", ContentCheck.RECYCLE);
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public  List<Content> getExpiredTopLevelContents(byte topLevel,Date expiredDay){
		String hql = "from  Content bean where bean.status=:status and bean.topLevel>:topLevel and bean.contentExt.topLevelDate<:topLevelDate";
		Finder f=Finder.create(hql).setParam("status", ContentCheck.CHECKED).setParam("topLevel", topLevel).setParam("topLevelDate", expiredDay);
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public  List<Content> getPigeonholeContents(Date pigeonholeDay){
		String hql = "from  Content bean where bean.status=:status and bean.contentExt.pigeonholeDate<:pigeonholeDate";
		Finder f=Finder.create(hql).setParam("status", ContentCheck.CHECKED).setParam("pigeonholeDate", pigeonholeDay);
		return find(f);
	}

	private void appendQuery_blog(Finder f, String title, Integer typeId,
			Integer inputUserId, ContentStatus status, boolean topLevel,
			boolean recommend ,Integer columnId) {
		
		if (inputUserId != null&&inputUserId!=0) {
			f.append(" and bean.user.id=:inputUserId");
			f.setParam("inputUserId", inputUserId);
		}else{
		
		}
		/*if(null != userGroupId){
			f.append(" and bean.user.group.id=:group");
			f.setParam("group", userGroupId);
		}*/
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		if (typeId != null) {
			f.append(" and bean.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		
		if (topLevel) {
			f.append(" and bean.topLevel>0");
		}
		if (recommend) {
			f.append(" and bean.recommend=true");
		}
		if(null != columnId ){
			f.append(" and bean.columnId=:columnId");
			f.setParam("columnId", columnId);
			
			String sql = "select contentId from ContentSend where recieveUserId="+inputUserId+" and columnId="+columnId+"";
			List contentIdList =find(sql);
			if(contentIdList!=null && contentIdList.size()>0){
				/*Integer[] contentIds = new Integer[]{60001672,60001673};*/
				f.append(" or bean.id in (:contentIds)");
				f.setParamList("contentIds", contentIdList);
			}
		}
		if (draft == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.DRAFT);
		}if (contribute == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CONTRIBUTE);
		} else if (checked == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CHECKED);
		} else if (prepared == status ) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CHECKING);
		} else if (rejected == status ) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.REJECT);
		}else if (passed == status) {
			f.append(" and (bean.status=:checking or bean.status=:checked)");
			f.setParam("checking", ContentCheck.CHECKING);
			f.setParam("checked", ContentCheck.CHECKED);
		} else if (all == status) {
			f.append(" and bean.status<>:status");
			f.setParam("status", ContentCheck.RECYCLE);
		} else if (recycle == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.RECYCLE);
		} else if (pigeonhole == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.PIGEONHOLE);
		} else {
			// never
		}
	}
	
	private void appendQuery(Finder f, String title, Integer typeId,
			Integer inputUserId, ContentStatus status, boolean topLevel,
			boolean recommend) {
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		if (typeId != null) {
			f.append(" and bean.type.id=:typeId");
			f.setParam("typeId", typeId);
		}
		if (inputUserId != null&&inputUserId!=0) {
			f.append(" and bean.user.id=:inputUserId");
			f.setParam("inputUserId", inputUserId);
		}else{
			//输入了没有的用户名的情况
			if(inputUserId==null){
				f.append(" and 1!=1");
			}
		}
		if (topLevel) {
			f.append(" and bean.topLevel>0");
		}
		if (recommend) {
			f.append(" and bean.recommend=true");
		}
		if (draft == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.DRAFT);
		}if (contribute == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CONTRIBUTE);
		} else if (checked == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CHECKED);
		} else if (prepared == status ) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.CHECKING);
		} else if (rejected == status ) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.REJECT);
		}else if (passed == status) {
			f.append(" and (bean.status=:checking or bean.status=:checked)");
			f.setParam("checking", ContentCheck.CHECKING);
			f.setParam("checked", ContentCheck.CHECKED);
		} else if (all == status) {
			f.append(" and bean.status<>:status");
			f.setParam("status", ContentCheck.RECYCLE);
		} else if (recycle == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.RECYCLE);
		} else if (pigeonhole == status) {
			f.append(" and bean.status=:status");
			f.setParam("status", ContentCheck.PIGEONHOLE);
		} else {
			// never
		}
	}

	public Content getContentCollection(Integer id, Integer siteId, boolean next, boolean cacheable,Integer userId){
		Finder f = Finder.create("select bean from Content bean join bean.collectUsers user where user.id=:userId").setParam("userId", userId);
		if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		if (next) {
			f.append(" and bean.id<:id");
			f.setParam("id", id);
			f.append(" order by bean.id desc");
		} else {
			f.append(" and bean.id>:id");
			f.setParam("id", id);
			f.append(" order by bean.id asc");
		}
		Query query = f.createQuery(getSession());
		query.setCacheable(cacheable).setMaxResults(1);
		return (Content) query.uniqueResult();
	}
	
	public Content getSide(Integer id, Integer siteId, Integer channelId,
			boolean next, boolean cacheable,Integer userId,Integer columnId,Integer topicId,Integer typeIds) {
		Finder f;
		if(null != topicId){
			channelId =null;
			f = Finder.create("select bean from Content bean join bean.topics topic where topic.id=:topicId").setParam("topicId", topicId);
		}else{
			f = Finder.create("from Content bean where 1=1");
		}
		
		if(null!=typeIds){
			f.append(" and bean.type.id=:typeIds");
			f.setParam("typeIds", typeIds);
		}
		
		
		if(null != userId){
			if(userId != -1){
				f.append(" and bean.user.id=:userId");
				f.setParam("userId", userId);
			}
		}
		if(null != columnId){
			f.append(" and bean.columnId=:columnId");
			f.setParam("columnId", columnId);
		}
		if (channelId != null) {
			f.append(" and bean.channel.id=:channelId");
			f.setParam("channelId", channelId);
		} else if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		if (next) {
			f.append(" and bean.id>:id");
			f.setParam("id", id);
			f.append(" and bean.status=" + ContentCheck.CHECKED);
			f.append(" order by bean.id asc");
		} else {
			f.append(" and bean.id<:id");
			f.setParam("id", id);
			f.append(" and bean.status=" + ContentCheck.CHECKED);
			f.append(" order by bean.id desc");
		}
		Query query = f.createQuery(getSession());
		query.setCacheable(cacheable).setMaxResults(1);
		return (Content) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByIdsForTag(Integer[] ids, int orderBy) {
		Finder f = Finder.create("from Content bean where bean.id in (:ids)");
		f.setParamList("ids", ids);
		appendOrder(f, orderBy);
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, int open,Map<String,String[]>attr,int orderBy, int pageNo, int pageSize) {
		Finder f = bySiteIds(siteIds, typeIds, titleImg, recommend, title,
				open,attr,orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open,Map<String,String[]>attr, int orderBy, Integer first, Integer count) {
		Finder f = bySiteIds(siteIds, typeIds, titleImg, recommend, title,open,attr,
				orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open, Map<String,String[]>attr, int orderBy, int option,int pageNo, int pageSize) {
		Finder f = byChannelIds(channelIds, typeIds, titleImg, recommend,
				title, open,attr,orderBy, option);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open,Map<String,String[]>attr, int orderBy, int option, Integer first, Integer count) {
		Finder f = byChannelIds(channelIds, typeIds, titleImg, recommend,
				title, open,attr,orderBy, option);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy, int pageNo,
			int pageSize) {
		Finder f = byChannelPaths(paths, siteIds, typeIds, titleImg, recommend,
				title,open,attr,orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,int open,Map<String,String[]>attr, int orderBy, Integer first,
			Integer count) {
		Finder f = byChannelPaths(paths, siteIds, typeIds, titleImg, recommend,
				title, open,attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy,
			int pageNo, int pageSize) {
		Finder f = byTopicId(topicId, siteIds, channelIds, typeIds, titleImg,
				recommend, title, open,attr,orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title,int open,Map<String,String[]>attr, int orderBy,
			Integer first, Integer count) {
		Finder f = byTopicId(topicId, siteIds, channelIds, typeIds, titleImg,
				recommend, title,open, attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	public Pagination getPageByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,int open, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize) {
		Finder f = byTagIds(tagIds, siteIds, channelIds, typeIds, excludeId,
				titleImg, recommend, title,open,attr, orderBy);
		f.setCacheable(true);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<Content> getListByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,int open,Map<String,String[]>attr, int orderBy, Integer first, Integer count) {
		Finder f = byTagIds(tagIds, siteIds, channelIds, typeIds, excludeId,
				titleImg, recommend, title, open,attr,orderBy);
		if (first != null) {
			f.setFirstResult(first);
		}
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(true);
		return find(f);
	}

	private Finder bySiteIds(Integer[] siteIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create("select  bean from Content bean");
		f.append(" left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
		appendJoinConentDoc(f, open);
		f.append(" join bean.contentExt as ext where 1=1");
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendOpen(f, open);
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendSiteIds(f, siteIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byChannelIds(Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy,
			int option) {
		Finder f = Finder.create();
		int len = channelIds.length;
		// 如果多个栏目
		if (option == 0 || len > 1) {
			f.append("select  bean from Content bean ");
			f.append(" left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
			appendJoinConentDoc(f, open);
			f.append(" join bean.contentExt as ext");
			if (len == 1) {
				f.append(" where ((bean.channel.id=:channelId ) or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.id=:channelId))");
				f.setParam("channelId", channelIds[0]);
			} else {
				f.append(" where ((bean.channel.id in (:channelIds) ) or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.id in (:channelIds)))");
				f.setParamList("channelIds", channelIds);
			}
		} else if (option == 1) {
			if(channelIds[0]!=null){
				//高中、初中、小学美术
				if(channelIds[0].toString().equals("316")||channelIds[0].toString().equals("323")){
					channelIds[0]=305;
				}
				//高中、初中、小学音乐
				if(channelIds[0].toString().equals("315")||channelIds[0].toString().equals("322")){
					channelIds[0]=304;
				}
				//高中、初中、小学体育
				if(channelIds[0].toString().equals("314")||channelIds[0].toString().equals("321")){
					channelIds[0]=303;
				}
				//高中、初中、小学综合实践
				if(channelIds[0].toString().equals("417")||channelIds[0].toString().equals("418")){
					channelIds[0]=416;
				}
				//高中、初中地理
				if(channelIds[0].toString().equals("311")){
					channelIds[0]=300;
				}
				//高中、初中历史
				if(channelIds[0].toString().equals("310")){
					channelIds[0]=299;
				}
				//初中、小学信息技术
				if(channelIds[0].toString().equals("319")){
					channelIds[0]=312;
				}
				//初中、小学政治
				if(channelIds[0].toString().equals("317")){
					channelIds[0]=309;
				}
				//高中、初中化学
				if(channelIds[0].toString().equals("307")){
					channelIds[0]=296;
				}
				//高中、初中物理
				if(channelIds[0].toString().equals("306")){
					channelIds[0]=295;
				}
				//高中、初中生物
				if(channelIds[0].toString().equals("308")){
					channelIds[0]=297;
				}
				//高中、初中语文
				if(channelIds[0].toString().equals("142")){
					channelIds[0]=140;
				}
				//高中、初中、小学心理健康
				if(channelIds[0].toString().equals("421")||channelIds[0].toString().equals("422")){
					channelIds[0]=420;
				}
				
			}
			// 包含子栏目
//			f.append("select  bean from Content bean");
//			f.append(" left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
//			appendJoinConentDoc(f, open);
//			f.append(" join bean.contentExt as ext");
//			f.append(" join bean.channel node,Channel parent");
//			f.append(" where ((node.lft between parent.lft and parent.rgt");
//			f.append(" and bean.site.id=parent.site.id");
//			f.append(" and parent.id=:channelId ) or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.lft between parent.lft and parent.rgt and tarChannel.site.id=parent.site.id and parent.id=:channelId))");
//			f.setParam("channelId", channelIds[0]);
			f.append("select  bean from Content bean");
			f.append(" join bean.channel node,Channel parent");
			f.append(" where node.lft between parent.lft and parent.rgt");
			f.append(" and bean.site.id=parent.site.id");
			f.append(" and parent.id=:channelId");
			f.setParam("channelId", channelIds[0]);
		} else if (option == 2) {
			// 包含副栏目
			f.append("select  bean from Content bean");
			f.append(" left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
			appendJoinConentDoc(f, open);
			f.append(" join bean.contentExt as ext");
			f.append(" join bean.channels as channel");
			f.append(" where ((channel.id=:channelId ) or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.id=:channelId))");
			f.setParam("channelId", channelIds[0]);
		} else {
			throw new RuntimeException("option value must be 0 or 1 or 2.");
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
//		if (recommend != null) {
//			f.append(" and bean.recommend=:recommend");
//			f.setParam("recommend", recommend);
//		}
//		appendOpen(f, open);
//		appendReleaseDate(f);
		if(null!=channelIds&&null!=typeIds){
			if(channelIds[0].toString().equals("280")&&typeIds[0].toString().equals("5")){	
				appendTypeIds(f, typeIds);
			}
		}
//		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byChannelPaths(String[] paths, Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title, int open,Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		f.append("select  bean from Content bean join bean.channel channel ");
		f.append(" left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
		appendJoinConentDoc(f, open);
		f.append(" join bean.contentExt as ext");
		int len = paths.length;
		if (len == 1) {
			f.append(" where (channel.path=:path  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.path=:path))").setParam("path", paths[0]);
		} else {
			f.append(" where (channel.path in (:paths) or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.path in (:paths)))");
			f.setParamList("paths", paths);
		}
		if (siteIds != null) {
			len = siteIds.length;
			if (len == 1) {
				f.append(" and (channel.site.id=:siteId   or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.site.id=:siteId))");
				f.setParam("siteId", siteIds[0]);
			} else if (len > 1) {
				f.append(" and (channel.site.id in (:siteIds)  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.site.id  in (:siteIds) ))");
				f.setParamList("siteIds", siteIds);
			}
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendOpen(f, open);
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byTopicId(Integer topicId, Integer[] siteIds,
			Integer[] channelIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		f.append("select bean from Content bean join bean.topics topic");
		appendJoinConentDoc(f, open);
		f.append(" join bean.contentExt as ext");
		f.append(" where topic.id=:topicId").setParam("topicId", topicId);
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendOpen(f, open);
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendChannelIds(f, channelIds);
		appendSiteIds(f, siteIds);
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private Finder byTagIds(Integer[] tagIds, Integer[] siteIds,
			Integer[] channelIds, Integer[] typeIds, Integer excludeId,
			Boolean titleImg, Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy) {
		Finder f = Finder.create();
		int len = tagIds.length;
		if (len == 1) {
			f.append("select bean from Content bean join bean.tags tag");
			appendJoinConentDoc(f, open);
			f.append(" join bean.contentExt as ext");
			f.append(" where tag.id=:tagId").setParam("tagId", tagIds[0]);
		} else {
			f.append("select bean from Content bean");
			f.append(" join bean.contentExt as ext");
			f.append(" join bean.tags tag");
			f.append(" where tag.id in(:tagIds)");
			f.setParamList("tagIds", tagIds);
		}
		if (titleImg != null) {
			f.append(" and bean.hasTitleImg=:titleImg");
			f.setParam("titleImg", titleImg);
		}
		if (recommend != null) {
			f.append(" and bean.recommend=:recommend");
			f.setParam("recommend", recommend);
		}
		appendOpen(f, open);
		appendReleaseDate(f);
		appendTypeIds(f, typeIds);
		appendChannelIds(f, channelIds);
		appendSiteIds(f, siteIds);
		if (excludeId != null) {
			f.append(" and bean.id<>:excludeId");
			f.setParam("excludeId", excludeId);
		}
		f.append(" and bean.status=" + ContentCheck.CHECKED);
		if (!StringUtils.isBlank(title)) {
			f.append(" and bean.contentExt.title like :title");
			f.setParam("title", "%" + title + "%");
		}
		appendAttr(f, attr);
		appendOrder(f, orderBy);
		return f;
	}

	private void appendReleaseDate(Finder f) {
		f.append(" and ext.releaseDate<:currentDate");
		//f.setParam("currentDate", new Date());
		f.setParam("currentDate", contentQueryFreshTimeCache.getTime());
	}

	private void appendTypeIds(Finder f, Integer[] typeIds) {
		int len;
		if (typeIds != null) {
			len = typeIds.length;
			if (len == 1) {
				f.append(" and bean.type.id=:typeId");
				f.setParam("typeId", typeIds[0]);
			} else if (len > 1) {
				f.append(" and bean.type.id in (:typeIds)");
				f.setParamList("typeIds", typeIds);
			}
		}
	}

	private void appendChannelIds(Finder f, Integer[] channelIds) {
		int len;
		if (channelIds != null) {
			len = channelIds.length;
			if (len == 1) {
				f.append(" and bean.channel.id=:channelId");
				f.setParam("channelId", channelIds[0]);
			} else if (len > 1) {
				f.append(" and bean.channel.id in (:channelIds)");
				f.setParamList("channelIds", channelIds);
			}
		}
	}

	private void appendSiteIds(Finder f, Integer[] siteIds) {
		int len;
		if (siteIds != null) {
			len = siteIds.length;
			if (len == 1) {
				f.append(" and (bean.site.id=:siteId  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and tarChannel.site.id=:siteId))");
				f.setParam("siteId", siteIds[0]);
			} else if (len > 1) {
				f.append(" and (bean.site.id in (:siteIds)  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and tarChannel.site.id in (:siteIds)))");
				f.setParamList("siteIds", siteIds);
			}
		}
	}
	
	
	private void appendJoinConentDoc(Finder f,int open){
		//默认不设置open参数 不连接查询ContentDoc
		if(open!=ContentDoc.ALL){
			f.append(" join bean.contentDocSet as doc ");
		}
	}
	
	private void appendOpen(Finder f,int open){
		if(open!=ContentDoc.ALL){
			if(open==ContentDoc.OPEN){
				f.append(" and doc.isOpen=true");
			}else{
				f.append(" and doc.isOpen=false");
			}
		}
	}

	private void appendAttr(Finder f, Map<String,String[]>attr){
		if(attr!=null&&!attr.isEmpty()){
			Set<String>keys=attr.keySet();
			Iterator<String>keyIterator=keys.iterator();
			while(keyIterator.hasNext()){
				String key=keyIterator.next();
				String[] mapValue=attr.get(key);
				String value=mapValue[0],operate=mapValue[1];
				if(StringUtils.isNotBlank(key)&&StringUtils.isNotBlank(value)){
					if(operate.equals(PARAM_ATTR_EQ)){
						f.append(" and bean.attr[:k"+key+"]=:v"+key).setParam("k"+key, key).setParam("v"+key, value);
					}else if(operate.equals(PARAM_ATTR_START)){
						f.append(" and bean.attr[:k"+key+"] like :v"+key).setParam("k"+key, key).setParam("v"+key, value+"%");
					}else if(operate.equals(PARAM_ATTR_END)){
						f.append(" and bean.attr[:k"+key+"] like :v"+key).setParam("k"+key, key).setParam("v"+key, "%"+value);
					}else if(operate.equals(PARAM_ATTR_LIKE)){
						f.append(" and bean.attr[:k"+key+"] like :v"+key).setParam("k"+key, key).setParam("v"+key, "%"+value+"%");
					}else if(operate.equals(PARAM_ATTR_IN)){
						if(StringUtils.isNotBlank(value)){
							f.append(" and bean.attr[:k"+key+"] in (:v"+key+")").setParam("k"+key, key);
							f.setParamList("v"+key, value.split(","));
						}
					}
					else {
						//取绝对值比较大小
						Float floatValue=Float.valueOf(value);
						if(operate.equals(PARAM_ATTR_GT)){
							if(floatValue>=0){
								f.append(" and (bean.attr[:k"+key+"]>=0 and abs(bean.attr[:k"+key+"])>:v"+key+")").setParam("k"+key, key).setParam("v"+key, floatValue);
							}else{
								f.append(" and ((bean.attr[:k"+key+"]<0 and abs(bean.attr[:k"+key+"])<:v"+key+") or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
							}
					 	}else if(operate.equals(PARAM_ATTR_GTE)){
					 		if(floatValue>=0){
								f.append(" and (abs(bean.attr[:k"+key+"])>=:v"+key+" and bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, floatValue);
							}else{
								f.append(" and ((abs(bean.attr[:k"+key+"])<=:v"+key+" and bean.attr[:k"+key+"]<0) or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
							}
						}else if(operate.equals(PARAM_ATTR_LT)){
							if(floatValue>=0){
								f.append(" and ((abs(bean.attr[:k"+key+"])<:v"+key+" and bean.attr[:k"+key+"]>=0) or bean.attr[:k"+key+"]<=0)").setParam("k"+key, key).setParam("v"+key, floatValue);
							}else{
								f.append(" and ((abs(bean.attr[:k"+key+"])>:v"+key+" and bean.attr[:k"+key+"]<0) or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
							}
						}else if(operate.equals(PARAM_ATTR_LTE)){
							if(floatValue>=0){
								f.append(" and ((abs(bean.attr[:k"+key+"])<=:v"+key+" and bean.attr[:k"+key+"]>=0) or bean.attr[:k"+key+"]<=0)").setParam("k"+key, key).setParam("v"+key, floatValue);
							}else{
								f.append(" and ((abs(bean.attr[:k"+key+"])>=:v"+key+" and bean.attr[:k"+key+"]<0) or bean.attr[:k"+key+"]>=0)").setParam("k"+key, key).setParam("v"+key, -floatValue);
							}
						}
					}
				}
			}
		}
	}
	
	
	private void appendOrder(Finder f, int orderBy) {
		switch (orderBy) {
		case 1:
			// ID升序
			f.append(" order by bean.id asc");
			break;
		case 2:
			// 发布时间降序
			f.append(" order by bean.sortDate desc");
			break;
		case 3:
			// 发布时间升序
			f.append(" order by bean.sortDate asc");
			break;
		case 4:
			// 固顶级别降序、发布时间降序
//			f.append(" order by bean.topLevel desc, bean.sortDate desc");
			f.append(" order by bean.sortDate desc");
			break;
		case 5:
			// 固顶级别降序、发布时间升序
			f.append(" order by bean.topLevel desc, bean.sortDate asc");
			break;
		case 6:
			// 日访问降序
			f.append(" order by bean.contentCount.viewsDay desc, bean.id desc");
			break;
		case 7:
			// 周访问降序
			f.append(" order by bean.contentCount.viewsWeek desc");
			f.append(", bean.id desc");
			break;
		case 8:
			// 月访问降序
			f.append(" order by bean.contentCount.viewsMonth desc");
			f.append(", bean.id desc");
			break;
		case 9:
			// 总访问降序
			f.append(" order by bean.contentCount.views desc");
			f.append(", bean.id desc");
			break;
		case 10:
			// 日评论降序
			f.append(" order by bean.commentsDay desc, bean.id desc");
			break;
		case 11:
			// 周评论降序
			f.append(" order by bean.contentCount.commentsWeek desc");
			f.append(", bean.id desc");
			break;
		case 12:
			// 月评论降序
			f.append(" order by bean.contentCount.commentsMonth desc");
			f.append(", bean.id desc");
			break;
		case 13:
			// 总评论降序
			f.append(" order by bean.contentCount.comments desc");
			f.append(", bean.id desc");
			break;
		case 14:
			// 日下载降序
			f.append(" order by bean.downloadsDay desc, bean.id desc");
			break;
		case 15:
			// 周下载降序
			f.append(" order by bean.contentCount.downloadsWeek desc");
			f.append(", bean.id desc");
			break;
		case 16:
			// 月下载降序
			f.append(" order by bean.contentCount.downloadsMonth desc");
			f.append(", bean.id desc");
			break;
		case 17:
			// 总下载降序
			f.append(" order by bean.contentCount.downloads desc");
			f.append(", bean.id desc");
			break;
		case 18:
			// 日顶降序
			f.append(" order by bean.upsDay desc, bean.id desc");
			break;
		case 19:
			// 周顶降序
			f.append(" order by bean.contentCount.upsWeek desc");
			f.append(", bean.id desc");
			break;
		case 20:
			// 月顶降序
			f.append(" order by bean.contentCount.upsMonth desc");
			f.append(", bean.id desc");
			break;
		case 21:
			// 总顶降序
			f.append(" order by bean.contentCount.ups desc, bean.id desc");
			break;
		case 22:
			// 推荐级别降序、发布时间降序
			f.append(" order by bean.recommendLevel desc, bean.sortDate desc");
			break;
		case 23:
			// 推荐级别升序、发布时间降序
			f.append(" order by bean.recommendLevel asc, bean.sortDate desc");
			break;
		default:
			// 默认： ID降序
			f.append(" order by bean.id desc");
		}
	}

	public int countByChannelId(int channelId) {
		String hql = "select count(1) from Content bean"
				+ " join bean.channel channel,Channel parent"
				+ " where channel.lft between parent.lft and parent.rgt"
				+ " and channel.site.id=parent.site.id"
				+ " and parent.id=:parentId";
		Query query = getSession().createQuery(hql);
		query.setParameter("parentId", channelId);
		return ((Number) (query.iterate().next())).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Content> countByColumnId(Integer columnId) {
		Finder f = Finder.create("select bean from Content bean");
		if (columnId != null) {
			f.append(" where bean.columnId=:columnId  ");
			f.setParam("columnId", columnId);
		} 
		return find(f);
	}

	public Content findById(Integer id) {
		Content entity = get(id);
		return entity;
	}

	public Content save(Content bean) {
		getSession().save(bean);
		return bean;
	}

	public Content deleteById(Integer id) {
		Content entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	/**不知为何要重写，此处先注释
	protected Pagination find(Finder finder, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	protected int countQueryResult(Finder finder) {
		Query query = getSession().createQuery(finder.getRowCountHql());
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return ((Number) query.iterate().next()).intValue();
	} 
	*/

	@Override
	protected Class<Content> getEntityClass() {
		return Content.class;
	}
	@Autowired
	private ContentQueryFreshTimeCache contentQueryFreshTimeCache;
	
	
	@Override
	public Pagination getPage_friendsBlog(int ids,String title, Integer typeId,
			Integer currUserId, Integer inputUserId, boolean topLevel,
			boolean recommend, ContentStatus status, Byte checkStep,
			Integer siteId, Integer modelId, Integer channelId, int orderBy,
			int pageNo, int pageSize, String columnId) {
		Finder f = Finder.create("select  bean from Content bean left join bean.contentShareCheckSet shareCheck left join shareCheck.channel tarChannel ");
		if (rejected == status) {
			f.append("  join bean.contentCheckSet check ");
		}
		if (prepared == status|| passed == status) {
			f.append("  join bean.eventSet event  ");
		}
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where ((channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId)   or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and  tarChannel.lft between parent.lft and parent.rgt and tarChannel.site.id=parent.site.id and parent.id=:parentId))");
			f.setParam("parentId", channelId);
		} else if (siteId != null) {
			f.append(" where (bean.site.id=:siteId  or (shareCheck.checkStatus<>0 and shareCheck.shareValid=true and tarChannel.site.id=:siteId))");
			f.setParam("siteId", siteId);
		} else {
			f.append(" where 1=1");
		}
		//跳级审核人不应该看到？
		if (passed == status) {
			//操作人不在待审人列表中且非终审 或非发起人
			f.append("  and ((:operateId not in(select eventUser.user.id from CmsWorkflowEventUser eventUser where eventUser.event.id=event.id) and event.initiator.id!=:operateId) or event.initiator.id=:operateId) and event.nextStep!=-1").setParam("operateId", currUserId);
		}
		/*if (prepared == status) {
			//操作人在待审人列表中
			f.append("  and :operateId in(select eventUser.user.id from CmsWorkflowEventUser eventUser where eventUser.event.id=event.id)").setParam("operateId", currUserId);
		}*/
		if (rejected == status) {
			f.append(" and check.rejected=true");
		}
		if(modelId!=null){
			f.append(" and bean.model.id=:modelId").setParam("modelId", modelId);
			/*f.append(" and bean.model.id in (11,21,24)");*/
		}/*else{
			f.append(" and bean.model.id in (11,21,24)");
		}*/
		inputUserId=ids;
		appendQuery_blog(f, title, typeId, inputUserId, status, topLevel, recommend,null);
		appendOrder(f, orderBy);
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Content> getListInfo(int userId) {
		Finder f = Finder.create("from Content bean");
		f.append(" where 1=1");
		f.append(" and bean.user.id=:userId");
		f.setParam("userId", userId);
		return find(f);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CmsComment> getListComment(int userId,Date lastDate) {
		Finder f = Finder.create("select bean from CmsComment bean");
		f.append(" where 1=1");
		f.append(" and (bean.content.user.id=:userId and bean.content.id is not null)");
		f.append(" and (bean.commentUser.id!=:userId)");
		f.append(" and bean.checked!=0");
		f.append(" and bean.isOrNo is null");
		f.setParam("userId", userId);
		return find(f);
	}

	@Override
	public int getTotalArticleNum(CmsUser user) {
		String hql = "select count(1) from Content bean"
				+ " where 1=1"
				+ " and bean.user.id="+user.getId()
		        + " and bean.status<>"+ContentCheck.RECYCLE;
		Query query = getSession().createQuery(hql);
		if(null != query.iterate() && null != query.iterate().next()){
			return ((Number) (query.iterate().next())).intValue();
		}else{
			return 0 ;
		}
		//+ " and bean.model.id in (11,21,24)"
	}

	@Override
	public int getTotalCommentNum(CmsUser user) {
		String hql = "select count(1) from CmsComment bean"
				+ " where 1=1"
				+ " and bean.content.id is not null "
				+ " and bean.content.user.id="+user.getId()
		        + " and bean.content.status<>"+ContentCheck.RECYCLE;
		Query query = getSession().createQuery(hql);
		if(null != query.iterate() && null != query.iterate().next()){
			return ((Number) (query.iterate().next())).intValue();
		}else{
			return 0 ;
		}
		//+ " and bean.content.model.id in (11,21,24)"
	}

	@Override
	public int getTotalCoverCommentNum(CmsUser user) {
		String hql = "select count(1) from CmsComment bean"
				+ " where 1=1"
				+ " and bean.content.id is not null "
				+ " and bean.commentUser.id="+user.getId()
				+ " and bean.content.status<>"+ContentCheck.RECYCLE;
		Query query = getSession().createQuery(hql);
		if(null != query.iterate() && null != query.iterate().next()){
			return ((Number) (query.iterate().next())).intValue();
		}else{
			return 0 ;
		}
		//+ " and bean.content.model.id in (11,21,24)"
	}

	@Override
	public int getTotalReadNum(CmsUser user) {
		String hql = "select sum(bean.contentCount.views) from Content bean"
				+ " where 1=1"
				+ " and bean.user.id="+user.getId()
				+ " and bean.status<>"+ContentCheck.RECYCLE;
		Query query = getSession().createQuery(hql);
		if(null != query.iterate() && null != query.iterate().next()){
			return ((Number) (query.iterate().next())).intValue();
		}else{
			return 0 ;
		}
		//+ " and bean.model.id in (11,21,24)"
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CmsBlogVisitor> getgetAllVistor(CmsUser user) {
		Finder f = Finder.create("select bean from CmsBlogVisitor bean");
		f.append(" where 1=1");
		f.append(" and bean.byVisitorId.id=:userId");
		f.setParam("userId", user.getId());
		return find(f);
	}

	@Override
	public Pagination getPage_visitor(int orderBy, int pageNo, int pageSize, CmsUser user) {
		Finder f = Finder.create("select bean from CmsBlogVisitor bean");
		f.append(" where 1=1");
		f.append(" and bean.byVisitorId.id=:userId");
		f.append(" order by bean.visitorTime desc");
		f.setParam("userId", user.getId());
		return findLimit(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CmsJoinGroup> getAlreadyJoinGroup(CmsUser user) {
		Finder f = Finder.create("select bean from CmsJoinGroup bean");
		f.append(" where 1=1");
		f.append(" and bean.joinUserId.id=:userId");
		f.setParam("userId", user.getId());
		return find(f);
	}
	
	@Override
	public ContentSend saveContentSend(ContentSend bean) {
		getSession().save(bean);
		return bean;
	}
	
	/**
	 * 根据用户帐号查询用户id
	 * @param userName 用户帐号
	 * @return Integer
	 */
	@Override
	public Integer getUserId(String userName){
		String hql = "select  id from CmsUser bean where bean.username='"+userName+"'";
		return (Integer) findUnique(hql);
	}
	
	/**
	 * 根据用id查询栏目的验证码
	 * @param userId 用户帐号
	 * @param validaCode 验证码
	 * @return String
	 */
	@Override
	public Integer getUniqueCode(int userId,int validaCode){
		String hql = "select  columnId from Columns bean where bean.userId="+userId+" and bean.uniqueCode="+validaCode+"";
		return (Integer) findUnique(hql);
	}
	
	/**
	 * 根据内容id查询栏目id
	 * @param contentId contentId
	 * @return Integer
	 */
	@Override
	public Integer getColumnId(int contentId){
		String hql = "select  columnId from Content bean where bean.id="+contentId+"";
		return (Integer) findUnique(hql);
	}
	
	/**
	 * 撤销用户发送的文章
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer deleteContentSend(int contentId,int userId){
		
		
		String hql = "delete   from ContentSend  where contentId="+contentId+" and sendUserId="+userId+" and type=1";
		return (Integer) deleteObject(hql);
	}
	
	/**
	 * 移除用户收录的文章
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer removeContentSend(int contentId,int userId){
		String hql = "delete   from ContentSend  where contentId="+contentId+" and recieve_user_id="+userId+" and type=2";
		return (Integer) deleteObject(hql);
	}
	
	/**
	 * 查询文章类型是发送的还是收录的
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer getContentSendType(Integer contentId, Integer userId){
		String hql = "select type   from ContentSend  where contentId="+contentId+" and recieve_user_id="+userId+"";
		return (Integer) findUnique(hql);
	}

}
