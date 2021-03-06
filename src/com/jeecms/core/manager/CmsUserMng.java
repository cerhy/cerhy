package com.jeecms.core.manager;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.jeecms.cms.entity.main.ContentStick;
import com.jeecms.common.email.EmailSender;
import com.jeecms.common.email.MessageTemplate;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;

public interface CmsUserMng {
	public CmsUser updateBlog(CmsUser user);
	
	public Pagination getPage(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank,
			String realName,Integer departId,Integer roleId,
			Boolean allChannel,Boolean allControlChannel,
			int pageNo, int pageSize);
	
	public List<CmsUser> getList(String username, String email, Integer siteId,
			Integer groupId, Boolean disabled, Boolean admin, Integer rank);

	public List<CmsUser> getAdminList(Integer siteId, Boolean allChannel,
			Boolean disabled, Integer rank);
	
	public Pagination getAdminsByDepartId(Integer id, int pageNo,int pageSize);
	
	public Pagination getAdminsByRoleId(Integer roleId, int pageNo, int pageSize);

	public CmsUser findById(Integer id);

	public CmsUser findByUsername(String username);

	public CmsUser registerMember(String username, String email,
			String password, String ip, Integer groupId,Integer grain, boolean disabled,CmsUserExt userExt,Map<String,String>attr);
	
	public CmsUser registerMember(String username, String email,
			String password, String ip, Integer groupId, boolean disabled,CmsUserExt userExt,Map<String,String>attr, Boolean activation , EmailSender sender, MessageTemplate msgTpl)throws UnsupportedEncodingException, MessagingException ;

	public void updateLoginInfo(Integer userId, String ip,Date loginTime,String sessionId);

	public void updateUploadSize(Integer userId, Integer size);
	
	public void updateUser(CmsUser user);

	public void updatePwdEmail(Integer id, String password, String email);

	public boolean isPasswordValid(Integer id, String password);

	public CmsUser saveAdmin(String username, String email, String password,
			String ip, boolean viewOnly, boolean selfAdmin, int rank,
			Integer groupId, Integer departmentId,Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels,
			CmsUserExt userExt);

	public CmsUser updateAdmin(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId,Integer[] roleIds, Integer[] channelIds,
			Integer[] siteIds, Byte[] steps, Boolean[] allChannels,Boolean[] allControlChannels);

	public CmsUser updateAdmin(CmsUser bean, CmsUserExt ext, String password,
			Integer groupId,Integer departmentId, Integer[] roleIds, Integer[] channelIds,
			Integer siteId, Byte step, Boolean allChannel,Boolean allControlChannel);

	public CmsUser updateMember(Integer id, String email, String password,
			Boolean isDisabled, CmsUserExt ext, Integer groupId,Integer grain,Map<String,String>attr);
	
	public CmsUser updateMember(Integer id, String email, String password,Integer groupId,String realname,String mobile,Boolean sex);
	
	public CmsUser updateUserConllection(CmsUser user,Integer cid,Integer operate);

	public void addSiteToUser(CmsUser user, CmsSite site, Byte checkStep);

	public CmsUser deleteById(Integer id);

	public CmsUser[] deleteByIds(Integer[] ids);

	public boolean usernameNotExist(String username);
	
	public boolean usernameNotExistInMember(String username);

	public boolean emailNotExist(String email);

	public CmsUser registerInterface(String no,String areacode,String mobile,String type,String username, String email,
			String password, String ip, Integer groupId, boolean disabled,
			CmsUserExt userExt,Map<String,String>attr, Boolean activation ,
			EmailSender sender, MessageTemplate msgTpl)throws UnsupportedEncodingException, MessagingException ;
	
	/**
	 * 发送文章
	 * @param contentId
	 * @param userId
	 * @param sendee
	 * @param validateCode
	 * @return int
	 */
	public int sendArticle(Integer contentId,Integer userId,String sendee,String validateCode,String title);
	
	/**
	 * 撤销文章
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer cancelArticle(Integer contentId,Integer userId);
	
	/**
	 * 收录文章
	 * @param contentId
	 * @param userId
	 * @param friendId
	 * @return int
	 */
	public Integer embodyArticle(Integer contentId,Integer userId,Integer friendId,Integer columnId,String title);
	
	/**
	 * 移除用户收录的 文章
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer removeArticle(Integer contentId,Integer userId);
	
	/**
	 * 查询文章是否是发送的还是收录的
	 * @param contentId
	 * @param userId
	 * @return int
	 */
	public Integer getContentSendType(Integer contentId,Integer userId);
	
	/**
	 * 文章置顶
	 * @param contentStick
	 */
	public Integer contentStick(ContentStick contentStick);
	
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
	 */
	public void cancelContentStick(Integer contentId,Integer userId);
	
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
	 * 获取博客公告
	 * @param userId 用户id
	 * @return
	 */
	public String getNoticeContent(Integer userId);
	
	/**
	 * 保存或更新博客公告
	 * @param userId
	 * @param notice
	 * @return
	 */
	public String saveUpdateNotice(Integer userId , String notice);
	
	/**
	 * 获取博客简介
	 * @param userId 用户id
	 * @return
	 */
	public String getSynopsisContent(Integer userId);
	
	
	/**
	 *保存或更新博客简介 
	 * @param userId
	 * @param Synopsis
	 * @return
	 */
	public String saveUpdateSynopsis(Integer userId , String synopsis);
	
	/**
	 * 清除博客公告或简介
	 * @param mark
	 * @param id
	 * @return
	 */
	public String clearNoticeSynopsis(String mark , Integer id);

	/**
	 * 通过选择群组发送文章
	 * @param groupsId
	 * @param contentId
	 * @param contTitle
	 * @param id
	 * @return
	 */
	public Integer sendArticleGroup(Integer groupsId, Integer contentId,
			String contTitle, Integer id);
}