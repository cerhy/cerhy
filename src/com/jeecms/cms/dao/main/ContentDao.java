package com.jeecms.cms.dao.main;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jeecms.cms.entity.assist.CmsBlogVisitor;
import com.jeecms.cms.entity.assist.CmsComment;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentStick;
import com.jeecms.cms.entity.main.Content.ContentStatus;
import com.jeecms.cms.entity.main.ContentSend;
import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsUser;

/**
 * 内容DAO接口。
 * 
 * 用于的排序参数
 * <ul>
 * <li>0：ID降序
 * <li>1：ID升序
 * <li>2：发布时间降序
 * <li>3：发布时间升序
 * <li>4：固顶级别降序,发布时间降序
 * <li>5：固顶级别降序,发布时间升序
 * <li>6：日访问降序
 * <li>7：周访问降序
 * <li>8：月访问降序
 * <li>9：总访问降序
 * <li>10：日评论降序
 * <li>11：周评论降序
 * <li>12：月评论降序
 * <li>13：总评论降序
 * <li>14：日下载降序
 * <li>15：周下载降序
 * <li>16：月下载降序
 * <li>17：总下载降序
 * <li>18：日顶降序
 * <li>19：周顶降序
 * <li>20：月顶降序
 * <li>21：总顶降序
 * </ul>
 */
public interface ContentDao {

	/**
	 * 获得内容列表
	 * 
	 * @param title
	 *            标题。支持模糊搜索，可以为null。
	 * @param typeId
	 *            内容类型ID。可以为null。
	 * @param inputUserId
	 *            内容录入员。可以为null。
	 * @param topLevel
	 *            是否固顶。
	 * @param recommend
	 *            是否推荐。
	 * @param status
	 *            状态。
	 * @param checkStep
	 *            审核步骤。当状态为prepared、passed、rejected时，不能为null。
	 * @param siteId
	 *            站点ID。可以为null。
	 * @param channelId
	 *            栏目ID。可以为null。
	 * @param orderBy
	 *            排序方式
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pagination getPage(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,Integer modelId,
			Integer channelId,int orderBy, int pageNo, int pageSize,String removeBlog);

	public Pagination getPage_blog(String title, Integer typeId,Integer inputUserId,
			 boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,Integer modelId,
			Integer channelId,int orderBy, int pageNo, int pageSize,Integer columnId,Integer channelId2,Integer recieveUserId);
	
	/**
	 * 获得自己发布的内容列表
	 * 
	 * @param title
	 *            标题。支持模糊搜索，可以为null。
	 * @param typeId
	 *            内容类型ID。可以为null。
	 * @param inputUserId
	 *            内容录入员。可以为null。
	 * @param topLevel
	 *            是否固顶。
	 * @param recommend
	 *            是否推荐。
	 * @param status
	 *            状态。
	 * @param checkStep
	 *            审核步骤。当状态为prepared、passed、rejected时，不能为null。
	 * @param siteId
	 *            站点ID。可以为null。
	 * @param channelId
	 *            站点ID。可以为null。
	 * @param userId
	 *            用户ID
	 * @param orderBy
	 *            排序方式
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pagination getPageBySelf(String title, Integer typeId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId,Integer userId, int orderBy, int pageNo,
			int pageSize);

	/**
	 * 获得有权限的内容列表
	 * 
	 * @param title
	 *            标题。支持模糊搜索，可以为null。
	 * @param typeId
	 *            内容类型ID。可以为null。
	 * @param inputUserId
	 *            内容录入员。可以为null。
	 * @param topLevel
	 *            是否固顶。
	 * @param recommend
	 *            是否推荐。
	 * @param status
	 *            状态。
	 * @param checkStep
	 *            审核步骤。当状态为prepared、passed、rejected时，不能为null。
	 * @param siteId
	 *            站点ID。可以为null。
	 * @param channelId
	 *            栏目ID。可以为null。
	 * @param userId
	 *            用户ID。
	 * @param selfData
	 *            是否只获取自己发表的数据。
	 * @param orderBy
	 *            排序方式
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pagination getPageByRight(String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,
			Integer channelId,Integer departId,Integer userId, boolean selfData, int orderBy,
			int pageNo, int pageSize);
	
	public List<Content> getExpiredTopLevelContents(byte topLevel,Date expiredDay);
	
	public  List<Content> getPigeonholeContents(Date pigeonholeDay);

	/**
	 * 获得一篇内容的上一篇或下一篇内容
	 * 
	 * @param id
	 *            文章ID。
	 * @param siteId
	 *            站点ID。可以为null。
	 * @param channelId
	 *            栏目ID。可以为null。
	 * @param next
	 *            根据文章ID，大者为下一篇，小者为上一篇。true：下一篇；fasle：上一篇。
	 * @param cacheable
	 *            是否使用缓存。
	 * @return
	 */
	
	public Content getSide(Integer id, Integer siteId, Integer channelId,
			boolean next, boolean cacheable,Integer userId,Integer columnId,Integer topicId,Integer typeIds);
	
	
	public Content getContentCollection(Integer id, Integer siteId,
			boolean next, boolean cacheable,Integer userId);
	

	/**
	 * 根据内容ID数组获取内容列表
	 * 
	 * @param ids
	 * @param orderBy
	 * @return
	 */
	public List<Content> getListByIdsForTag(Integer[] ids, int orderBy);

	public Pagination getPageBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open, Map<String,String[]>attr,int orderBy, int pageNo, int pageSize);

	public List<Content> getListBySiteIdsForTag(Integer[] siteIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open,Map<String,String[]>attr, int orderBy, Integer first, Integer count);

	public Pagination getPageByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open,Map<String,String[]>attr, int orderBy, int option, int pageNo, int pageSize);

	public List<Content> getListByChannelIdsForTag(Integer[] channelIds,
			Integer[] typeIds, Boolean titleImg, Boolean recommend,
			String title,int open, Map<String,String[]>attr, int orderBy, int option,Integer first, Integer count);

	public Pagination getPageByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,int open,Map<String,String[]>attr, int orderBy, int pageNo,
			int pageSize);

	public List<Content> getListByChannelPathsForTag(String[] paths,
			Integer[] siteIds, Integer[] typeIds, Boolean titleImg,
			Boolean recommend, String title,int open, Map<String,String[]>attr,int orderBy, Integer first,
			Integer count);

	public Pagination getPageByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy,
			int pageNo, int pageSize);

	public List<Content> getListByTopicIdForTag(Integer topicId,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Boolean titleImg, Boolean recommend, String title, int open,Map<String,String[]>attr,int orderBy,
			Integer first, Integer count);

	public Pagination getPageByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title, int open,Map<String,String[]>attr,int orderBy, int pageNo, int pageSize);

	public List<Content> getListByTagIdsForTag(Integer[] tagIds,
			Integer[] siteIds, Integer[] channelIds, Integer[] typeIds,
			Integer excludeId, Boolean titleImg, Boolean recommend,
			String title,int open, Map<String,String[]>attr,int orderBy, Integer first, Integer count);
	
	public Pagination getPageForCollection(Integer siteId, Integer memberId, int pageNo, int pageSize);

	public int countByChannelId(int channelId);

	public Content findById(Integer id);

	public Content save(Content bean);

	public Content updateByUpdater(Updater<Content> updater);

	public Content deleteById(Integer id);

	public Pagination getPage_friendsBlog(int ids,String title, Integer typeId,Integer currUserId,
			Integer inputUserId, boolean topLevel, boolean recommend,
			ContentStatus status, Byte checkStep, Integer siteId,Integer modelId,
			Integer channelId,int orderBy, int pageNo, int pageSize,String columnId,Integer recieveUserId);

	public List<Content> getListInfo(int userId);
	
	public List<Content> countByColumnId(Integer parseInt);
	
	public List<CmsComment> getListComment(int contentId,Date lastDate);

	public int getTotalArticleNum(CmsUser user);

	public int getTotalCommentNum(CmsUser user);

	public int getTotalCoverCommentNum(CmsUser user);

	public int getTotalReadNum(CmsUser user);

	public List<CmsBlogVisitor> getgetAllVistor(CmsUser user);


	public Pagination getPage_visitor(int i, int pageNo, int pageSize, CmsUser user);

	public List<CmsJoinGroup> getAlreadyJoinGroup(CmsUser user);
	
	public ContentSend saveContentSend(ContentSend bean);
	
	/**
	 * 根据用户帐号查询用户id
	 * @param userName 用户帐号
	 * @return Integer
	 */
	public Integer getUserId(String userName);
	
	/**
	 * 根据用id查询栏目的验证码
	 * @param userId 用户帐号
	 * @param validaCode 验证码
	 * @return String
	 */
	public Integer getUniqueCode(int userId,String validaCode);
	
	/**
	 * 根据内容id查询栏目id
	 * @param contentId 
	 * @return Integer
	 */
	public Integer getColumnId(int contentId);
	
	/**
	 * 撤销用户发送的文章
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer deleteContentSend(int contentId,int userId);
	
	/**
	 * 移除用户收录的文章
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer removeContentSend(int contentId,int userId);
	
	/**
	 * 查询文章是否是发送的还是收录的
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer getContentSendType(Integer contentId,Integer userId);
	
	/**
	 * 验证文章是否重复收录
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer getContentSend(int contentId,int userId);
	
	/**
	 * 修改发送表的标题
	 * @param contentId
	 * @param userId
	 * @return Integer
	 */
	public Integer updateContentSend(String title,int contentId);
	
	/**
	 * 修改置顶表的标题
	 * @param contentId
	 * @param userId
	 * @return Integer
	 */
	public Integer updateContentStick(String title,int contentId);
	
	/**
	 * 根据用户id查询用户置顶文章
	 * @param userId
	 * @return
	 */
	public List<ContentStick> getStickList(Integer userId);
	
	/**
	 * 文章置顶
	 * @param contentStick
	 * @return int
	 */
	public ContentStick contentStick(ContentStick contentStick);
	
	/**
	 * 根据文章id查询路径
	 * @param contentId
	 * @return int
	 */
	public String getChannelPath(Integer contentId);
	
	/**
	 * 查询该文章是否置顶
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer getStickId(Integer contentId,Integer userId);
	
	/**
	 * 取消文章置顶
	 * @param contentId
	 * @return int
	 */
	public Integer cancelContentStick(Integer contentId,Integer userId);
	
	/**
	 * 查询用户置顶的文章数
	 * @param userId
	 * @return int
	 */
	public Long getStickCount(Integer userId);
	
	/**
	 * 获取置顶的上一篇
	 * @param id
	 */
	public List<ContentStick> getPreStick(Integer id,Integer userId);
	
	/**
	 *获取置顶的下一篇
	 * @param id
	 */
	public List<ContentStick> getNextStick(Integer id,Integer userId);

	/**
	 *继续教育网首页文章
	 * @param count,userid
	 */
	public List<Content> getListByChannelIds(Integer count, Integer userid,Integer columnID);
	
	/**
	 * 获取博客公告
	 * @param id
	 * @return
	 */
	public String getNoticeContent(Integer userId);
	
	/**
	 * 保存或更新博客公告
	 * @param userId
	 * @param notice
	 * @return
	 */
	public String saveUpdateNotice(Integer userId ,String notice);
	
	/**
	 * 获取博客简介 
	 */
	public String getSynopsis(Integer userId);
	
	/**
	 * 保存或更新博客简介
	 * @param userId
	 * @param synopsis
	 * @return
	 */
	public String saveUpdateSynopsis(Integer userId , String synopsis);
	
	/**
	 * 清除博客公告或简介
	 * @param mark
	 * @param id
	 * @return
	 */
	public String clearNoticeSynopsis(String mark, Integer id);

	public Pagination getPageForWorkArticle(String articleType, Integer sitId,Integer userId, int pageNo, int pageSize, int coulmnId);
}
