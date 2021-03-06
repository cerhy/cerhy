package com.jeecms.cms.action.blog;

import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.common.page.SimplePage.cpn;

import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.ibm.db2.jcc.am.l;
import com.jeecms.cms.dao.main.ContentDao;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.CmsModel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.entity.main.ContentExt;
import com.jeecms.cms.entity.main.ContentTxt;
import com.jeecms.cms.entity.main.ContentType;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.cms.manager.assist.CmsKeywordMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.CmsModelMng;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.ContentTypeMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.cms.manager.main.impl.FocusMng;
import com.jeecms.common.page.Paginable;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.page.SimplePage;
import com.jeecms.common.util.RedisUtil;
import com.jeecms.common.util.StrUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsGroup;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.jeecms.core.web.util.URLHelper.PageInfo;
import com.octo.captcha.service.image.ImageCaptchaService;

public class BlogAct {
	private static final Logger log = LoggerFactory.getLogger(BlogAct.class);
	public String blog_index(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		if (user == null) {
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return FrontUtils.showLoginBlog(request, model, site);
			}
		}
		recieveUserId = user.getId();
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    int totalCount = blogCommon.getTotalArticleNum(model,user);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, user);
	    model = blogCommon.getStarBlogger(request, model);
	    model = blogCommon.getAlreadyJoinGroup(request, model,user);
	    model = blogCommon.getFriendLeft(user.getId(),model,1);
	    model = contentMng.getStickList(user,model);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,null,null,recieveUserId);
		//p.setTotalCount(totalCount);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	public void updateSetting(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
		}
		if (null != user) {
			String blogTitle = request.getParameter("blogTitle");
			if(blogTitle.contains("&＃40;")){
				blogTitle=blogTitle.replace("&＃40;", "(");
			}
			if(blogTitle.contains("&＃41;")){
				blogTitle=blogTitle.replace("&＃41;", ")");
			}
			String blogTitle2 = request.getParameter("blogTitle2");
			if(blogTitle2.contains("&＃40;")){
				blogTitle2=blogTitle2.replace("&＃40;", "(");
			}
			if(blogTitle2.contains("&＃41;")){
				blogTitle2=blogTitle2.replace("&＃41;", ")");
			}
			String blogNotice = request.getParameter("blogNotice");
			if (StringUtils.isNotEmpty(blogTitle)) {
				user.setBlogTitle(blogTitle);
			}
			if (StringUtils.isNotEmpty(blogTitle2)) {
				user.setBlogTitle2(blogTitle2);
			}
			if (StringUtils.isNotEmpty(blogNotice)) {
				user.setBlogNotice(blogNotice);
			}
			CmsUser u = cmsUserMng.updateBlog(user);
			model.addAttribute("user", u);
			FrontUtils.frontData(request, model, site);
		}
		try {
			request.getRequestDispatcher("/blog/index.jspx").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateSetting_refresh(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		JSONObject json = new JSONObject();
		if (null != user) {
			String blogTitle = request.getParameter("blogTitle");
			if(blogTitle.contains("&＃40;")){
				blogTitle=blogTitle.replace("&＃40;", "(");
			}
			if(blogTitle.contains("&＃41;")){
				blogTitle=blogTitle.replace("&＃41;", ")");
			}
			String blogTitle2 = request.getParameter("blogTitle2");
			if(blogTitle2.contains("&＃40;")){
				blogTitle2=blogTitle2.replace("&＃40;", "(");
			}
			if(blogTitle2.contains("&＃41;")){
				blogTitle2=blogTitle2.replace("&＃41;", ")");
			}
			String blogNotice = request.getParameter("blogNotice");
			if (StringUtils.isNotEmpty(blogTitle)) {
				user.setBlogTitle(blogTitle);
			}
			if (StringUtils.isNotEmpty(blogTitle2)) {
				user.setBlogTitle2(blogTitle2);
			}
			if (StringUtils.isNotEmpty(blogNotice)) {
				user.setBlogNotice(blogNotice);
			}
			try {
				cmsUserMng.updateBlog(user);
				json.put("status","0");
			} catch (Exception e) {
				json.put("status","1");
				e.printStackTrace();
			}
			FrontUtils.frontData(request, model, site);
			ResponseUtils.renderJson(response, json.toString());
		}else{
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String tzsetting(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return "/WEB-INF/t/cms/www/default/blog/login.html";
		}
		return "/WEB-INF/t/cms/www/default/blog/blog_setting_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogSetting");
	}
	
	public String blog_list(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		if (user == null) {
			
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return FrontUtils.showLogin(request, model, site);
			}
		}
		recieveUserId = user.getId();
		int userId=user.getId();
		String joinGroupStata=request.getParameter("joinGroupStata");
		Integer columnId = null;
		Integer channelId = null;
		if(joinGroupStata!=null&&joinGroupStata.equals("0")){
			model.addAttribute("GroupFlag", -1);
			userId=0;
			if(null != request.getParameter("columnId")){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("joinGroupStata", 0);
				model.addAttribute("submitOn1", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
		}else{
			model.addAttribute("GroupFlag", 0);
			//为了删除文章后能跳转回本栏目下
			if(null != request.getParameter("columnId")){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("submitOn", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
			if(null != request.getParameter("channelId")){
				model.addAttribute("channelId", request.getParameter("channelId"));
				model.addAttribute("submitOn", 1);
				channelId = Integer.parseInt(request.getParameter("channelId"));
			}
		}
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    int totalCount = blogCommon.getTotalArticleNum(model,user);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
 		model = contentMng.getStickList(user,model);
 		// model.addAttribute("channelId", 1);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,userId, cpn(pageNo), 20,columnId,channelId,recieveUserId);
		//p.setTotalCount(totalCount);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("channelId", 1);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	public String blog_list_own(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		
		if (user == null) {
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return "/WEB-INF/t/cms/www/default/blog/login.html";
			}
		}
		recieveUserId = user.getId();
		int userId=user.getId();
		String joinGroupStata=request.getParameter("joinGroupStata");
		Integer columnId = null;
		Integer channelId = null;
		if(joinGroupStata!=null&&joinGroupStata.equals("0")){
			model.addAttribute("GroupFlag", -1);
			userId=0;
			if(StringUtils.isNotBlank(request.getParameter("columnId"))){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("joinGroupStata", 0);
				model.addAttribute("submitOn1", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
		}else{
			model.addAttribute("GroupFlag", 0);
			//为了删除文章后能跳转回本栏目下
			if(StringUtils.isNotBlank(request.getParameter("columnId"))){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("submitOn", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
			if(StringUtils.isNotBlank(request.getParameter("channelId"))){
				model.addAttribute("channelId", request.getParameter("channelId"));
				model.addAttribute("submitOn", 1);
				channelId = Integer.parseInt(request.getParameter("channelId"));
			}
		}
 		// model.addAttribute("channelId", 1);
		FrontUtils.frontData(request, model, site);
		model = contentMng.getStickList(user,model);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,userId, cpn(pageNo), 20,columnId,channelId,recieveUserId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("channelId", 1);
		}
		return "/WEB-INF/t/cms/www/default/blog/contribute_list_own.html";
	}
	
	
	public String blog_add(boolean hasPermission,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    int totalCount = blogCommon.getTotalArticleNum(model,user);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
		if(hasPermission){
			model.addAttribute("site", site);
			model.addAttribute("sessionId",request.getSession().getId());
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, nextUrl);
		}else{
			WebErrors errors = WebErrors.create(request);
			errors.addErrorCode("error.uploadMoreNumber", user.getGroup().getAllowFileTotal());
			return FrontUtils.showError(request, response, model, errors);
		}
	}
	
	public String blog_ajax_add(boolean hasPermission,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			//return FrontUtils.showLogin(request, model, site);
			return "/WEB-INF/t/cms/www/default/blog/login.html";
		}
//		model = blogCommon.getColumn(request,model,user);
//	    model = blogCommon.getChannel(request,model,user,site);
// 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		if(hasPermission){
			model.addAttribute("site", site);
			model.addAttribute("sessionId",request.getSession().getId());
			return "/WEB-INF/t/cms/www/default/blog/contribute_add_own.html";
		}else{
			WebErrors errors = WebErrors.create(request);
			errors.addErrorCode("error.uploadMoreNumber", user.getGroup().getAllowFileTotal());
			return FrontUtils.showError(request, response, model, errors);
		}
	}
	
	
	
	 public static boolean containsEmoji(String source) {
		 if(StringUtils.isNotBlank(source)){
		   int len = source.length();
	        for (int i = 0; i < len; i++) {
	            char codePoint = source.charAt(i);
	            if (!notisEmojiCharacter(codePoint)) {
	            //判断确认有表情字符
	            	return true;
	            }
	        }
		 }
	        return false;
	 }

	 
	   private static boolean notisEmojiCharacter(char codePoint) {
	        return (codePoint == 0x0) || 
	                (codePoint == 0x9) ||                            
	                (codePoint == 0xA) ||
	                (codePoint == 0xD) ||
	                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
	                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
	                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	    }
	 
	public static String filterEmoji(String source) { 
     if(source != null)
     {
         Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
         Matcher emojiMatcher = emoji.matcher(source);
         if ( emojiMatcher.find())
         {
             source = emojiMatcher.replaceAll("");
             return source ;
         }
     return source;
    }
    return source; 
 }
	
	public String blog_ajaxsave(String title, String author, String description,
			String txt, String tagStr, Integer channelId,Integer columnId,Integer modelId,ContentDoc doc,
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,String password,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,Integer sta) {
				CmsSite site = CmsUtils.getSite(request);
				CmsUser user = CmsUtils.getUser(request);
				FrontUtils.frontData(request, model, site);
			if (user == null) {
				return "login";
				//return FrontUtils.showLogin(request, model, site);
			}
			txt=filterEmoji(txt);
			   if(containsEmoji(txt)){
				   return "emoji";
			   }
		Content c = new Content();
		WebErrors errors = validateSaves(title, author, description, txt,doc,
				tagStr,site, user, captcha, request, response,mediaPath,attachmentPaths);
		if (errors.hasErrors()) {
			//return FrontUtils.showError(request, response, model, errors);
			return "fail";
		}
		c.setSite(site);
		CmsModel defaultModel=cmsModelMng.getDefModel();
		
		int groupId = user.getGroup().getId();//学科教研模板，市县教研内容模板
		if (4 == groupId&&sta!=1) {
			modelId = 11;//学科教研
		} else if (5 == groupId&&sta!=1) {
			modelId = 21;//市县教研
		} else {
			modelId = 24;//普通博客
		}
		CmsModel m=cmsModelMng.findById(modelId);
		if(m!=null){
			c.setModel(m);
		}else{
			c.setModel(defaultModel);
		}
		ContentExt ext = new ContentExt();
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setTxt(txt);
		ContentType type = contentTypeMng.getDef();
		if (type == null) {
			//throw new RuntimeException("Default ContentType not found.");
			return "fail";
		}
		Integer typeId = type.getId();
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		if(c.getRecommendLevel()==null){
			c.setRecommendLevel((byte) 0);
		}
		try {
			c = contentMng.blog_save(c, ext, t,null, null, null, null, tagArr,
					attachmentPaths,attachmentNames, attachmentFilenames
					,picPaths,picDescs,channelId,columnId, typeId, null,true,
					charge,chargeAmount, user, true,password,request);
		} catch (Exception e) {
			log.error("**********************blogsave  error", e);
			return "fail";
		}
		if(doc!=null){
			contentDocMng.save(doc, c);
		}
		List<Content> list=new ArrayList<Content>();
		List<Content> listRedis=new ArrayList<Content>();
		if(c!=null&&channelId!=null){
			//往redis存储
			Integer parentId=null;
			String parentIds=null;
			Channel ids = channelMng.findById(channelId);//获取该栏目实体
			//根据ids.getParentId()判断该栏目是否为只有一级栏目
			if(null!=ids.getParentId()){
				parentId=ids.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
				//获取上一级栏目id
				Channel idss = channelMng.findById(parentId);
				//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
				if(null!=idss.getParentId()){
					//获取上上一级栏目id(一级栏目)
					parentIds=String.valueOf(idss.getParentId().toString());
				}
			}
			//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 且为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
			if(null!=parentId){
				//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
				if(null!=parentIds){
					//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
					try {
						list=RedisUtil.getList(String.valueOf(parentId));
						if(list!=null&&list.size()>0){
							list.add(c);
						}else{
							List<Content> listCopy=new ArrayList<Content>();
							listCopy.add(c);
							list=listCopy;
						}
						if(list!=null&&list.size()>1){
							Collections.sort(list, new Comparator<Content>(){ 
								public int compare(Content o1, Content o2) {  
									//进行降序排列  
									if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
										return 1;  
									}
									if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
										return 0;  
									}  
									return -1;  
									
								}  
							}); 
						} 
						for(int i=0;i<list.size();i++){
							if(list.size()>8){
								if((i+1)<=8){
									listRedis.add(list.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(list.get(i));
							}
						}
						RedisUtil.setList(String.valueOf(parentId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
					}
				}else{
					//只有二级栏目就把该栏目的ID 作为key也就是传进来的栏目id
					try {
						list=RedisUtil.getList(String.valueOf(parentId));
						if(list!=null&&list.size()>0){
							list.add(c);
						}else{
							List<Content> listCopy=new ArrayList<Content>();
							listCopy.add(c);
							list=listCopy;
						}
						if(list!=null&&list.size()>1){
							Collections.sort(list, new Comparator<Content>(){ 
								public int compare(Content o1, Content o2) {  
									//进行降序排列  
									if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
										return 1;  
									}
									if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
										return 0;  
									}  
									return -1;  
									
								}  
							}); 
						} 
						for(int i=0;i<list.size();i++){
							if(list.size()>8){
								if((i+1)<=8){
									listRedis.add(list.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(list.get(i));
							}
						}
						RedisUtil.setList(String.valueOf(parentId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
					}
				}
			}else{
				//只有1级栏目就把该栏目的ID 作为key
				try {
					list=RedisUtil.getList(String.valueOf(channelId));
					if(list!=null&&list.size()>0){
						list.add(c);
					}else{
						List<Content> listCopy=new ArrayList<Content>();
						listCopy.add(c);
						list=listCopy;
					}
					if(list!=null&&list.size()>1){
						Collections.sort(list, new Comparator<Content>(){ 
							public int compare(Content o1, Content o2) {  
								//进行降序排列  
								if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
									return 1;  
								}
								if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
									return 0;  
								}  
								return -1;  
								
							}  
						}); 
					}
					for(int i=0;i<list.size();i++){
						if(channelId.toString().equals("75")&&typeId.toString().equals("2")){
							listRedis.add(list.get(i));
						}else{
							if(list.size()>8){
								if((i+1)<=8){
									listRedis.add(list.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(list.get(i));
							}
						}
					}
					RedisUtil.setList(String.valueOf(channelId), listRedis);
				} catch (Exception e) {
					log.error("redis存储异常....", e);
				}
			}
			
		}else{
			if(user.getUsername().equals("9038")||user.getUsername().equals("9036")||user.getUsername().equals("9018")
				||user.getUsername().equals("9059")||user.getUsername().equals("9039")||user.getUsername().equals("609227")
				||user.getUsername().equals("674311")){
				if(user.getUsername().equals("9038")){
					//课程改革
					channelId=102;
				}else if(user.getUsername().equals("9036")){
					//教育科研
					channelId=99;
				}else if(user.getUsername().equals("9018")){
					//职业教育
					channelId=100;
				}else if(user.getUsername().equals("9059")){
					//质量监测
					channelId=103;
				}else if(user.getUsername().equals("9039")){
					//教师培训
					channelId=282;
				}else if(user.getUsername().equals("609227")){
					//党群建设
					channelId=260;
				}else if(user.getUsername().equals("674311")){
					//政策研究
					channelId=177;
				}
				try {
					list=RedisUtil.getList(String.valueOf(channelId));
					if(list!=null&&list.size()>0){
						list.add(c);
					}else{
						List<Content> listCopy=new ArrayList<Content>();
						listCopy.add(c);
						list=listCopy;
					}
					if(list!=null&&list.size()>1){
						Collections.sort(list, new Comparator<Content>(){ 
							public int compare(Content o1, Content o2) {  
								//进行降序排列  
								if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
									return 1;  
								}
								if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
									return 0;  
								}  
								return -1;  
								
							}  
						}); 
					}
					for(int i=0;i<list.size();i++){
						if(list.size()>8){
							if((i+1)<=8){
								listRedis.add(list.get(i));
							}else{
								break;
							}
						}else{
							listRedis.add(list.get(i));
						}
					}
					RedisUtil.setList(String.valueOf(channelId), listRedis);
				} catch (Exception e) {
					log.error("redis存储异常....", e);
				}
			}
		}
		return "success";
	}
	public String blog_save(String title, String author, String description,
			String txt, String tagStr, Integer channelId,Integer columnId,Integer modelId,ContentDoc doc,
			String captcha,String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			Short charge,Double chargeAmount,String password,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,Integer sta) {
				CmsSite site = CmsUtils.getSite(request);
				CmsUser user = CmsUtils.getUser(request);
				FrontUtils.frontData(request, model, site);
			if (user == null) {
				return FrontUtils.showLogin(request, model, site);
			}

		Content c = new Content();
		WebErrors errors = validateSaves(title, author, description, txt,doc,
				tagStr,site, user, captcha, request, response,mediaPath,attachmentPaths);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		c.setSite(site);
		CmsModel defaultModel=cmsModelMng.getDefModel();
		
		int groupId = user.getGroup().getId();//学科教研模板，市县教研内容模板
		if (4 == groupId&&sta!=1) {
			modelId = 11;//学科教研
		} else if (5 == groupId&&sta!=1) {
			modelId = 21;//市县教研
		} else {
			modelId = 24;//普通博客
		}
		CmsModel m=cmsModelMng.findById(modelId);
		if(m!=null){
			c.setModel(m);
		}else{
			c.setModel(defaultModel);
		}
		ContentExt ext = new ContentExt();
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setTxt(txt);
		ContentType type = contentTypeMng.getDef();
		if (type == null) {
			throw new RuntimeException("Default ContentType not found.");
		}
		Integer typeId = type.getId();
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		if(c.getRecommendLevel()==null){
			c.setRecommendLevel((byte) 0);
		}
		try {
			c = contentMng.blog_save(c, ext, t,null, null, null, null, tagArr,
					attachmentPaths,attachmentNames, attachmentFilenames
					,picPaths,picDescs,channelId,columnId, typeId, null,true,
					charge,chargeAmount, user, true,password,request);
		} catch (Exception e) {
			log.error("**********************blogsave  error", e);
			e.printStackTrace();
		}
		if(doc!=null){
			contentDocMng.save(doc, c);
		}
		if(c!=null&&channelId!=null){
			//往redis存储
			List<Content> list=new ArrayList<Content>();
			List<Content> listRedis=new ArrayList<Content>();
			Integer parentId=null;
			String parentIds=null;
			Channel ids = channelMng.findById(channelId);//获取该栏目实体
			//根据ids.getParentId()判断该栏目是否为只有一级栏目
			if(null!=ids.getParentId()){
				parentId=ids.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
				//获取上一级栏目id
				Channel idss = channelMng.findById(parentId);
				//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
				if(null!=idss.getParentId()){
					//获取上上一级栏目id(一级栏目)
					parentIds=String.valueOf(idss.getParentId().toString());
				}
			}
			//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 且为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
			if(null!=parentId){
				//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
				if(null!=parentIds){
					//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
					try {
						list=RedisUtil.getList(String.valueOf(parentId));
						if(list!=null&&list.size()>0){
							list.add(c);
						}else{
							List<Content> listCopy=new ArrayList<Content>();
							listCopy.add(c);
							list=listCopy;
						}
						if(list!=null&&list.size()>1){
							Collections.sort(list, new Comparator<Content>(){ 
								public int compare(Content o1, Content o2) {  
									//进行降序排列  
									if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
										return 1;  
									}
									if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
										return 0;  
									}  
									return -1;  
									
								}  
							}); 
						} 
						for(int i=0;i<list.size();i++){
							if(list.size()>8){
								if((i+1)<=8){
									listRedis.add(list.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(list.get(i));
							}
						}
						RedisUtil.setList(String.valueOf(parentId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
					}
				}else{
					//只有二级栏目就把该栏目的ID 作为key也就是传进来的栏目id
					try {
						list=RedisUtil.getList(String.valueOf(channelId));
						if(list!=null&&list.size()>0){
							list.add(c);
						}else{
							List<Content> listCopy=new ArrayList<Content>();
							listCopy.add(c);
							list=listCopy;
						}
						if(list!=null&&list.size()>1){
							Collections.sort(list, new Comparator<Content>(){ 
								public int compare(Content o1, Content o2) {  
									//进行降序排列  
									if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
										return 1;  
									}
									if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
										return 0;  
									}  
									return -1;  
									
								}  
							}); 
						} 
						for(int i=0;i<list.size();i++){
							if(list.size()>8){
								if((i+1)<=8){
									listRedis.add(list.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(list.get(i));
							}
						}
						RedisUtil.setList(String.valueOf(channelId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
					}
				}
			}else{
				//只有1级栏目就把该栏目的ID 作为key
				try {
					list=RedisUtil.getList(String.valueOf(channelId));
					if(list!=null&&list.size()>0){
						list.add(c);
					}else{
						List<Content> listCopy=new ArrayList<Content>();
						listCopy.add(c);
						list=listCopy;
					}
					if(list!=null&&list.size()>1){
						Collections.sort(list, new Comparator<Content>(){ 
							public int compare(Content o1, Content o2) {  
								//进行降序排列  
								if(o1.getSortDate().getTime() < o2.getSortDate().getTime()){  
									return 1;  
								}
								if(o1.getSortDate().getTime() == o2.getSortDate().getTime()){  
									return 0;  
								}  
								return -1;  
								
							}  
						}); 
					}
					for(int i=0;i<list.size();i++){
						if(channelId.toString().equals("75")&&typeId.toString().equals("2")){
							listRedis.add(list.get(i));
						}else{
							if(list.size()>8){
								if((i+1)<=8){
									listRedis.add(list.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(list.get(i));
							}
						}
					}
					RedisUtil.setList(String.valueOf(channelId), listRedis);
				} catch (Exception e) {
					log.error("redis存储异常....", e);
				}
			}
			
		}
		return "redirect:../blog/index.jspx";
	}
	
	public String blog_edit(Integer id, String nextUrl,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    int totalCount = blogCommon.getTotalArticleNum(model,user);
	    model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
	/*	WebErrors errors = validateEdit(id, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}*/
		Content content = contentMng.findById(id);
		model.addAttribute("content", content);
		model.addAttribute("site", site);
		model.addAttribute("sessionId",request.getSession().getId());
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_BLOG, nextUrl);
	}
	
	public String blog_ajax_edit(Integer id, String nextUrl,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		
		if (user == null) {
			return "/WEB-INF/t/cms/www/default/blog/login.html";
			//return FrontUtils.showLogin(request, model, site);
		}
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		Content content = contentMng.findById(id);
		model.addAttribute("content", content);
		model.addAttribute("site", site);
		model.addAttribute("sessionId",request.getSession().getId());
		
		return "/WEB-INF/t/cms/www/default/blog/contribute_edit_own.html";
		
	}
	public void blog_update(Integer id, String title, String author,
			String description, String txt, String tagStr,Integer columnId, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			ContentDoc doc,Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String password) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FrontUtils.frontData(request, model, site);
		if(null != columnId){
			channelId = 280;
			Content bean=contentMng.findById(id);
			List<Content> list=new ArrayList<Content>();
			Integer parentId=null;
			String parentIds=null;
			Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
			//根据ids.getParentId()判断该栏目是否为只有一级栏目
			if(null!=idDel.getParentId()){
				parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
				//获取上一级栏目id
				Channel idss = channelMng.findById(parentId);
				//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
				if(null!=idss.getParentId()){
					//获取上上一级栏目id(一级栏目)
					parentIds=String.valueOf(idss.getParentId().toString());
				}
			}
			//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
			if(null!=parentId){
				//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
				if(null!=parentIds){
					//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
					RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
				}else{
					//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
					RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
				}
			}else{
				//只有1级栏目就把该栏目的ID 作为key
				RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
			}
		}
		if(null != channelId){
			Content bean=contentMng.findById(id);
			List<Content> list=new ArrayList<Content>();
			Integer parentId=null;
			String parentIds=null;
			Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
			//根据ids.getParentId()判断该栏目是否为只有一级栏目
			if(null!=idDel.getParentId()){
				parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
				//获取上一级栏目id
				Channel idss = channelMng.findById(parentId);
				//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
				if(null!=idss.getParentId()){
					//获取上上一级栏目id(一级栏目)
					parentIds=String.valueOf(idss.getParentId().toString());
				}
			}
			//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
			if(null!=parentId){
				//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
				if(null!=parentIds){
					//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
					RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
				}else{
					//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
					RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
				}
			}else{
				//只有1级栏目就把该栏目的ID 作为key
				RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
			}
		}
		
//		String columnId = request.getParameter("columnId");
		/*MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		WebErrors errors = validateUpdate(id, channelId, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}*/
		CmsModel defaultModel=cmsModelMng.getDefModel();
		Content c = new Content();
		Integer modelId=null;
		int groupId = user.getGroup().getId();//学科教研模板，市县教研内容模板
		int sta=0;
		if(channelId!=null&&!channelId.toString().equals("280")){
			sta=1;
		}
		if (4 == groupId&&sta==1) {
			modelId = 11;//学科教研
		} else if (5 == groupId&&sta==1) {
			modelId = 21;//市县教研
		} else {
			modelId = 24;//普通博客
		}
		CmsModel m=cmsModelMng.findById(modelId);
		if(m!=null){
			c.setModel(m);
		}else{
			c.setModel(defaultModel);
		}
		c.setId(id);
		c.setSite(site);
		c.setPassword(password);
		ContentExt ext = new ContentExt();
		ext.setId(id);
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setId(id);
		t.setTxt(txt);
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		c=contentMng.blog_update(c, ext, t,null, tagArr, null, null, null, 
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs, null, columnId,channelId, null, null, 
				charge,chargeAmount,user, true,request);
		contentDao.updateContentSend(title,id);//修改发送的中间表标题
		if(doc!=null){
			contentDocMng.update(doc, c);
		}
		channelId=c.getChannel().getId();
		if(channelId!=null){
			//更新redis存储
			if(!channelId.toString().equals("280")){
				List<Content> list=new ArrayList<Content>();
				List<Content> listCopy=new ArrayList<Content>();
				List<Content> listRedis=new ArrayList<Content>();
				Integer parentId=null;
				String parentIds=null;
				Channel ids = channelMng.findById(channelId);//获取该栏目实体
				//根据ids.getParentId()判断该栏目是否为只有一级栏目
				if(null!=ids.getParentId()){
					parentId=ids.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
					//获取上一级栏目id
					Channel idss = channelMng.findById(parentId);
					//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
					if(null!=idss.getParentId()){
						//获取上上一级栏目id(一级栏目)
						parentIds=String.valueOf(idss.getParentId().toString());
					}
				}
				//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 且为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
				if(null!=parentId){
					//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
					if(null!=parentIds){
						//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
						try {
							list=RedisUtil.getList(String.valueOf(parentId));
							int ck=0;//主要目的是为了修改文章的时候将文章修改到学科教研,市县教研和普通栏目redis移除和添加
							if(list!=null&&list.size()>0){
								for(int i=0;i<list.size();i++){
									if(c.getId().toString().equals(list.get(i).getId().toString())){
										ck=1;//走到这说明修改文章时候市县教研或者学科教研redis已经存在该文章做修改处理.
										listCopy.add(c);
									}else{
										if(ck==0){
											listCopy.add(c);//走到这说明是普通栏目下的文章修改到市县教研或者学科教研下.做添加处理
											ck=2;//解决重复添加
										}
										listCopy.add(list.get(i));
									}
								}
							}else{
								listCopy.add(c);
							}
							for(int i=0;i<listCopy.size();i++){
								if(listCopy.size()>8){
									if((i+1)<=8){
										listRedis.add(listCopy.get(i));
									}else{
										break;
									}
								}else{
									listRedis.add(listCopy.get(i));
								}
							}
							RedisUtil.setList(String.valueOf(parentId), listRedis);
						} catch (Exception e) {
							log.error("redis存储异常....", e);
						}
					}else{
						//只有二级栏目就把该栏目的ID 作为key也就是传进来的栏目id
						try {
							list=RedisUtil.getList(String.valueOf(channelId));
							int ck=0;
							if(list!=null&&list.size()>0){
								for(int i=0;i<list.size();i++){
									if(c.getId().toString().equals(list.get(i).getId().toString())){
										ck=1;
										listCopy.add(c);
									}else{
										if(ck==0){
											listCopy.add(c);
											ck=2;//解决重复添加
										}
										listCopy.add(list.get(i));
									}
								}
							}else{
								listCopy.add(c);
							}
							for(int i=0;i<listCopy.size();i++){
								if(listCopy.size()>8){
									if((i+1)<=8){
										listRedis.add(listCopy.get(i));
									}else{
										break;
									}
								}else{
									listRedis.add(listCopy.get(i));
								}
							}
							RedisUtil.setList(String.valueOf(channelId), listRedis);
						} catch (Exception e) {
							log.error("redis存储异常....", e);
						}
					}
				}else{
					//只有1级栏目就把该栏目的ID 作为key
					try {
						list=RedisUtil.getList(String.valueOf(channelId));
						int ck=0;
						if(list!=null&&list.size()>0){
							for(int i=0;i<list.size();i++){
								if(c.getId().toString().equals(list.get(i).getId().toString())){
									ck=1;
									listCopy.add(c);
								}else{
									if(ck==0){
										listCopy.add(c);
										ck=2;//解决重复添加
									}
									listCopy.add(list.get(i));
								}
							}
						}else{
							listCopy.add(c);
						}
						for(int i=0;i<listCopy.size();i++){
							if(channelId.toString().equals("75")&&c.getType().getId().toString().equals("2")){
								listRedis.add(listCopy.get(i));
							}else{
								if(listCopy.size()>8){
									if((i+1)<=8){
										listRedis.add(listCopy.get(i));
									}else{
										break;
									}
								}else{
									listRedis.add(listCopy.get(i));
								}
							}
						}
						RedisUtil.setList(String.valueOf(channelId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
					}
				}
			}
				
			}
		
		try {
			response.sendRedirect("../blog/index.jspx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String blog_ajax_update(Integer id, String title, String author,
			String description, String txt, String tagStr,Integer columnId, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			ContentDoc doc,Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String password) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
				//request.getRequestDispatcher("/login.jspx").forward(request, response);
				return "login";
		}
		   txt=filterEmoji(txt);
		   if(containsEmoji(txt)){
			   return "emoji";
		   }

		FrontUtils.frontData(request, model, site);
		if(null != columnId){
			Content bean=contentMng.findById(id);
			List<Content> list=new ArrayList<Content>();
			if(user.getUsername().equals("9038")||user.getUsername().equals("9036")||user.getUsername().equals("9018")
					||user.getUsername().equals("9059")||user.getUsername().equals("9039")||user.getUsername().equals("609227")
					||user.getUsername().equals("674311")){
				channelId = 280;
				String channelIds=null;
				if(user.getUsername().equals("9038")){
					//课程改革
					channelIds="102";
				}else if(user.getUsername().equals("9036")){
					//教育科研
					channelIds="99";
				}else if(user.getUsername().equals("9018")){
					//职业教育
					channelIds="100";
				}else if(user.getUsername().equals("9059")){
					//质量监测
					channelIds="103";
				}else if(user.getUsername().equals("9039")){
					//教师培训
					channelIds="282";
				}else if(user.getUsername().equals("609227")){
					//党群建设
					channelIds="260";	
				}else if(user.getUsername().equals("674311")){
					//党群建设
					channelIds="177";	
				}
				try {
					RedisUtil.lrem(channelIds, 0, bean.getId().toString(),list);
				} catch (Exception e) {
					log.error("redis中栏目"+channelIds+"数据移除异常",e);
					e.printStackTrace();
				}				
			}else{
				channelId = 280;
				Integer parentId=null;
				String parentIds=null;
				Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
				//根据ids.getParentId()判断该栏目是否为只有一级栏目
				if(null!=idDel.getParentId()){
					parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
					//获取上一级栏目id
					Channel idss = channelMng.findById(parentId);
					//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
					if(null!=idss.getParentId()){
						//获取上上一级栏目id(一级栏目)
						parentIds=String.valueOf(idss.getParentId().toString());
					}
				}
				//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
				if(null!=parentId){
					//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
					if(null!=parentIds){
						//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
						try {
							RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
						} catch (Exception e) {
							log.error("redis中栏目"+parentId.toString()+"数据移除异常",e);
							e.printStackTrace();
						}
					}else{
						//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
						try {
							RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
						} catch (Exception e) {
							log.error("redis中栏目"+parentId.toString()+"数据移除异常",e);
							e.printStackTrace();
						}
					}
				}else{
					//只有1级栏目就把该栏目的ID 作为key
					try {
						RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
					} catch (Exception e) {
						log.error("redis中栏目"+bean.getChannel().getId().toString()+"数据移除异常",e);
						e.printStackTrace();
					}
				}
			}
		}
		if(null != channelId&&channelId!=280){
			Content bean=contentMng.findById(id);
			List<Content> list=new ArrayList<Content>();
			Integer parentId=null;
			String parentIds=null;
			Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
			//根据ids.getParentId()判断该栏目是否为只有一级栏目
			if(null!=idDel.getParentId()){
				parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
				//获取上一级栏目id
				Channel idss = channelMng.findById(parentId);
				//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
				if(null!=idss.getParentId()){
					//获取上上一级栏目id(一级栏目)
					parentIds=String.valueOf(idss.getParentId().toString());
				}
			}
			//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
			if(null!=parentId){
				//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
				if(null!=parentIds){
					//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
					try {
						RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
					} catch (Exception e) {
						log.error("redis中栏目"+parentId.toString()+"数据移除异常",e);
						e.printStackTrace();
					}
				}else{
					//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
					try {
						RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
					} catch (Exception e) {
						log.error("redis中栏目"+parentId.toString()+"数据移除异常",e);
						e.printStackTrace();
					}
				}
			}else{
				//只有1级栏目就把该栏目的ID 作为key
				try {
					RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
				} catch (Exception e) {
					log.error("redis中栏目"+bean.getChannel().getId().toString()+"数据移除异常",e);
					e.printStackTrace();
				}
			}
		}
		
//		String columnId = request.getParameter("columnId");
		/*MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		WebErrors errors = validateUpdate(id, channelId, site, user, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}*/
		CmsModel defaultModel=cmsModelMng.getDefModel();
		Content c = new Content();
		Integer modelId=null;
		int groupId = user.getGroup().getId();//学科教研模板，市县教研内容模板
		int sta=0;
		if(channelId!=null&&!channelId.toString().equals("280")){
			sta=1;
		}
		if (4 == groupId&&sta==1) {
			modelId = 11;//学科教研
		} else if (5 == groupId&&sta==1) {
			modelId = 21;//市县教研
		} else {
			modelId = 24;//普通博客
		}
		CmsModel m=cmsModelMng.findById(modelId);
		if(m!=null){
			c.setModel(m);
		}else{
			c.setModel(defaultModel);
		}
		c.setId(id);
		c.setSite(site);
		c.setPassword(password);
		ContentExt ext = new ContentExt();
		ext.setId(id);
		ext.setTitle(title);
		ext.setAuthor(author);
		ext.setDescription(description);
		ext.setMediaPath(mediaPath);
		ext.setMediaType(mediaType);
		ContentTxt t = new ContentTxt();
		t.setId(id);
		t.setTxt(txt);
		String[] tagArr = StrUtils.splitAndTrim(tagStr, ",", null);
		c=contentMng.blog_update(c, ext, t,null, tagArr, null, null, null, 
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs, null, columnId,channelId, null, null, 
				charge,chargeAmount,user, true,request);
		contentDao.updateContentSend(title,id);//修改发送的中间表标题
		contentDao.updateContentStick(title, id);//修改置顶表的标题
		if(doc!=null){
			contentDocMng.update(doc, c);
		}
		channelId=c.getChannel().getId();
		List<Content> list=new ArrayList<Content>();
		List<Content> listCopy=new ArrayList<Content>();
		List<Content> listRedis=new ArrayList<Content>();
		if(channelId!=null){
			//更新redis存储
			if(!channelId.toString().equals("280")){
				Integer parentId=null;
				String parentIds=null;
				Channel ids = channelMng.findById(channelId);//获取该栏目实体
				//根据ids.getParentId()判断该栏目是否为只有一级栏目
				if(null!=ids.getParentId()){
					parentId=ids.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
					//获取上一级栏目id
					Channel idss = channelMng.findById(parentId);
					//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
					if(null!=idss.getParentId()){
						//获取上上一级栏目id(一级栏目)
						parentIds=String.valueOf(idss.getParentId().toString());
					}
				}
				//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 且为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
				if(null!=parentId){
					//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
					if(null!=parentIds){
						//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
						try {
							list=RedisUtil.getList(String.valueOf(parentId));
							int ck=0;//主要目的是为了修改文章的时候将文章修改到学科教研,市县教研和普通栏目redis移除和添加
							if(list!=null&&list.size()>0){
								for(int i=0;i<list.size();i++){
									if(c.getId().toString().equals(list.get(i).getId().toString())){
										ck=1;//走到这说明修改文章时候市县教研或者学科教研redis已经存在该文章做修改处理.
										listCopy.add(c);
									}else{
										if(ck==0){
											listCopy.add(c);//走到这说明是普通栏目下的文章修改到市县教研或者学科教研下.做添加处理
											ck=2;//解决重复添加
										}
										listCopy.add(list.get(i));
									}
								}
							}else{
								listCopy.add(c);
							}
							for(int i=0;i<listCopy.size();i++){
								if(listCopy.size()>8){
									if((i+1)<=8){
										listRedis.add(listCopy.get(i));
									}else{
										break;
									}
								}else{
									listRedis.add(listCopy.get(i));
								}
							}
							RedisUtil.setList(String.valueOf(parentId), listRedis);
						} catch (Exception e) {
							log.error("redis存储异常....", e);
							//return "faile";
						}
					}else{
						//只有二级栏目就把该栏目的ID 作为key也就是传进来的栏目id
						try {
							list=RedisUtil.getList(String.valueOf(parentId));
							int ck=0;
							if(list!=null&&list.size()>0){
								for(int i=0;i<list.size();i++){
									if(c.getId().toString().equals(list.get(i).getId().toString())){
										ck=1;
										listCopy.add(c);
									}else{
										if(ck==0){
											listCopy.add(c);
											ck=2;//解决重复添加
										}
										listCopy.add(list.get(i));
									}
								}
							}else{
								listCopy.add(c);
							}
							for(int i=0;i<listCopy.size();i++){
								if(listCopy.size()>8){
									if((i+1)<=8){
										listRedis.add(listCopy.get(i));
									}else{
										break;
									}
								}else{
									listRedis.add(listCopy.get(i));
								}
							}
							RedisUtil.setList(String.valueOf(parentId), listRedis);
						} catch (Exception e) {
							log.error("redis存储异常....", e);
							//return "faile";
						}
					}
				}else{
					//只有1级栏目就把该栏目的ID 作为key
					try {
						list=RedisUtil.getList(String.valueOf(channelId));
						int ck=0;
						if(list!=null&&list.size()>0){
							for(int i=0;i<list.size();i++){
								if(c.getId().toString().equals(list.get(i).getId().toString())){
									ck=1;
									listCopy.add(c);
								}else{
									if(ck==0){
										listCopy.add(c);
										ck=2;//解决重复添加
									}
									listCopy.add(list.get(i));
								}
							}
						}else{
							listCopy.add(c);
						}
						for(int i=0;i<listCopy.size();i++){
							if(channelId.toString().equals("75")&&c.getType().getId().toString().equals("2")){
								listRedis.add(listCopy.get(i));
							}else{
								if(listCopy.size()>8){
									if((i+1)<=8){
										listRedis.add(listCopy.get(i));
									}else{
										break;
									}
								}else{
									listRedis.add(listCopy.get(i));
								}
							}
						}
						RedisUtil.setList(String.valueOf(channelId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
						//return "faile";
					}
				}
			}else{
				if(user.getUsername().equals("9038")||user.getUsername().equals("9036")||user.getUsername().equals("9018")
						||user.getUsername().equals("9059")||user.getUsername().equals("9039")||user.getUsername().equals("609227")
						||user.getUsername().equals("674311")){
					
					if(user.getUsername().equals("9038")){
						//课程改革
						channelId=102;
					}else if(user.getUsername().equals("9036")){
						//教育科研
						channelId=99;
					}else if(user.getUsername().equals("9018")){
						//职业教育
						channelId=100;
					}else if(user.getUsername().equals("9059")){
						//质量监测
						channelId=103;
					}else if(user.getUsername().equals("9039")){
						//教师培训
						channelId=282;
					}else if(user.getUsername().equals("609227")){
						//党群建设
						channelId=260;
					}else if(user.getUsername().equals("674311")){
						//政策研究
						channelId=177;
					}
					try {
						list=RedisUtil.getList(String.valueOf(channelId));
						int ck=0;
						if(list!=null&&list.size()>0){
							for(int i=0;i<list.size();i++){
								if(c.getId().toString().equals(list.get(i).getId().toString())){
									ck=1;
									listCopy.add(c);
								}else{
									if(ck==0){
										listCopy.add(c);
										ck=2;//解决重复添加
									}
									listCopy.add(list.get(i));
								}
							}
						}else{
							listCopy.add(c);
						}
						for(int i=0;i<listCopy.size();i++){
							if(listCopy.size()>8){
								if((i+1)<=8){
									listRedis.add(listCopy.get(i));
								}else{
									break;
								}
							}else{
								listRedis.add(listCopy.get(i));
							}
						}
						RedisUtil.setList(String.valueOf(channelId), listRedis);
					} catch (Exception e) {
						log.error("redis存储异常....", e);
					}
				}
			}
				
			}
		
		return "success";
	}
	public void blog_delete(Integer contentId,Integer columnId,Integer channelId, HttpServletRequest request,
			  HttpServletResponse response, ModelMap model) {
			int id;
			CmsUser user = CmsUtils.getUser(request);
			if (user == null) {
				try {
					request.getRequestDispatcher("/login.jspx").forward(request, response);
					return;
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		try {
			if(null != columnId){
				contentMng.deleteByIdBlog(contentId);
				String joinGroupStata=request.getParameter("joinGroupStata");
				id = columnId;
				if(joinGroupStata!=null&&joinGroupStata.equals("0")){
					response.sendRedirect("../blog/contribute_list.jspx?columnId="+id+"&joinGroupStata=0");
					//request.getRequestDispatcher("../blog/contribute_list.jspx?columnId="+id+"&joinGroupStata=0").forward(request, response);
				}else{
					response.sendRedirect("../blog/contribute_list.jspx?columnId="+id);
					//request.getRequestDispatcher("../blog/contribute_list.jspx?columnId="+id).forward(request, response);
				}
			}else if(null != channelId){
				Content bean=contentMng.findById(contentId);
				List<Content> list=new ArrayList<Content>();
				Integer parentId=null;
				String parentIds=null;
				Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
				//根据ids.getParentId()判断该栏目是否为只有一级栏目
				if(null!=idDel.getParentId()){
					parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
					//获取上一级栏目id
					Channel idss = channelMng.findById(parentId);
					//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
					if(null!=idss.getParentId()){
						//获取上上一级栏目id(一级栏目)
						parentIds=String.valueOf(idss.getParentId().toString());
					}
				}
				//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
				if(null!=parentId){
					//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
					if(null!=parentIds){
						//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
						RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
					}else{
						//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
						RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
					}
				}else{
					//只有1级栏目就把该栏目的ID 作为key
					RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
				}
				contentMng.deleteByIdBlog(contentId);
            	id = channelId;
            	response.sendRedirect("../blog/contribute_list.jspx?channelId="+id);
				//request.getRequestDispatcher("../blog/contribute_list.jspx?channelId="+id).forward(request, response);
			}else{
				Content bean=contentMng.findById(contentId);
				List<Content> list=new ArrayList<Content>();
				Integer parentId=null;
				String parentIds=null;
				Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
				if(!bean.getChannel().getId().toString().equals("280")){
					//根据ids.getParentId()判断该栏目是否为只有一级栏目
					if(null!=idDel.getParentId()){
						parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
						//获取上一级栏目id
						Channel idss = channelMng.findById(parentId);
						//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
						if(null!=idss.getParentId()){
							//获取上上一级栏目id(一级栏目)
							parentIds=String.valueOf(idss.getParentId().toString());
						}
					}
					//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
					if(null!=parentId){
						//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
						if(null!=parentIds){
							//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
							RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
						}else{
							//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
							RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
						}
					}else{
						//只有1级栏目就把该栏目的ID 作为key
						RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
					}
				}
				contentMng.deleteByIdBlog(contentId);
				response.sendRedirect("../blog/index.jspx");
				//request.getRequestDispatcher("../blog/index.jspx").forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String ajax_blog_delete(Integer contentId, HttpServletRequest request,
			  HttpServletResponse response, ModelMap model) {
		String result ="success";
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return "login";
		}
		try {
			//删除博客
			if(null!=contentId&&contentId.toString()!=""){
				Content bean=contentMng.findById(contentId);
				List<Content> list=new ArrayList<Content>();
				if(user.getUsername().equals("9038")||user.getUsername().equals("9036")||user.getUsername().equals("9018")
						||user.getUsername().equals("9059")||user.getUsername().equals("9039")||user.getUsername().equals("609227")
						||user.getUsername().equals("674311")){
					String channelId = null;
					if(user.getUsername().equals("9038")){
						//课程改革
						channelId="102";
					}else if(user.getUsername().equals("9036")){
						//教育科研
						channelId="99";
					}else if(user.getUsername().equals("9018")){
						//职业教育
						channelId="100";
					}else if(user.getUsername().equals("9059")){
						//质量监测
						channelId="103";
					}else if(user.getUsername().equals("9039")){
						//教师培训
						channelId="282";
					}else if(user.getUsername().equals("609227")){
						//党群建设
						channelId="260";
					}else if(user.getUsername().equals("674311")){
						//党群建设
						channelId="177";
					}
					try {
						RedisUtil.lrem(channelId, 0, bean.getId().toString(),list);
					} catch (Exception e) {
						log.error("redis栏目"+channelId+"数据移除异常",e);
						e.printStackTrace();
					}
				}else{
					if(!bean.getChannel().getId().toString().equals("280")){
						Integer parentId=null;
						String parentIds=null;
						Channel idDel = channelMng.findById(bean.getChannel().getId());//获取该栏目实体
						//根据ids.getParentId()判断该栏目是否为只有一级栏目
						if(null!=idDel.getParentId()){
							parentId=idDel.getParentId();//走到这说明该传进来的栏目ID 存在上一级栏目-二级栏目
							//获取上一级栏目id
							Channel idss = channelMng.findById(parentId);
							//再根据上一级栏目ID--parentId 来判读是否存在上上一级栏目--一级栏目(已知只有三级栏目,无序继续往上上上一级判断(4级栏目))
							if(null!=idss.getParentId()){
								//获取上上一级栏目id(一级栏目)
								parentIds=String.valueOf(idss.getParentId().toString());
							}
						}
						//判断parentId是否为null.如果为null则说明传进来的栏目ID 只有一级栏目 切为本身.如果不为null则说明传进来的栏目ID存在上一级栏目
						if(null!=parentId){
							//判断parentIds是否为null.如果为null则说明传进来的栏目ID 只有两级栏目.如果不为null则说明传进来的栏目ID存在上三级栏目
							if(null!=parentIds){
								//只有三级栏目就把该栏目的上一级栏目ID 作为key 也就是parentId
								try {
									RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
								} catch (Exception e) {
									log.error("redis栏目"+parentId.toString()+"数据移除异常",e);
									e.printStackTrace();
								}
							}else{
								//只有二级栏目就把该栏目的上一级栏目ID 作为key也就是传进来的栏目id
								try {
									RedisUtil.lrem(parentId.toString(), 0, bean.getId().toString(),list);
								} catch (Exception e) {
									log.error("redis栏目"+parentId.toString()+"数据移除异常",e);
									e.printStackTrace();
								}
							}
						}else{
							//只有1级栏目就把该栏目的ID 作为key
							try {
								RedisUtil.lrem(bean.getChannel().getId().toString(), 0, bean.getId().toString(),list);
							} catch (Exception e) {
								log.error("redis栏目"+bean.getChannel().getId().toString()+"数据移除异常",e);
								e.printStackTrace();
							}
						}
					}
				}
				contentMng.deleteByIdBlog(contentId);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result="fail";
		}
		return result;
	}
	
	public String blog_columns_list(String q, Integer modelId,Integer queryChannelId,
			String nextUrl,Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return "/WEB-INF/t/cms/www/default/blog/login.html";
		}
		String ccid=request.getParameter("ccId");
		if(StringUtils.isNotEmpty(ccid)){
			model.addAttribute("ccId", ccid);
		}
		model = blogCommon.getColumn(request,model,user);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return "/WEB-INF/t/cms/www/default/blog/columns_list_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, nextUrl);
	}
	
	public String columns_add(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		String parentId = request.getParameter("tcolumnInput");
		String name = request.getParameter("columnInput");
		if(name.contains("&＃40;")){
			name=name.replace("&＃40;", "(");
		}
		if(name.contains("&＃41;")){
			name=name.replace("&＃41;", ")");
		}
		Integer columsLevel=1;
		Columns cnew=null;
		if(StringUtils.isEmpty(parentId)){
			//如果父级栏目为空则该栏目作为一级栏目,且parentId(Columns)为null
			columsLevel=1;
			cnew=null;
		}else{
			//如果父级栏目不为空则该栏目作为二级栏目,且parentId(Columns)不为null
			columsLevel=2;
			cnew=columnsMng.findById(Integer.valueOf(parentId));
		}
		String order = request.getParameter("columnOrder");
		String uniqueCode = request.getParameter("uniqueCode");
		if(StringUtils.isEmpty(uniqueCode)){
			uniqueCode=null;
		}
		Integer i = 0;
		if(null != name && null != user){
			if(blogCommon.isNumeric(order)){
				i = Integer.parseInt(order);
			}
			Columns c = new Columns(user.getId(),name,i,uniqueCode,1,columsLevel,cnew);
			columnsMng.addColumns(c);
		}
		return "redirect:../blog/index.jspx";
	}
	
	
	public void columns_add_refresh(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException{
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			String parentId = request.getParameter("tcolumnInput");
			String name = request.getParameter("columnInput");
			if(name.contains("&＃40;")){
				name=name.replace("&＃40;", "(");
			}
			if(name.contains("&＃41;")){
				name=name.replace("&＃41;", ")");
			}
			Integer columsLevel=1;
			Columns cnew=null;
			if(StringUtils.isEmpty(parentId)){
				//如果父级栏目为空则该栏目作为一级栏目,且parentId(Columns)为null
				columsLevel=1;
				cnew=null;
			}else{
				//如果父级栏目不为空则该栏目作为二级栏目,且parentId(Columns)不为null
				columsLevel=2;
				cnew=columnsMng.findById(Integer.valueOf(parentId));
			}
			String order = request.getParameter("columnOrder");
			String uniqueCode = request.getParameter("uniqueCode");
			if(StringUtils.isEmpty(uniqueCode)){
				uniqueCode=null;
			}
			Integer i = 0;
			Columns cl=null;
			if(null != name && null != user){
				if(blogCommon.isNumeric(order)){
					i = Integer.parseInt(order);
				}
				Columns c = new Columns(user.getId(),name,i,uniqueCode,1,columsLevel,cnew);
				cl=columnsMng.addColumns(c);
				
			}
			if(null!=cl){
				//添加成功
				json.put("status",cl.getColumnId());
			}else{
				json.put("status","1");
			}
		} catch (Exception e) {
			//添加失败
			json.put("status","1");
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	public void columns_updates_refresh(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			String columnId = request.getParameter("id");
			String columsLevel=request.getParameter("columsLevel");
			String name = request.getParameter("updateName");
			if(name.contains("&＃40;")){
				name=name.replace("&＃40;", "(");
			}
			if(name.contains("&＃41;")){
				name=name.replace("&＃41;", ")");
			}
			String orderId = request.getParameter("updateOrderId");
			String uniqueCode = request.getParameter("uniqueCode");
			String parentIds = request.getParameter("parentId");
			Columns parentId=null;
			if(StringUtils.isEmpty(parentIds)){
				parentIds=null;
			}else{
				parentId=columnsMng.findById(Integer.valueOf(parentIds));
			}
			if(StringUtils.isEmpty(uniqueCode)){
				uniqueCode=null;
			}
			FrontUtils.frontData(request, model, site);
			Integer i = 0;
			if(null != name && null != user){
				if(blogCommon.isNumeric(orderId)){
					i = Integer.parseInt(orderId);
				}
				Columns c = new Columns(Integer.valueOf(columnId),user.getId(),name,i,uniqueCode,1,Integer.valueOf(columsLevel),parentId);
				columnsMng.updateColumns(c);
			}
			json.put("status","0");
		} catch (Exception e) {
			//添加失败
			json.put("status","1");
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	

	public void columns_query(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject json = new JSONObject(); 
		String columnId = request.getParameter("id");
		if(null != columnId){
			List<Content> list = contentMng.countByColumnId(Integer.parseInt(columnId));
			if(null == list || list.size()<1){
				try {
					json.put("status", "true");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				try {
					json.put("status", "false");
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	public void columns_delete_refresh(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject(); 
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			String columnId = request.getParameter("columnId");
			columnsMng.deleteColumns(Integer.parseInt(columnId));
			json.put("status","0");
		} catch (NumberFormatException e) {
			//添加失败
			json.put("status","1");
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	
	public String columns_delete(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		String columnId = request.getParameter("columnId");
		columnsMng.deleteColumns(Integer.parseInt(columnId));
		return "redirect:../blog/index.jspx";
	}
	
	public String columns_update(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		String columnId = request.getParameter("id");
		String columsLevel=request.getParameter("columsLevel");
		String name = request.getParameter("updateName");
		if(name.contains("&＃40;")){
			name=name.replace("&＃40;", "(");
		}
		if(name.contains("&＃41;")){
			name=name.replace("&＃41;", ")");
		}
		String orderId = request.getParameter("updateOrderId");
		String uniqueCode = request.getParameter("uniqueCode");
		String parentIds = request.getParameter("parentId");
		Columns parentId=null;
		if(StringUtils.isEmpty(parentIds)){
			parentIds=null;
		}else{
			parentId=columnsMng.findById(Integer.valueOf(parentIds));
		}
		if(StringUtils.isEmpty(uniqueCode)){
			uniqueCode=null;
		}
		FrontUtils.frontData(request, model, site);
		Integer i = 0;
		if(null != name && null != user){
			if(blogCommon.isNumeric(orderId)){
				i = Integer.parseInt(orderId);
			}
			Columns c = new Columns(Integer.valueOf(columnId),user.getId(),name,i,uniqueCode,1,Integer.valueOf(columsLevel),parentId);
			columnsMng.updateColumns(c);
		}
		return "redirect:../blog/index.jspx";
	}
	
	public String update_tz(String id,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return "/WEB-INF/t/cms/www/default/blog/login.html";
		}
		Columns column = columnsMng.findById(Integer.parseInt(id));
		List<Columns> twoList=columnsMng.findTwoByParentId(Integer.valueOf(id));
		if(null!=twoList&&twoList.size()>0){
			model.addAttribute("twoState", 1);
			if(null!=column.getParentId()){
				//该栏目为一级栏目 有二级栏目 展示二级栏目的序号
				twoList=columnsMng.findTwoByParentId(column.getParentId().getColumnId());
				model.addAttribute("twoList", twoList);
			}else{
				//该栏目只有一级栏目 展示用户下所有栏目 包括二级
				model = blogCommon.getColumn(request,model,user);
			}
		}else{
			//该栏目只有一级栏目 展示用户下所有栏目 包括二级
			if(null !=column.getParentId()){
				twoList=columnsMng.findTwoByParentId(column.getParentId().getColumnId());
				model.addAttribute("twoList", twoList);
			}else{
				model = blogCommon.getColumn(request,model,user);
			}
			model.addAttribute("twoState", 0);
		}
		int joinStatus = columnsMng.delGroup(id,user);
		model.addAttribute("joinStatus", joinStatus);
		model.addAttribute("column", column);
		return "/WEB-INF/t/cms/www/default/blog/columns_update_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.columnsUpdate");
	}

	public void add_update_refresh(String hyperlink,String nextUrl,HttpServletRequest request, HttpServletResponse response,ModelMap model) throws JSONException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		FrontUtils.frontData(request, model, site);
		try {
			channelMng.updateLinkUrl(hyperlink,user);
			json.put("status", "0");
		} catch (Exception e) {
			json.put("status", "1");
			e.printStackTrace();
		}
		FrontUtils.frontData(request, model, site);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	public String link_save(String hyperlink,String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		channelMng.updateLinkUrl(hyperlink,user);
		FrontUtils.frontData(request, model, site);
		return "redirect:../blog/index.jspx";
	}
	
	public void add_friends_refresh(String friends, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) throws JSONException {
		CmsUser user = CmsUtils.getUser(request);
		JSONObject json = new JSONObject();
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			channelMng.updateFriends(friends,user);
			json.put("status", "0");
		} catch (Exception e) {
			json.put("status", "1");
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}

	public void friends_save(String friends, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		channelMng.updateFriends(friends,user);
		try {
			response.sendRedirect("../blog/gotoDataShow.jspx?dataFlag=1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String friendCenter(String userIds,String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		/*if(user==null){
			return FrontUtils.showLogin(request, model, site);
		}*/
		CmsUser userT=null;
		if(request.getParameter("name")!=null){
			String username=request.getParameter("name");
			CmsUser uname=cmsUserMng.findByUsername(username);
			if(uname!=null){
				userT=cmsUserMng.findById(uname.getId());
			}else{
				return FrontUtils.showMessage(request, model, "该账号暂未开通博客,或者请检查输入的账号是否正确!"); 
			}
		}else{
			try {
				if(null!=userIds&&userIds!=""){
					boolean result=userIds.matches("[0-9]+");
					if (result) { 
						userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
					}
				}else{
					return FrontUtils.showMessage(request, model, "好友不存在，或地址有误！"); 
				}
			} catch (NumberFormatException e) {
				log.error("empty===="+userIds);
				e.printStackTrace();
			}
		}
		channelMng.updateBlogVisitNum(userT);
		if(user!=null){
			recieveUserId = null;
			if(!user.getId().equals(userT.getId())){
				channelMng.updateBlogVisitorTime(user,userT);
			}
		}
		model =blogCommon.getHyperlink(request,model,userT);
		model = blogCommon.getColumn(request,model,userT);
		model = blogCommon.getChannel(request,model,userT,site);
		int totalCount = blogCommon.getTotalArticleNum(model,userT);
		model.addAttribute("articleCount", totalCount);
		model = blogCommon.getTotalCommentNum(model, userT);
		model = blogCommon.getStarBlogger(request, model);
		model = blogCommon.getAlreadyJoinGroup(request, model,userT);
		model = blogCommon.getAddFriends(request, model,userT,user);
		model = blogCommon.getFouces(request, model,userT,user);
		model = blogCommon.getFriendLeft(userT.getId(),model,1);
		model = contentMng.getStickList(userT,model);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_firendsBlog(Integer.valueOf(userT.getId()),q, queryChannelId,site.getId(), modelId,null, cpn(pageNo), 20,null,recieveUserId);
		//p.setTotalCount(totalCount);
		model.addAttribute("pagination", p);
		model.addAttribute("GroupFlag", 0);
		model.addAttribute("usert", userT);
		model.addAttribute("userIds", userT.getId());
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		model.addAttribute("GroupStata", 0);
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}

	public String blog_list_friend(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser u = CmsUtils.getUser(request);
		CmsUser user =null;
		Integer recieveUserId = null;
		if(u!=null){
			recieveUserId = u.getId();
		}
		String user_ids = request.getParameter("user_ids");
		try {
			if(null!=user_ids&&user_ids!=""){
				boolean result=user_ids.matches("[0-9]+");
				if (result) { 
					user=cmsUserMng.findById(Integer.valueOf(user_ids.toString()));
				}
			}else{
				return FrontUtils.showMessage(request, model, "浏览器出错了，请刷新再试试！"); 
			}
		} catch (NumberFormatException e) {
			log.error("empty===="+user_ids);
			e.printStackTrace();
		}
//		String user_ids = request.getParameter("user_ids");
//		CmsUser user=cmsUserMng.findById(Integer.valueOf(user_ids.toString()));
		String joinGroupStata = request.getParameter("joinGroupStata");
		int userId=user.getId();
		Integer columnId = null;
		Integer channelId = null;
		if(joinGroupStata!=null&&joinGroupStata.equals("0")){
			userId=0;
			model.addAttribute("GroupFlag", -1);
			if(StringUtils.isNotBlank(request.getParameter("columnId"))&&!"null".equals(request.getParameter("columnId"))){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("joinGroupStata", 0);
				model.addAttribute("submitOn1", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
		}else{
			model.addAttribute("GroupFlag", 0);
			//为了删除文章后能跳转回本栏目下
			if(StringUtils.isNotBlank(request.getParameter("columnId"))&&!"null".equals(request.getParameter("columnId"))){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("submitOn", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
			if(StringUtils.isNotBlank(request.getParameter("channelId"))&&!"null".equals(request.getParameter("channelId"))){
				model.addAttribute("channelId", request.getParameter("channelId"));
				model.addAttribute("submitOn", 1);
				channelId = Integer.parseInt(request.getParameter("channelId"));
			}
		}
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.getChannel(request,model,user,site);
		int totalCount = blogCommon.getTotalArticleNum(model,user);
		model.addAttribute("articleCount", totalCount);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogCommon.getAddFriends(request, model,user,u);
 		model = blogCommon.getFouces(request, model,user,u);
 		model = blogCommon.getFriendLeft(user.getId(),model,1);
 		model = contentMng.getStickList(user,model);
		model.addAttribute("usert", user);
		model.addAttribute("userIds", user.getId());
		//model.addAttribute("columnId", columnId);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,userId, cpn(pageNo), 20,columnId,channelId,recieveUserId);
		//p.setTotalCount(totalCount);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	public void blog_focus(String focusUserId,String focusUserName, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String focusTime = format.format(date);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			try {
				request.getRequestDispatcher("/login.jspx").forward(request, response);
				return;
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Focus f = focusMng.add(user.getId(),user.getUsername(), Integer.parseInt(focusUserId), focusUserName, focusTime);
		if(null != f){
			response.getWriter().print("1");
		}else{
			response.getWriter().print("0");
		}
	}
	
	
	public void blog_focus_check(String focusUserId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		CmsUser user = CmsUtils.getUser(request);
		if (user != null) {
			List<Focus> list= focusMng.find(user.getId(), Integer.parseInt(focusUserId));
			if(null != list && list.size()>0){
				response.getWriter().print("1");
			}else{
				response.getWriter().print("0");
			}
		}
	}
	
	
	public void blog_cancel_focus(String focusUserId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return;
		}
		Focus f = focusMng.delete(user.getId(), Integer.parseInt(focusUserId));
		if(null != f ){
			response.getWriter().print("1");
		}else{
			response.getWriter().print("0");
		}
	}
	
	public ModelMap getAllVisitors(String q, Integer modelId,
			Integer queryChannelId, Integer pageNo, HttpServletRequest request,
			ModelMap model, CmsUser user) {
		if(user!=null){
			Pagination visitorList = contentMng.getPageForMember_visitor(cpn(pageNo), 24,user);
			model.addAttribute("pagination", visitorList);
		}else{
			model.addAttribute("pagination", "");
		}
		return model;
	}
	
	public String gotoDataShow(int dataFlag, HttpServletRequest request,HttpServletResponse response, ModelMap model,Integer pageNo) {
		CmsUser u = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		if(u==null){
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				u=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", u);
			}else{
				return "/WEB-INF/t/cms/www/default/blog/login.html";
			}
		}
		//好友
		if(1 == dataFlag){
			model = blogCommon.getFriends(u.getId(),model,cpn(pageNo));
			model.addAttribute("dataFlag", 1);
		//关注	
		}else if(2 == dataFlag){
			model = blogCommon.getBlogFocus(u.getId(),model,cpn(pageNo));
			model.addAttribute("dataFlag", 2);
		//粉丝	
		}else if(3 == dataFlag){
			model = blogCommon.getBlogFans(u.getId(),model,cpn(pageNo));
			model.addAttribute("dataFlag", 3);
		//明星博主	
		}else if(4 == dataFlag){
			model = blogCommon.getMoreStarBlogger(request,model,cpn(pageNo));
			model.addAttribute("dataFlag", 4);
		}
		model.addAttribute("checkPageInfo", 0);
		model.addAttribute("q", "");
		FrontUtils.frontData(request, model, site);
		return "/WEB-INF/t/cms/www/default/blog/data_show_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.dataShow");
	}
	
	public String gotoDataShowFriend(int dataFlag, HttpServletRequest request,HttpServletResponse response, ModelMap model,Integer pageNo,String userId) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		CmsUser u =cmsUserMng.findById( Integer.parseInt(userId));
		model.addAttribute("userIds", userId);
		model.addAttribute("usert", u);
		//好友
		if(1 == dataFlag){
			model = blogCommon.getFriends(u.getId(),model,cpn(pageNo));
			model.addAttribute("dataFlag", 1);
		//关注	
		}else if(2 == dataFlag){
			model = blogCommon.getBlogFocus(u.getId(),model,cpn(pageNo));
			model.addAttribute("dataFlag", 2);
		//粉丝	
		}else if(3 == dataFlag){
			model = blogCommon.getBlogFans(u.getId(),model,cpn(pageNo));
			model.addAttribute("dataFlag", 3);
		//明星博主	
		}else if(4 == dataFlag){
			model = blogCommon.getMoreStarBlogger(request,model,cpn(pageNo));
			model.addAttribute("dataFlag", 4);
		}
		model.addAttribute("checkPageInfo", 1);
		FrontUtils.frontData(request, model, site);
		return "/WEB-INF/t/cms/www/default/blog/friend_data_show_refresh.html";
		//return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.frienddataShow");
	}
	
	public String blogContentShow(String[] paths,String[] params,
			PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String columnId){
		Content content = contentMng.findById(Integer.parseInt(paths[1]));
		if (content == null) {
			log.debug("Content id not found: {}", paths[1]);
			return FrontUtils.pageNotFound(request, response, model);
		}
		CmsSite site = content.getSite();
		CmsUser u = CmsUtils.getUser(request);
		if(u==null){
			//return FrontUtils.showLogin(request, model, site);
			u=cmsUserMng.findById(content.getUser().getId());
			model.addAttribute("usert", u);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		config.getConfigAttr().getPreview();
		Set<CmsGroup> groups = content.getViewGroupsExt();
		//groups.size();
		
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		try {
			txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
			Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
			model.addAttribute("pagination", pagination);
			FrontUtils.frontPageData(request, model);
			model.addAttribute("content", content);
			model.addAttribute("channel", content.getChannel());
			model.addAttribute("title", content.getTitleByNo(pageNo));
			model.addAttribute("txt", txt);
			model.addAttribute("columnIdZ", columnId);
			model.addAttribute("columnId", columnId);
			model.addAttribute("pic", content.getPictureByNo(pageNo));
			String collection = request.getParameter("collection");
			
			//置顶下一页上一页参数
			String currentId = request.getParameter("currentId");//当前文章的id
			if(StringUtils.isNotBlank(currentId)){
				String stickUserId = request.getParameter("stickUserId");//当前文章的id
				model=blogCommon.getPreNextStick(model, Integer.parseInt(currentId),Integer.parseInt(stickUserId) );
			
			}
			
			String d = request.getParameter("d");
			if(null != collection || "2".equals(d)){//转载文章显示
				model.addAttribute("collection", 1);
				model.addAttribute("uId", u.getId());
			}else{
				String GroupFlag = request.getParameter("GroupFlag");
				if("-1".equals(GroupFlag)){
					model.addAttribute("GroupFlagData", -1);
				}else{
					model.addAttribute("GroupFlagData", content.getUser().getId());
				}
			}
			model = blogCommon.getAlreadyJoinGroup(request, model,u);
			try {
				model = blogCommon.getChannel(request,model,u,site);
			} catch (Exception e) {
				log.error("blogContentShow.getChannel error", e);
			}
			model =blogCommon.getHyperlink(request,model,u);
			model = blogCommon.getColumn(request,model,u);
			int totalCount = blogCommon.getTotalArticleNum(model,u);
			model.addAttribute("articleCount", totalCount);
			model = blogCommon.getTotalCommentNum(model, u);
			model = blogCommon.getStarBlogger(request, model);
			model = blogCommon.getFriendLeft(u.getId(),model,1);
			
			model = blogCommon.getContentSendType(model,Integer.parseInt(paths[1]),u);
			model = blogCommon.getStickId(model, Integer.parseInt(paths[1]), u);
		} catch (Exception e) {
			log.error("blogContentShow error", e);
		}
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogContentShow");
	}
	
	public String blogContentShowOwn(String[] paths,String[] params,
			PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String columnId){
		Content content = contentMng.findById(Integer.parseInt(paths[1]));
		if (content == null) {
			log.debug("Content id not found: {}", paths[1]);
			return FrontUtils.pageNotFound(request, response, model);
		}
		CmsSite site = content.getSite();
		CmsUser u = CmsUtils.getUser(request);
		if(u==null){
			//return FrontUtils.showLogin(request, model, site);
			u=cmsUserMng.findById(content.getUser().getId());
			model.addAttribute("usert", u);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		config.getConfigAttr().getPreview();
		Set<CmsGroup> groups = content.getViewGroupsExt();
		//groups.size();
		
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		try {
			txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
			Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
			model.addAttribute("pagination", pagination);
			FrontUtils.frontPageData(request, model);
			model.addAttribute("content", content);
			model.addAttribute("channel", content.getChannel());
			model.addAttribute("title", content.getTitleByNo(pageNo));
			model.addAttribute("txt", txt);
			model.addAttribute("columnIdZ", columnId);
			model.addAttribute("columnId", columnId);
			model.addAttribute("pic", content.getPictureByNo(pageNo));
			String collection = request.getParameter("collection");
			
			//置顶下一页上一页参数
			String currentId = request.getParameter("currentId");//当前文章的id
			if(StringUtils.isNotBlank(currentId)){
				String stickUserId = request.getParameter("stickUserId");//当前文章的id
				model=blogCommon.getPreNextStick(model, Integer.parseInt(currentId),Integer.parseInt(stickUserId) );
			
			}
			
			String d = request.getParameter("d");
			if(null != collection || "2".equals(d)){//转载文章显示
				model.addAttribute("collection", 1);
				model.addAttribute("uId", u.getId());
			}else{
				String GroupFlag = request.getParameter("GroupFlag");
				if("-1".equals(GroupFlag)){
					model.addAttribute("GroupFlagData", -1);
				}else{
					model.addAttribute("GroupFlagData", content.getUser().getId());
				}
			}
			//model = blogCommon.getAlreadyJoinGroup(request, model,u);
			/*try {
				model = blogCommon.getChannel(request,model,u,site);
			} catch (Exception e) {
				log.error("blogContentShow.getChannel error", e);
			}*/
			/*model =blogCommon.getHyperlink(request,model,u);
			model = blogCommon.getColumn(request,model,u);
			int totalCount = blogCommon.getTotalArticleNum(model,u);
			model.addAttribute("articleCount", totalCount);
			model = blogCommon.getTotalCommentNum(model, u);
			model = blogCommon.getStarBlogger(request, model);
			model = blogCommon.getFriendLeft(u.getId(),model,1);*/
			
			model = blogCommon.getContentSendType(model,Integer.parseInt(paths[1]),u);
			model = blogCommon.getStickId(model, Integer.parseInt(paths[1]), u);
		} catch (Exception e) {
			log.error("blogContentShow error", e);
		}
		FrontUtils.frontData(request, model, site);
		return "/WEB-INF/t/cms/www/default/blog/blog_content_show_own.html";
	}
	
	public String blogContentShowFriend(String[] paths,String[] params,
			PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String friend,String columnId){
			Content content = contentMng.findById(Integer.parseInt(paths[1]));
			if (content == null) {
				log.debug("Content id not found: {}", paths[1]);
				return FrontUtils.pageNotFound(request, response, model);
			}
			Integer pageCount=content.getPageCount();
			if(pageNo>pageCount||pageNo<0){
				return FrontUtils.pageNotFound(request, response, model);
			}
			//非终审文章
			CmsConfig config=CmsUtils.getSite(request).getConfig();
			config.getConfigAttr().getPreview();
			//CmsUser u = CmsUtils.getUser(request);
			if("2".equals(friend)){
				friend = request.getParameter("utId");
				model.addAttribute("collection", 1);
			}
			CmsUser user=cmsUserMng.findById(Integer.valueOf(friend.toString()));
			CmsSite site = content.getSite();
			Set<CmsGroup> groups = content.getViewGroupsExt();
			groups.size();
			
			String txt = content.getTxtByNo(pageNo);
			// 内容加上关键字
			txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
			Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
			model.addAttribute("pagination", pagination);
			FrontUtils.frontPageData(request, model);
			model.addAttribute("content", content);
			model.addAttribute("channel", content.getChannel());
			model.addAttribute("title", content.getTitleByNo(pageNo));
			model.addAttribute("txt", txt);
			model.addAttribute("columnIdZ", columnId);
			model.addAttribute("columnId", columnId);
			model.addAttribute("pic", content.getPictureByNo(pageNo));
			model.addAttribute("password", content.getPassword());
			
				model.addAttribute("who",0);
				model.addAttribute("userIds", user.getId());
				model.addAttribute("usert", user);
			String collection = request.getParameter("collection");
			if(null != collection ){//转载文章显示
				model.addAttribute("collection", 1);
			}else{
				String GroupFlag = request.getParameter("GroupFlag");
				if("-1".equals(GroupFlag)){
					model.addAttribute("GroupFlagData", -1);
				}else{
					model.addAttribute("GroupFlagData", content.getUser().getId());
				}
			}
			CmsUser u = CmsUtils.getUser(request);
			model =blogCommon.getHyperlink(request,model,user);
			model = blogCommon.getAlreadyJoinGroup(request, model,user);
			model = blogCommon.getChannel(request,model,user,site);
			model = blogCommon.getColumn(request,model,user);
			model = blogCommon.getEmbodyColumn(model,u);
			int totalCount = blogCommon.getTotalArticleNum(model,user);
			model.addAttribute("articleCount", totalCount);
	 		model = blogCommon.getTotalCommentNum(model, user);
	 		model = blogCommon.getStarBlogger(request, model);
	 		model = blogCommon.getAddFriends(request, model,user,u);
	 		model = blogCommon.getFouces(request, model,user,u);
	 		model = blogCommon.getFriendLeft(user.getId(),model,1);
	 		
	 		
			FrontUtils.frontData(request, model, site);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogContentShowFriend");
		}

	@SuppressWarnings("unused")
	private WebErrors validateSave(String title, String author,
			String description, String txt,ContentDoc doc, String tagStr,
			CmsSite site, CmsUser user, String captcha,
			HttpServletRequest request, HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		/*try {
			if (!imageCaptchaService.validateResponseForID(session
					.getSessionId(request, response), captcha)) {
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			}
		} catch (CaptchaServiceException e) {
			errors.addErrorCode("error.exceptionCaptcha");
			return errors;
		}*/
		if (errors.ifBlank(title, "title", 50)) {
			return errors;
		}
		if (errors.ifMaxLength(author, "author", 60)) {
			return errors;
		}
		if (errors.ifMaxLength(description, "description", 255)) {
			return errors;
		}
		if(doc==null){
			// 内容不能大于1M
			if (errors.ifBlank(txt, "文章内容", 4194304)) {
				return errors;
			}
		}else{
			if(StringUtils.isBlank(doc.getDocPath())){
				errors.addErrorCode("error.hasNotUploadDoc");
				return errors;
			}
		}
		
		if (errors.ifMaxLength(tagStr, "tagStr", 255)) {
			return errors;
		}
		return errors;
	}
	private WebErrors validateSaves(String title, String author,
			String description, String txt,ContentDoc doc, String tagStr,
			CmsSite site, CmsUser user, String captcha,
			HttpServletRequest request, HttpServletResponse response,String mediaPath,String[] attachmentPaths) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(title, "title", 150)) {
			return errors;
		}
		if (errors.ifMaxLength(author, "author", 60)) {
			return errors;
		}
		if (errors.ifMaxLength(description, "description", 255)) {
			return errors;
		}
		if(doc==null){
			// 内容不能大于4M
			if(StringUtils.isEmpty(mediaPath)&&attachmentPaths==null){
				if (errors.ifBlank(txt, "文章内容", 4194304)) {
					return errors;
				}
			}
		}else{
			if(StringUtils.isBlank(doc.getDocPath())){
				errors.addErrorCode("error.hasNotUploadDoc");
				return errors;
			}
		}
		
		if (errors.ifMaxLength(tagStr, "tagStr", 255)) {
			return errors;
		}
		return errors;
	}
	
	public String blog_list_friends(String q, Integer modelId,Integer queryChannelId, Object object, Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser u = CmsUtils.getUser(request);
		Integer recieveUserId = null;
//		if(u!=null){
//			if((site.getId()).equals(u.getId())){
//				recieveUserId = u.getId();
//			}else{
//				recieveUserId = null;
//			}
//		}
		String user_ids = request.getParameter("user_ids");
		CmsUser user=cmsUserMng.findById(Integer.valueOf(user_ids.toString()));
		String joinGroupStata = request.getParameter("joinGroupStata");
		int userId=user.getId();
		Integer columnId = null;
		Integer channelId = null;
		if(joinGroupStata!=null&&joinGroupStata.equals("0")){
			userId=0;
			model.addAttribute("GroupFlag", -1);
			if(StringUtils.isNotBlank(request.getParameter("columnId"))&&!"null".equals(request.getParameter("columnId"))){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("joinGroupStata", 0);
				model.addAttribute("submitOn1", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
		}else{
			model.addAttribute("GroupFlag", 0);
			//为了删除文章后能跳转回本栏目下
			if(StringUtils.isNotBlank(request.getParameter("columnId"))&&!"null".equals(request.getParameter("columnId"))){
				model.addAttribute("columnId", request.getParameter("columnId"));
				model.addAttribute("columnIdZ", request.getParameter("columnId"));
				model.addAttribute("submitOn", 1);
				columnId = Integer.parseInt(request.getParameter("columnId"));
			}
			if(StringUtils.isNotBlank(request.getParameter("channelId"))&&!"null".equals(request.getParameter("channelId"))){
				model.addAttribute("channelId", request.getParameter("channelId"));
				model.addAttribute("submitOn", 1);
				channelId = Integer.parseInt(request.getParameter("channelId"));
			}
		}
		model.addAttribute("usert", user);
		model.addAttribute("userIds", user.getId());
		FrontUtils.frontData(request, model, site);
		model = contentMng.getStickList(user,model);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,userId, cpn(pageNo), 20,columnId,channelId,recieveUserId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return "/WEB-INF/t/cms/www/default/blog/contribute_list_friends.html";
	}
	
	public String find_all_url_friends(String userIds,String q,Integer modelId, Integer queryChannelId, String string,Integer pageNo, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		CmsUser userT=null;
		if(request.getParameter("name")!=null){
			String username=request.getParameter("name").substring(1, request.getParameter("name").length());
			CmsUser uname=cmsUserMng.findByUsername(username);
			if(uname!=null){
				userT=cmsUserMng.findById(uname.getId());
			}else{
				return FrontUtils.showMessage(request, model, "该账号暂未开通博客,或者请检查输入的账号是否正确!"); 
			}
		}else{
			userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		}
		channelMng.updateBlogVisitNum(userT);
		if(user!=null){
			recieveUserId = null;
			if(!user.getId().equals(userT.getId())){
				channelMng.updateBlogVisitorTime(user,userT);
			}
		}
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_firendsBlog(Integer.valueOf(userT.getId()),q, queryChannelId,site.getId(), modelId,null, cpn(pageNo), 20,null,recieveUserId);
		model.addAttribute("pagination", p);
		model.addAttribute("GroupFlag", 0);
		model.addAttribute("usert", userT);
		model.addAttribute("userIds", userT.getId());
		model = contentMng.getStickList(userT,model);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		model.addAttribute("GroupStata", 0);
		return "/WEB-INF/t/cms/www/default/blog/contribute_list_friends.html";
	}
	
	
	public String blogContentShare(String[] paths, String[] params,
			PageInfo info, int pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, String f,
			String columnId) {
		Content content = contentMng.findById(Integer.parseInt(paths[1]));
		if (content == null) {
			log.debug("Content id not found: {}", paths[1]);
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		config.getConfigAttr().getPreview();
		CmsSite site = content.getSite();
		if(site.getId()!=1){
			site=siteMng.findById(1);
		}
		Set<CmsGroup> groups = content.getViewGroupsExt();
		groups.size();
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
		/*Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
		model.addAttribute("pagination", pagination);*/
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		model.addAttribute("title", content.getTitleByNo(pageNo));
		model.addAttribute("txt", txt);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogContentShare");
	}
	
	public String blogContentEdu(String[] paths, String[] params,
			PageInfo info, int pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, String f,
			String columnId) {
		Content content = contentMng.findById(Integer.parseInt(paths[1]));
		if (content == null) {
			log.debug("Content id not found: {}", paths[1]);
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		config.getConfigAttr().getPreview();
		CmsSite site = content.getSite();
		if(site.getId()!=1){
			site=siteMng.findById(1);
		}
		Set<CmsGroup> groups = content.getViewGroupsExt();
		groups.size();
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		model.addAttribute("title", content.getTitleByNo(pageNo));
		model.addAttribute("txt", txt);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.t");
	}
	
	public String blogContentSharePc(String[] paths, String[] params,
			PageInfo info, int pageNo, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, String f,
			String columnId) {
		Content content = contentMng.findById(Integer.parseInt(paths[1]));
		if (content == null) {
			log.debug("Content id not found: {}", paths[1]);
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		config.getConfigAttr().getPreview();
		CmsSite site = content.getSite();
		if(site.getId()!=1){
			site=siteMng.findById(1);
		}
		Set<CmsGroup> groups = content.getViewGroupsExt();
		groups.size();
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
		/*Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
		model.addAttribute("pagination", pagination);*/
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		model.addAttribute("title", content.getTitleByNo(pageNo));
		model.addAttribute("txt", txt);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogContentSharePc");
	}
	public String blog_indexs(String q, Integer modelId,
			Integer queryChannelId, String string, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		if (user == null) {
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return "/WEB-INF/t/cms/www/default/blog/login.html";
				//return FrontUtils.showLoginBlog(request, model, site);
			}
		}
		recieveUserId = user.getId();
	    model = contentMng.getStickList(user,model);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,null,null,recieveUserId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return "/WEB-INF/t/cms/www/default/blog/contribute_list_indexs.html";
	}
	
	public String blog_indexstwo(String q, Integer modelId,
			Integer queryChannelId, String string, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer recieveUserId = null;
		if (user == null) {
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return "/WEB-INF/t/cms/www/default/blog/login.html";
				//return FrontUtils.showLoginBlog(request, model, site);
			}
		}
		recieveUserId = user.getId();
	    model = contentMng.getStickList(user,model);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,null,null,recieveUserId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return "/WEB-INF/t/cms/www/default/blog/contribute_list_indexstwo.html";
	}
	
	public String refreshColumn(String q, Integer modelId,
			Integer queryChannelId, String string, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			String uid=request.getParameter("uid");
			if(StringUtils.isNotEmpty(uid)){
				user=cmsUserMng.findById(Integer.parseInt(uid));
				model.addAttribute("usert", user);
			}else{
				return FrontUtils.showLoginBlog(request, model, site);
			}
		}
		model =blogCommon.getHyperlink(request,model,user);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getStarBlogger(request, model);
	    model = blogCommon.getAlreadyJoinGroup(request, model,user);
	    model = blogCommon.getFriendLeft(user.getId(),model,1);
		model = contentMng.getStickList(user,model);
		FrontUtils.frontData(request, model, site);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}else{
			model.addAttribute("q", "");
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return "/WEB-INF/t/cms/www/default/blog/blog_left_column.html";
	}
	
	public String blogContentLocalRefreshFriend(String[] paths,String[] params,
			PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String friend,String columnId){
			Content content = contentMng.findById(Integer.parseInt(paths[1]));
			if (content == null) {
				log.debug("Content id not found: {}", paths[1]);
				return FrontUtils.pageNotFound(request, response, model);
			}
			Integer pageCount=content.getPageCount();
			if(pageNo>pageCount||pageNo<0){
				return FrontUtils.pageNotFound(request, response, model);
			}
			//非终审文章
			CmsConfig config=CmsUtils.getSite(request).getConfig();
			config.getConfigAttr().getPreview();
			//CmsUser u = CmsUtils.getUser(request);
			if("2".equals(friend)){
				friend = request.getParameter("utId");
				model.addAttribute("collection", 1);
			}
			CmsUser user=cmsUserMng.findById(Integer.valueOf(friend.toString()));
			CmsSite site = content.getSite();
			Set<CmsGroup> groups = content.getViewGroupsExt();
			groups.size();
			
			String txt = content.getTxtByNo(pageNo);
			
			//置顶下一页上一页参数
			String currentId = request.getParameter("currentId");//当前文章的id
			if(StringUtils.isNotBlank(currentId)){
				String stickUserId = request.getParameter("stickUserId");//当前文章的id
				model=blogCommon.getPreNextStick(model, Integer.parseInt(currentId),Integer.parseInt(stickUserId) );
			
			}
			
			// 内容加上关键字
			txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
			Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
			model.addAttribute("pagination", pagination);
			FrontUtils.frontPageData(request, model);
			model.addAttribute("content", content);
			model.addAttribute("channel", content.getChannel());
			model.addAttribute("title", content.getTitleByNo(pageNo));
			model.addAttribute("txt", txt);
			model.addAttribute("columnIdZ", columnId);
			model.addAttribute("columnId", columnId);
			model.addAttribute("pic", content.getPictureByNo(pageNo));
			model.addAttribute("password", content.getPassword());
			model.addAttribute("who",0);
			model.addAttribute("userIds", user.getId());
			model.addAttribute("usert", user);
			CmsUser u = CmsUtils.getUser(request);
			model = blogCommon.getEmbodyColumn(model,u);
			String collection = request.getParameter("collection");
			if(null != collection ){//转载文章显示
				model.addAttribute("collection", 1);
			}else{
				String GroupFlag = request.getParameter("GroupFlag");
				if("-1".equals(GroupFlag)){
					model.addAttribute("GroupFlagData", -1);
				}else{
					model.addAttribute("GroupFlagData", content.getUser().getId());
				}
			}
			model = blogCommon.getContentSendType(model,Integer.parseInt(paths[1]),user);
			FrontUtils.frontData(request, model, site);
			return "/WEB-INF/t/cms/www/default/blog/blog_content_local_refresh_friend.html";
		}
	
	
	
	
	@Autowired
	protected ImageCaptchaService imageCaptchaService;
	@Autowired
	protected SessionProvider session;
    @Autowired
    private CmsKeywordMng cmsKeywordMng;
	@Autowired
	protected ColumnsMng columnsMng;
	@Autowired
	protected ContentDocMng contentDocMng;
	@Autowired
	protected ContentTypeMng contentTypeMng;
	@Autowired
	protected CmsModelMng cmsModelMng;
	@Autowired
	protected CmsUserMng cmsUserMng;
	@Autowired
	protected BlogCommon blogCommon;
	@Autowired
	protected ChannelMng channelMng;
	@Autowired
	protected FocusMng focusMng;
	@Autowired
	protected ContentMng contentMng;
	@Autowired
	protected ContentDao contentDao;
	@Autowired
	private CmsSiteMng siteMng;
	
}
