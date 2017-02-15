package com.jeecms.cms.action.member;
import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.cms.Constants.TPLDIR_MEMBER;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jeecms.cms.action.blog.BlogAct;
import com.jeecms.cms.action.blog.BlogCommon;
import com.jeecms.cms.action.blog.CreateSerialNo;
import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.assist.CmsComment;
import com.jeecms.cms.entity.assist.CmsJoinGroup;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.cms.manager.assist.CmsFileMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.Ftp;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.manager.DbFileMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

/**
 * 会员投稿Action
 * 
 */
@Controller
public class ContributeAct extends AbstractContentMemberAct {
	private static final Logger log = LoggerFactory.getLogger(ContributeAct.class);
	
	public static final String CONTRIBUTE_LIST = "tpl.contributeList";
	public static final String CONTRIBUTE_ADD = "tpl.contributeAdd";
	public static final String CONTRIBUTE_EDIT = "tpl.contributeEdit";
	public static final String CONTRIBUTE_UPLOADMIDIA = "tpl.uploadMedia";
	public static final String CONTRIBUTE_UPLOADATTACHMENT = "tpl.uploadAttachment";

	/**
	 * 会员投稿列表
	 * 
	 * @param title
	 *            文章标题
	 * @param channelId
	 *            栏目ID
	 * @param pageNo
	 *            页码
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_list.jspx")
	public String list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return super.list(queryTitle, modelId, queryChannelId, CONTRIBUTE_LIST,
				pageNo, request, model);
	}

	/**
	 * 会员投稿添加
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_add.jspx")
	public String add(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return super.add(true, CONTRIBUTE_ADD, request, response, model);
	}

	/**
	 * 会员投稿保存
	 * 
	 * @param id
	 *            文章ID
	 * @param title
	 *            标题
	 * @param author
	 *            作者
	 * @param description
	 *            描述
	 * @param txt
	 *            内容
	 * @param tagStr
	 *            TAG字符串
	 * @param channelId
	 *            栏目ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_save.jspx")
	public void save(String title, String author, String description,
			String txt, String tagStr, Integer channelId,Integer columnId,Integer modelId, 
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		   String blog = request.getParameter("blog");
		   String channelIds = request.getParameter("channelIds");
		   int sta=0;
		   if(channelIds!=null){
			   String[] str=channelIds.split("&");
			   if(str[1].equals("chan")){
				   channelId=Integer.valueOf(str[0]);
			   }else if(str[1].equals("colu")){
				   columnId=Integer.valueOf(str[0]);
				   sta=1;
			   }
		   }
		   if(null != blog ){
			  blogAct.blog_save(title, author, description, txt, tagStr, channelId,columnId,modelId,
						null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
						,picPaths,picDescs,charge,chargeAmount,
						nextUrl, request, response, model,sta);
					   }else{
			   super.save(title, author, description, txt, tagStr, channelId,modelId,
						null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
						,picPaths,picDescs,charge,chargeAmount,
						nextUrl, request, response, model);
		   }
		
	}

	/**
	 * 会员投稿修改
	 * 
	 * @param id
	 *            文章ID
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_edit.jspx")
	public String edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.edit(id, CONTRIBUTE_EDIT, request, response, model);
	}

	/**
	 * 会有投稿更新
	 * 
	 * @param id
	 *            文章ID
	 * @param title
	 *            标题
	 * @param author
	 *            作者
	 * @param description
	 *            描述
	 * @param txt
	 *            内容
	 * @param tagStr
	 *            TAG字符串
	 * @param channelId
	 *            栏目ID
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_update.jspx")
	public String update(Integer id, String title, String author,
			String description, String txt, String tagStr, Integer columnId,Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String blog = request.getParameter("blog");
		String channelIds = request.getParameter("channelIds");
		   if(channelIds!=null&&channelIds!=""){
			   String[] str=channelIds.split("&");
			   if(str[1]==null){
				   channelId=Integer.valueOf(str[0]);
			   }else if(str[1].equals("colu")){
				   columnId=Integer.valueOf(str[0]);
			   }
		   }
		   if(null != blog ){
			   return blogAct.blog_update(id, title, author, description, txt, tagStr,
					   columnId,channelId, mediaPath,mediaType,attachmentPaths,
						attachmentNames, attachmentFilenames
						,picPaths,picDescs,null,charge, chargeAmount,
						nextUrl, request, response, model);
		   }else{
			   return super.update(id, title, author, description, txt, tagStr,
						channelId, mediaPath,mediaType,attachmentPaths,
						attachmentNames, attachmentFilenames
						,picPaths,picDescs,null,charge, chargeAmount,
						nextUrl, request, response, model);
		   }
		
	}

	/**
	 * 会员投稿删除
	 * 
	 * @param ids
	 *            待删除的文章ID数组
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/contribute_delete.jspx")
	public String delete(Integer[] ids, HttpServletRequest request,
			String nextUrl, HttpServletResponse response, ModelMap model) {
		return super.delete(ids, request, nextUrl, response, model);
	}
	
	@RequestMapping("/member/o_upload_media.jspx")
	public String uploadMedia(
			@RequestParam(value = "mediaFile", required = false) MultipartFile file,
			String filename, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file, request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, CONTRIBUTE_UPLOADMIDIA);
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(dbFilePath.length());
					fileUrl = dbFileMng.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = dbFileMng.storeByExt(site.getUploadPath(), ext,
							file.getInputStream());
					// 加上访问地址
					fileUrl = request.getContextPath() + dbFilePath + fileUrl;
				}
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ftpUrl.length());
					fileUrl = ftp.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
							.getInputStream());
					// 加上url前缀
					fileUrl = ftpUrl + fileUrl;
				}
			} else {
				String ctx = request.getContextPath();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ctx.length());
					fileUrl = fileRepository.storeByFilename(filename, file);
				} else {
					fileUrl = fileRepository.storeByExt(site.getUploadPath(),
							ext, file);
					// 加上部署路径
					fileUrl = ctx + fileUrl;
				}
			}
			cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileMng.saveFileByPath(fileUrl, fileUrl, false);
			model.addAttribute("mediaPath", fileUrl);
			model.addAttribute("mediaExt", ext);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, CONTRIBUTE_UPLOADMIDIA);
	}
	
	@RequestMapping("/member/o_upload_attachment.jspx")
	public String uploadAttachment(
			@RequestParam(value = "attachmentFile", required = false) MultipartFile file,
			String attachmentNum, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user= CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file,request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, CONTRIBUTE_UPLOADATTACHMENT);
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				fileUrl = dbFileMng.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上访问地址
				fileUrl = request.getContextPath() + dbFilePath + fileUrl;
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上url前缀
				fileUrl = ftpUrl + fileUrl;
			} else {
				String ctx = request.getContextPath();
				fileUrl = fileRepository.storeByExt(site.getUploadPath(), ext,
						file);
				// 加上部署路径
				fileUrl = ctx + fileUrl;
			}
			cmsUserMng.updateUploadSize(user.getId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileMng.saveFileByPath(fileUrl, origName, false);
			model.addAttribute("attachmentPath", fileUrl);
			model.addAttribute("attachmentName", origName);
			model.addAttribute("attachmentNum", attachmentNum);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, CONTRIBUTE_UPLOADATTACHMENT);
	}
	
	private WebErrors validateUpload(MultipartFile file,
			HttpServletRequest request) {
		String origName = file.getOriginalFilename();
		CmsUser user= CmsUtils.getUser(request);
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		int fileSize = (int) (file.getSize() / 1024);
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(file, "file")) {
			return errors;
		}
		if(origName!=null&&(origName.contains("/")||origName.contains("\\")||origName.indexOf("\0")!=-1)){
			errors.addErrorCode("upload.error.filename", origName);
		}
		//非允许的后缀
		if(!user.isAllowSuffix(ext)){
			errors.addErrorCode("upload.error.invalidsuffix", ext);
			return errors;
		}
		//超过附件大小限制
		if(!user.isAllowMaxFile((int)(file.getSize()/1024))){
			errors.addErrorCode("upload.error.toolarge",origName,user.getGroup().getAllowMaxFile());
			return errors;
		}
		//超过每日上传限制
		if (!user.isAllowPerDay(fileSize)) {
			long laveSize=user.getGroup().getAllowPerDay()-user.getUploadSize();
			if(laveSize<0){
				laveSize=0;
			}
			errors.addErrorCode("upload.error.dailylimit", laveSize);
		}
		return errors;
	}
	

	//获取最上一级模板id
	@RequestMapping(value = "/member/channelId.jspx")
	public void aliPayOrderQuery(String channelId,HttpServletRequest request,
			HttpServletResponse response, ModelMap model)
					throws UnsupportedEncodingException, JSONException {
		JSONObject json = new JSONObject();
		CmsSite site=CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		//获取上级栏目ID
		Channel ids = channelMng.findById(Integer.valueOf(channelId));
		if(null!=ids.getModel()){
			json.put("status", ids.getModel().getId());
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@Autowired
	private DbFileMng dbFileMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsFileMng fileMng;
	@Autowired
	protected ChannelMng channelMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	protected BlogAct blogAct;
	@Autowired
	protected BlogCommon blogCommon;
	@Autowired
	protected ColumnsMng columnsMng;
	
	
	//博客主页
	@RequestMapping(value = "/blog/index.jspx")
	public String blog_index(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		 return blogAct.blog_index(queryTitle, modelId, queryChannelId, "tpl.blogCenter",
				pageNo, request, model);
	} 
	
	@RequestMapping(value = "/blog/tzsetting.jspx")
	public String tzsetting(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.tzsetting(request, response, model);
	}

	/**
	 * 博客更新参数保存
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/updateSetting.jspx")
	public void updateSetting(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		blogAct.updateSetting(request, response, model);
	}
	
	/**
	 * 博客文章
	 */
	@RequestMapping(value = "/blog/contribute_list.jspx")
	public String blog_list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.blog_list(queryTitle, modelId, queryChannelId, CONTRIBUTE_LIST,
				pageNo, request, model);
	}
	
	//好友博客栏目
	@RequestMapping(value = "/blog/contribute_list_friend.jspx")
	public String blog_list_friend(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.blog_list_friend(queryTitle, modelId, queryChannelId, "tpl.testPage",
				pageNo, request, model);
	}

	/**
	 * 新建博客文章
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/contribute_add.jspx")
	public String blog_add(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return blogAct.blog_add(true, CONTRIBUTE_ADD, request, response, model);
	}
	
	/**
	 * 博客栏目列表
	 * @param queryTitle
	 * @param modelId
	 * @param queryChannelId
	 * @param pageNo
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/columns_list.jspx")
	public String blog_columns_list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.blog_columns_list(queryTitle, modelId, queryChannelId, "tpl.columnsList",
				pageNo, request, model);
	}
	
	/**
	 * 新建博客栏目
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/newColumn.jspx")
	public void columns_add(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		blogAct.columns_add(request, response, model);
	}
	
	/**
	 * 删除博客栏目
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/deteleColumn.jspx")
	public void columns_detele(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		blogAct.columns_detele(request, response, model);
	}
	
	/**
	 * 跳转到博客更新参数页面
	 * @param id
	 * @param orderId
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/update_tz.jspx")
	public String update_tz(String id, String orderId, HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return blogAct.update_tz(id,orderId,request, response, model);
	}
	
	/**
	 * 博客更新博客栏目
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/blog/updateColumn.jspx")
	public void columns_update(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		blogAct.columns_update(request, response, model);
	}
	
	/**
	 * 博客文章编辑
	 * @param id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/blog/contribute_edit.jspx")
	public String blog_edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return blogAct.blog_edit(id, CONTRIBUTE_EDIT, request, response, model);
	}

	/**
	 * 博客文章删除
	 */
	@RequestMapping(value = "/blog/contribute_delete.jspx")
	public void blog_delete(Integer ids,Integer columnId,Integer channelId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		blogAct.blog_delete(ids,columnId,channelId, request,  response, model);
	}
	
	/**
	 *跳转链接页面方法 
	  */
	@RequestMapping(value = "/blog/link_list.jspx")
	public String linkList(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.linkList");
	}
	
	@RequestMapping(value = "/blog/add_link.jspx")
	public String custom(String linkUrl,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return blogAct.link_save(linkUrl.replaceAll("\r\n", " "),nextUrl,request, response, model);
	}
	
	/**
	 *跳转好友页面方法 
	 */
	@RequestMapping(value = "/blog/friends.jspx")
	public String friends(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getFriends(user.getId(), model, 1);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friends");
	}

	@RequestMapping(value = "/blog/add_friends.jspx")
	public void friends(String friends,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		blogAct.friends_save(friends.replaceAll("\r\n", " "),nextUrl,request, response, model);
	}
	
	/**
	 *跳转好友博客页面方法 
	  */
	@RequestMapping(value = "/blog/find_all_url_friend.jspx")
	public String findAllInfo(String userIds,String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return blogAct.friendCenter(userIds,queryTitle, modelId, queryChannelId, "tpl.testPage",
				pageNo, request, model);
	}
	
	
	/**
	 *博客关注 
	 * @throws IOException 
	  */
	@RequestMapping(value = "/blog/focus.jspx")
	public void blog_focus(String focusUserId,String focusUserName, HttpServletRequest request,HttpServletResponse response) throws IOException {
		blogAct.blog_focus(focusUserId,focusUserName, request, response);
	}
	
	/**
	 * 博客检验是否关注
	 */
	@RequestMapping(value = "blog/focuscheck.jspx")
	public void blog_focus_check(String focusUserId, HttpServletRequest request,HttpServletResponse response) throws IOException {
		blogAct.blog_focus_check(focusUserId,request, response);
	}
	
	/**
	 * 博客取消关注
	 *  
	 */
	@RequestMapping(value = "blog/cancelfocus.jspx")
	public void blog_cancel_focus(String focusUserId, HttpServletRequest request,HttpServletResponse response) throws IOException {
		blogAct.blog_cancel_focus(focusUserId,request, response);
	}
	
	/**
	 * 博客背景
	 */
	@RequestMapping(value = "/blog/changeTheme.jspx", method = RequestMethod.POST)
	public void updateTheme(String theme, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
		}
		if(StringUtils.isNotBlank(theme)){
			user.setTheme(theme);
			cmsUserMng.updateUser(user);
			log.info("update CmsUser success. id={}", user.getId());
		}	
		try {
			response.sendRedirect("../blog/index.jspx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *读取评论消息数目
	 */
	@RequestMapping(value = "/member/findCommentNu.jspx")
	public void findCommentNu(HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		if(user!=null){
			JSONObject json = new JSONObject();
			CmsSite site=CmsUtils.getSite(request);
			FrontUtils.frontData(request, model, site);
			Date lastDate=user.getLastLoginTime();
			int userId=user.getId();
			List<CmsComment> listNum=contentMng.findCommentByConid(userId,lastDate);
			json.put("status", listNum.size());
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	
	/**
	 *校验添加的好友是否存在!
	 */
	@RequestMapping(value = "/member/checkAddfinds.jspx")
	public void checkAddfinds(String friends,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		String[] strs=friends.split("~");
		int addnum=1;
		String no="";
		for(int i=0;i<strs.length;i++){
			CmsUser u=channelMng.findUserImage(strs[i].split("=")[1].toString());
			if(null==u){
				no+=strs[i].split("=")[1].toString()+",";
			}else{
				if(user.getId()==u.getId()){
					no="repeatName";
					break;
				}
			}
		}
		if(no!=""){
			if(no.equals("repeatName")){
				addnum=2;
			}else{
				addnum=0;
			}
		}else{
			addnum=1;
		}
		JSONObject json = new JSONObject();
		CmsSite site=CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		if(addnum==0){
			json.put("status",no.substring(0,no.length() - 1));
		}else if(addnum==2){
			json.put("status",2);
		}else{
			json.put("status","");	
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	/**
	 *跳转登陆人数据统计页面方法 
	 */
	@RequestMapping(value = "/blog/dataStatistics.jspx")
	public String dataStatistics(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getTotalCoverCommentNum(model, user);
 		model = blogCommon.getTotalReadNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.dataStatistics");
	}
	/**
	 *跳转好友数据统计页面方法 
	 */
	@RequestMapping(value = "/blog/friendDataStatistics.jspx")
	public String friendDataStatistics(String userIds,String q,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		model = blogCommon.getColumn(request,model,userT);
	    model = blogCommon.getChannel(request,model,userT,site);
 		model = blogCommon.getTotalArticleNum(model,userT);
 		model = blogCommon.getTotalCommentNum(model, userT);
 		model = blogCommon.getTotalCoverCommentNum(model, userT);
 		model = blogCommon.getTotalReadNum(model, userT);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,userT);
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
	    List<Focus> l = null;
	    if(null != list){
	    	if(list.size()>3){
	    		l = new ArrayList<Focus>();
	    		for(int i =0;i<3;i++){
	    			l.add(list.get(i));
	    		}
	    	}
	    	if(null != l){
	    		model.addAttribute("focusMax", l);
	    	}else{
	    		model.addAttribute("focusMax", list);
	    	}
	    }
	    model.addAttribute("usert", userT);
	    model.addAttribute("userIds", userIds);
	    FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friendDataStatistics");
	}
	
	/**
	 * 显示好友，关注，粉丝，明星博主数据-----自己数据
	 */
	@RequestMapping(value = "/blog/gotoDataShow.jspx")
	public String gotoDataShow(int dataFlag,HttpServletRequest request, HttpServletResponse response,ModelMap model,Integer pageNo) {
		return blogAct.gotoDataShow(dataFlag, request, response, model,pageNo);
	}
	
	/**
	 * 显示好友，关注，粉丝，明星博主数据-----好友博客数据
	 */
	@RequestMapping(value = "/blog/gotoDataShowFriend.jspx")
	public String gotoDataShowFriend(int dataFlag,HttpServletRequest request, HttpServletResponse response,ModelMap model,Integer pageNo,String userId) {
		return blogAct.gotoDataShowFriend(dataFlag, request, response, model,pageNo,userId);
	}
	
	/**
	 *查询访客 
	 */
	@RequestMapping(value = "/blog/visitor.jspx")
	public String visitor(String queryTitle, Integer modelId,Integer queryChannelId, Integer pageNo,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
//		model = blogCommon.getLinks(model,user);
//		model = blogCommon.getFriends(model,user);
//		model = blogCommon.blog_focus_find(null,request,model);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
// 		model = blogCommon.getMaxFocus(request, model);
 		model = blogCommon.getAllVistor(request, model,user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogAct.getAllVisitors(queryTitle, modelId, queryChannelId,pageNo, request, model,user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.visitor");
	}
	
	
	/**
	 *好友访客页面 
	 */
	@RequestMapping(value = "/blog/friends_visitor.jspx")
	public String friends_visitor(String userIds,String q,Integer modelId,Integer queryChannelId, Integer pageNo,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		model = blogCommon.getColumn(request,model,userT);
	    model = blogCommon.getChannel(request,model,userT,site);
//	    model = blogCommon.blog_focus_find(Integer.parseInt(userIds),request,model);
//	    model = blogCommon.getLinks(model,userT);
//		model = blogCommon.getFriends(model,userT);
 		model = blogCommon.getTotalArticleNum(model,userT);
 		model = blogCommon.getTotalCommentNum(model, userT);
 		model = blogCommon.getTotalCoverCommentNum(model, userT);
 		model = blogCommon.getTotalReadNum(model, userT);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,userT);
 		model = blogAct.getAllVisitors(q, modelId, queryChannelId,pageNo, request, model,userT);
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Focus> list = (new BlogDao()).findMaxFocusCount( path);
	    List<Focus> l = null;
	    if(null != list){
	    	if(list.size()>3){
	    		l = new ArrayList<Focus>();
	    		for(int i =0;i<3;i++){
	    			l.add(list.get(i));
	    		}
	    	}
	    	if(null != l){
	    		model.addAttribute("focusMax", l);
	    	}else{
	    		model.addAttribute("focusMax", list);
	    	}
	    }
	    model.addAttribute("usert", userT);
	    model.addAttribute("userIds", userIds);
	    FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friends_visitor");
	}
	
	
	/**
	 * 产生不重复的六位数
	 */
	@RequestMapping(value = "/blog/uniqueNum.jspx")
	public void uniqueNum(HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
			JSONObject json = new JSONObject();
			json.put("status", CreateSerialNo.generateNumber());
			ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 加入群组
	 */
	@RequestMapping(value = "/blog/joinGroup.jspx")
	public void joinGroup(String code,String createUserId,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(createUserId.toString()));
		Columns cu=columnsMng.findInfoByCode(code);
		JSONObject json = new JSONObject();
		CmsJoinGroup cjg=new CmsJoinGroup();
		cjg.setCreateUserId(userT);
		cjg.setJoinUserId(user);
		cjg.setJoinCode(code);
		cjg.setJoinTime(new Date());
		cjg.setColumnsId(cu);
		int joinStatus=columnsMng.saveJoinGroup(cjg);
		json.put("status",joinStatus);
		ResponseUtils.renderJson(response, json.toString());
	}
	/**
	 * 检查是否加入群组-页面现在用
	 */
	@RequestMapping(value = "/blog/checkJoinState.jspx")
	public void checkJoinState(String joinState,HttpServletRequest request,HttpServletResponse response, ModelMap model)throws UnsupportedEncodingException, JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		int joinStatus=columnsMng.checkJoinState(joinState,user);
		json.put("status",joinStatus);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
}
