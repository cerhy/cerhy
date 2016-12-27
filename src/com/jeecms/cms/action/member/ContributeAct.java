package com.jeecms.cms.action.member;
import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.cms.Constants.TPLDIR_MEMBER;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.manager.assist.CmsFileMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;
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
	public String save(String title, String author, String description,
			String txt, String tagStr, Integer channelId,Integer modelId, 
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		   String blog = request.getParameter("blog");
		   if(null != blog ){
			  return super.blog_save(title, author, description, txt, tagStr, channelId,modelId,
						null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
						,picPaths,picDescs,charge,chargeAmount,
						nextUrl, request, response, model);
					   }else{
			   return super.save(title, author, description, txt, tagStr, channelId,modelId,
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
			String description, String txt, String tagStr, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String blog = request.getParameter("blog");
		   if(null != blog ){
			   return super.blog_update(id, title, author, description, txt, tagStr,
						channelId, mediaPath,mediaType,attachmentPaths,
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
	
	@RequestMapping(value = "/blog/help.jspx")
	public String blogHelp(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		model.addAttribute("columnsList", columnsList);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.blogHelp");
	}
	
	@RequestMapping(value = "/blog/tzsetting.jspx")
	public String tzsetting(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return super.tzsetting(request, response, model);
	}

	@RequestMapping(value = "/blog/updateSetting.jspx")
	public void updateSetting(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		 super.updateSetting(request, response, model);
	}
	
	@RequestMapping(value = "/blog/index.jspx")
	public String center(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return super.center(queryTitle, modelId, queryChannelId, "tpl.blogCenter",
				pageNo, request, model);
	}
	
	@RequestMapping(value = "/blog/contribute_list.jspx")
	public String blog_list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return super.blog_list(queryTitle, modelId, queryChannelId, CONTRIBUTE_LIST,
				pageNo, request, model);
	}

	@RequestMapping(value = "/blog/contribute_add.jspx")
	public String blog_add(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return super.blog_add(true, CONTRIBUTE_ADD, request, response, model);
	}
	
	@RequestMapping(value = "/blog/columns_list.jspx")
	public String blog_columns_list(String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return super.blog_columns_list(queryTitle, modelId, queryChannelId, "tpl.columnsList",
				pageNo, request, model);
	}
	
	@RequestMapping(value = "/blog/newColumn.jspx")
	public void columns_add(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		 super.columns_add(request, response, model);
	}
	
	@RequestMapping(value = "/blog/deteleColumn.jspx")
	public void columns_detele(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		 super.columns_detele(request, response, model);
	}
	
	@RequestMapping(value = "/blog/update_tz.jspx")
	public String update_tz(String id, String orderId, HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		return super.update_tz(id,orderId,request, response, model);
	}
	
	@RequestMapping(value = "/blog/updateColumn.jspx")
	public void columns_update(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		 super.columns_update(request, response, model);
	}
	

/*	@RequestMapping(value = "/blog/contribute_save.jspx")
	public String blog_save(String title, String author, String description,
			String txt, String tagStr, String column_id,Integer modelId, 
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.blog_save(title, author, description, txt, tagStr, column_id,modelId,
				null, captcha,mediaPath,mediaType,attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs,charge,chargeAmount,
				nextUrl, request, response, model);
	}*/

	@RequestMapping(value = "/blog/contribute_edit.jspx")
	public String blog_edit(Integer id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.blog_edit(id, CONTRIBUTE_EDIT, request, response, model);
	}

/*	@RequestMapping(value = "/blog/contribute_update.jspx")
	public String blog_update(Integer id, String title, String author,
			String description, String txt, String tagStr, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		return super.blog_update(id, title, author, description, txt, tagStr,
				channelId, mediaPath,mediaType,attachmentPaths,
				attachmentNames, attachmentFilenames
				,picPaths,picDescs,null,charge, chargeAmount,
				nextUrl, request, response, model);
	}*/

	@RequestMapping(value = "/blog/contribute_delete.jspx")
	public void blog_delete(Integer ids,Integer column_id, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		 super.blog_delete(ids,column_id, request,  response, model);
	}
	
	
	/**
	 *跳转链接页面方法 
	  */
	@RequestMapping(value = "/blog/link_list.jspx")
	public String linkList(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		//获取链接列表
		String linkUrl=user.getLinkUrl();
		List listU=new ArrayList();
		if(linkUrl!=null){
			String[] strs=linkUrl.split(" ");
			String newUrl="";
			for(int i=0;i<strs.length;i++){
				if(i!=strs.length-1){
					if(!strs[i].contains("http")&&strs[i+1].contains("http")){
						newUrl+="~"+strs[i]+" ";
					}else{
						newUrl+=strs[i]+" ";
					}
				}else{
					if(!strs[i].contains("http")){
						newUrl+="~"+strs[i]+" ";
					}else{
						newUrl+=strs[i]+" ";
					}
				}
			}
			String[] str=newUrl.split("~");
			for(int j=0;j<str.length;j++){
				Map<String,Object> map=new HashMap<String,Object>();
				String[] st=str[j].toString().split(" ");
				List lists=new ArrayList();
				String newName="";
				for(int k=0;k<st.length;k++){
					if(st[0].contains("http")){
						newName="";
					}else{
						newName=st[0];
					}
					lists.add(st[k]);
				}
				map.put(newName, lists);
				listU.add(map);
			}
			model.addAttribute("urlList", listU);
			model.addAttribute("linkUrls", linkUrl.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("urlList",listU);
			model.addAttribute("linkUrls","");
			
		}
		//获取好友列表
		String friends=user.getFriends();
		List listF = new ArrayList<>();
		if(friends!=null){
			
			String[] strs=friends.split(" ");
			for(int i=0;i<strs.length;i++){
				String[] str=strs[i].split("=");
				Map<String,Object> map=new HashMap<String,Object>();
				CmsUser u=channelMng.findUserImage(str[1].toString());
				String newName=str[0]+"~"+u.getId()+"~"+u.getUserExt().getUserImg();
				map.put(newName, u.getUserExt().getUserImg());
				listF.add(map);
			}
			model.addAttribute("friendsList", listF);
			model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("friendsList", listF);
			model.addAttribute("friends","");
		}
		
		model.addAttribute("columnsList", columnsList);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.linkList");
	}
	
	@RequestMapping(value = "/blog/add_link.jspx")
	public String custom(String linkUrl,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return super.link_save(linkUrl.replaceAll("\r\n", " "),nextUrl,request, response, model);
	}
	
	/**
	 *跳转好友页面方法 
	 */
	@RequestMapping(value = "/blog/friends.jspx")
	public String friends(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		int user_id = user.getId();
		String path = request.getSession().getServletContext().getRealPath("/");
		List<Columns> columnsList = (new BlogDao()).findByUserId(user_id, path);
		//获取链接列表
		String linkUrl=user.getLinkUrl();
		List listU=new ArrayList();
		if(linkUrl!=null){
			String[] strs=linkUrl.split(" ");
			String newUrl="";
			for(int i=0;i<strs.length;i++){
				if(i!=strs.length-1){
					if(!strs[i].contains("http")&&strs[i+1].contains("http")){
						newUrl+="~"+strs[i]+" ";
					}else{
						newUrl+=strs[i]+" ";
					}
				}else{
					if(!strs[i].contains("http")){
						newUrl+="~"+strs[i]+" ";
					}else{
						newUrl+=strs[i]+" ";
					}
				}
			}
			String[] str=newUrl.split("~");
			for(int j=0;j<str.length;j++){
				Map<String,Object> map=new HashMap<String,Object>();
				String[] st=str[j].toString().split(" ");
				List lists=new ArrayList();
				String newName="";
				for(int k=0;k<st.length;k++){
					if(st[0].contains("http")){
						newName="";
					}else{
						newName=st[0];
					}
					lists.add(st[k]);
				}
				map.put(newName, lists);
				listU.add(map);
			}
			model.addAttribute("urlList", listU);
			model.addAttribute("linkUrls", linkUrl.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("urlList",listU);
			model.addAttribute("linkUrls","");
			
		}
		//获取好友列表
		String friends=user.getFriends();
		List listF = new ArrayList<>();
		if(friends!=null){
			
			String[] strs=friends.split(" ");
			for(int i=0;i<strs.length;i++){
				String[] str=strs[i].split("=");
				Map<String,Object> map=new HashMap<String,Object>();
				CmsUser u=channelMng.findUserImage(str[1].toString());
				String newName=str[0]+"~"+u.getId()+"~"+u.getUserExt().getUserImg();
				map.put(newName, u.getUserExt().getUserImg());
				listF.add(map);
			}
			model.addAttribute("friendsList", listF);
			model.addAttribute("friends", friends.replaceAll(" ", "\r\n"));
		}else{
			model.addAttribute("friendsList", listF);
			model.addAttribute("friends","");
		}
		model.addAttribute("columnsList", columnsList);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, "tpl.friends");
	}


	@RequestMapping(value = "/blog/add_friends.jspx")
	public String friends(String friends,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return super.friends_save(friends.replaceAll("\r\n", " "),nextUrl,request, response, model);
	}
	
	
	
	/**
	 *跳转好友博客页面方法 
	  */
	@RequestMapping(value = "/blog/find_all_url_friend.jspx")
	public String findAllInfo(String userIds,String queryTitle, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		return super.friendCenter(userIds,queryTitle, modelId, queryChannelId, "tpl.testPage",
				pageNo, request, model);
	}
	
	
	@RequestMapping(value = "/blog/changeTheme.jspx", method = RequestMethod.POST)
	public String updateTheme(String theme, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model){
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		if(StringUtils.isNotBlank(theme)){
			user.setTheme(theme);
			cmsUserMng.updateUser(user);
			log.info("update CmsUser success. id={}", user.getId());
		}	
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
}
