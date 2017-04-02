package com.jeecms.cms.action.blog;

import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.jeecms.cms.action.front.DynamicPageAct;
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
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.jeecms.core.web.util.URLHelper.PageInfo;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

public class BlogAct {
	private static final Logger log = LoggerFactory.getLogger(DynamicPageAct.class);
	public String blog_index(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
	    model = blogCommon.getStarBlogger(request, model);
	    model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,null,null);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
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
			String blogTitle2 = request.getParameter("blogTitle2");
			String blogNotice = request.getParameter("blogNotice");
			if (null != blogTitle || "" != blogTitle) {
				user.setBlogTitle(blogTitle);
			}
			if (null != blogTitle2 || "" != blogTitle2) {
				user.setBlogTitle2(blogTitle2);
			}
			if (null != blogNotice || "" != blogNotice) {
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
	
	public String tzsetting(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogSetting");
	}
	
	public String blog_list(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
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
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,userId, cpn(pageNo), 20,columnId,channelId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	public String blog_add(boolean hasPermission,String nextUrl,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		if(hasPermission){
			FrontUtils.frontData(request, model, site);
			model.addAttribute("site", site);
			model.addAttribute("sessionId",request.getSession().getId());
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, nextUrl);
		}else{
			WebErrors errors = WebErrors.create(request);
			errors.addErrorCode("error.uploadMoreNumber", user.getGroup().getAllowFileTotal());
			return FrontUtils.showError(request, response, model, errors);
		}
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
		WebErrors errors = validateSave(title, author, description, txt,doc,
				tagStr,site, user, captcha, request, response);
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
		c = contentMng.blog_save(c, ext, t,null, null, null, null, tagArr,
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs,channelId,columnId, typeId, null,true,
				charge,chargeAmount, user, true,password,request);
		if(doc!=null){
			contentDocMng.save(doc, c);
		}
		if(channelId!=null){
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
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
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
		FrontUtils.frontData(request, model, site);
		if(null != columnId){
			channelId = 280;
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
		Content c = new Content();
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
		if(doc!=null){
			contentDocMng.update(doc, c);
		}
		channelId=c.getChannel().getId();
		if(channelId!=null){
			//更新redis存储
			if(!channelId.toString().equals("280")){
				List<Content> list=new ArrayList<Content>();
				List<Content> listCopy=new ArrayList<Content>();
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
								for(int i=0;i<list.size();i++){
									if(c.getId().toString().equals(list.get(i).getId().toString())){
										listCopy.add(c);
									}else{
										listCopy.add(list.get(i));
									}
								}
							}
							RedisUtil.setList(String.valueOf(parentId), listCopy);
						} catch (Exception e) {
							log.error("redis存储异常....", e);
						}
					}else{
						//只有二级栏目就把该栏目的ID 作为key也就是传进来的栏目id
						try {
							list=RedisUtil.getList(String.valueOf(channelId));
							if(list!=null&&list.size()>0){
								for(int i=0;i<list.size();i++){
									if(c.getId().toString().equals(list.get(i).getId().toString())){
										listCopy.add(c);
									}else{
										listCopy.add(list.get(i));
									}
								}
							}
							RedisUtil.setList(String.valueOf(channelId), listCopy);
						} catch (Exception e) {
							log.error("redis存储异常....", e);
						}
					}
				}else{
					//只有1级栏目就把该栏目的ID 作为key
					try {
						list=RedisUtil.getList(String.valueOf(channelId));
						if(list!=null&&list.size()>0){
							for(int i=0;i<list.size();i++){
								if(c.getId().toString().equals(list.get(i).getId().toString())){
									listCopy.add(c);
								}else{
									listCopy.add(list.get(i));
								}
							}
						}
						RedisUtil.setList(String.valueOf(channelId), listCopy);
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
	
	public void blog_delete(Integer contentId,Integer columnId,Integer channelId, HttpServletRequest request,
			  HttpServletResponse response, ModelMap model) {
			int id;
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
	
	public String blog_columns_list(String q, Integer modelId,Integer queryChannelId,
			String nextUrl,Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model = blogCommon.getChannel(request,model,user,site);
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember(q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG, nextUrl);
	}
	
	public void columns_add(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
		}
		String name = request.getParameter("columnInput");
		String order = request.getParameter("columnOrder");
		String uniqueCode = request.getParameter("uniqueCode");
		if(uniqueCode==""){
			uniqueCode=null;
		}
		Integer i = 0;
		if(null != name && null != user){
			if(blogCommon.isNumeric(order)){
				i = Integer.parseInt(order);
			}
			Columns c = new Columns(user.getId(),name,i,uniqueCode);
			columnsMng.addColumns(c);
		}
		try {
			request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void columns_query(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
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
	
	public void columns_delete(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
		}
		String columnId = request.getParameter("columnId");
		columnsMng.deleteColumns(Integer.parseInt(columnId));
		try {
			request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void columns_update(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
		}
		String columnId = request.getParameter("id");
		String name = request.getParameter("updateName");
		String orderId = request.getParameter("updateOrderId");
		String uniqueCode = request.getParameter("uniqueCode");
		if(uniqueCode==""){
			uniqueCode=null;
		}
		Integer i = 0;
		if(null != name && null != user){
			if(blogCommon.isNumeric(orderId)){
				i = Integer.parseInt(orderId);
			}
			Columns c = new Columns(Integer.parseInt(columnId),user.getId(),name,i,uniqueCode);
			columnsMng.updateColumns(c);
		}
		
		try {
			request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String update_tz(String id,String orderId,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
		Columns column = new Columns(Integer.parseInt(id),user.getId(),request.getParameter("name"),Integer.parseInt(orderId),request.getParameter("uniqueCode"));
		if(column.getUniqueCode()==""){
			column.setUniqueCode(null);
		}
		model.addAttribute("column", column);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.columnsUpdate");
	}

	public String link_save(String linkUrl,String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		channelMng.updateLinkUrl(linkUrl,user);
		return FrontUtils.showSuccess(request, model, nextUrl);
	}

	public void friends_save(String friends, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			FrontUtils.showLogin(request, model, site);
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
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		channelMng.updateBlogVisitNum(userT);
		if(user!=null){
			if(user.getId()!=userT.getId()){
				channelMng.updateBlogVisitorTime(user,userT);
			}
		}
		model = blogCommon.getColumn(request,model,userT);
		model = blogCommon.getChannel(request,model,userT,site);
		model = blogCommon.getTotalArticleNum(model,userT);
		model = blogCommon.getTotalCommentNum(model, userT);
		model = blogCommon.getStarBlogger(request, model);
		model = blogCommon.getAlreadyJoinGroup(request, model,userT);
		model = blogCommon.getAddFriends(request, model,userT,user);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_firendsBlog(Integer.valueOf(userIds),q, queryChannelId,site.getId(), modelId,null, cpn(pageNo), 20,null);
		model.addAttribute("pagination", p);
		model.addAttribute("GroupFlag", 0);
		model.addAttribute("usert", userT);
		model.addAttribute("userIds", userIds);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
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
		String user_ids = request.getParameter("user_ids");
		CmsUser user=cmsUserMng.findById(Integer.valueOf(user_ids.toString()));
		String joinGroupStata = request.getParameter("joinGroupStata");
		int userId=user.getId();
		Integer columnId = null;
		Integer channelId = null;
		if(joinGroupStata!=null&&joinGroupStata.equals("0")){
			userId=0;
			model.addAttribute("GroupFlag", -1);
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
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.getChannel(request,model,user,site);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,user);
 		model = blogCommon.getAddFriends(request, model,user,u);
		model.addAttribute("usert", user);
		model.addAttribute("userIds", user.getId());
		//model.addAttribute("columnId", columnId);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,userId, cpn(pageNo), 20,columnId,channelId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
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
		CmsSite site = CmsUtils.getSite(request);
		if (user == null) {
			FrontUtils.showLogin(request, null, site);
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
		CmsSite site = CmsUtils.getSite(request);
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
		Pagination visitorList = contentMng.getPageForMember_visitor(cpn(pageNo), 24,user);
		model.addAttribute("pagination", visitorList);
		return model;
	}
	
	public String gotoDataShow(int dataFlag, HttpServletRequest request,HttpServletResponse response, ModelMap model,Integer pageNo) {
		CmsUser u = CmsUtils.getUser(request);
		CmsSite site = CmsUtils.getSite(request);
		if(u==null){
			FrontUtils.showLogin(request, model, site);
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
		model = blogCommon.getChannel(request,model,u,site);
		model = blogCommon.getColumn(request,model,u);
		model = blogCommon.getTotalArticleNum(model,u);
 		model = blogCommon.getTotalCommentNum(model, u);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,u);
		FrontUtils.frontData(request, model, site);
		    return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.dataShow");
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
		model = blogCommon.getChannel(request,model,u,site);
		model = blogCommon.getColumn(request,model,u);
		model = blogCommon.getTotalArticleNum(model,u);
 		model = blogCommon.getTotalCommentNum(model, u);
 		model = blogCommon.getStarBlogger(request, model);
 		model = blogCommon.getAlreadyJoinGroup(request, model,u);
 		model = blogCommon.getAddFriends(request, model,u,user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.frienddataShow");
	}
	
	public String blogContentShow(String[] paths,String[] params,
			PageInfo info,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String columnId){
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
			CmsUser u = CmsUtils.getUser(request);
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
			model.addAttribute("pic", content.getPictureByNo(pageNo));
			String collection = request.getParameter("collection");
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
			model = blogCommon.getChannel(request,model,u,site);
			model = blogCommon.getColumn(request,model,u);
			model = blogCommon.getTotalArticleNum(model,u);
	 		model = blogCommon.getTotalCommentNum(model, u);
	 		model = blogCommon.getStarBlogger(request, model);
			FrontUtils.frontData(request, model, site);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogContentShow");
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
			model = blogCommon.getAlreadyJoinGroup(request, model,user);
			model = blogCommon.getChannel(request,model,user,site);
			model = blogCommon.getColumn(request,model,user);
			model = blogCommon.getTotalArticleNum(model,user);
	 		model = blogCommon.getTotalCommentNum(model, user);
	 		model = blogCommon.getStarBlogger(request, model);
	 		model = blogCommon.getAddFriends(request, model,user,u);
			FrontUtils.frontData(request, model, site);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogContentShowFriend");
		}

	private WebErrors validateSave(String title, String author,
			String description, String txt,ContentDoc doc, String tagStr,
			CmsSite site, CmsUser user, String captcha,
			HttpServletRequest request, HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		try {
			if (!imageCaptchaService.validateResponseForID(session
					.getSessionId(request, response), captcha)) {
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			}
		} catch (CaptchaServiceException e) {
			errors.addErrorCode("error.exceptionCaptcha");
			return errors;
		}
		if (errors.ifBlank(title, "title", 50)) {
			return errors;
		}
		if (errors.ifMaxLength(author, "author", 6)) {
			return errors;
		}
		if (errors.ifMaxLength(description, "description", 255)) {
			return errors;
		}
		if(doc==null){
			// 内容不能大于1M
			if (errors.ifBlank(txt, "txt", 1048575)) {
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
}
