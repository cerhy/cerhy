package com.jeecms.cms.action.blog;

import static com.jeecms.cms.Constants.TPLDIR_BLOG;
import static com.jeecms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.jeecms.cms.dao.main.impl.BlogDao;
import com.jeecms.cms.entity.main.CmsModel;
import com.jeecms.cms.entity.main.Columns;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.entity.main.ContentExt;
import com.jeecms.cms.entity.main.ContentTxt;
import com.jeecms.cms.entity.main.ContentType;
import com.jeecms.cms.entity.main.Focus;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.CmsModelMng;
import com.jeecms.cms.manager.main.ContentDocMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.cms.manager.main.ContentTypeMng;
import com.jeecms.cms.manager.main.impl.ColumnsMng;
import com.jeecms.cms.manager.main.impl.FocusMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.common.util.StrUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

public class BlogAct {

	public String blog_index(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.blog_focus_find(null,request,model);
	    model = blogCommon.getLinks(model,user);
	    model = blogCommon.getFriends(model,user);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
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
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,null,user.getGroup().getId(), cpn(pageNo), 20,null,null);
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
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.blog_focus_find(null,request,model);
	    model = blogCommon.getLinks(model,user);
	    model = blogCommon.getFriends(model,user);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_BLOG,"tpl.blogSetting");
	}
	
	public String blog_list(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		Integer columnId = null;
		Integer channelId = null;
		//为了删除文章后能跳转回本栏目下
		if(null != request.getParameter("columnId")){
			model.addAttribute("columnId", request.getParameter("columnId"));
			columnId = Integer.parseInt(request.getParameter("columnId"));
		}
		if(null != request.getParameter("channelId")){
			model.addAttribute("channelId", request.getParameter("channelId"));
			channelId = Integer.parseInt(request.getParameter("channelId"));
		}
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.blog_focus_find(null,request,model);
	    model = blogCommon.getLinks(model,user);
	    model = blogCommon.getFriends(model,user);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(),user.getGroup().getId(), cpn(pageNo), 20,columnId,channelId);
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
		model = blogCommon.getColumn(request,model,user);
	    model = blogCommon.getChannel(request,model,user,site);
	    model = blogCommon.blog_focus_find(null,request,model);
	    model = blogCommon.getLinks(model,user);
	    model = blogCommon.getFriends(model,user);
	    model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
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
			Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
				CmsSite site = CmsUtils.getSite(request);
				CmsUser user = CmsUtils.getUser(request);
				FrontUtils.frontData(request, model, site);
			if (user == null) {
				return FrontUtils.showLogin(request, model, site);
			}

		Content c = new Content();
		c.setSite(site);
		CmsModel defaultModel=cmsModelMng.getDefModel();
		
		int groupId = user.getGroup().getId();//学科教研模板，市县教研内容模板
		if (4 == groupId) {
			modelId = 11;//学科教研
		} else if (5 == groupId) {
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
				charge,chargeAmount, user, true);
		if(doc!=null){
			contentDocMng.save(doc, c);
		}
		/*try {
			request.getRequestDispatcher("/blog/index.jspx?").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		nextUrl += "/blog/index.jspx";
		return FrontUtils.showSuccess(request, model, nextUrl);
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
	    model = blogCommon.blog_focus_find(null,request,model);
	    model = blogCommon.getLinks(model,user);
		model = blogCommon.getFriends(model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
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
	
	public String blog_update(Integer id, String title, String author,
			String description, String txt, String tagStr,Integer columnId, Integer channelId,
			String mediaPath,String mediaType,
			String[] attachmentPaths, String[] attachmentNames,
			String[] attachmentFilenames, String[] picPaths, String[] picDescs,
			ContentDoc doc,Short charge,Double chargeAmount,
			String nextUrl, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
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
		contentMng.blog_update(c, ext, t,null, tagArr, null, null, null, 
				attachmentPaths,attachmentNames, attachmentFilenames
				,picPaths,picDescs, null, columnId,channelId, null, null, 
				charge,chargeAmount,user, true);
		if(doc!=null){
			contentDocMng.update(doc, c);
		}
		nextUrl += "/blog/index.jspx";
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	public void blog_delete(Integer contentId,Integer columnId,Integer channelId, HttpServletRequest request,
			  HttpServletResponse response, ModelMap model) {
			contentMng.deleteByIdBlog(contentId);
			int id;
		try {
			if(null != columnId){
				id = columnId;
				request.getRequestDispatcher("/blog/contribute_list.jspx?columnId="+id).forward(request, response);
			}

            if(null != channelId){
            	id = channelId;
				request.getRequestDispatcher("/blog/contribute_list.jspx?channelId="+id).forward(request, response);
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
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.blog_focus_find(null,request,model);
		model = blogCommon.getLinks(model,user);
		model = blogCommon.getFriends(model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
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
		String name = request.getParameter("columnInput");
		String order = request.getParameter("columnOrder");
		CmsUser user = CmsUtils.getUser(request);
		Integer i = 0;
		if(null != name && null != user){
			if(blogCommon.isNumeric(order)){
				i = Integer.parseInt(order);
			}
			Columns c = new Columns(user.getId(),name,i);
			columnsMng.addColumns(c);
		}
		try {
			request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void columns_detele(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		String columnId = request.getParameter("id");
		if(null != columnId){
			List<Content> l = contentMng.countByColumnId(Integer.parseInt(columnId));
			if(null == l || l.size()<1){
				columnsMng.deleteColumns(Integer.parseInt(columnId));
			}
		}
		try {
			request.getRequestDispatcher("/blog/columns_list.jspx").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void columns_update(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		String columnId = request.getParameter("id");
		String name = request.getParameter("updateName");
		String orderId = request.getParameter("updateOrderId");
		Integer i = 0;
		if(null != name && null != user){
			if(blogCommon.isNumeric(orderId)){
				i = Integer.parseInt(orderId);
			}
			Columns c = new Columns(Integer.parseInt(columnId),user.getId(),name,i);
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
		String name =null ;
		  try {
			request.setCharacterEncoding("UTF-8");
		    name = new String(request.getParameter("name").getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.blog_focus_find(null,request,model);
		model = blogCommon.getLinks(model,user);
		model = blogCommon.getFriends(model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
		Columns column = new Columns(Integer.parseInt(id),user.getId(),name,Integer.parseInt(orderId));
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

	public String friends_save(String friends, String nextUrl,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		channelMng.updateFriends(friends,user);
		return FrontUtils.showSuccess(request, model, nextUrl);
	}

	public String friendCenter(String userIds,String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		CmsUser userT=cmsUserMng.findById(Integer.valueOf(userIds.toString()));
		channelMng.updateBlogVisitNum(userT);
		model = blogCommon.getColumn(request,model,userT);
	    model = blogCommon.getChannel(request,model,userT,site);
	    model = blogCommon.blog_focus_find(Integer.parseInt(userIds),request,model);
	    model = blogCommon.getLinks(model,userT);
		model = blogCommon.getFriends(model,userT);
 		model = blogCommon.getTotalArticleNum(model,userT);
 		model = blogCommon.getTotalCommentNum(model, userT);
		FrontUtils.frontData(request, model, site);
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
		Pagination p = contentMng.getPageForMember_firendsBlog(Integer.valueOf(userIds),q, queryChannelId,site.getId(), modelId,user.getId(), cpn(pageNo), 20,null);
		model.addAttribute("pagination", p);
		model.addAttribute("usert", userT);
		model.addAttribute("userIds", userIds);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}

	public String blog_list_friend(String q, Integer modelId,Integer queryChannelId,String nextUrl,Integer pageNo,HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		//CmsUser user = CmsUtils.getUser(request);
		String user_ids = request.getParameter("user_ids");
		Integer columnId = null;
		Integer channelId = null;
		if(null != request.getParameter("columnId")){
			model.addAttribute("columnId", request.getParameter("columnId"));
			columnId = Integer.parseInt(request.getParameter("columnId"));
		}
		if(null != request.getParameter("channelId")){
			model.addAttribute("channelId", request.getParameter("channelId"));
			channelId = Integer.parseInt(request.getParameter("channelId"));
		}
		CmsUser user=cmsUserMng.findById(Integer.valueOf(user_ids.toString()));
		model = blogCommon.getColumn(request,model,user);
		model = blogCommon.getChannel(request,model,user,site);
		model = blogCommon.blog_focus_find(null,request,model);
		model = blogCommon.getLinks(model,user);
		model = blogCommon.getFriends(model,user);
		model = blogCommon.getTotalArticleNum(model,user);
 		model = blogCommon.getTotalCommentNum(model, user);
		model.addAttribute("usert", user);
		model.addAttribute("userIds", user.getId());
		model.addAttribute("columnId", columnId);
		FrontUtils.frontData(request, model, site);
		Pagination p = contentMng.getPageForMember_blog(q, queryChannelId,site.getId(), modelId,user.getId(),user.getGroup().getId(), cpn(pageNo), 20,columnId,channelId);
		model.addAttribute("pagination", p);
		if (!StringUtils.isBlank(q)) {
			model.addAttribute("q", q);
		}
		if (modelId != null) {
			model.addAttribute("modelId", modelId);
		}
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
		return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_BLOG, nextUrl);
	}
	
	public void blog_focus(String focusUserId,String focusUserName, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String focusTime = format.format(date);
		CmsUser user = CmsUtils.getUser(request);
		Focus f = focusMng.add(user.getId(),user.getUsername(), Integer.parseInt(focusUserId), focusUserName, focusTime);
		if(null != f){
			response.getWriter().print("1");
		}else{
			response.getWriter().print("0");
		}
	}
	
	public void blog_focus_check(String focusUserId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		CmsUser user = CmsUtils.getUser(request);
		List<Focus> list= focusMng.find(user.getId(), Integer.parseInt(focusUserId));
		if(null != list && list.size()>0){
			response.getWriter().print("1");
		}else{
			response.getWriter().print("0");
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
